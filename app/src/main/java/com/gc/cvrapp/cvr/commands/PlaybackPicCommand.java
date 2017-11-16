package com.gc.cvrapp.cvr.commands;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.MediaListener;
import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.utils.LogUtil;

import java.nio.ByteBuffer;

public class PlaybackPicCommand extends Command {

    private String mPicname;
    private ByteBuffer mPayload;
    private MediaListener mListener;
    private static final String TAG = "PlaybackPicCommand";

    public PlaybackPicCommand(Cvr cvr, MediaListener listener, String picname) {
        super(cvr);
        mPicname = picname;
        mPayload = ByteBuffer.allocate(64);
        mPayload.put(picname.getBytes());

        mListener = listener;
        LogUtil.i(TAG, "picname: " + picname);
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, CvrConstants.CommandCode.PlaybackPic, 0, 0, mPicname.length() + 1);
    }

    @Override
    public void exec(Cvr.CommandIO io) {
        io.handleCommand(this, mPayload.array(), mPicname.length() + 1);
    }

    @Override
    public void resp(Cvr.ResponseIO io, ByteBuffer resp, int payloadlen) {
        io.handleResponse(this, resp, payloadlen);
    }

    @Override
    public short code() {
        return CvrConstants.ResponseCode.PlaybackPic;
    }

    @Override
    protected void decodeData(short code, int arg1, int payloadLen, ByteBuffer payLoad) {
        if (code == CvrConstants.ResponseCode.PlaybackPic) {
            LogUtil.i(TAG, "playbackPic decodeData");
            Bitmap bitmap = BitmapFactory.decodeByteArray(payLoad.array(), 0, payloadLen);
            mListener.onPicture(mPicname, bitmap);
        }
    }
}
