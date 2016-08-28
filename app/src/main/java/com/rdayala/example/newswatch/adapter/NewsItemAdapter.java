package com.rdayala.example.newswatch.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rdayala.example.newswatch.R;
import com.rdayala.example.newswatch.WebViewActivity;
import com.rdayala.example.newswatch.model.FavoriteNewsItem;
import com.rdayala.example.newswatch.model.FeedItem;
import com.rdayala.example.newswatch.model.NewsItemTag;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by rdayala on 8/4/2016.
 */
public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemAdapter.MyViewHolder> {

    private Context mContext;
    private List<FeedItem> mData;
    private LayoutInflater mInflater;
    private String mFeedTag;

    public NewsItemAdapter(Context context, List<FeedItem> data) {

        this.mContext = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.news_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d("NewsItemAdapter : ", "onBindViewHolder " + position);
        FeedItem item = mData.get(position);
        holder.setData(item, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setFilter(List<FeedItem> feedItems) {
        mData = new ArrayList<>();
        mData.addAll(feedItems);
        notifyDataSetChanged();
    }

    public String getFeedTag() {
        return mFeedTag;
    }

    public void setFeedTag(String feedTag) {
        this.mFeedTag = feedTag;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        TextView title, pubDate, description;
        protected FeedItem rssFeed;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.news_title);
            pubDate = (TextView)itemView.findViewById(R.id.pubdate);
            description = (TextView)itemView.findViewById(R.id.description);

            // Style text views
            Typeface titleTypeFace = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Knowledge-Bold.ttf"); // JosefinSans-Bold.ttf
            title.setTextColor(Color.parseColor("#d84315"));
            title.setTypeface(titleTypeFace);
            Typeface descTypeFace = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/AndikaNewBasic-R.ttf"); // JosefinSans-SemiBoldItalic.ttf
            description.setTypeface(descTypeFace);
            Typeface pubDateTypeFace = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/JosefinSans-SemiBoldItalic.ttf");
            pubDate.setTypeface(pubDateTypeFace);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent webIntent = new Intent(v.getContext(), WebViewActivity.class);

                    webIntent.putExtra("title", rssFeed.getMtitle().toString());

                    webIntent.putExtra("url", rssFeed.getMlink().toString());

                    v.getContext().startActivity(webIntent);
                }
            });

            itemView.setOnCreateContextMenuListener(this);

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Select The Action");
            MenuItem myActionItem = menu.add("Bookmark");
            myActionItem.setOnMenuItemClickListener(this);
            MenuItem myTagActionItem = menu.add("Tag & Save");
            myTagActionItem.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            // Menu Item Clicked!
            if(item.getTitle().equals("Bookmark")) {
                // TO DO
                // save rssFeed data member to Realm DB

                RealmList<NewsItemTag> tags = new RealmList<>();
                tags.add(new NewsItemTag(mFeedTag));

                FavoriteNewsItem favItem = new FavoriteNewsItem();
                favItem.setMtitle(rssFeed.getMtitle());
                favItem.setMlink(rssFeed.getMlink());
                favItem.setMdescription(rssFeed.getMdescription());
                favItem.setMpubDate(rssFeed.getMpubDate());
                favItem.setmTags(tags);

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                FavoriteNewsItem favs = realm.copyToRealmOrUpdate(favItem);
                realm.commitTransaction();
                realm.close();

                Toast.makeText(mContext, "Added to Bookmarks!!", Toast.LENGTH_SHORT).show();

            }
            else if (item.getTitle().equals("Tag & Save")) {

                final FavoriteNewsItem favoriteNewsItem = new FavoriteNewsItem();

                favoriteNewsItem.setMtitle(rssFeed.getMtitle());
                favoriteNewsItem.setMlink(rssFeed.getMlink());
                favoriteNewsItem.setMdescription(rssFeed.getMdescription());
                favoriteNewsItem.setMpubDate(rssFeed.getMpubDate());
                favoriteNewsItem.setmTags(null);

                RealmList<NewsItemTag> oldTags = favoriteNewsItem.getmTags();
                StringBuilder oldItemTags = new StringBuilder();
                if(oldTags != null ) {
                    for (NewsItemTag itemTag : oldTags) {
                        oldItemTags.append(itemTag.getmTag());
                        oldItemTags.append(" ");
                    }
                }

                final String oldTagsString = oldItemTags.toString().trim();

                AlertDialog.Builder alertDialog;
                final EditText et_Tags;
                int item_position;

                alertDialog = new AlertDialog.Builder(mContext);
                View view = mInflater.inflate(R.layout.dialog_layout,null);
                alertDialog.setView(view);

                alertDialog.setTitle("Tags");
                et_Tags = (EditText)view.findViewById(R.id.et_tags);
                et_Tags.setText(oldItemTags.toString().trim());

                alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String newTagsString = et_Tags.getText().toString().trim();

                        if(!oldTagsString.equals(newTagsString)) {

                            String[] newTagsArray = TextUtils.split(newTagsString, " +");

                            RealmList<NewsItemTag> newTagsList = new RealmList<NewsItemTag>();
                            for(String newTag : newTagsArray) {
                                NewsItemTag newsItemTag = new NewsItemTag();
                                newsItemTag.setmTag(newTag);
                                newTagsList.add(newsItemTag);
                            }

                            FavoriteNewsItem favItem = new FavoriteNewsItem();
                            favItem.setMtitle(favoriteNewsItem.getMtitle());
                            favItem.setMlink(favoriteNewsItem.getMlink());
                            favItem.setMdescription(favoriteNewsItem.getMdescription());
                            favItem.setMpubDate(favoriteNewsItem.getMpubDate());
                            favItem.setmTags(newTagsList);

                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(favItem);
                            realm.commitTransaction();
                            realm.close();

                            Toast.makeText(mContext, "Added tags and saved to Bookmarks!!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                        }
                    }
                });
                alertDialog.show();
            }

            return true;
        }

        public void setData(FeedItem feedItem, int position) {
            this.title.setText(feedItem.getMtitle());
            this.pubDate.setText(feedItem.getMpubDate());
            this.description.setText(feedItem.getMdescription());
            this.rssFeed = feedItem;

        }

    }

}
