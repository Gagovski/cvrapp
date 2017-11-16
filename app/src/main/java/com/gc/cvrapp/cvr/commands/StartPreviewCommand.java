package com.gc.cvrapp.cvr.commands;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.MediaListener;
import com.gc.cvrapp.cvr.CvrConstants;

import java.nio.ByteBuffer;


public class StartPreviewCommand extends Command {
    private MediaListener mListener;

    public StartPreviewCommand(Cvr cvr, MediaListener listener) {
        super(cvr);
        mListener = listener;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, CvrConstants.CommandCode.StartPreview);
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
        return CvrConstants.ResponseCode.StartPreview;
    }

    @Override
    protected void decodeData(short code, int arg1, int payloadLen, ByteBuffer payLoad) {
        if (code == CvrConstants.ResponseCode.StartPreview) {
            mListener.onPreview(payLoad, payloadLen);
        }
    }

    @Override
    public boolean complete() {
        return false;
    }
}
