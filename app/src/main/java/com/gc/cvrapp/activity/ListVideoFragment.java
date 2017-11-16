package com.gc.cvrapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import com.gc.cvrapp.R;
import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.utils.LogUtil;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;


public class ListVideoFragment extends Fragment implements PlaylistAdapter.AdapterCallback {
    private Cvr mCvr;
    private int mPosition = 0;
    private SwipeMenuListView listView;
    private PlaylistActivity parent;
    private SwipyRefreshLayout swipeRefreshLayout;
    private PlaylistAdapter mAdapter;
    private List<String> mVideoList = new ArrayList<>();
    private static final String ARG_FILELIST = "filelist";
    private static final String TAG = "VideoListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LogUtil.i(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.filelist, container, false);
        swipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipy_layout);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.i(TAG, "onViewCreated");
        mAdapter = new PlaylistAdapter(mVideoList, this);

        listView = (SwipeMenuListView)view.findViewById(R.id.swipfilelist_view);
        listView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                // create "lock" item
                SwipeMenuItem lockItem = new SwipeMenuItem(getActivity());
                // set item background
                lockItem.setBackground(new ColorDrawable(Color.GRAY));
                // set item width
                lockItem.setWidth(dp2px(90));
                // set item title
                lockItem.setTitle("lock");
                // set item title fontsize
                lockItem.setTitleSize(18);
                // set item title font color
                lockItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(lockItem);

                // create "lock" item
                SwipeMenuItem unlockItem = new SwipeMenuItem(getActivity());
                // set item background
                unlockItem.setBackground(new ColorDrawable(Color.LTGRAY));
                // set item width
                unlockItem.setWidth(dp2px(90));
                // set item title
                unlockItem.setTitle("unlock");
                // set item title fontsize
                unlockItem.setTitleSize(18);
                // set item title font color
                unlockItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(unlockItem);

                // create "del" item
                SwipeMenuItem delItem = new SwipeMenuItem(getActivity());
                // set item background
                delItem.setBackground(new ColorDrawable(Color.RED));
                // set item width
                delItem.setWidth(dp2px(90));
                // set item title
                delItem.setTitle("delete");
                // set item title fontsize
                delItem.setTitleSize(18);
                // set item title font color
                delItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(delItem);
            }
        };

        listView.setMenuCreator(creator);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (null == mCvr) {
                    return false;
                }
                mPosition   = position;
                String item = mVideoList.get(position);
                switch (index) {
                    case 0:
                        // lock
                        LogUtil.i(TAG, "lock " + item);
                        if (!item.contains("LOCK")) mCvr.lockFile(filecallback, item);
                        break;
//
                    case 1:
                        // unlock
                        LogUtil.i(TAG, "unlock " + item);
                        if (item.contains("LOCK")) mCvr.unlockFile(filecallback, item);
                        break;

                    case 2:
                        // delete
                        LogUtil.i(TAG, "delete " + item);
                        mCvr.deleteFile(filecallback, item);
                        break;

                }
                return false;
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.i(TAG, "onAttach");
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            parent = (PlaylistActivity) getActivity();
            parent.addFragmentCallback(Icallback);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(TAG, "Pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.i(TAG, "Stop");
        mVideoList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "Resume");
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void startPlayback(String item) {
        LogUtil.i(TAG, "startPlayback: " + item);
        FileItemList videolist = new FileItemList();
        videolist.setFilelist(mVideoList);
        videolist.setCurFile(item);
        Intent intent = new Intent();
        intent.setClass(getActivity().getApplicationContext(), PlaybackVideoActivity.class);
        intent.putExtra(ARG_FILELIST, videolist);
        getActivity().startActivity(intent);
    }

    private Cvr.FileListener filecallback = new Cvr.FileListener() {
        @Override
        public void onDeleteFile(String item) {
            LogUtil.i(TAG, "onDeleteFile " + item);
            mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgDeleteFile));
        }

        @Override
        public void onLockFile(String item) {
            mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgLock, item));
        }

        @Override
        public void onUnLockFile(String item) {
            mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgUnLock, item));
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MsgCode.MsgUpdateFileList:
//                    mVideoList.clear();
                    mVideoList.addAll((List<String>) msg.obj);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MsgCode.MsgLock:
                    LogUtil.i(TAG, "Lock: " + (String)msg.obj);
                    mVideoList.set(mPosition, (String)msg.obj);
                    mAdapter.notifyDataSetChanged();
                    break;

                case MsgCode.MsgUnLock:
                    LogUtil.i(TAG, "unLock: " + (String)msg.obj);
                    mVideoList.set(mPosition, (String)msg.obj);
                    mAdapter.notifyDataSetChanged();
                    break;

                case MsgCode.MsgDeleteFile:
                    mVideoList.remove(mPosition);
                    mAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        }
    };

    private FragmentCallback Icallback = new FragmentCallback() {
        @Override
        public void setCvr(Cvr cvr) {
            LogUtil.i(TAG, "set cvr");
            mCvr = cvr;
        }

        @Override
        public void setList(List<String> videolist, List<String> photolist) {
            if (null == videolist)
                return;
            LogUtil.i(TAG, "video list " + videolist);
            mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgUpdateFileList, videolist));
        }
    };

    private SwipyRefreshLayout.OnRefreshListener refreshListener = new SwipyRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
//            Log.d("ListVideoFragment", "Refresh triggered at "
//                    + (swipyRefreshLayoutDirection == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
            if (parent == null) {
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            parent.updateFilelist();
            swipeRefreshLayout.setRefreshing(false);
        }
    };
}
