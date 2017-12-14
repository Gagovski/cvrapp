package com.gc.cvrapp.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.gc.cvrapp.AppConfig;
import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.CvrListener;
import com.gc.cvrapp.utils.LogUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Base class for managing UsbCvrService or NetCvrService
 */
public class CvrServiceManager implements ServiceConnection {
    private Cvr mCvr;
    private CvrService mService;
    private boolean mBound = false;
    private int mServiceId;
    private Context mContext;
    private ScheduledExecutorService mExecutor;
    private static final String TAG = "CvrServiceManager";

    public CvrServiceManager(Context context, int srvid) {
        LogUtil.i(TAG, "srvid " + String.valueOf(srvid));
        mServiceId = srvid;
        mContext = context;
        mExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * bind UsbCvrService or NetCvrService
     */
    public void bindCvrService() {
        if (mBound) {
            return;
        }

        Intent intent;
        if (AppConfig.UsbService == mServiceId) {
            intent = new Intent(mContext, UsbCvrService.class);
        } else {
            intent = new Intent(mContext, NetCvrService.class);
        }
        mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    /**
     * unbind UsbCvrService or NetCvrService
     */
    public void unbindCvrService() {
        if (mBound) {
            mContext.unbindService(this);
            mBound = false;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBound = true;
        if (AppConfig.UsbService == mServiceId) {
            UsbCvrService.BinderService usbBinder = (UsbCvrService.BinderService) service;
            mService = usbBinder.getService();
        } else {
            NetCvrService.BinderService netBinder = (NetCvrService.BinderService) service;
            mService = netBinder.getService();
        }
        mService.setCvrListener(mListener);
        mCvr = mService.getCvr();
        if (null != mCvr) {
            mCallback.onCvrConnected(mCvr, mService);
        } else {
            mService.initialize();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBound = false;
    }

    /**
     * the callback interface of CvrService
     * */
    public interface CvrServiceConnection {
        /** connect cvr service
         * @param cvr cvr device
         * @param service usb or net service
         */
        void onCvrConnected(Cvr cvr, CvrService service);
        /** disconnect cvr service
         * @param service usb or net service
         */
        void onCvrDisconnected(CvrService service);
    }

    /**
     * set CvrServiceManager callback interface
     * @param callback the CvrServiceConnection callback interface
     */
    public void setCallback(@NonNull CvrServiceConnection callback) {
        mCallback = callback;
    }

    private CvrServiceConnection mCallback;

    private CvrListener mListener = new CvrListener() {

        @Override
        public void onCvrAttached(Cvr cvr) {
            LogUtil.i(TAG, "onCvrAttached");
            mCvr = cvr;
            mCallback.onCvrConnected(mCvr, mService);
        }

        @Override
        public void onCvrDetached(Cvr cvr) {
            LogUtil.i(TAG, "onCvrDetached");
            mCvr = null;
            mCallback.onCvrDisconnected(mService);
        }

        @Override
        public void onCvrError(String message) {
            LogUtil.i(TAG, "onError " + message);
            mExecutor.schedule(new HandleRunnable(), 5, TimeUnit.SECONDS);
        }

        @Override
        public void onCvrNotFound() {
            LogUtil.i(TAG, "onCvrNotFound");
            mCallback.onCvrDisconnected(mService);
        }
    };

    private class HandleRunnable implements Runnable {
        @Override
        public void run() {
            mService.reinitialize();
        }
    }
}
