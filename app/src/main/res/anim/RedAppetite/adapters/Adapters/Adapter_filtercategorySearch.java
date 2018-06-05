package com.vadevelopment.RedAppetite.adapters.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vadevelopment.RedAppetite.R;

import java.util.ArrayList;

/**
 * Created by vibrantappz on 8/10/2017.
 */

public class Adapter_filtercategorySearch extends RecyclerView.Adapter<com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_filtercategorySearch.ViewHolder> {

    Context context;
    ArrayList<com.vadevelopment.RedAppetite.searchsection.Category_Skeleton> arraylist;
    ArrayList<String> selected_catgid;
    ArrayList<String> only_catgid;
    ArrayList<Boolean> positionArray;
    public static ArrayList<String> checkbox_checkedcatgid;
    boolean check;

    public Adapter_filtercategorySearch(Context context, ArrayList<com.vadevelopment.RedAppetite.searchsection.Category_Skeleton> arraylist, ArrayList<String> selected_catgid, ArrayList<String> only_catgid, boolean check) {
        this.context = context;
        this.arraylist = arraylist;
        this.selected_catgid = selected_catgid;
        this.only_catgid = only_catgid;
        this.check = check;
        checkbox_checkedcatgid = new ArrayList<>();
        positionArray = new ArrayList<Boolean>(arraylist.size());

        if (selected_catgid.isEmpty()) {
            if (check == true) {
                for (int i = 0; i < arraylist.size(); i++) {
                    positionArray.add(false);
                }
            } else {
                for (int i = 0; i < arraylist.size(); i++) {
                    positionArray.add(true);
                }
                for (int i = 0; i < only_catgid.size(); i++) {
                    checkbox_checkedcatgid.add(only_catgid.get(i));
                }
            }
        } else {
            for (int i = 0; i < arraylist.size(); i++) {
                positionArray.add(false);
            }
            for (int i = 0; i < only_catgid.size(); i++) {
                checkbox_checkedcatgid.clear();
                for (int k = 0; k < selected_catgid.size(); k++) {
                    if (only_catgid.contains(selected_catgid.get(k))) {
                        int o = only_catgid.indexOf(selected_catgid.get(k));
                        positionArray.set(o, true);
                        checkbox_checkedcatgid.add(only_catgid.get(o));
                    }
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_filtercategorySearch.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, null);
        // create ViewHolder
        com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_filtercategorySearch.ViewHolder viewHolder = new com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_filtercategorySearch.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_filtercategorySearch.ViewHolder viewHolder, final int position) {
        viewHolder.name.setText(arraylist.get(position).getName());
        Glide.with(context).load(arraylist.get(position).getFree_image()).placeholder(R.drawable.clubicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(viewHolder.imageview);
        viewHolder.checkbox.setChecked(positionArray.get(position));
        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    checkbox_checkedcatgid.add(arraylist.get(position).getId());
                    positionArray.set(position, true);
                    if (isalltrue(positionArray) == true) {
                        com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.checkbox_selectall.setChecked(true);
                    } else {
                        com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.checkbox_selectall.setChecked(false);
                    }
                } else {
                    checkbox_checkedcatgid.remove(checkbox_checkedcatgid.indexOf(arraylist.get(position).getId()));
                    positionArray.set(position, false);
                    if (isalltrue(positionArray) == true) {
                        com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.checkbox_selectall.setChecked(true);
                    } else {
                        com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.checkbox_selectall.setChecked(false);
                    }

                }
            }
        });
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        private CheckBox checkbox;
        private ImageView imageview;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            name = (TextView) itemLayoutView.findViewById(R.id.name);
            checkbox = (CheckBox) itemLayoutView.findViewById(R.id.checkbox);
            imageview = (ImageView) itemLayoutView.findViewById(R.id.imageview);

        }
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    private boolean isalltrue(ArrayList<Boolean> arraylist) {
        for (Boolean b : arraylist) {
            if (b == false) {
                return false;
            }
        }
        return true;
    }
}

