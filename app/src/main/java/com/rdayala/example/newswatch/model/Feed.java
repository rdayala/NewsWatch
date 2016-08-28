package com.rdayala.example.newswatch.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;


/**
 * Created by rdayala on 8/4/2016.
 */

@Root(name = "rss", strict = false)
public class Feed implements Serializable {
    @Element(name = "channel")
    private Channel mChannel;

    public Channel getmChannel() {
        return mChannel;
    }

    public Feed() {
    }

    public Feed(Channel mChannel) {
        this.mChannel = mChannel;
    }
}