package com.gc.cvrapp.cvr.commands;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.cvr.Cvr.MediaListener;

import java.nio.ByteBuffer;


public class StopRecordCommand extends Command {
    private  MediaListener mListener;

    public StopRecordCommand(Cvr camera, MediaListener listener) {
        super(camera);
        mListener = listener;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, CvrConstants.CommandCode.StopRecord);
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
        return CvrConstants.ResponseCode.StopRecord;
    }

    @Override
    protected void decodeData(short code, int arg1) {
        super.decodeData(code, arg1);
        if (code == CvrConstants.ResponseCode.StopRecord) {
            mListener.onPreviewStopRecord();
        }
    }
}
