package com.gc.cvrapp.cvr.netcvr;

import com.gc.cvrapp.cvr.CvrConstants;

import java.net.Socket;

public class NetSockEndpoint {
    private Socket mSocket;
    private int mDirection = CvrConstants.NetConstants.SockDirOut;

    public NetSockEndpoint(Socket sock, int direction) {
        mSocket = sock;
        mDirection = direction;
    }

    public void setDirection(int direction) {
        mDirection = direction;
    }

    public int getDirection() {
        return mDirection;
    }

    public void setSocket(Socket sock) {
        mSocket = sock;
    }

    public Socket getSocket() {
        return mSocket;
    }
}
