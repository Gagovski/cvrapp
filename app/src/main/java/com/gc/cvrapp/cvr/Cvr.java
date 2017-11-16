package com.gc.cvrapp.cvr;

import android.graphics.Bitmap;

import com.gc.cvrapp.cvr.commands.CapturePicCommand;
import com.gc.cvrapp.cvr.commands.Command;
import com.gc.cvrapp.cvr.commands.CvrAction;
import com.gc.cvrapp.cvr.commands.DeleteFileCommand;
import com.gc.cvrapp.cvr.commands.FormatSDCommand;
import com.gc.cvrapp.cvr.commands.GetFileListCommand;
import com.gc.cvrapp.cvr.commands.LockFileCommand;
import com.gc.cvrapp.cvr.commands.PlaybackPicCommand;
import com.gc.cvrapp.cvr.commands.PlaybackVideoCommand;
import com.gc.cvrapp.cvr.commands.PlaybackVideoPauseCommand;
import com.gc.cvrapp.cvr.commands.PlaybackVideoSeekCommand;
import com.gc.cvrapp.cvr.commands.PlaybackVideoStopCommand;
import com.gc.cvrapp.cvr.commands.SettingCommand;
import com.gc.cvrapp.cvr.commands.StartPreviewCommand;
import com.gc.cvrapp.cvr.commands.StartRecordCommand;
import com.gc.cvrapp.cvr.commands.StopPreviewCommand;
import com.gc.cvrapp.cvr.commands.StopRecordCommand;
import com.gc.cvrapp.cvr.commands.UnLockFileCommand;
import com.gc.cvrapp.media.audio.AudioInfo;
import com.gc.cvrapp.media.video.VideoInfo;
import com.gc.cvrapp.utils.LogUtil;
import com.gc.cvrapp.utils.PacketUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Base class for usb or net cvr device
 */
public abstract class Cvr {
    private CvrConnection mConnection;
    private ScheduledExecutorService mExecutor;
    private List<WorkerThread> workerThreads;
    private static final String TAG = "Cvr";

    public Cvr(CvrConnection connection) {
        mExecutor = Executors.newSingleThreadScheduledExecutor();
        mConnection = connection;
        workerThreads = new ArrayList<>(connection.getIns().size());
        for (int i = 0; i < connection.getIns().size(); i ++) {
            WorkerThread workerThread = new WorkerThread(i, connection, connection.getIns().get(i));
            workerThreads.add(workerThread);
            workerThread.start();
        }
    }

    /**
     * interface CvrListener for cvr device listener
     */
    public interface CvrListener {
        /**
         * listen cvr attached
         * @param cvr the cvr device object
         */
        void onCvrAttached(Cvr cvr);

        /**
         * listen cvr detached
         * @param cvr the cvr device object
         */
        void onCvrDetached(Cvr cvr);

        /**
         * listen cvr error
         * @param message the cvr error message
         */
        void onCvrError(String message);

        /**
         * listen cvr device not find
         */
        void onCvrNotFound();
    }

    /**
     * interface ConnectCheckListener for net cvr connection state listening
     */
    public interface ConnectCheckListener {
        void onConnectCheck();
        void onConnectError();
    }

    /**
     * interface CommandIO for cvr command
     */
    public interface CommandIO {
        /**
         * handle cvr message command
         * @param command message command
         * @param data message payload data
         * @param datalen message payload data len
         * */
        void handleCommand(Command command, byte[] data, int datalen);
    }

    /**
     * interface ResponseIO for cvr response
     */
    public interface ResponseIO {
        /**
         * handle cvr message response
         * @param command message command
         * @param resp message payload data
         * @param datalen message payload data len
         * */
        int handleResponse(Command command, ByteBuffer resp, int datalen);
    }

    /**
     * interface MediaListener for cvr media
     */
    public interface MediaListener {
        /**
         * listen response preview sample
         * @param data preview sample data
         * @param size preview sample data size
         */
        void onPreview(ByteBuffer data, int size);

        /**
         * listen response preview stop
         */
        void onPreviewStop();

        /**
         * listen preview start record
         */
        void onPreviewStartRecord();

        /**
         * listen preview stop record
         */
        void onPreviewStopRecord();

        /**
         * listen preview take photo
         */
        void onPreviewTakePhoto();

        /**
         * listen playback video info and audio info
         * @param videoInfo video info
         * @param audioInfo audio info
         */
        void onPlaybackInfo(VideoInfo videoInfo, AudioInfo audioInfo);

        /**
         * listen playback stop
         */
        void onPlaybackStop();

        /**
         * listen playback pause
         */
        void onPlaybackPause();

        /**
         * listen playback seek
         * @param sample seek sample
         */
        void onPlaybackSeek(int sample);

        /**
         * listen playback video sample data
         * @param data video sample data
         * @param timestamp video sample timestamp
         * @param size video sample data size
         */
        void onVideoSampleData(ByteBuffer data, int timestamp, int size);

        /**
         * listen playback audio sample data
         * @param data audio sample data
         * @param size audio sample data size
         */
        void onAudioSampleData(ByteBuffer data, int size);

        /**
         * listen playback sample end
         */
        void onSampleEnd();

        /**
         * listen playback photo bitmap data
         * @param picname photo name
         * @param bitmap bitmap data
         */
        void onPicture(String picname, Bitmap bitmap);
    }

    /**
     * interface FileListListener for cvr file list
     */
    public interface FileListListener {
        void onFileList(List<String> mp4list, List<String> piclist);
    }

    /**
     * interface FileListener for cvr file operation
     */
    public interface FileListener {
        /**
         * listen cvr delete file
         * @param item file item name
         */
        void onDeleteFile(String item);

        /**
         * listen cvr lock file
         * @param item file item name
         */
        void onLockFile(String item);

        /**
         * listen cvr unlock file
         * @param item file item name
         */
        void onUnLockFile(String item);
    }

    protected void queueMsg(CvrAction command) {
        for (int i = 0; i < workerThreads.size(); i ++) {
            workerThreads.get(i).queueMsg(command);
        }
    }

    protected void queueClear() {
        for (int i = 0; i < workerThreads.size(); i ++) {
            workerThreads.get(i).queueClear();
        }
        workerThreads.clear();
    }

    /**
     * cvr take photo
     * @param listener the cvr MediaListener
     */
    public void capturePic(MediaListener listener) {
        CapturePicCommand command = new CapturePicCommand(this, listener);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr start preview
     * @param listener the cvr MediaListener
     */
    public void startPreview(MediaListener listener) {
        StartPreviewCommand command = new StartPreviewCommand(this, listener);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr stop preview
     * @param listener the cvr MediaListener
     */
    public void stopPreview(MediaListener listener) {
        StopPreviewCommand command = new StopPreviewCommand(this, listener);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr start recording
     * @param listener the cvr MediaListener
     */
    public void startRecord(MediaListener listener) {
        StartRecordCommand command = new StartRecordCommand(this, listener);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr stop recording
     * @param listener the cvr MediaListener
     */
    public void stopRecord(MediaListener listener) {
        StopRecordCommand command = new StopRecordCommand(this, listener);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr settings
     * @param settings the cvr settings parameter
     */
    public void setting(CvrSettings settings) {
        LogUtil.i(TAG, "setting");
        SettingCommand command = new SettingCommand(this, settings);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr get file list
     * @param listener the cvr FileListListener
     */
    public void getFileList(FileListListener listener) {
        LogUtil.i(TAG, "getFileList");
        GetFileListCommand command = new GetFileListCommand(this, listener);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr playback video
     * @param listener the cvr MediaListener
     * @param filename the video filename
     */
    public void playbackVideo(MediaListener listener, String filename) {
        LogUtil.i(TAG, "playbackVideo");
        PlaybackVideoCommand command = new PlaybackVideoCommand(this, listener, filename);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr continue playback video
     * @param listener the cvr MediaListener
     */
    public void playbackVideoContinue(MediaListener listener) {
        LogUtil.i(TAG, "playbackVideo play");
        PlaybackVideoPauseCommand command = new PlaybackVideoPauseCommand(this, listener);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr pause playback video
     * @param listener the cvr MediaListener
     */
    public void playbackVideoPause(MediaListener listener) {
        LogUtil.i(TAG, "playbackVideo pause");
        PlaybackVideoPauseCommand command = new PlaybackVideoPauseCommand(this, listener);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr stop playback video
     * @param listener the cvr MediaListener
     */
    public void playbackVideoStop(MediaListener listener) {
        PlaybackVideoStopCommand command = new PlaybackVideoStopCommand(this, listener);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr seek playback video
     * @param listener the cvr MediaListener
     * @param sample the video sample
     */
    public void playbackVideoSeek(MediaListener listener, int sample) {
        PlaybackVideoSeekCommand command = new PlaybackVideoSeekCommand(this, listener, sample);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr playback photo
     * @param listener the cvr MediaListener
     * @param filename the photo name
     */
    public void playbackPic(MediaListener listener, String filename) {
        PlaybackPicCommand command = new PlaybackPicCommand(this, listener, filename);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr format sd
     */
    public void formatSD() {
        LogUtil.i(TAG, "formatSD");
        FormatSDCommand command = new FormatSDCommand(this);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr check net connection state
     * @param listener the cvr ConnectCheckListener
     */
    public void connectCheck(ConnectCheckListener listener) {

    }

    /**
     * cvr delete file
     * @param listener the cvr FileListener
     * @param item the file item name
     */
    public void deleteFile(FileListener listener, String item) {
        LogUtil.i(TAG, "deleteFile");
        DeleteFileCommand command = new DeleteFileCommand(this, listener, item);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr lock file
     * @param listener the cvr FileListener
     * @param item the file item name
     */
    public void lockFile(FileListener listener, String item) {
        LogUtil.i(TAG, "LockFile");
        LockFileCommand command = new LockFileCommand(this, listener, item);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    /**
     * cvr unlock file
     * @param listener the cvr FileListener
     * @param item the file item name
     */
    public void unlockFile(FileListener listener, String item) {
        LogUtil.i(TAG, "unLockFile");
        UnLockFileCommand command = new UnLockFileCommand(this, listener, item);
        queueMsg(command);
        mExecutor.execute(new CommandRunnable(command, mConnection));
    }

    private CvrListener mListener;
    /**
     * set cvr listener
     * @param listener the cvr CvrListener
     */
    public void setListener(CvrListener listener) {
        mListener = listener;
    }

    private class CommandRunnable implements Runnable, CommandIO {
        private CvrAction action;
        private CvrConnection connection;
        private ByteBuffer msg;
        private int maxPacketOutSize;
        private static final String TAG = "CommandRunnable";

        CommandRunnable(CvrAction action, CvrConnection connection) {
            this.connection = connection;
            this.action = action;
            this.maxPacketOutSize = connection.getMaxPacketOutSize();
            msg = ByteBuffer.allocate(maxPacketOutSize);
            msg.order(ByteOrder.LITTLE_ENDIAN);
        }

        @Override
        public void handleCommand(Command command, byte[] data, int datalen) {
            ByteBuffer b = msg;
            b.position(0);
            command.encodeCommand(b);

            int res = connection.transferOut(connection.getOut(), b.array(), CvrConstants.MsgHeaderLen, 0);
            if (res < CvrConstants.MsgHeaderLen) {
                mListener.onCvrError(String.format("bulk out err res: %d", res));
                return;
            }

            PacketUtil.logHexdump(TAG, b.array(), CvrConstants.MsgHeaderLen);
            if (datalen > 0) {
                res = connection.transferOut(connection.getOut(), data, datalen, 0);
                if (res < datalen) {
                    mListener.onCvrError(String.format("bulk out err res: %d", res));
                    return;
                }
//                PacketUtil.logHexdump(TAG, data, datalen);
            }
        }

        @Override
        public void run() {
            if (action != null)
                this.action.exec(this);
        }
    }

    private class WorkerThread extends Thread implements ResponseIO {
        private boolean isStop = false;
        private int tid = 0;
        private final LinkedBlockingQueue<CvrAction> mQueue = new LinkedBlockingQueue<CvrAction>();
        private Object object;
        private CvrConnection connection;
        private ByteBuffer msg;
        private static final String TAG = "WorkerThread";

        WorkerThread(int tid, CvrConnection connection, Object object) {
            this.connection = connection;
            this.object = object;
            this.tid = tid;
            LogUtil.i(TAG, "workThread tid: " + String.valueOf(this.tid));
        }

        private void queueMsg(CvrAction command) {
            synchronized (mQueue) {
                for (CvrAction e : mQueue) {
                    if (command.code() == e.code()) {
                        return;
                    }
                }
                mQueue.add(command);
            }
        }

        private void queueClear() {
            synchronized (mQueue) {
                mQueue.clear();
            }
        }
        @Override
        public void run() {
            super.run();

            LogUtil.i(TAG, "workThread run ");
            msg = ByteBuffer.allocate(CvrConstants.MsgSize);
            msg.order(ByteOrder.LITTLE_ENDIAN);

            for (;;) {
                if (isStop) {
                    break;
                }

                msg.clear();
                int msgLen = connection.transferIn(object, msg.array(), CvrConstants.MsgHeaderLen);
                if (CvrConstants.MsgHeaderLen > msgLen) {
                    LogUtil.i(TAG, "bulkin err " + String.valueOf(msgLen));
                    mListener.onCvrError("bulkin err " + String.valueOf(msgLen));
                    isStop = true;
                    continue;
                }

                int payLoadLen = msg.getInt(CvrConstants.MsgPayLoadLenOffset);
                Short code = msg.getShort(CvrConstants.MsgCodeOffset);
                CvrAction action = null;
                synchronized (mQueue) {
                    for (CvrAction e : mQueue) {
                        if (code == e.code()) {
                            action = e;
                            action.resp(this, msg, payLoadLen);
                            break;
                        }
                    }

                    if ((null != action ) && (action.complete())) {
                        mQueue.remove(action);
                        LogUtil.i(TAG, "e.code complete " + String.valueOf(action.code()));
                    }
                }
            }
            LogUtil.i(TAG, "usb receive thread exit");
        }

        @Override
        public int handleResponse(Command command, ByteBuffer resp, int datalen) {
            resp.position(0);
            if (datalen > 0) {
                ByteBuffer payLoadData = ByteBuffer.allocate(datalen);
                payLoadData.order(ByteOrder.LITTLE_ENDIAN);
                int res = connection.transferIn(object, payLoadData.array(), datalen, 0);
                if (res < datalen) {
                    //Todo
                    mListener.onCvrError("bulkin err " + String.valueOf(res));
                    return 0;
                }
                payLoadData.position(0);
                command.receiveRead(resp, payLoadData, datalen);
            } else {
                command.receiveRead(resp);
            }
            return 0;
        }
    }
}
