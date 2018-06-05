package com.vadevelopment.RedAppetite.adapters.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.R;

import java.util.ArrayList;

/**
 * Created by vibrantappz on 9/19/2017.
 */

public class Adapter_Newsinsidenew extends RecyclerView.Adapter<com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_Newsinsidenew.ViewHolder> {

    Context context;
    ArrayList<com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside> arraylist;
    com.vadevelopment.RedAppetite.dashboard.DashboardActivity mdashboard;

    public Adapter_Newsinsidenew(Context context, ArrayList<com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside> arraylist) {
        this.context = context;
        this.arraylist = arraylist;
        mdashboard = (com.vadevelopment.RedAppetite.dashboard.DashboardActivity) context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_Newsinsidenew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_newsinside, null);
        // create ViewHolder
        com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_Newsinsidenew.ViewHolder viewHolder = new com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_Newsinsidenew.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_Newsinsidenew.ViewHolder viewHolder, final int position) {

        viewHolder.news.setText(arraylist.get(position).getDate() + "  " + arraylist.get(position).getHeadline());
        viewHolder.rl_containor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.vadevelopment.RedAppetite.newssection.NewsDetail_Fragment newsdetail_frgm = new com.vadevelopment.RedAppetite.newssection.NewsDetail_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString(com.vadevelopment.RedAppetite.Consts.NEWS_CLICKNID, arraylist.get(position).getNid());
                bundle.putString(com.vadevelopment.RedAppetite.Consts.ONNEWSDETAIL_FROM, "list_inside");
                newsdetail_frgm.setArguments(bundle);
                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(newsdetail_frgm);
            }
        });

    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView news;
        private RelativeLayout rl_containor;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            news = (TextView) itemLayoutView.findViewById(R.id.news);
            rl_containor = (RelativeLayout) itemLayoutView.findViewById(R.id.rl_containor);
        }
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (arraylist.size() > 5) {
            return 5;
        } else {
            return arraylist.size();
        }

    }
}

