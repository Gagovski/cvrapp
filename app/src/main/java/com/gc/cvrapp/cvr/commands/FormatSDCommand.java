package com.gc.cvrapp.cvr.commands;


import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.cvr.Cvr.FormattingListener;

import java.nio.ByteBuffer;

public class FormatSDCommand extends Command {
    private FormattingListener mListener;

    public FormatSDCommand(Cvr cvr, FormattingListener listener) {
        super(cvr);
        mListener = listener;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, CvrConstants.CommandCode.FormatSD);
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
        return CvrConstants.ResponseCode.FormatSD;
    }

    @Override
    protected void decodeData(short code, int arg1) {
        super.decodeData(code, arg1);
        mListener.onFormatDone();
    }
}
