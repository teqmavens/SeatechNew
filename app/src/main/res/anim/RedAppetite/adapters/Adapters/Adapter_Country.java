package com.vadevelopment.RedAppetite.adapters.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.R;

import java.util.ArrayList;

/**
 * Created by vibrantappz on 7/26/2017.
 */

public class Adapter_Country extends RecyclerView.Adapter<com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_Country.ViewHolder> {

    Context context;
    ArrayList<com.vadevelopment.RedAppetite.login.CountrySkeleton> arraylist;

    public Adapter_Country(Context context, ArrayList<com.vadevelopment.RedAppetite.login.CountrySkeleton> arraylist) {
        this.context = context;
        this.arraylist = arraylist;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_Country.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_country, null);
        // create ViewHolder
        com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_Country.ViewHolder viewHolder = new com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_Country.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_Country.ViewHolder viewHolder, int position) {
        viewHolder.ll_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  mdashboard.displayWithoutViewFragment(new DetailDltFrmFvrt_EdtRvw_Fragment());
            }
        });
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView country_name;
        private LinearLayout ll_container;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            country_name = (TextView) itemLayoutView.findViewById(R.id.country_name);
        }
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return arraylist.size();
    }
}

