package com.rdayala.example.newswatch.model;

import io.realm.RealmObject;

/**
 * Created by rdayala on 8/22/2016.
 */
public class NewsItemTag extends RealmObject {

    private String mTag;

    public NewsItemTag() {

    }

    public NewsItemTag(String tag) {
        this.mTag = tag;
    }

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }
}
