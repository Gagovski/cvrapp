package com.gc.cvrapp.cvr.commands;


import android.util.Log;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.CvrConstants;

import java.nio.ByteBuffer;

/** Basic class for cvr commands */
public abstract class Command implements CvrAction {

    private static final String TAG = "Command";

    private boolean hasResponseReceived = false;

    Command(Cvr cvr) {}

    /**
     * encode command buffer
     * @param b command buffer
     */
    public abstract void encodeCommand(ByteBuffer b);

    protected void encodeCommand(ByteBuffer b, short code) {
        b.putShort(CvrConstants.MsgType.MsgCommand);    // msgType
        b.putShort(code);   // msgCode
        b.putInt(0);    // arg0
        b.putInt(0);    // arg1
        b.putInt(0);    // payloadLen
//        PacketUtil.logHexdump(TAG, b.array(), 16);
    }

    protected void encodeCommand(ByteBuffer b, short code, int arg0) {
        b.putShort(CvrConstants.MsgType.MsgCommand);    // msgType
        b.putShort(code);   // msgCode
        b.putInt(arg0);     // arg0
        b.putInt(0);        // arg1
        b.putInt(0);        // payloadLen
//        PacketUtil.logHexdump(TAG, b.array(), 16);
    }

    protected void encodeCommand(ByteBuffer b, short code, int arg0, int arg1) {
        b.putShort(CvrConstants.MsgType.MsgCommand);    // msgType
        b.putShort(code);   // code
        b.putInt(arg0);     // arg0
        b.putInt(arg1);     // arg1
        b.putInt(0);        // payloadLen

//        PacketUtil.logHexdump(TAG, b.array(), 16);
    }

    protected void encodeCommand(ByteBuffer b, short code, int arg0, int arg1, int payloadLen) {
        b.putShort(CvrConstants.MsgType.MsgCommand); // msgType
        b.putShort(code);   // msgCode
        b.putInt(arg0);     // arg0
        b.putInt(arg1);     // arg1
        b.putInt(payloadLen);   // payloadLen
//        PacketUtil.logHexdump(TAG, b.array(), (16 + payLoad.length));
    }

    protected void decodeData(short code, int arg1, ByteBuffer payLoad) {}

    protected void decodeData(short code, int arg1, int payloadLen, ByteBuffer payLoad) { Log.i(TAG, "decodeData"); }

    protected void decodeData(short code, int arg1) {}

    protected void errorResp(int ret) {
        Log.e(TAG, "error resp retcode: " + String.valueOf(ret));
    }

    public boolean complete() {
        return hasResponseReceived;
    }

    /**
     * receive response buffer
     * @param bb resp buffer
     */
    public void receiveRead(ByteBuffer bb) {
        short   type  = bb.getShort();
        short   code  = bb.getShort();
        int     ret   = bb.getInt();
        int     arg1  = bb.getInt();

        if (CvrConstants.MsgType.MsgResponse != type) {
            return ;
        }

        if (CvrConstants.ResponseRetCode.RespOk == ret) {
            hasResponseReceived = true;
            decodeData(code, arg1);
        } else {
            Log.e(TAG, "error resp retcode: " + String.valueOf(ret));
            errorResp(ret);
        }
    }

    /**
     * receive response buffer
     * @param bb resp buffer
     * @param payLoad response payload buffer
     * @param payloadLen response payload data len
     */
    public void receiveRead(ByteBuffer bb, ByteBuffer payLoad, int payloadLen) {
        short   type  = bb.getShort();
        short   code  = bb.getShort();
        int     ret   = bb.getInt();
        int     arg1  = bb.getInt();


        if (CvrConstants.MsgType.MsgResponse != type) {
            return ;
        }

        if (CvrConstants.ResponseRetCode.RespOk == ret) {
            hasResponseReceived = true;
            decodeData(code, arg1, payloadLen, payLoad);
        } else {
            Log.e(TAG, "error resp retcode: " + String.valueOf(ret));
            errorResp(ret);
        }
    }
}
