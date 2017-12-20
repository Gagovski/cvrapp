package com.gc.cvrapp.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.gc.cvrapp.AppConfig;
import com.gc.cvrapp.R;
import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.FormattingListener;
import com.gc.cvrapp.service.CvrService;
import com.gc.cvrapp.service.CvrServiceManager;
import com.gc.cvrapp.service.CvrServiceManager.CvrServiceConnection;
import com.gc.cvrapp.utils.LogUtil;

import java.lang.ref.WeakReference;

public class FormatActivity extends AppCompatActivity implements CvrServiceConnection {
    private Cvr mCvr;
    private CvrServiceManager mServiceManager;
    private ImageView imgFormating;
    private AnimationDrawable mAniDraw;
    private boolean isFormatting = false;
    private static final String TAG = "FormatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_format);

        mServiceManager = new CvrServiceManager(this, AppConfig.APP_SERVICE);
        mServiceManager.setCallback(this);
        imgFormating = findViewById(R.id.img_formating);
        imgFormating.setBackgroundResource(R.drawable.formating);
        imgFormating.setVisibility(View.INVISIBLE);
        mAniDraw = (AnimationDrawable)imgFormating.getBackground();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mServiceManager.bindCvrService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServiceManager.unbindCvrService();
    }

    @Override
    public void onCvrConnected(Cvr cvr, CvrService service) {
        mCvr = cvr;
    }

    @Override
    public void onCvrDisconnected(CvrService service) {

    }

    public void onClickFormatting(View view) {
        if (!isFormatting) {
            isFormatting = true;
            mHandler.sendEmptyMessage(MsgCode.MsgFormatting);
            mCvr.formatSD(formattingListener);
        }
    }

    private Handler mHandler = new FormattingHandler(this);
    private static class FormattingHandler extends Handler {
        private final WeakReference<FormatActivity> mActivity;

        FormattingHandler(FormatActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FormatActivity activity = mActivity.get();
            switch (msg.what) {
                case MsgCode.MsgFormatting:
                    activity.imgFormating.setVisibility(View.VISIBLE);
                    activity.mAniDraw.start();
                    break;

                case MsgCode.MsgFormatdone:
                    activity.mAniDraw.stop();
                    activity.imgFormating.setVisibility(View.INVISIBLE);
                    break;

                default:
                    break;
            }
        }
    }

    private FormattingListener formattingListener = new FormattingListener() {
        @Override
        public void onFormatDone() {
            LogUtil.i(TAG, "format sdcard done");
            mHandler.sendEmptyMessage(MsgCode.MsgFormatdone);
            isFormatting = false;
        }
    };
}
