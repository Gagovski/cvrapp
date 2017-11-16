package com.gc.cvrapp.cvr;

/**
 * class for cvr constants
 */
public class CvrConstants {
    /**
     * class for cvr usb constants
     */
    public static class UsbConstants {
        /** UsbConstants: usb device vendor id */
        public static final int VendorId                = 0x601a;

        /** UsbConstants: usb device product id */
        public static final int ProductId               = 0x0030;

        /** UsbConstants: usb protocol code */
        public static final int Protocol                = 0xff;

        /** UsbConstants: usb class code */
        public static final int Class                   = 0xff;

        /** UsbConstants: usb subclass code */
        public static final int SubClass                = 0xff;

        /** UsbConstants: usb transfer direction in */
        public static final int USB_DIR_IN              = 0x80;

        /** UsbConstants: usb transfer direction out */
        public static final int USB_DIR_OUT             = 0x00;
    }

    /**
     * class for cvr net constants
     */
    public static class NetConstants {
        /** UsbConstants: net socket count */
        public static final int SockCnt                 = 2;

        /** UsbConstants: net socket min port */
        public static final int SockPortMin             = 9001;

        /** UsbConstants: net socket max port */
        public static final int SockPortMax             = 9002;

        /** UsbConstants: net socket packet size */
        public static final int SockPacketSize          = 512;

        /** UsbConstants: net socket transfer out */
        public static final int SockDirOut              = 0;

        /** UsbConstants: net socket transfer in */
        public static final int SockDirIn               = 1;
    }

    /** transfer message size */
    public static final int MsgSize                 = 64;

    /** transfer message header len */
    public static final int MsgHeaderLen            = 16;

    public static final int MsgPadLen               = 48;

    /** transfer message payload offset */
    public static final int MsgPayLoadLenOffset     = 12;

    /** transfer message code offset */
    public static final int MsgCodeOffset           = 2;

    /** class for transfer message type */
    public static class MsgType {
        /** transfer message command type */
        public static final short MsgCommand        = 0x0001;

        /** transfer message response type */
        public static final short MsgResponse       = 0x0002;

        /** transfer message data type */
        public static final short MsgData           = 0x0003;
    }

    /** class for transfer message command code */
    public static class CommandCode {
        /** transfer message command StartPreview */
        public static final short StartPreview          = 0x0001;

        /** transfer message command StopPreview */
        public static final short StopPreview           = 0x0002;

        /** transfer message command StartRecord */
        public static final short StartRecord           = 0x0003;

        /** transfer message command StopRecord */
        public static final short StopRecord            = 0x0004;

        /** transfer message command CapturePic */
        public static final short CapturePic            = 0x0005;

        /** transfer message command PlaybackVideo */
        public static final short PlaybackVideo         = 0x0006;

        /** transfer message command PlaybackVideoPause */
        public static final short PlaybackVideoPause    = 0x0007;

        /** transfer message command PlaybackVideoSeek */
        public static final short PlaybackVideoSeek     = 0x0008;

        /** transfer message command PlaybackVideoStop */
        public static final short PlaybackVideoStop     = 0x0009;

        /** transfer message command PlaybackPic */
        public static final short PlaybackPic           = 0x000a;

        /** transfer message command GetFileList */
        public static final short GetFileList           = 0x000b;

        /** transfer message command FormatSD */
        public static final short FormatSD              = 0x000c;

        /** transfer message command Setting */
        public static final short Setting               = 0x000d;

        /** transfer message command DeleteFile */
        public static final short DeleteFile            = 0x000e;

        /** transfer message command LockFile */
        public static final short LockFile              = 0x000f;

        /** transfer message command UnLockFile */
        public static final short UnLockFile            = 0x0010;

        /** transfer message command ConnectCheck */
        public static final short ConnectCheck          = 0x0011;
    }

    /** class for transfer message response code */
    public static class ResponseCode extends CommandCode{}

    /** class for transfer message response ret code */
    public static class ResponseRetCode {
        /** transfer message response ret ok */
        public static final int RespOk          = 0;

        /** transfer message response ret timeout */
        public static final int RespTimeout     = 1;

        /** transfer message response ret sd card error */
        public static final int RespSdError     = 2;

        /** transfer message response ret unknown error */
        public static final int RespUnknowError = 3;
    }

    /** class for transfer sample code */
    public static class SampleCode {
        /** sample type info */
        public static final int SampleInfo  = 0;

        /** sample type video */
        public static final int SampleVideo = 1;

        /** sample type audio */
        public static final int SampleAudio = 2;

        /** sample type end */
        public static final int SampleEnd   = 3;
    }
}
