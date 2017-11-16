package com.gc.cvrapp.cvr.commands;

import com.gc.cvrapp.cvr.Cvr;

import java.nio.ByteBuffer;

import com.gc.cvrapp.cvr.Cvr.FileListener;
import com.gc.cvrapp.cvr.CvrConstants;

public class DeleteFileCommand extends Command {
    private String mItem;
    private FileListener mListener;
    private ByteBuffer mPayload;

    public DeleteFileCommand(Cvr cvr, FileListener listener, String item) {
        super(cvr);
        mListener = listener;
        mItem = item;
        mPayload = ByteBuffer.allocate(64);
        mPayload.put(mItem.getBytes());
    }

    @Override
    public void exec(Cvr.CommandIO io) {
        io.handleCommand(this, mPayload.array(), mItem.length() + 1);
    }

    @Override
    public void resp(Cvr.ResponseIO io, ByteBuffer resp, int payloadlen) {
        io.handleResponse(this, resp, payloadlen);
    }

    @Override
    public short code() {
        return CvrConstants.ResponseCode.DeleteFile;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, CvrConstants.CommandCode.DeleteFile, 0, 0, mItem.length() + 1);
    }

    @Override
    protected void decodeData(short code, int arg1) {
        super.decodeData(code, arg1);
        mListener.onDeleteFile(mItem);
    }
}
