package com.gc.cvrapp.activity;

import java.io.Serializable;
import java.util.List;

public class FileItemList implements Serializable {
    private List<String> filelist;
    private String curFile;

    public List<String> getFilelist() {
        return filelist;
    }

    public void setFilelist(List<String> filelist) {
        this.filelist = filelist;
    }

    public String getCurFile() {
        return this.curFile;
    }

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

    public String getFile(int pos) {
        return filelist.get(pos);
    }

    public void setCurFile(String curFile) {
        this.curFile = curFile;
    }

    public int getCount() {
        return filelist.size();
    }
}
