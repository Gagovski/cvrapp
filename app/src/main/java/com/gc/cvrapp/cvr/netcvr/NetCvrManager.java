package com.gc.cvrapp.cvr.netcvr;

import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.utils.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class NetCvrManager {
    private static List<NetSockEndpoint> endpoints = new ArrayList<>();
    private static final String TAG = "NetCvrManager";

    public void connect() {
        LogUtil.i(TAG, "net sock connect");
        int port = CvrConstants.NetConstants.SockPortMin;
        for (int i = 0; i < CvrConstants.NetConstants.SockCnt; i ++) {
            ConnectThread connThread = new ConnectThread(port + i);
            connThread.start();
        }
    }

    public List<NetSockEndpoint> getEndpoints() {
        return endpoints;
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
            try {
                byte[] msg = new byte[CvrConstants.MsgSize];
                DatagramSocket serverSocket = new DatagramSocket(this.port);
                DatagramPacket receivePacket = new DatagramPacket(msg, CvrConstants.MsgSize);

                try {
                    serverSocket.receive(receivePacket);
                    ByteBuffer bb = ByteBuffer.wrap(receivePacket.getData());
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    short   type  = bb.getShort();
                    short   code  = bb.getShort();
                    if ((type == CvrConstants.MsgType.MsgCommand) && (code == CvrConstants.CommandCode.ConnectCheck)) {
                        LogUtil.i(TAG, "endpoint: port " + serverSocket.getLocalPort() + " addr " +  receivePacket.getAddress());
                        endpoints.add(new NetSockEndpoint(serverSocket, receivePacket.getAddress(), receivePacket.getPort()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }

            if (1 < endpoints.size()) {
                Icallback.isConnect();
            }
        }
    }
}
