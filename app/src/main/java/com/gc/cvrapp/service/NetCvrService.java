package com.gc.cvrapp.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.CvrListener;
import com.gc.cvrapp.cvr.netcvr.NetCvr;
import com.gc.cvrapp.cvr.netcvr.NetCvrConnection;
import com.gc.cvrapp.cvr.netcvr.NetCvrManager;
import com.gc.cvrapp.cvr.netcvr.NetSockEndpoint;
import com.gc.cvrapp.utils.LogUtil;

import java.util.List;

public class NetCvrService extends CvrService {

    private NetCvrManager mNetManager;

    private NetCvr mNetCvr;

    private List<NetSockEndpoint> mEndpoints;

    private static final String TAG = "NetCvrService";

    private final BinderService mBinderService = new BinderService();


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate");
        mNetManager = new NetCvrManager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinderService;
    }

    public class BinderService extends Binder {
        public NetCvrService getService() { return NetCvrService.this; }
    }


    private CvrListener mListener;

    @Override
    public void setCvrListener(CvrListener listener) {
        mListener = listener;
    }

    @Override
    public void initialize() {
        if (null != mNetCvr) {
            if (null != mListener) {
                mListener.onCvrAttached(mNetCvr);
            }
            return;
        }

        LogUtil.i(TAG, "initialize");
        mNetManager.setCallback(Icallback);
        mNetManager.connect();
    }

    @Override
    public void reinitialize() {

    }

    public Cvr getCvr() {
        return mNetCvr;
    }

    private NetCvrManager.NetCvrManagerCallback Icallback = new NetCvrManager.NetCvrManagerCallback() {
        @Override
        public void isConnect() {
            LogUtil.i(TAG, "net sock is connected");
            mEndpoints = mNetManager.getEndpoints();

            NetCvrConnection connection = new NetCvrConnection(mEndpoints, mEndpoints.get(0));
            mNetCvr = new NetCvr(connection);
            mNetCvr.setListener(mListener);
            mListener.onCvrAttached(mNetCvr);
        }

        @Override
        public void isError() {
            mEndpoints = null;
        }
    };
}
