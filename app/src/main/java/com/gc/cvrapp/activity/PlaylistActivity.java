package com.gc.cvrapp.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.gc.cvrapp.AppConfig;
import com.gc.cvrapp.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.service.CvrService;
import com.gc.cvrapp.service.CvrServiceManager;
import com.gc.cvrapp.service.CvrServiceManager.CvrServiceConnection;
import com.gc.cvrapp.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends FragmentActivity implements CvrServiceConnection {

    private Cvr mCvr;
    private CvrServiceManager mServiceManager;
    private int mCurrentItem = 0;
    private ListViewPager mViewPager;
    private List<FragmentCallback> mCallback = new ArrayList<>();
    private static final String TAG = "PlaylistActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        LogUtil.i(TAG, "onCreate");
        createListPages();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.i(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.i(TAG, "onRestart");
        if (mCallback.size() > 1) {
            mServiceManager = new CvrServiceManager(this, AppConfig.APP_SERVICE);
            mServiceManager.setCallback(this);
            mServiceManager.bindCvrService();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onPause");
        if (null == mViewPager) {
            return;
        }
        mCurrentItem = mViewPager.getCurrentItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
        if (null == mViewPager) {
            return;
        }
        mViewPager.setCurrentItem(mCurrentItem);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG, "onStop");
        mServiceManager.unbindCvrService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
    }

    @Override
    public void onCvrConnected(Cvr cvr, CvrService service) {
        mCvr = cvr;
        // get file list
        for (FragmentCallback cb : mCallback) {
            cb.setCvr(mCvr);
        }
        LogUtil.i(TAG, "createFilelist");
        mCvr.getFileList(mFileListener);
    }

    @Override
    public void onCvrDisconnected(CvrService service) {

    }

    private Cvr.FileListListener mFileListener = new Cvr.FileListListener() {

        @Override
        public void onFileList(List<String> videolist, List<String> photolist) {
            for (FragmentCallback cb : mCallback) {
                cb.setList(videolist, photolist);
            }
        }
    };

    private void createListPages() {
        LogUtil.i(TAG, "createListPages");
        ViewGroup tab = (ViewGroup) findViewById(R.id.tab);
        tab.addView(LayoutInflater.from(this).inflate(R.layout.tab, tab, false));
        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of("video", ListVideoFragment.class));
        pages.add(FragmentPagerItem.of("photo", ListPhotoFragment.class));
        FragmentStatePagerItemAdapter adapter = new FragmentStatePagerItemAdapter(getSupportFragmentManager(), pages);
        mViewPager = (ListViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(adapter);

        SmartTabLayout viewpagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewpagerTab.setViewPager(mViewPager);
    }

    public void addFragmentCallback(FragmentCallback cb) {
        mCallback.add(cb);
        if (mCallback.size() > 1) {
            mServiceManager = new CvrServiceManager(this, AppConfig.APP_SERVICE);
            mServiceManager.setCallback(this);
            mServiceManager.bindCvrService();
        }
    }

    public void updateFilelist() {
        if (null != mCvr) {
            mCvr.getFileList(mFileListener);
        }
    }
}
