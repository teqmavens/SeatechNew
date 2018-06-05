package com.vadevelopment.RedAppetite.adapters.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vadevelopment.RedAppetite.R;

/**
 * Created by vibrantappz on 5/24/2017.
 */

public class AdapterSearchFirstFragment extends RecyclerView.Adapter<com.vadevelopment.RedAppetite.adapters.Adapters.AdapterSearchFirstFragment.ViewHolder> {

    private Context context;

    public AdapterSearchFirstFragment(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_searchfirstfrgmt, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
