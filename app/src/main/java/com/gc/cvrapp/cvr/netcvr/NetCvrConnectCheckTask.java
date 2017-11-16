package com.gc.cvrapp.cvr.netcvr;

import android.util.Log;

import java.util.TimerTask;

import com.gc.cvrapp.cvr.Cvr.ConnectCheckListener;

public class NetCvrConnectCheckTask extends TimerTask implements ConnectCheckListener {

    private static final int maxCountCheck = 5;

    private int countCheck = 0;

    private ConnectCheckTaskListener mListener;

    private NetCvr cvr;

    private static final String TAG = "NetCvrConnectCheckTask";

    NetCvrConnectCheckTask(NetCvr cvr, ConnectCheckTaskListener listener) {
        this.cvr = cvr;
        mListener = listener;
        countCheck = 0;
    }

    @Override
    public void run() {
        if (countCheck > maxCountCheck) {
            mListener.onDisconnect();
        }
        ++ countCheck;
        Log.i(TAG, "countCheck :" + String.valueOf(countCheck));
        cvr.connectCheck(this);
    }

    @Override
    public void onConnectCheck() {
        Log.i(TAG, "onConnectCheck " + String.valueOf(countCheck));
        countCheck = 0;
    }

    @Override
    public void onConnectError() {

    }

    public interface ConnectCheckTaskListener {
        void onDisconnect();
    }
}
