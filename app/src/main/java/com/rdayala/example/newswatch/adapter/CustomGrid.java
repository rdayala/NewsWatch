package com.rdayala.example.newswatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rdayala.example.newswatch.R;
import com.rdayala.example.newswatch.model.NewsItemTag;

import io.realm.RealmList;

/**
 * Created by rdayala on 8/24/2016.
 */

public class CustomGrid extends BaseAdapter {
    private Context mContext;
    private final RealmList<NewsItemTag> itemTags;

    public CustomGrid(Context c, RealmList<NewsItemTag> tags ) {
        mContext = c;
        this.itemTags = tags;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return itemTags.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_single, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            textView.setText(itemTags.get(position).getmTag());
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}