package com.vadevelopment.RedAppetite.adapters.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.R;

import java.util.ArrayList;

/**
 * Created by vibrantappz on 9/19/2017.
 */

public class Adapter_Newsinside extends BaseAdapter {

    ArrayList<com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside> arrayList;
    Context context;
    LayoutInflater inflater = null;

    public Adapter_Newsinside(Context context, ArrayList<com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_newsinside, null);
            holder = new ViewHolder();
            holder.news = (TextView) convertView.findViewById(R.id.news);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.news.setText(arrayList.get(i).getHeadline());
        return convertView;
    }

    private class ViewHolder {
        TextView news;
    }
}
