package com.gc.cvrapp.cvr.commands;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.MediaListener;
import com.gc.cvrapp.cvr.CvrConstants;

import java.nio.ByteBuffer;

public class StartRecordCommand extends Command {

    private MediaListener mListener;

    public StartRecordCommand(Cvr camera, MediaListener listener) {
        super(camera);
        mListener = listener;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, CvrConstants.CommandCode.StartRecord);
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
        return CvrConstants.ResponseCode.StartRecord;
    }

    @Override
    protected void decodeData(short code, int arg1) {
        if (code == CvrConstants.ResponseCode.StartRecord) {
            mListener.onPreviewStartRecord();
        }
    }
}
