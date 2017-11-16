package com.gc.cvrapp.activity;

import com.gc.cvrapp.cvr.Cvr;

import java.util.List;

/** the interface for fragment callback */
public interface FragmentCallback {

    /**
     * set cvr device object
     * @param cvr the cvr object
     */
    void setCvr(Cvr cvr);

    /**
     * set video and photo list
     * @param videolist the video file name list
     * @param photolist the photo file name list
     */
    void setList(List<String> videolist, List<String> photolist);
}
