package com.gc.cvrapp.cvr.netcvr;

import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.utils.LogUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetCvrManager {

    private static List<Socket> mSocks = new ArrayList<>();
    private static final String TAG = "NetCvrManager";

    public void connect() {
        LogUtil.i(TAG, "net sock connect");
        int port = CvrConstants.NetConstants.SockPortMin;
        for (int i = 0; i < CvrConstants.NetConstants.SockCnt; i ++) {
            ConnectThread connThread = new ConnectThread(port + i);
            connThread.start();
        }
    }

    public NetDeviceConnection openDevice() {
        return new NetDeviceConnection();
    }

    public List<Socket> getSocks() {
        return mSocks;
    }

    public void setCallback(NetCvrManagerCallback cb) {
        Icallback = cb;
    }

    public interface NetCvrManagerCallback {
        void isConnect();
        void isError();
    }

    private static NetCvrManagerCallback Icallback;

    private class ConnectThread extends Thread {
        private int port;

        ConnectThread(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            super.run();

            for (;;) {
                try {
                    ServerSocket serverSocket = new ServerSocket(this.port);
                    Socket socket = serverSocket.accept();
                    mSocks.add(socket);
                    LogUtil.i(TAG, "net sock connected port " + String.valueOf(this.port));
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    Icallback.isError();
                    return;
                }
            }

            if (1 < mSocks.size()) {
                Icallback.isConnect();
            }
        }
    }

}
