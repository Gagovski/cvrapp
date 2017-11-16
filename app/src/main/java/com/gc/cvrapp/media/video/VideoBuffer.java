package com.gc.cvrapp.media.video;


import java.nio.ByteBuffer;

/**
 * class for video buffer
 */
public class VideoBuffer {
    private ByteBuffer sampleData;
    private int sampleSize;
    private boolean mark;

    public VideoBuffer() {
        this.sampleData = null;
        this.sampleSize = 0;
        mark = true;
    }

    public VideoBuffer(ByteBuffer seqhdr, int seqhdrlen, ByteBuffer sample, int sampleSize) {
        this.sampleData = sample;
        this.sampleSize = sampleSize;
        mark = false;
    }

    public VideoBuffer(ByteBuffer bb, int size) {
        sampleData = bb;
        sampleSize = size;
        mark = false;
    }

    public VideoBuffer(ByteBuffer bb, int size, boolean mark) {
        sampleData = bb;
        sampleSize = size;
        this.mark = mark;
    }

    /**
     * get video buffer mark
     * @return video buffer mark
     */
    public boolean getMark() {
        return mark;
    }

    /**
     * get video sample data
     * @return video buffer sample buffer
     */
    public ByteBuffer getSampleData() {
        return sampleData;
    }

    /**
     * get video sample data size
     * @return video buffer sample buffer size
     */
    public int getSampleSize() {
        return sampleSize;
    }
}
