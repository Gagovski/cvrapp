package com.gc.cvrapp.cvr.netcvr;

import com.gc.cvrapp.cvr.CvrConnection;

import java.io.IOException;
import java.util.List;

public class NetCvrConnection extends CvrConnection<NetSockEndpoint> {
    private NetDeviceConnection connection;
    private List<NetSockEndpoint> bulkin;
    private NetSockEndpoint bulkout;
    private static final String TAG = "SockConnection";

    public NetCvrConnection(NetDeviceConnection connection, List<NetSockEndpoint> bulkin, NetSockEndpoint bulkout) {
        this.connection = connection;
        this.bulkin = bulkin;
        this.bulkout = bulkout;
    }

    @Override
    public void close() {
        if ((null == bulkin) || (null == bulkout)) {
            return;
        }

        for (NetSockEndpoint endpoint : bulkin) {
            try {
                connection.close(endpoint);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            connection.close(bulkout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int transferOut(Object endpoint, byte[] buffer, int length, int timeout) {
        int len = 0;

        try {
            len = connection.sockTransfer((NetSockEndpoint) endpoint, buffer, 0, length, timeout);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return len;
    }

    @Override
    public int transferIn(Object endpoint, byte[] buffer, int maxLength, int timeout) {
        int readCount = 0;
        while (readCount < maxLength) {
            try {
                readCount += connection.sockTransfer((NetSockEndpoint) endpoint, buffer, readCount, maxLength - readCount, timeout);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return readCount;
    }

    @Override
    public int transferIn(Object endpoint, byte[] buffer, int maxLength) {
        int readCount = 0;
        try {
            readCount =  connection.sockTransfer((NetSockEndpoint) endpoint, buffer, 0, maxLength, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readCount;
    }

    @Override
    public int getMaxPacketOutSize() {
        return connection.getMaxPacketSize();
    }

    @Override
    public int getMaxPackeInSize() {
        return connection.getMaxPacketSize();
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