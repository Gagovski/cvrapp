package com.gc.cvrapp.cvr.commands;

import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.MediaListener;
import com.gc.cvrapp.media.audio.AudioInfo;
import com.gc.cvrapp.media.video.VideoInfo;
import com.gc.cvrapp.utils.LogUtil;

import java.nio.ByteBuffer;

public class PlaybackVideoCommand extends Command {

    private String mFilename;
    private ByteBuffer mPayload = ByteBuffer.allocate(64);
    private MediaListener mListener;
    private static final String TAG = "PlaybackVideoCommand";

    public PlaybackVideoCommand(Cvr cvr, MediaListener listener, String filename) {
        super(cvr);
        mFilename = filename;
        mPayload = ByteBuffer.allocate(64);
        mPayload.put(mFilename.getBytes());
        mListener = listener;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        LogUtil.i(TAG, "encodeCommand: " + mFilename);
        encodeCommand(b, CvrConstants.CommandCode.PlaybackVideo, 0, 0, mFilename.length() + 1);
    }

    @Override
    public void exec(Cvr.CommandIO io) {
        io.handleCommand(this, mPayload.array(), mFilename.length() + 1);
    }

    @Override
    public void resp(Cvr.ResponseIO io, ByteBuffer resp, int payloadlen) {
//        LogUtil.i(TAG, "payload len:" + String.valueOf(payloadlen));
        io.handleResponse(this, resp, payloadlen);
    }


    @Override
    public short code() {
        return CvrConstants.ResponseCode.PlaybackVideo;
    }

    @Override
    protected void errorResp(int ret) {
        mListener.onPlaybackInfo(null, null);
    }

    @Override
    protected void decodeData(short code, int arg1) {
        super.decodeData(code, arg1);
        if (CvrConstants.SampleCode.SampleEnd == arg1) {
            mListener.onSampleEnd();
        }
    }

    @Override
    protected void decodeData(short code, int arg1, int payloadLen, ByteBuffer payLoad) {
        super.decodeData(code, arg1, payLoad);
        if (code != CvrConstants.ResponseCode.PlaybackVideo) {
            return;
        }

        switch (arg1) {
            case CvrConstants.SampleCode.SampleInfo:
                decodeInfo(payLoad, payloadLen);
                break;

            case CvrConstants.SampleCode.SampleVideo:
                decodeVideoSample(payLoad, payloadLen);
                break;

            case CvrConstants.SampleCode.SampleAudio:
                decodeAudioSample(payLoad, payloadLen);
                break;

            default:

                break;
        }
    }

    @Override
    public boolean complete() {
        return false;
    }

    private void decodeInfo(ByteBuffer payLoad, int payloadLen) {
        LogUtil.i(TAG, "payLoadLen: " + payloadLen);
        /* decode audio info */
        AudioInfo audioInfo = new AudioInfo();
        audioInfo.setAudioSamples(payLoad.getInt());
        audioInfo.setAudioChannels(payLoad.getInt());
        audioInfo.setAudioSampleRate(payLoad.getInt());
        audioInfo.setAudioBps(payLoad.getInt());
        audioInfo.printInfo();

        /* decode video info */
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setVideoSamples(payLoad.getInt());
        videoInfo.setVideoSpsPpsLen(payLoad.getInt());
        byte[] spspps = new byte[videoInfo.getVideoSpsPpsLen()];
        payLoad.get(spspps, 0, videoInfo.getVideoSpsPpsLen());
        videoInfo.setVideoSpsPps(spspps);
        videoInfo.printInfo();

        mListener.onPlaybackInfo(videoInfo, audioInfo);
    }

    private void decodeVideoSample(ByteBuffer data, int datalen) {
        mListener.onVideoSampleData(data, 0, datalen);
    }

    private void decodeAudioSample(ByteBuffer data, int datalen) {
        mListener.onAudioSampleData(data, datalen);
    }
}
