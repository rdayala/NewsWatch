package com.rdayala.example.newswatch.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by rdayala on 8/4/2016.
 */

@Root(name = "item", strict = false)
public class FeedItem implements Parcelable {
    @Element(name = "title")
    private String mtitle;
    @Element(name = "link")
    private String mlink;
    @Element(name = "description", required = false)
    private String mdescription;
    @Element(name = "author", required = false)
    private String mauthor;
    // @Path("channel/item/pubdate")
    @Element(name = "pubDate", required = false)
    private String mpubDate;

    public FeedItem() {
    }

    private FeedItem(Parcel in) {
        mpubDate = in.readString();
        mtitle = in.readString();
        mlink = in.readString();
        mdescription = in.readString();
    }

    public FeedItem(String mdescription, String mlink, String mtitle, String mpubDate) {
        this.mdescription = mdescription;
        this.mlink = mlink;
        this.mtitle = mtitle;
        this.mpubDate = mpubDate;
    }

    public String getMpubDate() {
        return mpubDate;
    }

    public void setMpubDate(String mpubDate) {
        this.mpubDate = mpubDate;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mpubDate);
        out.writeString(mtitle);
        out.writeString(mlink);
        out.writeString(mdescription);
    }

    public static final Parcelable.Creator<FeedItem> CREATOR = new Parcelable.Creator<FeedItem>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public FeedItem createFromParcel(Parcel in) {
            return new FeedItem(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };
}
