package com.gc.cvrapp.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Surface;

import com.gc.cvrapp.cvr.Cvr.MediaListener;
import com.gc.cvrapp.media.audio.AudioBuffer;
import com.gc.cvrapp.media.audio.AudioInfo;
import com.gc.cvrapp.media.video.VideoBuffer;
import com.gc.cvrapp.media.video.VideoInfo;
import com.gc.cvrapp.media.audio.AudioPlayer;
import com.gc.cvrapp.media.video.VideoPlayer;
import com.gc.cvrapp.utils.LogUtil;

import java.nio.ByteBuffer;

import static android.content.Context.POWER_SERVICE;

/**
 * Base class for cvr media, provide user preview, playback, take photo.
 */
public class Media implements MediaListener {
    private int sample = 0;
    private int mPlaybackState;
    private VideoPlayer             mVideoPlayer;
    private AudioPlayer             mAudioPlayer;
    private VideoInfo               mVideoInfo;
    private AudioInfo               mAudioInfo;
    private Surface                 mSurface;
    private WakeLock                mLock;
    private PlaybackCallback        IplaybackCb;
    private PreviewCallback         IpreviewCb;
    private boolean isRunning       = false;
    private boolean isSeek          = false;
    private boolean hasSeek         = false;
    private boolean isPreviewing    = false;
    private boolean isRecording     = false;
    private boolean isTakePhotoing  = false;
    private static final String TAG = "Media";

    public Media(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
        if (pm != null) {
            mLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cn");
            mLock.setReferenceCounted(false);
        }

        mVideoPlayer = new VideoPlayer();
        mAudioPlayer = new AudioPlayer();
    }

    @Override
    public void onPreview(ByteBuffer data, int size) {
        if (!isPreviewing) {
            return;
        }
//        LogUtil.i(TAG, "preview :" + String.valueOf(size));
        VideoBuffer videoBuffer = new VideoBuffer(data, size);
        mVideoPlayer.put(videoBuffer);
    }

    @Override
    public void onPreviewStop() {
        IpreviewCb.onPreviewState(isPreviewing);
    }

    @Override
    public void onPreviewStartRecord() {
        isRecording = false;
        IpreviewCb.onPreviewRecordState(true);
    }

    @Override
    public void onPreviewStopRecord() {
        isRecording = false;
        IpreviewCb.onPreviewRecordState(false);
    }

    @Override
    public void onPreviewTakePhoto() {
        isTakePhotoing = false;
    }

    /**
     * media set record video state
     * @param state start record or finish
     */
    public void setPreviewRecordState(boolean state) {
        isRecording = state;
    }

    /**
     * media get record video state
     * @return record state
     */
    public boolean getPreviewRecordState() {
        return isRecording;
    }

    /**
     * media set take photo state
     * @param state start take photo or not
     */
    public void setPreviewTakePhotoState(boolean state) {
        isTakePhotoing = state;
    }

    /**
     * media get take photo state
     * @return state of take photo
     */
    public boolean getPreviewTakePhotoState() {
        return isTakePhotoing;
    }

    /**
     * interface PreviewCallback for media preview
     */
    public interface PreviewCallback {
        /**
         * call back preview state
         * @param state MediaPreviewState
         */
        void onPreviewState(boolean state);

        /**
         * call back preview record state
         * @param state MediaPreview record state
         */
        void onPreviewRecordState(boolean state);
    }

    /**
     * set media callback for preview
     * @param cb the Preview callback interface
     */
    public void setPreviewCallback(PreviewCallback cb) {
        IpreviewCb = cb;
    }

    /**
     * media set preview state
     * @param state start preview or not
     */
    public void setPreviewState(boolean state) {
        LogUtil.i(TAG, "setPreviewState: old state: " + String.valueOf(isPreviewing) + " new state: " + String.valueOf(state));
        isPreviewing = state;
    }

    /**
     * media get preview state
     * @return state of preview
     */
    public boolean getPreviewState() {
        return isPreviewing;
    }


    /**
     * class for media playback state constants
     */
    public static class MediaPlaybackState {
        /** MediaPlaybackState: playback start */
        public static final int Start       = 1;
        /** MediaPlaybackState: playback stop */
        public static final int Stop        = 2;
        /** MediaPlaybackState: playback continue */
        public static final int Continue    = 3;
        /** MediaPlaybackState: playback pause */
        public static final int Pause       = 4;
        /** MediaPlaybackState: playback seek */
        public static final int Seek        = 5;
        /** MediaPlaybackState: playback codec release */
        public static final int Release     = 6;
        /** MediaPlaybackState: playback restart */
        public static final int Restart     = 7;
        /** MediaPlaybackState: playback previous or next */
        public static final int PrevNext	= 8;
        /** MediaPlaybackState: playback error */
        public static final int Error       = 9;
    }

    @Override
    public void onPlaybackInfo(VideoInfo videoInfo, AudioInfo audioInfo) {
        if (null == videoInfo) {
            setPlaybackState(MediaPlaybackState.Error);
            IplaybackCb.onPlaybackState(MediaPlaybackState.Error);
            return;
        }

        setPlaybackState(MediaPlaybackState.Start);
        mVideoInfo = videoInfo;
        mAudioInfo = audioInfo;
        IplaybackCb.onPlaybackState(MediaPlaybackState.Start);
    }

    @Override
    public void onPlaybackStop() {
        LogUtil.i(TAG, "onPlaybackStop");
        IplaybackCb.onPlaybackState(mPlaybackState);
    }

    @Override
    public void onPlaybackPause() {
        switch (mPlaybackState) {
            case MediaPlaybackState.Continue:
                LogUtil.i(TAG, "resp playback continue");
                IplaybackCb.onPlaybackState(MediaPlaybackState.Continue);
                break;

            case MediaPlaybackState.Pause:
                LogUtil.i(TAG, "resp playback pause");
                IplaybackCb.onPlaybackState(MediaPlaybackState.Pause);
                break;

            case MediaPlaybackState.PrevNext:
                LogUtil.i(TAG, "resp playback prevnext pause");
                IplaybackCb.onPlaybackState(MediaPlaybackState.Pause);
                break;

            default:
                break;
        }
    }

    @Override
    public void onPlaybackSeek(int sample) {
        isSeek = true;
        this.sample = sample;
        LogUtil.i(TAG, "seek sample: " + String.valueOf(sample));
        stepPlayback(this.sample);
    }

    @Override
    public void onVideoSampleData(ByteBuffer data, int timestamp, int size) {

        if (!mSurface.isValid()) {
            LogUtil.w(TAG, "surface invalid");
            return;
        }
//        LogUtil.i(TAG, "sample:" + size);
        VideoBuffer videoBuffer;
        if (isSeek) {
            LogUtil.i(TAG, "fetch one I frame");
            videoBuffer = new VideoBuffer(data, size, isSeek);
            isSeek = false;
        } else {
            if (!isRunning) {
                LogUtil.i(TAG, "not running playstate : " + String.valueOf(mPlaybackState));
                return;
            }
            videoBuffer = new VideoBuffer(data, size);
        }
        mVideoPlayer.put(videoBuffer);
        stepPlayback(sample++);
    }

    @Override
    public void onAudioSampleData(ByteBuffer data, int size) {
//        LogUtil.i(TAG, "audio sample " + String.valueOf(size));
        AudioBuffer audioBuffer = new AudioBuffer(data, size);
        mAudioPlayer.put(audioBuffer);
    }

    @Override
    public void onSampleEnd() {
        LogUtil.i(TAG, "sample end");
        VideoBuffer videoBuffer = new VideoBuffer();
        mVideoPlayer.put(videoBuffer);
        setPlaybackState(MediaPlaybackState.Stop);
        IplaybackCb.onPlaybackState(mPlaybackState);
    }

    @Override
    public void onPhoto(String picname, Bitmap bitmap) {
        IplaybackCb.onPlaybackPhoto(picname, bitmap);
    }

    /**
     *  media start preview
     *  @param surface the Surface object for managing screen
     */
    public void startPreview(Surface surface) {
        isPreviewing = true;
        mVideoPlayer.start(surface, null);
    }

    /**
     * media start preview
     */
    public void stopPreview() {
        mVideoPlayer.release();
    }

    /**
     * interface PlaybackCallback for media playback
     */
    public interface PlaybackCallback {
        /**
         * call back playback photo
         * @param picname photo name
         * @param bitmap photo bitmap data
         */
        void onPlaybackPhoto(String picname, Bitmap bitmap);

        /**
         * call back playback state
         * @param state MediaPlaybackState
         */
        void onPlaybackState(int state);

        /**
         * call back playback step
         * @param step video playback progressing step
         */
        void onPlaybackStep(int step);
    }

    /**
     * set media callback for playback
     * @param cb the Playback callback
     */
    public void setPlaybackCallback(PlaybackCallback cb) {
        IplaybackCb = cb;
    }

    /**
     * start media playback
     * @param surface the Surface object for managing screen
     */
    public void startPlayback(Surface surface) {
        LogUtil.i(TAG, "startPlayback");
        mSurface = surface;
        mVideoPlayer.start(surface, mVideoInfo);
        mAudioPlayer.start(mAudioInfo);
        mPlaybackState = MediaPlaybackState.Continue;
        sample = 0;
    }

    /**
     * media stop playback
     */
    public void stopPlayback() {
        mVideoPlayer.stop();
        mAudioPlayer.stop();
        sample = 0;
        stepPlayback(sample);
    }

    /**
     * media pause playback
     */
    public void pausePlayback() {
//        mVideoPlayer.stop();
        mAudioPlayer.pause();
    }

    /**
     * media continue playback
     * @param surface the Surface object for managing screen
     */
    public void continuePlayback(Surface surface) {
//        mVideoPlayer.start(surface, mVideoInfo);

        mSurface = surface;
        mAudioPlayer.play();
    }

    /**
     * media restart playback
     */
    public void restartPlayback() {
        release();
    }

    /**
     * media release playback
     */
    public void releasePlayback() {
        release();
    }

    /**
     * get media playback state
     * @return playback state
     */
    public int getPlaybackState() {
        return mPlaybackState;
    }

    /**
     * set media playback state
     * @param state playback state The state are:
     *              {MediaPlaybackState.Start,MediaPlaybackState.Continue,
     *              MediaPlaybackState.Pause,MediaPlaybackState.Stop,
     *              MediaPlaybackState.Seek,MediaPlaybackState.Restart,
     *              MediaPlaybackState.Release,MediaPlaybackState.PrevNext,
     *              MediaPlaybackState.Error.}
     */
    public void setPlaybackState(int state) {
        switch (state) {
            case MediaPlaybackState.Start:
            case MediaPlaybackState.Continue:
                mPlaybackState = state;
                isRunning = true;
                hasSeek = false;
                break;
            case MediaPlaybackState.Pause:
            case MediaPlaybackState.Stop:
                mPlaybackState = state;
                isRunning = false;
                break;

            case MediaPlaybackState.Seek:
                LogUtil.i(TAG, "seek clear" + " running: " + String.valueOf(isRunning) +
                        " hasSeek: " + String.valueOf(hasSeek));
                mVideoPlayer.clear();
                mAudioPlayer.clear();

                if (isRunning) return;
                if (hasSeek)   return;
                hasSeek = true;
                LogUtil.i(TAG, "seek clear 1");
                mVideoPlayer.put(new VideoBuffer());
                break;

            case MediaPlaybackState.Restart:
                mPlaybackState = MediaPlaybackState.Restart;
                break;

            case MediaPlaybackState.Release:
                isRunning = false;
                mPlaybackState = MediaPlaybackState.Release;
                break;

            case MediaPlaybackState.PrevNext:
                isRunning = false;
                mPlaybackState = MediaPlaybackState.PrevNext;
                break;

            case MediaPlaybackState.Error:
                mPlaybackState = MediaPlaybackState.Error;
                break;

            default:
                break;
        }
    }

    /**
     * get media playback video total samples
     * @return playback video total samples
     */
    public int getPlaybackSamples() {
        return mVideoInfo.getVideoSamples();
    }

    /**
     * media lock screen wake
     */
    public void wakeLock() {
        if (null != mLock) {
            mLock.acquire();
        }
    }

    /**
     * media unlock screen wake
     */
    public void wakeUnLock(){
        if (null != mLock) {
            mLock.release();
        }
    }

    /* stepping playback */
    private void stepPlayback(int sample) {
        int total = mVideoInfo.getVideoSamples();
        double step = (((double)sample / (double)total) * 100);
        IplaybackCb.onPlaybackStep((int)step);
    }

    private void release() {
        mVideoPlayer.release();
        mAudioPlayer.release();
    }
}
