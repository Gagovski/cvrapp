package com.gc.cvrapp.activity;

/**
 * class for activity message handling the message code
 */
public class MsgCode {

    /** MsgCode: cvr attach the tablet or mobile */
    public static final int MsgCvrAttach        = 1;

    /** MsgCode: cvr detach the tablet or mobile */
    public static final int MsgCvrDetach        = 2;

    /** MsgCode: cvr not found any cvr device */
    public static final int MsgNoCvrFound       = 3;

    /** MsgCode: cvr lock the file */
    public static final int MsgLock             = 4;

    /** MsgCode: cvr unlock the file */
    public static final int MsgUnLock           = 5;

    /** MsgCode: cvr delete the file */
    public static final int MsgDeleteFile       = 6;

    /** MsgCode: cvr update the file list */
    public static final int MsgUpdateFileList   = 7;

    /** MsgCode: cvr capture a picture */
    public static final int MsgPicture          = 8;

    /** MsgCode: cvr start preview or playback */
    public static final int MsgStart            = 9;

    /** MsgCode: cvr restart playback */
    public static final int MsgRestart          = 10;

    /** MsgCode: cvr stop preview or playback */
    public static final int MsgStop             = 11;

    /** MsgCode: cvr pause playback */
    public static final int MsgPause            = 12;

    /** MsgCode: cvr continue playback */
    public static final int MsgContinue         = 13;

    /** MsgCode: cvr playback next or previous video */
    public static final int MsgPrevNext         = 14;

    /** MsgCode: cvr playback video sample step progressing */
    public static final int MsgStep             = 15;

    /** MsgCode: cvr playback video seek */
    public static final int MsgSeek             = 16;

    /** MsgCode: cvr start recording trip video */
    public static final int MsgStartRecord      = 17;

    /** MsgCode: cvr stop recording trip video */
    public static final int MsgStopRecord       = 18;

    /** MsgCode: cvr is recording trip video now */
    public static final int MsgRecording        = 19;

    /** MsgCode: cvr location */
    public static final int MsgLocation         = 20;

    /** MsgCode: cvr somethings wrong happen */
    public static final int MsgError            = 21;
}
