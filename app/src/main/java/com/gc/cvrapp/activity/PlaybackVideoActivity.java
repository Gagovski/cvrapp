package com.gc.cvrapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gc.cvrapp.AppConfig;
import com.gc.cvrapp.BuildConfig;
import com.gc.cvrapp.R;
import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.service.CvrService;
import com.gc.cvrapp.service.CvrServiceManager;
import com.gc.cvrapp.service.CvrServiceManager.CvrServiceConnection;
import com.gc.cvrapp.media.Media;
import com.gc.cvrapp.media.Media.MediaPlaybackState;
import com.gc.cvrapp.utils.LogUtil;

import java.lang.ref.WeakReference;

public class PlaybackVideoActivity extends AppCompatActivity implements CvrServiceConnection,
             SurfaceHolder.Callback,
             SeekBar.OnSeekBarChangeListener {

    private FileItemList mVideoList;
    private int mCurVideoId;
    private CvrServiceManager mServiceManager;
    private Media mMedia;
    private Cvr mCvr;
    private Button mButtonPlay;
    private SurfaceView mSurfaceView;
    private View mControllerView;
    private SeekBar mSeekBar;
    private Dialog mDialog;
    private TextView mTimer;
    private TextView mTitle;
    private int mStep = 0;
    private static final String ARG_FILELIST = "filelist";
    private static final String TAG = "PlaybackVideoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playvideo);
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        mControllerView = findViewById(R.id.playback_controller);
        mTimer = (TextView) findViewById(R.id.play_timer);
        mTitle = (TextView) findViewById(R.id.videotitle);
        //Todo find play button
        mButtonPlay = (Button) findViewById(R.id.btn_play);
        //Todo seekbar
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mDialog = new AlertDialog.Builder(this).setTitle("Error:").setMessage("mp4 file error cannot playback!").create();

        //Todo open SurfaceView
        mSurfaceView = (SurfaceView) findViewById(R.id.playback_surface);
        openSurface(mSurfaceView);
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.i(TAG, "click surface");
                mHandler.postDelayed(dispPanel, 0);
            }
        });

        mServiceManager = new CvrServiceManager(this, AppConfig.APP_SERVICE);
        mServiceManager.setCallback(this);
        mVideoList  = (FileItemList) getIntent().getSerializableExtra(ARG_FILELIST);
        mCurVideoId = getCurVideoId(mVideoList.getCurFile());
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG, "onStop");
        mHandler.removeCallbacks(dispPanel);
        if (null != mMedia) {
            mMedia.setPlaybackState(MediaPlaybackState.Release);
            mMedia.wakeUnLock();
        }
        if (null != mCvr)
            mCvr.playbackVideoStop(mMedia);
        mServiceManager.unbindCvrService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
        if (null != mDialog) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onCvrConnected(Cvr cvr, CvrService service) {
        LogUtil.i(TAG, "onCvrConnected");
        mMedia = service.getMedia();
        if (null != mMedia) {
            mMedia.setPlaybackCallback(Icallback);
            mMedia.wakeLock();
        }
        mCvr = cvr;
        if (null != mCvr) {
            //Todo cvr send playbackVideo command
            mCvr.playbackVideo(mMedia, mVideoList.getFilelist().get(mCurVideoId));
        }
    }

    @Override
    public void onCvrDisconnected(CvrService service) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceCreated");
        mServiceManager.bindCvrService();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void onClickPrevVideo(View view) {
        LogUtil.i(TAG, "play prev");
        if (mVideoList.getFilelist().isEmpty() || (MediaPlaybackState.PrevNext == mMedia.getPlaybackState())) {
            return;
        }
        -- mCurVideoId;
        if (mCurVideoId < 0) {
            mCurVideoId = (mVideoList.getFilelist().size() - 1);
        }

        mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgPrevNext));
    }

    public void onClickNextVideo(View view) {
        LogUtil.i(TAG, "play next");
        if (mVideoList.getFilelist().isEmpty() || (MediaPlaybackState.PrevNext == mMedia.getPlaybackState())) {
            return;
        }
        ++ mCurVideoId;
        if (mCurVideoId >= mVideoList.getFilelist().size()) {
            mCurVideoId = 0;
        }

        mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgPrevNext));
    }

    public void onClickPlay(View view) {
        LogUtil.i(TAG, "click state: " + String.valueOf(mMedia.getPlaybackState()));
        switch (mMedia.getPlaybackState()) {
            case MediaPlaybackState.Stop:
                mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgRestart));
                break;

            case MediaPlaybackState.Pause:
                mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgContinue));
                break;

            case MediaPlaybackState.Start:
            case MediaPlaybackState.Continue:
                mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgPause));
                break;

            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgSeek));
    }

    private int getCurVideoId(String picname) {

        for (int i = 0; i < mVideoList.getFilelist().size(); i++) {
            String file = mVideoList.getFilelist().get(i);
            if (file.contentEquals(picname)) {
                return i;
            }
        }

        return 0;
    }

    private void openSurface(SurfaceView surfaceView) {
        LogUtil.i(TAG, "openSurface");
        ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();

        surfaceView.getHolder().addCallback(this);
        surfaceView.setLayoutParams(layoutParams);
    }

    private void pause() {
        LogUtil.i(TAG, "Click pause");
        mMedia.setPlaybackState(MediaPlaybackState.Pause);
        mCvr.playbackVideoPause(mMedia);
    }

    private void play() {
        LogUtil.i(TAG, "Click continue");
        mMedia.setPlaybackState(MediaPlaybackState.Continue);
        mCvr.playbackVideoContinue(mMedia);
    }

    private void seek() {
        int step = mSeekBar.getProgress() * mMedia.getPlaybackSamples() / 100;
        mMedia.setPlaybackState(MediaPlaybackState.Seek);
        mCvr.playbackVideoSeek(mMedia, step);
    }

    private void prevnext() {
        //Todo stop playback video
        LogUtil.i(TAG, "switching");
        int state = mMedia.getPlaybackState();
        mMedia.setPlaybackState(MediaPlaybackState.PrevNext);
        switch (state) {
            case MediaPlaybackState.Continue:
                mCvr.playbackVideoPause(mMedia);
                break;

            case MediaPlaybackState.Pause:
            case MediaPlaybackState.Stop:
                mCvr.playbackVideoStop(mMedia);
                break;

            default:
                break;
        }
    }

    private void error() {
        Log.e(TAG, "mp4 file error cannot playback!");
        mDialog.show();
    }

    private Handler mHandler = new PlaybackHandler(this);

    private static class PlaybackHandler extends Handler {
        private final WeakReference<PlaybackVideoActivity> mActivity;

        PlaybackHandler(PlaybackVideoActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            PlaybackVideoActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            switch (msg.what) {
                case MsgCode.MsgPlay:
                    activity.mCvr.playbackVideo(activity.mMedia, activity.mVideoList.getFilelist().get(activity.mCurVideoId));
                    break;

                case MsgCode.MsgStart:
                    activity.mTitle.setText(activity.mVideoList.getFilelist().get(activity.mCurVideoId));
                    activity.mButtonPlay.setText("pause");
                    break;

                case MsgCode.MsgRestart:
                    activity.mButtonPlay.setText("pause");
                    activity.mTitle.setText(activity.mVideoList.getFilelist().get(activity.mCurVideoId));
                    sendEmptyMessageDelayed(MsgCode.MsgPlay, 500);
                    break;

                case MsgCode.MsgStop:
                    activity.mButtonPlay.setText("play");
                    break;

                case MsgCode.MsgPause:
                    activity.mButtonPlay.setText("play");
                    activity.pause();
                    break;

                case MsgCode.MsgContinue:
                    activity.mButtonPlay.setText("pause");
                    activity.play();
                    break;

                case MsgCode.MsgPrevNext:
                    activity.prevnext();
                    break;

                case MsgCode.MsgSeek:
                    activity.seek();
                    break;

                case MsgCode.MsgStep:
                    activity.mSeekBar.setProgress(msg.arg1);
                    activity.mTimer.setText(activity.timestamp((msg.arg1 * activity.mMedia.getPlaybackSamples() / 100) * 30));
                    break;

                case MsgCode.MsgError:
                    activity.error();
                    break;

                default:
                    break;
            }

        }
    }

    private Media.PlaybackCallback Icallback = new Media.PlaybackCallback() {

        @Override
        public void onPlaybackPhoto(String picname, Bitmap bitmap) {}

        @Override
        public void onPlaybackState(int state) {
            switch (state) {
                case MediaPlaybackState.Start:
                    mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgStart));
                    mMedia.startPlayback(mSurfaceView.getHolder().getSurface());
                    break;

                case MediaPlaybackState.Stop:
                    mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgStop));
                    mMedia.stopPlayback();
                    break;

                case MediaPlaybackState.Pause:
                    mMedia.pausePlayback();
                    if (MediaPlaybackState.PrevNext == mMedia.getPlaybackState()) {
                        mCvr.playbackVideoStop(mMedia);
                    }
                    break;

                case MediaPlaybackState.Continue:
                    mMedia.continuePlayback(mSurfaceView.getHolder().getSurface());
                    break;

                case MediaPlaybackState.Restart:
                    mMedia.restartPlayback();
                    break;

                case MediaPlaybackState.Release:
                    mMedia.releasePlayback();
                    break;

                case MediaPlaybackState.PrevNext:
                    mMedia.releasePlayback();
                    mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgRestart));
                    break;

                case MediaPlaybackState.Error:
                    Log.e(TAG, "mp4 file error cannot playback!");
                    mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgError));
                    break;

                default:

                    break;
            }
        }

        @Override
        public void onPlaybackStep(int step) {
            if (mStep != step) {
                mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgStep, step, 0));
                mStep = step;
            }
        }
    };

    private Runnable dispPanel = new Runnable() {
        @Override
        public void run() {
            if (mControllerView.isShown()) {
                LogUtil.i(TAG, "controller visible");
                mControllerView.setVisibility(View.INVISIBLE);
            } else {
                LogUtil.i(TAG, "controller invisible");
                mControllerView.setVisibility(View.VISIBLE);
            }
        }
    };

    private String timestamp(long sampletime) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = sampletime / dd;
        long hour = (sampletime - day * dd) / hh;
        long minute = (sampletime - day * dd - hour * hh) / mi;
        long second = (sampletime - day * dd - hour * hh - minute * mi) / ss;

        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;

        return strHour + ":" + strMinute + ":" + strSecond;
    }
}
