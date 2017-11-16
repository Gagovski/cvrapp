package com.gc.cvrapp.utils;

import org.jcodec.codecs.h264.H264Utils;

import java.nio.ByteBuffer;

/** class for h264 decode util */
public class H264DecodeUtil {
    public static final int TYPE_SPS = 0;
    public static final int TYPE_PPS = 1;
    private static final String TAG = "H264DecodeUtil";

    /**
     * the frame is key frame
     * @return key frame or not
     */
    public static boolean isIFrame(ByteBuffer data) {
        return H264Utils.iFrame(data);
    }

    /**
     * get sps buffer
     * @param in input h264 seq header buffer
     * @return the sps buffer
     */
    public static byte[] getSps(ByteBuffer in) {
        byte[] seq = in.array();
        byte[] sps = null;
        int firstStartcodeIndex     = 0;

        for (int i = 0; i < seq.length; i ++) {
            if ((0x00 == seq[i + 0]) && (0x00 == seq[i + 1]) && (0x00 == seq[i + 2]) && (0x01 == seq[i + 3])) {
                /* sps */
                if (0x07 == (seq[i + 4] & 0x1f)) {
                    firstStartcodeIndex = i;
                    break;
                }
            }
        }

        int firstNaluSize = 0;
        for (int i = firstStartcodeIndex + 4; i < firstStartcodeIndex + seq.length - 4; i ++) {
            if ((0x00 == seq[i + 0]) && (0x00 == seq[i + 1]) && (0x00 == seq[i + 2]) && (0x01 == seq[i + 3])) {
                if (firstNaluSize == 0)
                {
                    firstNaluSize = i - firstStartcodeIndex;
                }

                /* pps */
                if (0x08 == (seq[i + 4] & 0x1f)) {
                    break;
                }
            }
        }

        if (firstNaluSize == 0) {
            return null;
        }
        sps = new byte[firstNaluSize];
        System.arraycopy(seq, firstStartcodeIndex, sps, 0, firstNaluSize);

        return sps;
    }

    /**
     * get pps buffer
     * @param in input h264 seq header buffer
     * @return the pps buffer
     */
    public static byte[] getPps(ByteBuffer in) {
        byte[] pps = null;
        byte[] seq = in.array();
        int firstStartcodeIndex     = 0;
        int secondStartcodeIndex    = 0;

        for (int i = 0; i < seq.length; i ++) {
            if ((0x00 == seq[i + 0]) && (0x00 == seq[i + 1]) && (0x00 == seq[i + 2]) && (0x01 == seq[i + 3])) {
                /* sps */
                if (0x07 == (seq[i + 4] & 0x1f)) {
                    firstStartcodeIndex = i;
                    break;
                }
            }
        }

        int firstNaluSize = 0;
        for (int i = firstStartcodeIndex + 4; i < firstStartcodeIndex + seq.length - 4; i ++) {
            if ((0x00 == seq[i + 0]) && (0x00 == seq[i + 1]) && (0x00 == seq[i + 2]) && (0x01 == seq[i + 3])) {
                if (firstNaluSize == 0)
                {
                    firstNaluSize = i - firstStartcodeIndex;
                }

                /* pps */
                if (0x08 == (seq[i + 4] & 0x1f)) {
                    secondStartcodeIndex = i;
                    break;
                }
            }
        }

        int secondNaluSize = seq.length - firstNaluSize;
        if (secondNaluSize == 0) {
            return null;
        }

        pps = new byte[secondNaluSize];
        System.arraycopy(seq, secondStartcodeIndex, pps, 0, secondNaluSize);

        return pps;
    }

    /**
     * get nal unit header buffer
     * @param in input h264 seq header buffer
     * @param type TYPE_SPS, TYPE_PPS
     * @return the sps or pps
     */
    public static byte[] getNalu(ByteBuffer in, int type) {
        byte[] nalu = null;
        byte[] seq  = in.array();
        int firstStartcodeIndex     = 0;
        int secondStartcodeIndex    = 0;

        for (int i = 0; i < seq.length; i ++) {
            if ((0x00 == seq[i + 0]) && (0x00 == seq[i + 1]) && (0x00 == seq[i + 2]) && (0x01 == seq[i + 3])) {
                /* sps */
                if (0x07 == (seq[i + 4] & 0x1f)) {
                    firstStartcodeIndex = i;
                    break;
                }
             }
        }

        int firstNaluSize = 0;
        for (int i = firstStartcodeIndex + 4; i < firstStartcodeIndex + seq.length - 4; i ++) {
            if ((0x00 == seq[i + 0]) && (0x00 == seq[i + 1]) && (0x00 == seq[i + 2]) && (0x01 == seq[i + 3])) {
                if (firstNaluSize == 0)
                {
                    firstNaluSize = i - firstStartcodeIndex;
                }

                /* pps */
                if (0x08 == (seq[i + 4] & 0x1f)) {
                    secondStartcodeIndex = i;
                    break;
                }
            }
        }

        int secondNaluSize = seq.length - firstNaluSize;
        if ((0 == firstNaluSize) || (0 == secondNaluSize)) {
            return null;
        }

        switch (type) {
            case TYPE_SPS:
                nalu = new byte[firstNaluSize];
                System.arraycopy(seq, firstStartcodeIndex, nalu, 0, firstNaluSize);
                break;

            case TYPE_PPS:
                nalu = new byte[secondNaluSize];
                System.arraycopy(seq, secondStartcodeIndex, nalu, 0, secondNaluSize);
                break;

            default:
                break;
        }

//        PacketUtil.logHexdump(TAG, sps, firstNaluSize);
//        PacketUtil.logHexdump(TAG, pps, secondNaluSize);
        return nalu;
    }
}
