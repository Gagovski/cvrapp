package com.gc.cvrapp.media.photo;


import android.graphics.Bitmap;

import java.util.concurrent.ConcurrentHashMap;

/** class for photo cache */
public class PicCachePool {
    public static final int PicCacheSize = 3;

    protected final ConcurrentHashMap<String, Bitmap> linkedHashMap = new ConcurrentHashMap<String, Bitmap>();

    /**
     * whether photo cache is empty or not empty
     * @return true or false
     */
    public boolean isEmpty() {
        return linkedHashMap.isEmpty();
    }

    /**
     * put bitmap into cache pool
     * @param picname photo name
     * @param bitmap  bitmap data
     */
    public void put(String picname, Bitmap bitmap) {
        linkedHashMap.put(picname, (Bitmap) bitmap);
    }

    /**
     * get bitmap
     * @param picname photo name
     * @return bitmap
     */
    public Bitmap get(String picname) {
        return (Bitmap)linkedHashMap.get(picname);
    }

    /**
     * clear cache pool
     */
    public void clear() {
        linkedHashMap.clear();
    }

    /**
     * get cache size
     * @return cache size
     */
    public int size() {
        return linkedHashMap.size();
    }
}