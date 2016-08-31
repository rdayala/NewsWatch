package com.rdayala.example.newswatch.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rdayala on 8/22/2016.
 */
public class FavoriteNewsItem extends RealmObject implements Parcelable {

    private String mtitle;

    @PrimaryKey
    private String mlink;
    private String mdescription;
    private String mpubDate;
    private String mTags;
    private String mCategory;
    private Date mDateAdded;
    private boolean isAddedFavorite;

    public FavoriteNewsItem() {

    }

    private FavoriteNewsItem(Parcel in) {
        mtitle = in.readString();
        mlink = in.readString();
        mdescription = in.readString();
        mpubDate = in.readString();
        mTags = in.readString();
        mCategory = in.readString();
        mDateAdded = (java.util.Date)in.readSerializable();
        isAddedFavorite = in.readByte() != 0;
    }

    public FavoriteNewsItem(String title, String link, String description, String pubDate, String tags, String category, Date dateAdded, boolean isFav) {
        this.mtitle = title;
        this.mlink = link;
        this.mdescription = description;
        this.mpubDate = pubDate;
        this.mTags = tags;
        this.mCategory = category;
        this.mDateAdded = dateAdded;
        this.isAddedFavorite = isFav;
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

    public String getmTags() {
        return mTags;
    }

    public void setmTags(String mTags) {
        this.mTags = mTags;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public Date getmDateAdded() {
        return mDateAdded;
    }

    public void setmDateAdded(Date mDateAdded) {
        this.mDateAdded = mDateAdded;
    }

    public boolean isAddedFavorite() {
        return isAddedFavorite;
    }

    public void setAddedFavorite(boolean addedFavorite) {
        isAddedFavorite = addedFavorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mtitle);
        out.writeString(mlink);
        out.writeString(mdescription);
        out.writeString(mpubDate);
        out.writeString(mTags);
        out.writeString(mCategory);
        out.writeSerializable(mDateAdded);
        out.writeByte((byte) (isAddedFavorite ? 1 : 0));
    }

    public static final Parcelable.Creator<FavoriteNewsItem> CREATOR = new Parcelable.Creator<FavoriteNewsItem>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public FavoriteNewsItem createFromParcel(Parcel in) {
            return new FavoriteNewsItem(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public FavoriteNewsItem[] newArray(int size) {
            return new FavoriteNewsItem[size];
        }
    };
}
