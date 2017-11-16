package com.gc.cvrapp.cvr.commands;


import android.util.Log;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.cvr.Cvr.FileListListener;
import com.gc.cvrapp.utils.LogUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GetFileListCommand extends Command {

    private FileListListener mListener;

    private static final String TAG = "GetFileListCommand";

    public GetFileListCommand(Cvr camera, FileListListener listener) {
        super(camera);
        mListener = listener;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, CvrConstants.CommandCode.GetFileList);
    }

    @Override
    public void exec(Cvr.CommandIO io) {
        io.handleCommand(this, null, 0);
    }

    @Override
    protected void errorResp(int ret) {
        super.errorResp(ret);
        mListener.onFileList(null, null);
    }

    @Override
    public void resp(Cvr.ResponseIO io, ByteBuffer resp, int payloadlen) {
//        if (payloadlen > 0) {
//            io.requestData(this, payloadlen);
//        }
        LogUtil.i(TAG, "GetFileList resp");
        io.handleResponse(this, resp, payloadlen);
    }

    @Override
    public short code() {
        return CvrConstants.ResponseCode.GetFileList;
    }


    @Override
    protected void decodeData(short code, int arg1) {
        super.decodeData(code, arg1);
        Log.i(TAG, "GetFileListCommand decodeData");
        mListener.onFileList(null, null);
    }

    @Override
    protected void decodeData(short code, int arg1, int payloadLen, ByteBuffer payLoad) {
        LogUtil.i(TAG, "GetFileListCommand decodeData");
        if (code == CvrConstants.ResponseCode.GetFileList) {
            List<String> mp4list   = new ArrayList<>();
            List<String> piclist   = new ArrayList<>();

            String filelist = new String(payLoad.array(), 0, payloadLen);
            LogUtil.i(TAG, "filelist: " + filelist);
            String[] filename = filelist.split(";");
            for (int i = 0; i < filename.length; i++) {
                if (filename[i].contains("jpg")) {
                    piclist.add(filename[i]);
                }

                if (filename[i].contains("mp4")) {
                    mp4list.add(filename[i]);
                }
            }

            mListener.onFileList(mp4list, piclist);
        }
    }
}
