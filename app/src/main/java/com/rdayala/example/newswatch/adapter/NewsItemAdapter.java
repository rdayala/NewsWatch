package com.rdayala.example.newswatch.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by rdayala on 8/4/2016.
 */
public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemAdapter.MyViewHolder> {

    private Context mContext;
    private List<FavoriteNewsItem> mData;
    private LayoutInflater mInflater;
    private String mDefaultTag;

    public NewsItemAdapter(Context context, List<FavoriteNewsItem> data) {

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
        FavoriteNewsItem item = mData.get(position);
        holder.setData(item, position);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    public void setDefaultTag(String defaultTag) {
        mDefaultTag = defaultTag;
    }

    public String getmDefaultTag() {
        return mDefaultTag;
    }

    public void setFilter(List<FavoriteNewsItem> feedItems) {
        mData = new ArrayList<>();
        mData.addAll(feedItems);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        CardView cardView;
        TextView title, pubDate, description;
        protected FavoriteNewsItem rssFeed;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.news_title);
            pubDate = (TextView) itemView.findViewById(R.id.pubdate);
            description = (TextView) itemView.findViewById(R.id.description);

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
                    webIntent.putExtra("defaultTag", rssFeed.getmCategory());
                    webIntent.putExtra("feedItem", rssFeed);
                    v.getContext().startActivity(webIntent);
                }
            });

            itemView.setOnCreateContextMenuListener(this);

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem myAddAction, myRemoveAction, myTagAction;
            menu.setHeaderTitle("Select The Action");

            if (!rssFeed.isAddedFavorite()) {
                myAddAction = menu.add("Bookmark");
                myAddAction.setOnMenuItemClickListener(this);
            } else {
                myRemoveAction = menu.add("Remove Bookmark");
                myRemoveAction.setOnMenuItemClickListener(this);
            }

            myTagAction = menu.add("Tag & Save");
            myTagAction.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            // Menu Item Clicked!
            if (item.getTitle().equals("Bookmark")) {
                // TO DO
                // save rssFeed data member to Realm DB
                rssFeed.setAddedFavorite(true);
                if (rssFeed.getmTags() == null) {
                    rssFeed.setmTags(mDefaultTag);
                }
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                FavoriteNewsItem favs = realm.copyToRealmOrUpdate(rssFeed);
                realm.commitTransaction();
                realm.close();

                Toast.makeText(mContext, "Added to Bookmarks!!", Toast.LENGTH_SHORT).show();
                cardView.setCardBackgroundColor(Color.parseColor("#e7fad8"));

            } else if (item.getTitle().equals("Remove Bookmark")) {

                // save rssFeed data member to Realm DB
                rssFeed.setAddedFavorite(false);

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(rssFeed);
                realm.commitTransaction();
                realm.close();

                Toast.makeText(mContext, "Removed from Bookmarks!!", Toast.LENGTH_SHORT).show();
                cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
            } else if (item.getTitle().equals("Tag & Save")) {

                final String oldTagsString;

                if (TextUtils.isEmpty(rssFeed.getmTags())) {
                    oldTagsString = rssFeed.getmCategory();
                } else {
                    oldTagsString = rssFeed.getmTags().toString().trim();
                }

                AlertDialog.Builder alertDialog;
                final EditText et_Tags;
                int item_position;

                alertDialog = new AlertDialog.Builder(mContext);
                View view = mInflater.inflate(R.layout.dialog_layout, null);
                alertDialog.setView(view);

                alertDialog.setTitle("Tags");
                et_Tags = (EditText) view.findViewById(R.id.et_tags);
                et_Tags.setText(oldTagsString);

                alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newTagsString;
                        if(TextUtils.isEmpty(et_Tags.getText())) {
                            newTagsString = rssFeed.getmCategory();
                        } else {
                            newTagsString = et_Tags.getText().toString().trim();
                        }

                        rssFeed.setmTags(newTagsString);
                        rssFeed.setAddedFavorite(true);

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(rssFeed);
                        realm.commitTransaction();
                        realm.close();

                        Toast.makeText(mContext, "Added tags and saved to Bookmarks!!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        cardView.setCardBackgroundColor(Color.parseColor("#e7fad8"));

                    }
            });
            alertDialog.show();
        }

        return true;
    }

    public void setData(FavoriteNewsItem feedItem, int position) {
        this.title.setText(feedItem.getMtitle());
        this.pubDate.setText(feedItem.getMpubDate());
        this.description.setText(feedItem.getMdescription());
        this.rssFeed = feedItem;
        if (feedItem.isAddedFavorite()) {
            cardView.setCardBackgroundColor(Color.parseColor("#e7fad8"));
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }
}
}
