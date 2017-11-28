package com.gc.cvrapp.service;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.CvrListener;
import com.gc.cvrapp.media.Media;
import com.gc.cvrapp.utils.LogUtil;

/**
 * Base class for usb or net cvr service
 */
public abstract class CvrService extends Service {
    private Media media;
    private LocationManager locationManager;
    private static final String TAG = "CvrService";

    @Override
    public void onCreate() {
        super.onCreate();
        if (null == media) {
            media = new Media(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * get cvr device
     * @return cvr device instant
     */
    public abstract Cvr getCvr();

    /**
     * get cvr media
     * @return cvr media instant
     */
    public Media getMedia() {
        return media;
    }

    /**
     * set cvr callback listener
     * @param listener the cvr CvrListener listener
     */
    public abstract void setCvrListener(CvrListener listener);

    /**
     * init cvr
     */
    public abstract void initialize();

    /**
     * reinit cvr
     */
    public abstract void reinitialize();

    public void locationServiceInit(LocationCallback cb) {
        locateCallback = cb;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.setTestProviderEnabled("gps",false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LogUtil.i(TAG, "already open gps");
            getLocation();
        } else {
            LogUtil.i(TAG, "not open gps");
        }
    }

    private void getLocation() {
        Location location = null;
        try {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (location != null) {
            locateCallback.onLocationChanged(location);
        } else {
            LogUtil.i(TAG, "location null");
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            if (null == location) {
                return;
            }
            locateCallback.onLocationChanged(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {
            LogUtil.i(TAG, "location provider is enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            LogUtil.i(TAG, "location provider is disabled");
        }
    };

    public interface LocationCallback {
        void onLocationChanged(Location location);
    }

    private LocationCallback locateCallback;
}
