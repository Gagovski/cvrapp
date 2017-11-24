package com.gc.cvrapp.media.video;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;

import com.gc.cvrapp.utils.H264DecodeUtil;
import com.gc.cvrapp.utils.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * class for video player
 */
public class VideoPlayer {
    private MediaCodec mDecoder;
    private ByteBuffer[] mInputBuffers;
    private MediaCodec.BufferInfo mBufferInfo;
    private boolean isStop = true;
    private InputThread inthread;
    private OutputThread outthread;

    protected final LinkedBlockingQueue<VideoBuffer> mQueue = new LinkedBlockingQueue<VideoBuffer>();

    private static final String TAG = "VideoPlayer";

    private long startTime = 0;

    public VideoPlayer() {
        mDecoder = createDecoder();
    }

    /**
     * video decoder start
     * @param surface managing the screen
     * @param videoInfo video info
     */
    public void start(@NonNull Surface surface, VideoInfo videoInfo) {
        LogUtil.i(TAG, "start");

        if (null == mDecoder) {
            mDecoder = createDecoder();
        }
        configDecoder(mDecoder, videoInfo, surface);
        try {
            mDecoder.start();
            mInputBuffers = mDecoder.getInputBuffers();
            mBufferInfo = new MediaCodec.BufferInfo();
            inthread = new InputThread();
            outthread = new OutputThread();
            isStop = false;
            inthread.start();
            outthread.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * video decoder stop
     */
    public void stop() {
        if (null == mDecoder)
            return;

        isStop = true;
        try {
            mDecoder.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * video decoder release
     */
    public void release() {
        LogUtil.w(TAG, "media codec release");
        if (null == mDecoder)
            return;

        isStop = true;
        try {
            mQueue.clear();
            mDecoder.release();
            mDecoder = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * clear video buffer queue
     */
    public void clear() {
        mQueue.clear();
    }

    /**
     * put video buffer into the queue
     * @param buffer video buffer
     */
    public void put(VideoBuffer buffer) {
        mQueue.add(buffer);
    }

    private MediaCodec createDecoder() {
        MediaCodec codec = null;
        try {
            String mime = "video/avc";
            codec = MediaCodec.createDecoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return codec;
    }

    private void configDecoder(MediaCodec codec, VideoInfo videoInfo, Surface surface) {

        if (!surface.isValid()) {
            return;
        }

        String mime = "Video/AVC";
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString(MediaFormat.KEY_MIME, mime);
        mediaFormat.setInteger(MediaFormat.KEY_WIDTH, 1920);
        mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, 1080);
        if (null != videoInfo) {
            LogUtil.i(TAG, "config decoder video sps pps");

            byte[] sps = H264DecodeUtil.getNalu(videoInfo.getVideoSpsPps(), H264DecodeUtil.TYPE_SPS);
            if (null == sps) {
                LogUtil.e(TAG, "sps null");
                return;
            }

            byte[] pps = H264DecodeUtil.getNalu(videoInfo.getVideoSpsPps(), H264DecodeUtil.TYPE_PPS);
            if (null == pps) {
                LogUtil.e(TAG, "pps null");
                return;
            }

            mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(sps));
            mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
        }

        try {
            codec.configure(mediaFormat, surface, null, 0);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void decode(VideoBuffer videoBuffer) {
        try {
            int inIndex = mDecoder.dequeueInputBuffer(10000);
            if (inIndex < 0) {
                Log.w(TAG, "decode index err");
                return;
            }
            ByteBuffer inBuffer = mInputBuffers[inIndex];
            inBuffer.clear();
            inBuffer.put(videoBuffer.getSampleData().array(), 0, videoBuffer.getSampleSize());
            mDecoder.queueInputBuffer(inIndex, 0, videoBuffer.getSampleSize(), System.currentTimeMillis() - startTime, 0);
        } catch (IllegalStateException e) {
            LogUtil.i(TAG, "decode except");
            e.printStackTrace();
        }
    }

    private void decodeEnd(VideoBuffer videoBuffer) {
        LogUtil.i(TAG, "decode end");
        try {
            int inIndex = mDecoder.dequeueInputBuffer(10000);
            if (0 > inIndex) {
                return;
            }
            if (0 < videoBuffer.getSampleSize()) {
                ByteBuffer inBuffer = mInputBuffers[inIndex];
                inBuffer.clear();
                inBuffer.put(videoBuffer.getSampleData().array(), 0, videoBuffer.getSampleSize());
                mDecoder.queueInputBuffer(inIndex, 0, videoBuffer.getSampleSize(),
                        System.currentTimeMillis() - startTime,
                        (MediaCodec.BUFFER_FLAG_END_OF_STREAM));
            } else {
                mDecoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            }
        } catch (IllegalStateException e) {
            LogUtil.i(TAG, "decode except");
            e.printStackTrace();
        }
    }

    private class InputThread extends Thread {

        @Override
        public void run() {
            super.run();
            startTime = System.currentTimeMillis();
            while(!Thread.interrupted()) {
                if (isStop) {
                    LogUtil.w(TAG, "vthread stop");
                    break;
                }

                if (mQueue.isEmpty()) {
                    continue;
                }

                VideoBuffer videoBuffer = mQueue.poll();
                if (null == videoBuffer) {
                    continue;
                }

                if (!videoBuffer.getMark()) {
                    decode(videoBuffer);
                } else {
                    decodeEnd(videoBuffer);
                }
            }
        }
    }

    private class OutputThread extends Thread {
        @Override
        public void run()  {
            super.run();
            LogUtil.i(TAG, "outhread enter");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int outIndex = mDecoder.dequeueOutputBuffer(mBufferInfo, -1);
                    if (0 <= outIndex) {
                        mDecoder.releaseOutputBuffer(outIndex, (mBufferInfo.size > 0));
                        if (0 != (mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM)) {
                            mDecoder.flush();
                        }
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            LogUtil.i(TAG, "outthread exit");
        }
    }
}
