package com.vadevelopment.RedAppetite.adapters.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.login.CountrySkeleton;

import java.util.ArrayList;

/**
 * Created by vibrantappz on 7/27/2017.
 */

public class Adapter_NewCountry extends BaseAdapter {

    Context context;
    ArrayList<CountrySkeleton> arraylist;
    LayoutInflater inflater = null;

    public Adapter_NewCountry(Context context, ArrayList<CountrySkeleton> arraylist) {
        this.context = context;
        this.arraylist = arraylist;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arraylist.size();
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
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = mInflater.inflate(R.layout.row_country, null);
            holder = new ViewHolder();
            holder.country_name = (TextView) convertview.findViewById(R.id.country_name);
            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        holder.country_name.setText(arraylist.get(i).getName());
        return convertview;
    }

    class ViewHolder {
     TextView country_name;
    }
}
