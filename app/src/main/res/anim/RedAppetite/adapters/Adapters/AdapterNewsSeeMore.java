package com.vadevelopment.RedAppetite.adapters.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;
import com.vadevelopment.RedAppetite.newssection.NewsDetail_Fragment;
import com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside;

import java.util.ArrayList;

/**
 * Created by vibrantappz on 3/23/2017.
 */

public class AdapterNewsSeeMore extends RecyclerView.Adapter<com.vadevelopment.RedAppetite.adapters.Adapters.AdapterNewsSeeMore.ViewHolder> {

    private Context context;
    private android.app.FragmentManager fm;
    private DashboardActivity mdashboard;
    private ArrayList<NewsSkeletonInside> arrayList;

    public AdapterNewsSeeMore(Context context, android.app.FragmentManager fm, ArrayList<NewsSkeletonInside> arrayList) {
        this.context = context;
        this.fm = fm;
        this.arrayList = arrayList;
        mdashboard = (DashboardActivity) context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public com.vadevelopment.RedAppetite.adapters.Adapters.AdapterNewsSeeMore.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rownews_seemore, null);
        com.vadevelopment.RedAppetite.adapters.Adapters.AdapterNewsSeeMore.ViewHolder viewHolder = new com.vadevelopment.RedAppetite.adapters.Adapters.AdapterNewsSeeMore.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final com.vadevelopment.RedAppetite.adapters.Adapters.AdapterNewsSeeMore.ViewHolder viewHolder, final int position) {

        viewHolder.date.setText(arrayList.get(position).getDate());
        viewHolder.headline.setText(arrayList.get(position).getHeadline());
        Glide.with(context).load(arrayList.get(position).getImage()).placeholder(R.drawable.news_cameraicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(viewHolder.imageview);
        viewHolder.ll_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDetail_Fragment newsdetail_frgm = new NewsDetail_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString(Consts.NEWS_CLICKNID, arrayList.get(position).getNid());
                bundle.putString(Consts.ONNEWSDETAIL_FROM, "list_inside");
                newsdetail_frgm.setArguments(bundle);
                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(newsdetail_frgm);
            }
        });
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_container;
        private TextView date, headline;
        private ImageView imageview;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            date = (TextView) itemLayoutView.findViewById(R.id.date);
            headline = (TextView) itemLayoutView.findViewById(R.id.headline);
            ll_container = (LinearLayout) itemLayoutView.findViewById(R.id.ll_container);
            imageview = (ImageView) itemLayoutView.findViewById(R.id.imageview);
        }
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}


