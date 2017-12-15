package com.gc.cvrapp.cvr.netcvr;

import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NetDeviceConnection {
    private static final String TAG = "NetDeviceConnection";

    private static void checkBounds(byte[] buffer, int start, int length) {
        final int bufferLength = (buffer != null ? buffer.length : 0);
        if (length < 0 || start < 0 || start + length > bufferLength) {
            throw new IllegalArgumentException("Buffer start or length out of bounds.");
        }
    }

    public int sockTransfer(NetSockEndpoint endpoint, byte[] buffer, int offset, int length, int timeout) throws IOException {
        int len;

        checkBounds(buffer, offset, length);
        if (CvrConstants.NetConstants.SockDirIn == endpoint.getDirection()) {
            InputStream inputStream = endpoint.getSocket().getInputStream();
            len = inputStream.read(buffer, offset, length);
        } else {
            OutputStream outputStream = endpoint.getSocket().getOutputStream();
            outputStream.write(buffer, offset, length);
            len = length;
        }

        return len;
    }

    public void close(NetSockEndpoint endpoint) throws IOException {
        if (CvrConstants.NetConstants.SockDirIn == endpoint.getDirection()) {
            endpoint.getSocket().getInputStream().close();
            endpoint.getSocket().close();

        } else {
            endpoint.getSocket().getOutputStream().close();
            endpoint.getSocket().close();
        }

    }

    public int getMaxPacketSize() {
        return CvrConstants.NetConstants.SockPacketSize;
    }
}
