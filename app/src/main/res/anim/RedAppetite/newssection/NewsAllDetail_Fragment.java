package com.vadevelopment.RedAppetite.newssection;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

/**
 * Created by vibrantappz on 3/23/2017.
 */

public class NewsAllDetail_Fragment extends Fragment implements View.OnClickListener {

    DashboardActivity mdashboard;
    private TextView addnews, news_seemore;
    private ImageView img_share;
    private Dialog mediaDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //   View view = inflater.inflate(R.layout.frgm_newsalldetail, container, false);
        View view = inflater.inflate(R.layout.frgm_searchdetail, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        img_share = (ImageView) view.findViewById(R.id.img_share);
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText(getString(R.string.details));
        mdashboard.mapButton.setVisibility(View.VISIBLE);
        mdashboard.mapButton.setText(getString(R.string.map));
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.setting.setText(getString(R.string.back));
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        mdashboard.mapicon.setVisibility(View.GONE);
        mdashboard.settingicon.setVisibility(View.GONE);
        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mdashboard.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdashboard.displayWithoutViewFragment(new com.vadevelopment.RedAppetite.newssection.Map_NewsDetailFragment());
            }
        });

        addnews = (TextView) view.findViewById(R.id.addnews);
        news_seemore = (TextView) view.findViewById(R.id.news_seemore);

        addnews.setOnClickListener(this);
        news_seemore.setOnClickListener(this);
        img_share.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addnews:
                mdashboard.displayWithoutViewFragmentWithoutv4(new com.vadevelopment.RedAppetite.moresection.AdvertisementFragment());
                break;
            case R.id.news_seemore:
                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(new com.vadevelopment.RedAppetite.newssection.NewsSeemore_Fragment());
                break;
            case R.id.img_share:
                // displayBottomDialog();
                String shareBody = "Here is the share content body";
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;
        }
    }

    private void displayBottomDialog() {
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        mediaDialog = new Dialog(getActivity());
        mediaDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mediaDialog.setCanceledOnTouchOutside(false);
        Window window = mediaDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.y = 30;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window.setAttributes(wlp);
        mediaDialog.setContentView(R.layout.dialog_tellafrnd);
        LinearLayout approx_lay = (LinearLayout) mediaDialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 40, (h / 2) - 10);
        approx_lay.setLayoutParams(params);

        RelativeLayout rl_cancel = (RelativeLayout) mediaDialog.findViewById(R.id.rl_cancel);

        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaDialog.dismiss();
            }
        });

        if (mediaDialog.isShowing()) {
        } else {
            mediaDialog.show();
        }

    }
}

