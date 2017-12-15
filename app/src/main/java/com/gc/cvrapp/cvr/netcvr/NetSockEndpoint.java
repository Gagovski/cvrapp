package com.gc.cvrapp.cvr.netcvr;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetSockEndpoint {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;

    public NetSockEndpoint(DatagramSocket sock, InetAddress address, int port) {
        this.socket = sock;
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public DatagramSocket getSocket() {
        return socket;
    }
}
