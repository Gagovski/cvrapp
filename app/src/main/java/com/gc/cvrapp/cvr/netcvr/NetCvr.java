package com.gc.cvrapp.cvr.netcvr;

import com.gc.cvrapp.cvr.CvrSettings;
import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.utils.LogUtil;

public class NetCvr extends Cvr {
    private static final String TAG = "NetCvr";

    public NetCvr(NetCvrConnection connection) {
        super(connection);
    }

    @Override
    public void setListener(CvrListener listener) {
        super.setListener(listener);
    }

    @Override
    public void capturePic(MediaListener listener) {
        LogUtil.i(TAG, "capturePic");
        super.capturePic(listener);
    }

    @Override
    public void startPreview(MediaListener listener) {
        LogUtil.i(TAG, "startPreview");
        super.startPreview(listener);
    }

    @Override
    public void stopPreview(MediaListener listener) {
        LogUtil.i(TAG, "stopPreview");
        super.stopPreview(listener);
    }

    @Override
    public void startRecord(MediaListener listener) {
        LogUtil.i(TAG, "startRecord");
        super.startRecord(listener);
    }

    @Override
    public void stopRecord(MediaListener listener) {
        LogUtil.i(TAG, "stopRecord");
        super.stopRecord(listener);
    }

    @Override
    public void setting(CvrSettings settings) {
        LogUtil.i(TAG, "setting");
        super.setting(settings);
    }

    @Override
    public void getFileList(FileListListener listener) {
        LogUtil.i(TAG, "getFileList");
        super.getFileList(listener);
    }

    @Override
    public void playbackVideo(MediaListener listener, String filename) {
        LogUtil.i(TAG, "playbackVideo");
        super.playbackVideo(listener, filename);
    }

    @Override
    public void playbackVideoContinue(MediaListener listener) {
        LogUtil.i(TAG, "playbackVideoContinue");
        super.playbackVideoContinue(listener);
    }

    @Override
    public void playbackVideoPause(MediaListener listener) {
        LogUtil.i(TAG, "playbackVideoPause");
        super.playbackVideoPause(listener);
    }

    @Override
    public void playbackVideoStop(MediaListener listener) {
        LogUtil.i(TAG, "playbackVideoStop");
        super.playbackVideoStop(listener);
    }

    @Override
    public void playbackVideoSeek(MediaListener listener, int sample) {
        LogUtil.i(TAG, "playbackVideoSeek");
        super.playbackVideoSeek(listener, sample);
    }

    @Override
    public void playbackPic(MediaListener listener, String filename) {
        LogUtil.i(TAG, "playbackPic");
        super.playbackPic(listener, filename);
    }

    @Override
    public void formatSD() {
        LogUtil.i(TAG, "playbackPic");
        super.formatSD();
    }

    @Override
    public void connectCheck(ConnectCheckListener listener) {
        LogUtil.i(TAG, "connectCheck");
    }

    @Override
    public void deleteFile(FileListener listener, String item) {
        LogUtil.i(TAG, "deleteFile");
        super.deleteFile(listener, item);
    }

    @Override
    public void lockFile(FileListener listener, String item) {
        LogUtil.i(TAG, "lockFile");
        super.lockFile(listener, item);
    }

    @Override
    public void unlockFile(FileListener listener, String item) {
        LogUtil.i(TAG, "unlockFile");
        super.unlockFile(listener, item);
    }
}
