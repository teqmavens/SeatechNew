package com.vadevelopment.RedAppetite.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.moresection.AdvertisementFragment;

/**
 * Created by vibrantappz on 10/31/2017.
 */

public class AdvertiseWithUs extends AppCompatActivity {

    public RelativeLayout searchrelativeLayout, textsearchimage, rl_headingwithcount;
    public LinearLayout searchdatatab, profiletab, moretab, newstab, advertisetab, lltool_top;
    public ImageView searchicon, profileicon, moreicon, newsfeedicon, adverticeicon, settingicon, mapicon;
    public TextView searchtext, profiletext, moretext, setting, centertextsearch, mapButton, newstext, search, adverticetext, text_headingcount, text_heading;
    public LinearLayout footer;
    public View btm_line;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertise_activity);
        searchrelativeLayout = (RelativeLayout) findViewById(R.id.searchtoolbar);
        rl_headingwithcount = (RelativeLayout) findViewById(R.id.rl_headingwithcount);
        searchrelativeLayout.setVisibility(View.VISIBLE);
        setting = (TextView) findViewById(R.id.setting);
        textsearchimage = (RelativeLayout) findViewById(R.id.textsearchcenter);
        mapButton = (TextView) findViewById(R.id.mapButton);
        centertextsearch = (TextView) findViewById(R.id.centertextsearch);
        searchdatatab = (LinearLayout) findViewById(R.id.searchdata);
        profiletab = (LinearLayout) findViewById(R.id.profiletab);
        moretab = (LinearLayout) findViewById(R.id.moretab);
        newstab = (LinearLayout) findViewById(R.id.newstab);
        lltool_top = (LinearLayout) findViewById(R.id.lltool_top);
        advertisetab = (LinearLayout) findViewById(R.id.advertisetab);
        profileicon = (ImageView) findViewById(R.id.profileicon);
        profiletext = (TextView) findViewById(R.id.profiletext);
        searchicon = (ImageView) findViewById(R.id.searchicon);
        searchtext = (TextView) findViewById(R.id.searchtext);
        moreicon = (ImageView) findViewById(R.id.moreicon);
        moretext = (TextView) findViewById(R.id.moretext);
        newsfeedicon = (ImageView) findViewById(R.id.newsfeedicon);
        adverticeicon = (ImageView) findViewById(R.id.adverticeicon);
        newstext = (TextView) findViewById(R.id.newstext);
        search = (TextView) findViewById(R.id.search);
        adverticetext = (TextView) findViewById(R.id.adverticetext);
        text_heading = (TextView) findViewById(R.id.text_heading);
        text_headingcount = (TextView) findViewById(R.id.text_headingcount);
        footer = (LinearLayout) findViewById(R.id.footer);
        settingicon = (ImageView) findViewById(R.id.settingicon);
        mapicon = (ImageView) findViewById(R.id.mapicon);
        btm_line = (View) findViewById(R.id.btm_line);
        preferences = PreferenceManager.getDefaultSharedPreferences(com.vadevelopment.RedAppetite.dashboard.AdvertiseWithUs.this);
        editor = preferences.edit();

        displayWithoutViewFragmentWithoutv4(new AdvertisementFragment());
    }

    public void displayWithoutViewFragmentWithoutv4(android.app.Fragment mFragment) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_advertise, mFragment)
                .commit();
    }
}
