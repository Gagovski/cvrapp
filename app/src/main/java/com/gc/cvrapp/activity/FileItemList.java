package com.gc.cvrapp.activity;

import java.io.Serializable;
import java.util.List;

/**
 * class for file item list
 */
public class FileItemList implements Serializable {
    private List<String> filelist;
    private String curFile;

    /**
     * get file list
     * @return file list
     */
    public List<String> getFilelist() {
        return filelist;
    }


    /**
     * set file list
     * @param filelist file list
     */
    public void setFilelist(List<String> filelist) {
        this.filelist = filelist;
    }

    /**
     * get current file item
     * @return current file item name
     */
    public String getCurFile() {
        return this.curFile;
    }

    /**
     * get current file item position
     * @return current file item position
     */
    public int getCurPosition() {
        int pos = 0;
        for (String filename : filelist) {
            if (filename.contentEquals(curFile)) {
                return pos;
            }
            pos ++;
        }
        return 0;
    }

    /**
     * get specify file item name
     * @param pos specify file item position
     * @return specify file item name
     */
    public String getFile(int pos) {
        return filelist.get(pos);
    }

    /**
     * set specify file item name
     * @param curFile current file item position
     */
    public void setCurFile(String curFile) {
        this.curFile = curFile;
    }

    /**
     * get file list item count
     * @return total file items count
     */
    public int getCount() {
        return filelist.size();
    }
}
