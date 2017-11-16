package com.gc.cvrapp.media.audio;


import java.nio.ByteBuffer;

/**
 * class for audio buffer
 */
public class AudioBuffer {
    private ByteBuffer sampleData;
    private int sampleSize;

    public AudioBuffer(ByteBuffer data, int size) {
        sampleData = data;
        sampleSize = size;
    }

    /**
     * get audio sample data
     * @return sample data buffer
     */
    public ByteBuffer getSampleData() {
        return sampleData;
    }

    /**
     * get audio sample data size
     * @return sample data buffer size
     */
    public int getSampleSize() {
        return sampleSize;
    }
}
