package com.gc.cvrapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.cvrapp.AppConfig;
import com.gc.cvrapp.cvr.CvrSettings;
import com.gc.cvrapp.R;
import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.service.CvrService;
import com.gc.cvrapp.service.CvrServiceManager;
import com.gc.cvrapp.service.CvrServiceManager.CvrServiceConnection;
import com.gc.cvrapp.media.Media;
import com.gc.cvrapp.media.Media.PreviewCallback;
import com.gc.cvrapp.utils.LogUtil;

import java.lang.ref.WeakReference;

public class PreviewActivity extends AppCompatActivity implements CvrServiceConnection, SurfaceHolder.Callback {
    private Media mMedia;
    private CvrSettings mCvrSettings;
    private Dialog dialog;
    private TextView mTextRecording;
    private TextView mTextLocation;
    private SurfaceView mSurfaceView;
    private View mMenuView;
    private CvrServiceManager mServiceManager;
    private Cvr mCvr;
    private static final String TAG = "PreviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mMenuView = findViewById(R.id.preview_controller);
        mTextRecording = (TextView) findViewById(R.id.tv_record);
        mTextRecording.setVisibility(View.INVISIBLE);
        mTextLocation = (TextView) findViewById(R.id.tv_location);
        dialog = new AlertDialog.Builder(this).setTitle("Warning").setMessage("Please connect your cvr device.").create();
        mServiceManager = new CvrServiceManager(this, AppConfig.APP_SERVICE);
        mServiceManager.setCallback(this);

        mCvrSettings = new CvrSettings(this);
        mSurfaceView = (SurfaceView) findViewById(R.id.preview_surface);
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.postDelayed(dispPanel, 0);
            }
        });
        openSurface(mSurfaceView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG, "onStop");
        if ((null != mMedia) && (null != mCvr)) {
            if (mMedia.getPreviewState()) {
                mCvr.stopPreview(mMedia);
            }
            mMedia.wakeUnLock();
            mMedia.setPreviewState(false);
        }

        mHandler.removeCallbacks(dispPanel);
        mServiceManager.unbindCvrService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
        if (null != dialog) {
            dialog.dismiss();
        }
    }

    public void onClickCapturePic(View view) {
        if (null == mCvr) {
            mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgNoCvrFound));
            return;
        }

        if (mMedia.getPreviewTakePhotoState()) {
            return;
        }
        mMedia.setPreviewTakePhotoState(true);
        mCvr.capturePic(mMedia);
    }

    public void onClickStartRecord(View view) {
        if (null == mCvr) {
            mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgNoCvrFound));
            return;
        }

        if (mMedia.getPreviewRecordState()) {
            return;
        }

        mMedia.setPreviewRecordState(true);
        if (!mCvrSettings.getEnRecord()) {
            mCvr.startRecord(mMedia);
        } else {
            mCvr.stopRecord(mMedia);
        }
    }

    public void onClickSetting(View view) {
        if (null == mCvr) {
            mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgNoCvrFound));
            return;
        }

        mMedia.setPreviewState(false);
        if (mCvrSettings.getEnRecord()) {
            mCvr.stopRecord(mMedia);
        }
        mCvr.stopPreview(mMedia);
        Intent intent = new Intent();
        intent.setClass(PreviewActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    public void onClickPlayList(View view) {
        if (null == mCvr) {
            mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgNoCvrFound));
            return;
        }

        mMedia.setPreviewState(false);
        if (mCvrSettings.getEnRecord()) {
            mCvr.stopRecord(mMedia);
        }
        mCvr.stopPreview(mMedia);
        Intent intent = new Intent();
        intent.setClass(PreviewActivity.this, PlaylistActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCvrConnected(Cvr cvr, CvrService service) {
        LogUtil.i(TAG, "onCvrConnected");
        if (dialog.isShowing()) {
            dialog.hide();
        }

        mMedia = service.getMedia();
        if (null == mMedia) {
            return;
        }
        mMedia.wakeLock();
        mMedia.setPreviewCallback(Icallback);
        mMedia.startPreview(mSurfaceView.getHolder().getSurface());

        mCvr = cvr;
        mCvr.setting(mCvrSettings);
        mCvr.startPreview(mMedia);
    }

    @Override
    public void onCvrDisconnected(CvrService service) {
        if (null != mMedia) {
            mMedia.stopPreview();
        }
        mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgCvrDetach));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mServiceManager.bindCvrService();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private  void openSurface(SurfaceView surfaceView) {
        LogUtil.i(TAG, "openSurface");
        ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
        surfaceView.getHolder().addCallback(this);
        surfaceView.setLayoutParams(layoutParams);
    }

    private PreviewCallback Icallback = new PreviewCallback() {
        @Override
        public void onPreviewState(boolean state) {
            if (state) {
                LogUtil.i(TAG, "is previewing");
            } else {
                LogUtil.i(TAG, "callback stop preivew");
                mMedia.stopPreview();
            }
        }

        @Override
        public void onPreviewRecordState(boolean state) {
            if (state) {
                mCvrSettings.setEnRecord(true);
                mHandler.postDelayed(recordRunnable, 1000);
            } else {
                mCvrSettings.setEnRecord(false);
                mHandler.removeCallbacks(recordRunnable);
                mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgStopRecord));
            }
        }
    };

    private Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            if (mTextRecording.isShown()) {
                mTextRecording.setVisibility(View.INVISIBLE);
            } else {
                mTextRecording.setVisibility(View.VISIBLE);
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    private Handler mHandler = new PreviewHandler(this);

    private static class PreviewHandler extends Handler {
        private final WeakReference<PreviewActivity> mActivity;

        PreviewHandler(PreviewActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PreviewActivity activity = mActivity.get();
            if (activity != null) {
                // ...
                switch (msg.what) {
                    case MsgCode.MsgLocation:
                        Location location = (Location)msg.obj;
                        activity.mTextLocation.setText("[Latitude: " + String.valueOf(location.getLatitude()) +
                                " Longtitude: " + String.valueOf(location.getLongitude()) + "]");
                        break;
                    case MsgCode.MsgStartRecord:
                        break;
                    case MsgCode.MsgStopRecord:
                        activity.mTextRecording.setVisibility(View.INVISIBLE);
                        break;
                    case MsgCode.MsgCvrDetach:
                    case MsgCode.MsgNoCvrFound:
                        activity.dialog.show();
                        break;
                    case MsgCode.MsgRecording:
                        break;
                    default:
                        break;
                }
            }
        }
    }

//    private CvrService.LocationCallback locateCallback = new CvrService.LocationCallback() {
//        @Override
//        public void onLocationChanged(Location location) {
//            LogUtil.i(TAG, "latitude: " + String.valueOf(location.getLatitude()) +
//                    " longtitude: " + String.valueOf(location.getLongitude()));
//            mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgLocation, location));
//        }
//    };

    private Runnable dispPanel = new Runnable() {
        @Override
        public void run() {
            if (mMenuView.isShown()) {
                LogUtil.i(TAG, "controller visible");
                mMenuView.setVisibility(View.INVISIBLE);
            } else {
                LogUtil.i(TAG, "controller invisible");
                mMenuView.setVisibility(View.VISIBLE);
            }
        }
    };
}
