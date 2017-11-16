package com.gc.cvrapp.cvr.usbcvr;

import com.gc.cvrapp.cvr.CvrSettings;
import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.CvrConnection;

public class UsbCvr extends Cvr {
    private CvrConnection mConnection;
    private static final String TAG = "UsbCvr";

    public UsbCvr(CvrConnection connection) {
        super(connection);
        mConnection = connection;
    }

    @Override
    public void setListener(CvrListener listener) {
        super.setListener(listener);
    }

    @Override
    public void startPreview(MediaListener listener) {
        super.startPreview(listener);
    }

    @Override
    public void stopPreview(MediaListener listener) {
        super.stopPreview(listener);
    }

    @Override
    public void startRecord(MediaListener listener) {
        super.startRecord(listener);
    }

    @Override
    public void stopRecord(MediaListener listener) {
        super.stopRecord(listener);
    }

    @Override
    public void capturePic(MediaListener listener) {
        super.capturePic(listener);
    }

    @Override
    public void setting(CvrSettings settings) {
        super.setting(settings);
    }

    @Override
    public void getFileList(FileListListener listener) {
        super.getFileList(listener);
    }

    @Override
    public void playbackVideo(MediaListener listener, String filename) {
        super.playbackVideo(listener, filename);
    }

    @Override
    public void playbackVideoContinue(MediaListener listener) {
        super.playbackVideoContinue(listener);
    }

    @Override
    public void playbackVideoPause(MediaListener listener) {
        super.playbackVideoPause(listener);
    }

    @Override
    public void playbackVideoStop(MediaListener listener) {
        super.playbackVideoStop(listener);
    }

    @Override
    public void playbackVideoSeek(MediaListener listener, int sample) {
        super.playbackVideoSeek(listener, sample);
    }

    @Override
    public void playbackPic(MediaListener listener, String filename) {
        super.playbackPic(listener, filename);
    }

    @Override
    public void formatSD() {
        super.formatSD();
    }

    @Override
    public void connectCheck(ConnectCheckListener listener) {
        super.connectCheck(listener);
    }

    @Override
    public void deleteFile(FileListener listener, String item) {
        super.deleteFile(listener, item);
    }

    @Override
    public void lockFile(FileListener listener, String item) {
        super.lockFile(listener, item);
    }

    @Override
    public void unlockFile(FileListener listener, String item) {
        super.unlockFile(listener, item);
    }

    public void shutdownHard() {
        queueClear();
        if (null != mConnection) {
            mConnection.close();
            mConnection = null;
        }
    }
}
