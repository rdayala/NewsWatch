package com.rdayala.example.newswatch.utils;

import android.text.Html;
import android.util.Log;

import com.rdayala.example.newswatch.model.FavoriteNewsItem;
import com.rdayala.example.newswatch.model.FeedItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by rdayala on 8/28/2016.
 */
public class Feed2DBConversion {

    public static FavoriteNewsItem convertFeedToDBModelObject(FeedItem rssFeed, String category) {

        String title = rssFeed.getMtitle().trim();
        String description = rssFeed.getMdescription().trim();
        String pubDate = null;
        if(rssFeed.getMpubDate() != null) {
            pubDate = getPubDateString(rssFeed.getMpubDate().trim());
        }

        FavoriteNewsItem favItem = new FavoriteNewsItem();
        favItem.setMtitle(Html.fromHtml(title).toString().trim());
        favItem.setMdescription(Html.fromHtml(description).toString().trim());
        favItem.setMpubDate(pubDate);
        favItem.setMlink(rssFeed.getMlink());
        // favItem.setmTags(tags);

        // backend fields, not be shown to user
        favItem.setmCategory(category);
        if(rssFeed.getMpubDate() != null) {
            if(category.equals("PIBNews")) {
                favItem.setmDateAdded(getDateFromPubDatePIB(rssFeed.getMpubDate().trim()));
            } else {
                favItem.setmDateAdded(getDateFromPubDate(rssFeed.getMpubDate().trim()));
            }
        }
        else {
            favItem.setmDateAdded(new Date());
        }
        // favItem.setAddedFavorite(isFav);

        return favItem;
    }

    public static String getPubDateString(String oldPubDateStr) {

        String dateStr = oldPubDateStr;
        SimpleDateFormat f = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        SimpleDateFormat f2 = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh:mm:ss a");
        try {
            f2.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
            Date localDate = f.parse(dateStr);
            dateStr = f2.format(localDate);

        }catch(ParseException e) {
            return oldPubDateStr;
        }

        return dateStr;
    }

    public static Date getDateFromPubDate(String oldPubDateStr) {

        Date dateObj = null;
        SimpleDateFormat f = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        try {
            dateObj = f.parse(oldPubDateStr);
        }catch(ParseException e) {
        }

        return dateObj;
    }

    public static Date getDateFromPubDatePIB(String oldPubDateStr) {
        Date dateObj = null;
        String trimmedString = oldPubDateStr.substring(0, 16);
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm"); // 31/08/2016 19:26 IST
        try {
            dateObj = f.parse(trimmedString);
        }catch(ParseException e) {
            Log.e("DataParseError", e.getMessage());
        }

        return dateObj;
    }
}
