package com.gc.cvrapp.cvr.commands;


import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.CvrConstants;

import java.nio.ByteBuffer;

public class FormatSDCommand extends Command {

    public FormatSDCommand(Cvr cvr) {
        super(cvr);
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

}
