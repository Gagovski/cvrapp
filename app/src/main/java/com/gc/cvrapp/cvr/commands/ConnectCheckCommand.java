package com.gc.cvrapp.cvr.commands;

import android.util.Log;

import java.nio.ByteBuffer;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.ConnectCheckListener;
import com.gc.cvrapp.cvr.CvrConstants;

public class ConnectCheckCommand extends Command {

    private ConnectCheckListener mListener;

    private static final String TAG = "ConnectCheckCommand";

    public ConnectCheckCommand(Cvr cvr, ConnectCheckListener listener) {
        super(cvr);
        mListener = listener;
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
        return CvrConstants.CommandCode.ConnectCheck;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, CvrConstants.CommandCode.ConnectCheck);
    }

    @Override
    protected void decodeData(short code, int arg1) {
        if (code == CvrConstants.ResponseCode.ConnectCheck) {
            Log.i(TAG, "decodeData");
            mListener.onConnectCheck();
        }
    }
}
