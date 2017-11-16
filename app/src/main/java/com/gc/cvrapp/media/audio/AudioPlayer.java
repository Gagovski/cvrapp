package com.gc.cvrapp.media.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.gc.cvrapp.utils.LogUtil;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * class for audio player
 */
public class AudioPlayer {
    private AudioTrack mTrack;
    private boolean isStop = true;
    private AudioPlayerThread athread;
    protected final LinkedBlockingQueue<AudioBuffer> mQueue = new LinkedBlockingQueue<AudioBuffer>();
    private static final String TAG = "AudioPlayer";

    public AudioPlayer() {
        athread = new AudioPlayerThread();
        athread.start();
    }

    /**
     * audio track start
     * @param info audio info
     */
    public void start(AudioInfo info) {
        //Todo start audio track
        if (0 == info.getAudioSamples()) {
            LogUtil.w(TAG, "Audio info sample = 0");
            return;
        }

        LogUtil.i(TAG, "Audio Info sampleRate: " + info.getAudioSampleRate() + " samples: " + info.getAudioSamples());
        int bufferSize = AudioTrack.getMinBufferSize(info.getAudioSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mTrack = new AudioTrack(AudioManager.STREAM_MUSIC, info.getAudioSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize * 2, AudioTrack.MODE_STREAM);
        mTrack.play();

        isStop = false;
        synchronized (athread) {
            athread.notify();
        }
    }

    /**
     * audio track pause
     */
    public void pause() {
        if (null == mTrack) {
            return;
        }
        LogUtil.i(TAG, "pause");
        isStop = true;
        mTrack.pause();
    }

    /**
     * audio track play
     */
    public void play() {
        if (null == mTrack) {
            return;
        }
        LogUtil.i(TAG, "play");
        isStop = false;
        synchronized (athread) {
            athread.notify();
        }
        mTrack.play();
    }

    /**
     * audio track stop
     */
    public void stop() {
        //Todo stop audio track
        if (null == mTrack) {
            return;
        }

        LogUtil.i(TAG, "stop");
        isStop = true;
        mQueue.clear();
        mTrack.flush();
        mTrack.stop();
    }

    /**
     * audio track release
     */
    public void release() {
        //Todo release audio track
        if (null == mTrack) {
            return;
        }
        LogUtil.i(TAG, "release");
        isStop = true;
        mQueue.clear();
        mTrack.release();
        mTrack = null;
    }

    /**
     * clear audio buffer queue
     */
    public void clear() {
        if (null == mTrack) {
            return;
        }
        mQueue.clear();
        mTrack.flush();
    }

    /**
     * put audio buffer into queue
     * @param buffer audio buffer
     */
    public void put(AudioBuffer buffer) {
        mQueue.add(buffer);
    }

    private class AudioPlayerThread extends Thread {
        @Override
        public void run() {
            super.run();
            LogUtil.i(TAG, "athread run");
            while (!Thread.interrupted()) {
                try {
                    while (isStop) {
                        synchronized (this) {
                            wait();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mQueue.isEmpty()) {
                    continue;
                }

                if (null != mTrack) {
                    AudioBuffer audioBuffer = mQueue.poll();
                    if (null == audioBuffer) {
                        continue;
                    }

//                    LogUtil.i(TAG, "audio track write: " + audioBuffer.getSampleSize());
                    mTrack.write(audioBuffer.getSampleData().array(), 0, audioBuffer.getSampleSize());
                }
            }
        }
    }
}
