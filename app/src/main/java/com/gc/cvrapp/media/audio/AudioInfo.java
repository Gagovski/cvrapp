package com.gc.cvrapp.media.audio;

import com.gc.cvrapp.utils.LogUtil;
import com.gc.cvrapp.utils.PacketUtil;

/**
 * class for audio info
 */
public class AudioInfo {
    private int audioSamples;
    private int audioChannels;
    private int audioSampleRate;
    private int audioBps;
    private static final String TAG = "AudioInfo";

    /**
     * get audio total samples
     * @return audio total samples
     */
    public int getAudioSamples() {
        return audioSamples;
    }

    /**
     * set audio total samples
     * @param audioSamples  audio total samples
     */
    public void setAudioSamples(int audioSamples) {
        this.audioSamples = audioSamples;
    }

    public int getAudioChannels() {
        return audioChannels;
    }

    /**
     * set audio channels
     * @param audioChannels  audio channels
     */
    public void setAudioChannels(int audioChannels) {
        this.audioChannels = audioChannels;
    }

    /**
     * get audio sample rate
     * @return audio sample rate
     */
    public int getAudioSampleRate() {
        return audioSampleRate;
    }

    /**
     * set audio sample rate
     * @param audioSampleRate  audio sample rate
     */
    public void setAudioSampleRate(int audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }

    /**
     * get audio bps
     * @return  audioBps  audio bps
     */
    public int getAudioBps() {
        return audioBps;
    }

    /**
     * set audio bps
     * @param audioBps  audio bps
     */
    public void setAudioBps(int audioBps) {
        this.audioBps = audioBps;
    }

    /**
     * print audio info
     */
    public void printInfo() {
        LogUtil.i(TAG, String.format("video: samples=%d channels=%d samplerate=%d bps=%d",
                audioSamples, audioChannels, audioSampleRate, audioBps));
    }
}
