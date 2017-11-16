package com.gc.cvrapp.cvr.netcvr;

import com.gc.cvrapp.cvr.CvrConstants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class NetCvrManager {

    private static List<Socket> mSocks = new ArrayList<>();
    private static ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private static final String TAG = "NetCvrManager";

    public void connect() {
        mExecutor.execute(new ConnRunnable());
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

    private static class ConnRunnable implements Runnable {
        @Override
        public void run() {
            mSocks.clear();
            for (int port = CvrConstants.NetConstants.SockPortMin; port <= CvrConstants.NetConstants.SockPortMax; port ++) {
                try {
                    ServerSocket serverSocket = new ServerSocket(port);
                    Socket socket = serverSocket.accept();
                    mSocks.add(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    Icallback.isError();
                    return;
                }
            }

            Icallback.isConnect();
        }
    }
}
