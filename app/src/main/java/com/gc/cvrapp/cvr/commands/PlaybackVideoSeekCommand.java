package com.gc.cvrapp.cvr.commands;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.MediaListener;
import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.utils.LogUtil;

import java.nio.ByteBuffer;

public class PlaybackVideoSeekCommand extends Command {

    private MediaListener mListener;
    private int mSample;
    private static final String TAG = "PlaybackVideoSeekCommand";

    public PlaybackVideoSeekCommand(Cvr cvr, MediaListener listener, int sample) {
        super(cvr);
        mListener   = listener;
        mSample     = sample;
    }

    @Override
    public void exec(Cvr.CommandIO io) {
        io.handleCommand(this, null, 0);
    }

    @Override
    public void resp(Cvr.ResponseIO io, ByteBuffer resp, int payloadlen) {
        io.handleResponse(this, resp, payloadlen);
    }

    @Override
    public short code() {
        return CvrConstants.ResponseCode.PlaybackVideoSeek;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, CvrConstants.CommandCode.PlaybackVideoSeek, 0, mSample);
    }

    @Override
    protected void decodeData(short code, int arg1) {
        super.decodeData(code, arg1);
        LogUtil.i(TAG, "decodeData");
        mListener.onPlaybackSeek(arg1);
    }
}
