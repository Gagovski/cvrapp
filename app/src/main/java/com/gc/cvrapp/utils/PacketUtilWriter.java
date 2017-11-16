package com.gc.cvrapp.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PacketUtilWriter {
    private static final String mPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() +
                    File.separator + "FromMac";

    public PacketUtilWriter() {
    }

    public void write(String filename, byte[] data, int datalen) {
        String file = mPath + File.separator + filename;
        try {
            FileOutputStream out = new FileOutputStream(file);
            try {
                out.write(data, 0, datalen);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
