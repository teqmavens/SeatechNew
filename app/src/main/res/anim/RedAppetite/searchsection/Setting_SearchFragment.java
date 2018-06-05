package com.vadevelopment.RedAppetite.searchsection;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

/**
 * Created by vibrantappz on 3/22/2017.
 */

public class Setting_SearchFragment extends android.app.Fragment implements View.OnClickListener {

    DashboardActivity mdashboard;
    private RelativeLayout rl_mode, rl_radius;
    private LinearLayout ll_main;
    private Dialog openDialog;
    private TextView radius_value;
    private SQLiteDatabase database;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String radius_frmdatabase, which_latlng, which_latlngaddrs;
    private Cursor cursor;
    private TextView modevalue;
    public static boolean searchsetting_fromcancel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_settingsearch, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        rl_mode = (RelativeLayout) view.findViewById(R.id.rl_mode);
        rl_radius = (RelativeLayout) view.findViewById(R.id.rl_radius);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        radius_value = (TextView) view.findViewById(R.id.radius_value);
        modevalue = (TextView) view.findViewById(R.id.modevalue);
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();
        mdashboard.footer.setVisibility(View.GONE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText(getString(R.string.search));
        mdashboard.settingicon.setVisibility(View.GONE);
        mdashboard.setting.setText(getString(R.string.cancel));
        mdashboard.setting.setBackgroundResource(R.drawable.cancelbutton);
        mdashboard.mapButton.setVisibility(View.VISIBLE);
        mdashboard.mapButton.setText(getString(R.string.done));
        mdashboard.mapicon.setVisibility(View.GONE);
        mdashboard.btm_line.setVisibility(View.GONE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();
        rl_mode.setOnClickListener(this);
        rl_radius.setOnClickListener(this);
        if (SearchFirstFragment.fromfirstsearch == true) {
            slideToAbove();
        }

        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchsetting_fromcancel = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getFragmentManager().popBackStack();
                    }
                }, 430);
                slideToBottom();
            }
        });

        mdashboard.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues();
                if (radius_value.getText().toString().equalsIgnoreCase("Show all")) {
                    cv.put("search_radius", radius_value.getText().toString());
                } else {
                    cv.put("search_radius", radius_value.getText().toString().split("\\s+")[0]);
                }
                if (com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment.searchedlatlong != null && !com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment.searchedlatlong.isEmpty()) {
                    if (com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment.searchedlatlong.equalsIgnoreCase("Current Position")) {
                        cv.put("search_whichlatlong", "current");
                        cv.put("search_whichlatlongaddrs", "current");
                    } else {
                        cv.put("search_whichlatlong", com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment.searchedlatlong);
                        cv.put("search_whichlatlongaddrs", com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment.searched_address);
                    }
                } else if (which_latlngaddrs.equalsIgnoreCase("current")) {
                    cv.put("search_whichlatlong", "current");
                    cv.put("search_whichlatlongaddrs", "current");
                } else {
                    cv.put("search_whichlatlong", which_latlng);
                    cv.put("search_whichlatlongaddrs", which_latlngaddrs);
                }
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, cv, null, null);
                //  getActivity().getSupportFragmentManager().popBackStack();
                searchsetting_fromcancel = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getFragmentManager().popBackStack();
                    }
                }, 420);
                slideToBottom();
            }
        });


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setData_Fromdatabase();
            }
        });
    }

    private void slideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillBefore(true);
        ll_main.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_main.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void slideToBottom() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        slide.setDuration(460);
        slide.setFillAfter(true);
        slide.setFillBefore(true);
        ll_main.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }


    private void RadiusDialog() {
        openDialog = new Dialog(getActivity());
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setCanceledOnTouchOutside(false);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        openDialog.show();
        Window window = openDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window.setAttributes(wlp);


        openDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View vview = inflater.inflate(R.layout.dialog_radius, null, false);
        openDialog.setContentView(vview);
        LinearLayout approx_lay = (LinearLayout) openDialog.findViewById(R.id.approx_lay);
      /*  Double d = new Double(2.99);
        int hei = d.intValue();*/
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width - 60, (height / 3) + 20);
        approx_lay.setLayoutParams(params);
        TextView fifty = (TextView) vview.findViewById(R.id.fifty);
        TextView eighty = (TextView) vview.findViewById(R.id.eighty);
        TextView hundred = (TextView) vview.findViewById(R.id.hundred);
        TextView onefifty = (TextView) vview.findViewById(R.id.onefifty);
        TextView twohundred = (TextView) vview.findViewById(R.id.twohundred);
        TextView twohfifty = (TextView) vview.findViewById(R.id.twohfifty);
        TextView showall = (TextView) vview.findViewById(R.id.showall);

        fifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                radius_value.setText("50 Km");
            }
        });

        eighty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.dismiss();
                radius_value.setText("80 Km");
            }
        });
        hundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                radius_value.setText("100 Km");
            }
        });

        onefifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.dismiss();
                radius_value.setText("150 Km");
            }
        });
        twohundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                radius_value.setText("200 Km");
            }
        });

        twohfifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.dismiss();
                radius_value.setText("250 Km");
            }
        });

        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.dismiss();
                radius_value.setText("Show all");
            }
        });

        openDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment.searchedlatlong = "";
        SearchFirstFragment.fromfirstsearch = false;
        com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.finalcatgid_string = "";
    }

    private void setData_Fromdatabase() {
        cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, null, null, null, null, null, null);


        //   Cursor cursor = database.query(ParseOpenHelper.TABLE_FORSEARCH, null, ParseOpenHelper.SEARCH_UID + "=?", new String[]{preferences.getString("uid", "")}, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    radius_frmdatabase = cursor.getString(cursor.getColumnIndex("search_radius"));
                    which_latlng = cursor.getString(cursor.getColumnIndex("search_whichlatlong"));
                    which_latlngaddrs = cursor.getString(cursor.getColumnIndex("search_whichlatlongaddrs"));

                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
        if (radius_frmdatabase.equalsIgnoreCase("Show all")) {
            radius_value.setText(radius_frmdatabase);
        } else {
            radius_value.setText(radius_frmdatabase + " km");
        }
        if (com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment.searchedlatlong != null && !com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment.searchedlatlong.isEmpty()) {
            if (com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment.searchedlatlong.equalsIgnoreCase("Current Position")) {
                modevalue.setText(com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment.searchedlatlong);
            } else {
                modevalue.setText(com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment.searched_address);
            }
        } else {
            if (which_latlngaddrs.equalsIgnoreCase("current")) {
                modevalue.setText("Current Position");
            } else {
                modevalue.setText(which_latlngaddrs);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_mode:
                com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment mode_frgmt = new com.vadevelopment.RedAppetite.searchsection.Mode_SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Consts.TOMODE_FROM, "search");
                mode_frgmt.setArguments(bundle);
                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(mode_frgmt);
                //mdashboard.displayWithoutViewFragmentWithanim(new Mode_SearchFragment());
                break;
            case R.id.rl_radius:
                RadiusDialog();
                break;
        }
    }
}

