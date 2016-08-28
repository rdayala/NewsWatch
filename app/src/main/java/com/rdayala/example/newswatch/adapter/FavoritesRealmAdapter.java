package com.rdayala.example.newswatch.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.rdayala.example.newswatch.ContentActivity;
import com.rdayala.example.newswatch.R;
import com.rdayala.example.newswatch.WebViewActivity;
import com.rdayala.example.newswatch.model.FavoriteNewsItem;
import com.rdayala.example.newswatch.model.NewsItemTag;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by rdayala on 8/22/2016.
 */

public class FavoritesRealmAdapter extends RecyclerView.Adapter<FavoritesRealmAdapter.MyFavoritesViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private Realm mRealm;
    private RealmList<FavoriteNewsItem> mData;
    private RealmList<FavoriteNewsItem> mFavoritesData;


    public FavoritesRealmAdapter(Context context, Realm realm, RealmList<FavoriteNewsItem> data) {

        this.mContext = context;
        this.mData = data;
        this.mFavoritesData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyFavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.favorite_item_layout, parent, false);
        MyFavoritesViewHolder holder = new MyFavoritesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyFavoritesViewHolder holder, int position) {
        Log.d("NewsItemAdapter : ", "onBindViewHolder " + position);
        FavoriteNewsItem item = mData.get(position);
        holder.setData(item, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setFilter(RealmList<FavoriteNewsItem> feedItems) {
        mData = feedItems;
        notifyDataSetChanged();
    }

    class MyFavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        TextView title, pubDate, description;
        TagView tagGroup;
        protected FavoriteNewsItem favoriteNewsItem;

        public MyFavoritesViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.fav_item_news_title);
            pubDate = (TextView) itemView.findViewById(R.id.fav_item_pubdate);
            description = (TextView) itemView.findViewById(R.id.fav_item_description);
            tagGroup = (TagView) itemView.findViewById(R.id.fav_item_tag_group);

            //set click listener
            tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
                @Override
                public void onTagClick(Tag tag, int position) {

                    RealmList<FavoriteNewsItem> filteredList =
                            ((ContentActivity)mContext).favoritesTagFilter(mFavoritesData, favoriteNewsItem.getmTags().get(position).getmTag());

                    SpannableString s = new SpannableString("Favorites : " + favoriteNewsItem.getmTags().get(position).getmTag());
                    s.setSpan(new TypefaceSpan("fonts/Knowledge-Bold.ttf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((ContentActivity)mContext).getSupportActionBar().setTitle(s);

                    mData = filteredList;
                    notifyDataSetChanged();
                    int numberOfArticles = filteredList.size();
                    if(numberOfArticles == 1) {
                        Toast.makeText(mContext, "Found " + numberOfArticles + " article.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(mContext, "Found " + numberOfArticles + " articles.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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

                    webIntent.putExtra("title", favoriteNewsItem.getMtitle().toString());

                    webIntent.putExtra("url", favoriteNewsItem.getMlink().toString());

                    v.getContext().startActivity(webIntent);
                }
            });

            itemView.setOnCreateContextMenuListener(this);

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Select The Action");
            MenuItem myActionItem = menu.add("Remove Bookmark");
            myActionItem.setOnMenuItemClickListener(this);
            MenuItem myTagActionItem = menu.add("Tags");
            myTagActionItem.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            // Menu Item Clicked!
            if (item.getTitle().equals("Remove Bookmark")) {

                // save rssFeed data member to Realm DB
                final FavoriteNewsItem favItem = new FavoriteNewsItem();
                favItem.setMtitle(favoriteNewsItem.getMtitle());
                favItem.setMlink(favoriteNewsItem.getMlink());
                favItem.setMdescription(favoriteNewsItem.getMdescription());
                favItem.setMpubDate(favoriteNewsItem.getMpubDate());
                favItem.setmTags(null);

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<FavoriteNewsItem> result = realm.where(FavoriteNewsItem.class).equalTo("mlink", favItem.getMlink()).findAll();
                        result.deleteAllFromRealm();
                    }
                });
                realm.close();

                mData.remove(getAdapterPosition());
                notifyDataSetChanged();

            }
            else if (item.getTitle().equals("Tags")) {

                RealmList<NewsItemTag> oldTags = favoriteNewsItem.getmTags();
                StringBuilder oldItemTags = new StringBuilder();
                for(NewsItemTag itemTag : oldTags) {
                    oldItemTags.append(itemTag.getmTag());
                    oldItemTags.append(" ");
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

                            notifyDataSetChanged();
                            dialog.dismiss();

                        }
                    }
                });

                alertDialog.show();
            }
            return true;
        }

        public void setData(final FavoriteNewsItem favoriteItem, int position) {
            this.title.setText(favoriteItem.getMtitle());
            this.pubDate.setText(favoriteItem.getMpubDate());
            this.description.setText(favoriteItem.getMdescription());

            RealmList<NewsItemTag> oldTags = favoriteItem.getmTags();
            StringBuilder oldItemTags = new StringBuilder();
            for(NewsItemTag itemTag : oldTags) {
                oldItemTags.append(itemTag.getmTag());
                oldItemTags.append(" ");
            }

            final String oldTagsString = oldItemTags.toString().trim();
            String[] tagsArray = TextUtils.split(oldTagsString, " +");
            this.tagGroup.removeAll();
            this.tagGroup.addTags(tagsArray);

            this.favoriteNewsItem = favoriteItem;
        }
    }

}

