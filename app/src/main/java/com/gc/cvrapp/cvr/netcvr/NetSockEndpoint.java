package com.gc.cvrapp.cvr.netcvr;

import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Base class for socket endpoint
 */
public class NetSockEndpoint {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;

    public NetSockEndpoint(DatagramSocket sock, InetAddress address, int port) {
        this.socket = sock;
        this.address = address;
        this.port = port;
    }

    /** get endpoints remote address
     * @return inet address
     */
    public InetAddress getAddress() {
        return address;
    }

    /** get endpoints remote port
     * @return port
     */
    public int getPort() {
        return port;
    }

    /** get udp socket
     * @return udp socket
     */
    public DatagramSocket getSocket() {
        return socket;
    }
}
