package com.gc.cvrapp.cvr.usbcvr;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

import com.gc.cvrapp.cvr.CvrConnection;
import com.gc.cvrapp.utils.LogUtil;

import java.util.List;

public class UsbCvrConnection extends CvrConnection<UsbEndpoint> {
    private final UsbInterface intf;
    private final UsbDeviceConnection connection;
    private List<UsbEndpoint> bulkin;
    private UsbEndpoint bulkOut;
    private static final String TAG = "UsbCvrConnection";

    public UsbCvrConnection(UsbDeviceConnection connection, UsbInterface intf, List<UsbEndpoint> bulkin, UsbEndpoint bulkOut) {
        super();
        this.connection = connection;
        this.intf = intf;
        this.bulkin = bulkin;
        this.bulkOut = bulkOut;
        this.connection.claimInterface(intf, true);
    }

    @Override
    public void close() {
        LogUtil.i(TAG, "close");
        connection.releaseInterface(intf);
        connection.close();
    }

    @Override
    public int transferOut(Object endpoint, byte[] buffer, int length, int timeout) {
        return connection.bulkTransfer((UsbEndpoint) endpoint, buffer, length, timeout);
    }

    @Override
    public int transferIn(Object endpoint, byte[] buffer, int maxLength, int timeout) {
        int readCount = 0;
        while (readCount < maxLength) {
            try {
                readCount += connection.bulkTransfer((UsbEndpoint) endpoint, buffer, readCount, maxLength - readCount, timeout);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return readCount;
            }
        }
        return readCount;
    }

    @Override
    public int transferIn(Object endpoint, byte[] buffer, int maxLength) {
        return connection.bulkTransfer((UsbEndpoint) endpoint, buffer, 0, maxLength, 0);
    }

    @Override
    public int getMaxPacketOutSize() {
        return bulkOut.getMaxPacketSize();
    }

    @Override
    public int getMaxPacketInSize() {
        return bulkin.get(0).getMaxPacketSize();
    }

    @Override
    public Object getOut() {
        return bulkOut;
    }

    @Override
    public Object getIn() {
        return null;
    }

    @Override
    public List<UsbEndpoint> getIns() {
        return bulkin;
    }
}
