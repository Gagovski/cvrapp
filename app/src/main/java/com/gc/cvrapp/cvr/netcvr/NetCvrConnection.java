package com.gc.cvrapp.cvr.netcvr;

import com.gc.cvrapp.cvr.CvrConnection;
import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.utils.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.List;

public class NetCvrConnection extends CvrConnection<NetSockEndpoint> {
    private List<NetSockEndpoint> bulkin;
    private NetSockEndpoint bulkout;
    private static final String TAG = "SockConnection";

    public NetCvrConnection(List<NetSockEndpoint> bulkin, NetSockEndpoint bulkout) {
        this.bulkin = bulkin;
        this.bulkout = bulkout;
    }

    @Override
    public void close() {
        if ((null == bulkin) || (null == bulkout)) {
            return;
        }
    }

    @Override
    public int transferOut(Object endpoint, byte[] buffer, int length, int timeout) {
        int len = length;

        NetSockEndpoint sockEndpoint = (NetSockEndpoint) endpoint;
//        LogUtil.i(TAG, "transferOut length: " + String.valueOf(length) + sockEndpoint.getAddress());
        try {
            sockEndpoint.getSocket().send(new DatagramPacket(buffer, length, sockEndpoint.getAddress(), sockEndpoint.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return len;
    }

    @Override
    public int transferIn(Object endpoint, byte[] buffer, int maxLength, int timeout) {
        int readCount = maxLength;

        NetSockEndpoint sockEndpoint = (NetSockEndpoint) endpoint;
//        LogUtil.i(TAG, "transferIn length: " + String.valueOf(maxLength) + sockEndpoint.getAddress());
        try {
            sockEndpoint.getSocket().receive(new DatagramPacket(buffer, maxLength, sockEndpoint.getAddress(), sockEndpoint.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readCount;
    }

    @Override
    public int transferIn(Object endpoint, byte[] buffer, int maxLength) {
        int readCount = maxLength;

        NetSockEndpoint sockEndpoint = (NetSockEndpoint) endpoint;
//        LogUtil.i(TAG, "transferIn length: " + String.valueOf(maxLength) + sockEndpoint.getAddress());
        try {
            sockEndpoint.getSocket().receive(new DatagramPacket(buffer, maxLength, sockEndpoint.getAddress(), sockEndpoint.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readCount;
    }

    @Override
    public int getMaxPacketOutSize() {
        return CvrConstants.NetConstants.SockPacketSize;
    }

    @Override
    public int getMaxPacketInSize() {
        return CvrConstants.NetConstants.SockPacketSize;
    }

    @Override
    public Object getOut() {
        return bulkout;
    }

    @Override
    public Object getIn() {
        return null;
    }

    @Override
    public List<NetSockEndpoint> getIns() {
        return bulkin;
    }
}