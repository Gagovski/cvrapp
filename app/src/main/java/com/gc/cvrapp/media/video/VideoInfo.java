package com.gc.cvrapp.media.video;

import android.util.Log;

import com.gc.cvrapp.utils.LogUtil;
import com.gc.cvrapp.utils.PacketUtil;

import java.nio.ByteBuffer;

/**
 * class for video info
 */
public class VideoInfo {
    private int videoSamples;
    private int videoSpsPpsLen;
    private ByteBuffer videoSpsPps;
    private static final String TAG = "VideoInfo";

    public VideoInfo() {

    }

    /**
     * get video total samples
     * @return total samples
     */
    public int getVideoSamples() {
        return videoSamples;
    }

    /**
     * set video total samples
     * @param videoSamples total samples
     */
    public void setVideoSamples(int videoSamples) {
        this.videoSamples = videoSamples;
    }

    /**
     * get video sps pps length
     * @return  sps and pps length
     */
    public int getVideoSpsPpsLen() {
        return videoSpsPpsLen;
    }

    /**
     * set video sps pps length
     * @param videoSpsPpsLen   sps and pps length
     */
    public void setVideoSpsPpsLen(int videoSpsPpsLen) {
        this.videoSpsPps = ByteBuffer.allocate(videoSpsPpsLen);
        this.videoSpsPpsLen = videoSpsPpsLen;
    }

    /**
     * get video sps pps buffer
     * @return  sps and pps buffer
     */
    public ByteBuffer getVideoSpsPps() {
        return videoSpsPps;
    }

    /**
     * set video sps pps buffer
     * @param videoSpsPps  sps and pps buffer
     */
    public void setVideoSpsPps(byte[] videoSpsPps) {
        this.videoSpsPps.put(videoSpsPps);
    }

    /**
     * print video info
     */
    public void printInfo() {
        LogUtil.i(TAG, String.format("video: samples=%d spsppsLen=%d", videoSamples, videoSpsPpsLen));
        PacketUtil.logHexdump(TAG, videoSpsPps.array(), videoSpsPpsLen);
    }
}
