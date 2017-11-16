package com.gc.cvrapp.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.CvrListener;
import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.cvr.usbcvr.UsbCvr;
import com.gc.cvrapp.cvr.usbcvr.UsbCvrConnection;
import com.gc.cvrapp.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsbCvrService extends CvrService {
    private UsbManager mUsbManager;
    private UsbCvr mUsbCvr;
    private boolean isConnected = false;
    private static final String ACTION_USB_PERMISSION = "com.gc.USB_PERMISSION";
    private static final String TAG = "UsbCvrService";

    public UsbCvrService(){}

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate");

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        /* register usb receiver */
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
        unregisterReceiver(mUsbReceiver);
        if (null != mUsbCvr) {
            mUsbCvr.shutdownHard();
        }
        mUsbCvr = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.i(TAG, "onBind");
        return mBinderService;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    private final BinderService mBinderService = new BinderService();

    public class BinderService extends Binder {
        public CvrService getService() { return UsbCvrService.this; }
    }

    private CvrListener mListener;

    @Override
    public Cvr getCvr() {
        return mUsbCvr;
    }

    @Override
    public void setCvrListener(CvrListener listener) {
            mListener = listener;
    }

    @Override
    public void initialize() {
        if (null != mUsbCvr) {
            if (mListener != null) {
                mListener.onCvrAttached(mUsbCvr);
            }
            return ;
        }

        LogUtil.i(TAG, "initialize");
        /* setup usb device */
        setupDevice();
    }

    @Override
    public void reinitialize() {
        LogUtil.i(TAG, "reiniailize" + String.valueOf(isConnected));
/*
        if (isConnected) {
            isConnected = false;
            if (null != mUsbCvr) {
                mUsbCvr.shutdownHard();
            }
            mUsbCvr = null;
            initialize();
        }
*/
    }

    @Nullable
    private UsbDevice discoverDevice() {
        LogUtil.i(TAG, "discoverDevice");

        Map<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if (deviceList.isEmpty()) {
            LogUtil.e(TAG, "deviceList empty");
            return null;
        }
        for (Map.Entry<String, UsbDevice> e : deviceList.entrySet()) {
            UsbDevice d = e.getValue();
            LogUtil.i(TAG, String.valueOf(d));
            if ((d.getProductId() == CvrConstants.UsbConstants.ProductId) && (d.getVendorId() == CvrConstants.UsbConstants.VendorId)) {
                return d;
            }
        }
        return null;
    }

    @org.jetbrains.annotations.Contract("null -> false")
    private boolean connectDevice(UsbDevice device) {

        if (null == device) {
            return false;
        }

        LogUtil.i(TAG, "connectDevice");
        for (int i = 0, n = device.getInterfaceCount(); i < n; ++i) {
            UsbInterface intf = device.getInterface(i);

            if ((CvrConstants.UsbConstants.Class != intf.getInterfaceClass()) ||
                    (CvrConstants.UsbConstants.SubClass != intf.getInterfaceSubclass()) ||
                    (CvrConstants.UsbConstants.Protocol != intf.getInterfaceProtocol())) {
                continue;
            }

//            UsbEndpoint in = null;
            List<UsbEndpoint> ins = new ArrayList<UsbEndpoint>();
            UsbEndpoint out = null;

            for (int e = 0, en = intf.getEndpointCount(); e < en; ++e) {
                UsbEndpoint endpoint = intf.getEndpoint(e);
                if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                        ins.add(endpoint);
                    } else if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                        out = endpoint;
                    }
                    endpoint.getAddress();
                }
            }

            if (ins.size() ==0 || out == null) {
                continue;
            }

            LogUtil.i(TAG, "Found compatible USB interface");
//            Log.i(TAG, "Interface class " + intf.getInterfaceClass());
//            Log.i(TAG, "Interface subclass " + intf.getInterfaceSubclass());
//            Log.i(TAG, "Interface protocol " + intf.getInterfaceProtocol());
//            Log.i(TAG, "Bulk out " + out);

            if ((device.getVendorId() != CvrConstants.UsbConstants.VendorId) ||
                    (device.getProductId() != CvrConstants.UsbConstants.ProductId)) {
                return false;
            }
            UsbDeviceConnection usbDeviceConnection = mUsbManager.openDevice(device);
            if (null == usbDeviceConnection) {
                return false;
            }

            UsbCvrConnection connection = new UsbCvrConnection(usbDeviceConnection, intf, ins, out);
            if (null == mUsbCvr) {
                mUsbCvr = new UsbCvr(connection);
                mUsbCvr.setListener(mListener);
                mListener.onCvrAttached(mUsbCvr);
            } else {
                LogUtil.i(TAG, "recover usb connection");
            }
        }
        LogUtil.w(TAG, "connectDevice succ");
        return true;
    }

    private void setupDevice() {
        UsbDevice device = discoverDevice();
        if (null == device) {
            mListener.onCvrNotFound();
        } else {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                    ACTION_USB_PERMISSION), 0);
            mUsbManager.requestPermission(device, permissionIntent);
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (device == null) {
                        return;
                    }
                    if (connectDevice(device)) {
                        isConnected = true;
                    }
                }

            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                LogUtil.i(TAG, "Usb Cvr Attached");
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(
                        ACTION_USB_PERMISSION), 0);
                mUsbManager.requestPermission(device, permissionIntent);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                LogUtil.i(TAG, "Usb Cvr Detach");
                isConnected = false;
                if (null != mUsbCvr) {
                    mUsbCvr.shutdownHard();
                }
                mUsbCvr = null;
                mListener.onCvrDetached(mUsbCvr);
            } else {
                LogUtil.i(TAG, action);
            }
        }
    };
}
