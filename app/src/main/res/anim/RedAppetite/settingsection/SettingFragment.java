package com.vadevelopment.RedAppetite.settingsection;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

/**
 * Created by vibrantappz on 4/4/2017.
 */

public class SettingFragment extends Fragment implements View.OnClickListener{

    DashboardActivity mdashboard;
    private RelativeLayout rl_login,rl_register;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frgm_settings, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        setToolbar();
        rl_login = (RelativeLayout) view.findViewById(R.id.rl_login);
        rl_register = (RelativeLayout) view.findViewById(R.id.rl_register);

        rl_login.setOnClickListener(this);
        rl_register.setOnClickListener(this);
    }

    private void setToolbar() {
            mdashboard.searchicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colortext));
            mdashboard.searchtext.setTextColor(new Color().parseColor("#541c1d"));
            mdashboard.profileicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colortext));
            mdashboard.profiletext.setTextColor(new Color().parseColor("#541c1d"));
            mdashboard.moreicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colortext));
            mdashboard.moretext.setTextColor(new Color().parseColor("#541c1d"));
            mdashboard.newsfeedicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colortext));
            mdashboard.newstext.setTextColor(new Color().parseColor("#541c1d"));
            mdashboard.adverticeicon.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.white));
            mdashboard.adverticetext.setTextColor(new Color().parseColor("#ffffff"));

            mdashboard.centertextsearch.setVisibility(View.VISIBLE);
            mdashboard.centertextsearch.setText("Settings");
            mdashboard.textsearchimage.setVisibility(View.GONE);
            mdashboard.setting.setVisibility(View.GONE);
            mdashboard.mapButton.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_login:
                Intent ilogin = new Intent(getActivity(), com.vadevelopment.RedAppetite.login.ContainerActivity.class);
                ilogin.putExtra("Login", true);
                startActivity(ilogin);
                break;
            case R.id.rl_register:
                Intent iregister = new Intent(getActivity(), com.vadevelopment.RedAppetite.login.ContainerActivity.class);
                iregister.putExtra("Register", false);
                iregister.putExtra("from","settings");
                startActivity(iregister);
                break;
        }
    }
}
