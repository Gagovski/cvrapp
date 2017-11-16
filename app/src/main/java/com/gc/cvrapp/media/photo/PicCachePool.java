package com.gc.cvrapp.media.photo;


import android.graphics.Bitmap;

import java.util.concurrent.ConcurrentHashMap;

public class PicCachePool {
    public static final int PicCacheSize = 3;

    protected final ConcurrentHashMap<String, Bitmap> linkedHashMap = new ConcurrentHashMap<String, Bitmap>();

    public boolean isEmpty() {
        return linkedHashMap.isEmpty();
    }

    public void put(String picname, Bitmap bitmap) {
        linkedHashMap.put(picname, (Bitmap) bitmap);
    }

    public Bitmap get(String picname) {
        return (Bitmap)linkedHashMap.get(picname);
    }

    public void clear() {
        linkedHashMap.clear();
    }

    public int size() {
        return linkedHashMap.size();
    }
}