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
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.Cvr.FileListener;
import com.gc.cvrapp.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class ListPhotoFragment extends Fragment implements PlaylistAdapter.AdapterCallback {
    private Cvr mCvr;
    private int mPosition = 0;
    private PlaylistAdapter mAdapter;
    private SwipeMenuListView listView;
    private PlaylistActivity parent;
    private SwipyRefreshLayout swipeRefreshLayout;
    private List<String> mPiclist = new ArrayList<>();
    private static final String ARG_FILELIST = "filelist";
    private static final String TAG = "PhotoListFragment";

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
        mAdapter = new PlaylistAdapter(mPiclist, this);
        listView = (SwipeMenuListView)view.findViewById(R.id.swipfilelist_view);
        listView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "del" item
                SwipeMenuItem delItem = new SwipeMenuItem(
                        getActivity());
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
                mPosition   = position;
                String item = mPiclist.get(position);
                switch (index) {
                    case 0:
                        // delete
                        LogUtil.i(TAG, "del " + item);
                        if (null != mCvr) mCvr.deleteFile(filecallback, item);
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
        mPiclist.clear();
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
        FileItemList piclist = new FileItemList();
        piclist.setFilelist(mPiclist);
        piclist.setCurFile(item);
        Intent intent = new Intent();
        intent.setClass(getActivity().getApplicationContext(), PlaybackPhotoActivity.class);
        intent.putExtra(ARG_FILELIST, piclist);
        getActivity().startActivity(intent);
    }

    private FileListener filecallback = new FileListener() {
        @Override
        public void onDeleteFile(String item) {
            mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgDeleteFile));
        }

        @Override
        public void onLockFile(String item) {}
        @Override
        public void onUnLockFile(String item) {}
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MsgCode.MsgUpdateFileList:
//                    mPiclist.clear();
                    mPiclist.addAll((List<String>)msg.obj);
                    mAdapter.notifyDataSetChanged();
                    break;

                case MsgCode.MsgDeleteFile:
                    mPiclist.remove(mPosition);
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
            LogUtil.i(TAG, "photo list" + photolist);
            if (null == photolist)
                return;

            mHandler.sendMessage(mHandler.obtainMessage(MsgCode.MsgUpdateFileList, photolist));
        }
    };

    private SwipyRefreshLayout.OnRefreshListener refreshListener = new SwipyRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
//            Log.d("ListPhotoFragment", "Refresh triggered at "
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
