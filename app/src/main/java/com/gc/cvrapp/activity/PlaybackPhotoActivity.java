package com.gc.cvrapp.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gc.cvrapp.AppConfig;
import com.gc.cvrapp.R;
import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.service.CvrService;
import com.gc.cvrapp.service.CvrServiceManager.CvrServiceConnection;
import com.gc.cvrapp.service.CvrServiceManager;
import com.gc.cvrapp.media.Media;
import com.gc.cvrapp.media.photo.PicCachePool;
import com.gc.cvrapp.utils.LogUtil;

import java.util.ArrayList;

public class PlaybackPhotoActivity extends AppCompatActivity implements CvrServiceConnection {

    private Cvr mCvr;
    private CvrServiceManager mServiceManager;
    private FileItemList mPicList;
    private int mCurPicId;
    private boolean isDone = true;
    private View mCurrentView;
    private PicCachePool mPicPool;
    private Media mMedia;
    private ViewPager mPhotoPager;
    private PagerTitleStrip mPhotoTitle;
    private static final String ARG_FILELIST = "filelist";
    private static final String TAG = "PlaybackPicActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playphoto);

        mServiceManager = new CvrServiceManager(this, AppConfig.APP_SERVICE);
        mServiceManager.setCallback(this);

        mPicList    = (FileItemList) getIntent().getSerializableExtra(ARG_FILELIST);
        mCurPicId   = getCurPicId(mPicList.getCurFile());

        mPhotoPager = (ViewPager) findViewById(R.id.photopager);
        mPhotoTitle = (PagerTitleStrip) findViewById(R.id.phototitle);
        mPhotoTitle.setTextColor(Color.WHITE);
        mPicPool    = new PicCachePool();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mServiceManager.bindCvrService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mServiceManager.unbindCvrService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPicPool.clear();
    }

    @Override
    public void onCvrConnected(Cvr cvr, CvrService service) {
        LogUtil.i(TAG, "onCvrConnected");
        mMedia = service.getCvrMedia();
        if (null != mMedia) {
            mMedia.setPlaybackCallback(Icallback);
        }

        mCvr = cvr;
        if (null != mCvr) {
            // get file list
            LogUtil.i(TAG, "Service bind ");
            mCvr.playbackPic(mMedia, mPicList.getCurFile());
        }
    }

    @Override
    public void onCvrDisconnected(CvrService service) {
        LogUtil.i(TAG, "onCvrDisconnected");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MsgCode.MsgCvrAttach:
                    initPhotoPager();
                    break;

                case MsgCode.MsgPicture:
                    ImageView imageView = (ImageView) mCurrentView.findViewById(R.id.image_photo);
                    imageView.setImageBitmap((Bitmap) msg.obj);
                    break;

                default:
                    break;
            }
        }
    };

    private void initPhotoPager() {
        LayoutInflater layoutInflater = getLayoutInflater();
        final ArrayList<View> views = new ArrayList<View>();
        final ArrayList <String> titles =new ArrayList<String>();
        for (int i = 0; i < mPicList.getFilelist().size(); i ++) {
            titles.add(mPicList.getFilelist().get(i));
            views.add(layoutInflater.inflate(R.layout.photopager, null));
        }

        LogUtil.i(TAG, "initPhotoPager");
        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager)container).removeView(views.get(position));
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                mCurrentView = views.get(position);
                container.addView(mCurrentView);

                Bitmap bitmap = mPicPool.get(titles.get(position));
                if (null != bitmap) {
                    ImageView imageView = (ImageView) mCurrentView.findViewById(R.id.image_photo);
                    imageView.setImageBitmap(bitmap);
                    return mCurrentView;
                }

                if ((isDone && (null != mCvr))) {
                    isDone = false;
                    mCvr.playbackPic(mMedia, titles.get(position));
                }

                mCurrentView = views.get(position);
                return mCurrentView;
            }
        };
        mPhotoPager.setAdapter(mPagerAdapter);
        mPhotoPager.setCurrentItem(mCurPicId);
    }

    private int getCurPicId(String picname) {

        for (int i = 0; i < mPicList.getFilelist().size(); i ++) {
            String file = mPicList.getFilelist().get(i);
            if (file.contentEquals(picname)) {
                return i;
            }
        }
        return 0;
    }

    private Media.PlaybackCallback Icallback = new Media.PlaybackCallback() {
        @Override
        public void onPlaybackPicture(String picname, Bitmap bitmap) {
            LogUtil.i(TAG, "media playback pic " + picname);
            isDone = true;
            if (mPicPool.isEmpty()) {
                mPicPool.put(picname, bitmap);
                mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgCvrAttach));
            } else {
                mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgPicture, bitmap));
            }
        }

        @Override
        public void onPlaybackState(int state) {}

        @Override
        public void onPlaybackStep(int step) {}
    };
}
