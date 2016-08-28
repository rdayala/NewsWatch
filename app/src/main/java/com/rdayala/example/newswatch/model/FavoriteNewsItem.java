package com.rdayala.example.newswatch.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rdayala on 8/22/2016.
 */
public class FavoriteNewsItem extends RealmObject {

    private String mtitle;

    @PrimaryKey
    private String mlink;
    private String mdescription;
    private String mpubDate;
    private RealmList<NewsItemTag> mTags;

    public FavoriteNewsItem() {

    }

    public FavoriteNewsItem(String title, String link, String description, String pubDate, RealmList<NewsItemTag> tags) {
        this.mtitle = title;
        this.mlink = link;
        this.mdescription = description;
        this.mpubDate = pubDate;
        this.mTags = tags;
    }

    public String getMtitle() {
        return mtitle;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public String getMlink() {
        return mlink;
    }

    public void setMlink(String mlink) {
        this.mlink = mlink;
    }

    public String getMdescription() {
        return mdescription;
    }

    public void setMdescription(String mdescription) {
        this.mdescription = mdescription;
    }

    public String getMpubDate() {
        return mpubDate;
    }

    public void setMpubDate(String mpubDate) {
        this.mpubDate = mpubDate;
    }

    public RealmList<NewsItemTag> getmTags() {
        return mTags;
    }

    public void setmTags(RealmList<NewsItemTag> mTags) {
        this.mTags = mTags;
    }
}
