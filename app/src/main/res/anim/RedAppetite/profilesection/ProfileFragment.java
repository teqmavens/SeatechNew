package com.vadevelopment.RedAppetite.profilesection;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vibrant Android on 08-03-2017.
 */

public class ProfileFragment extends android.app.Fragment implements View.OnClickListener {

    private static String TAG = "ProfileFragment";
    DashboardActivity mdashboard;
    private RelativeLayout rl_email, rl_pwd, rl_favorites, rl_review, rl_deleteprofile, rl_nickname, rl_bday, rl_country;
    private TextView nickname, emaildetail, pwddetail, bday_detail, countrydetail, ttl_fvrt, ttl_review, bday;
    private LinearLayout ll_main;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CheckBox checkbox_newsletter;
    String myFormat = "dd-MM-yyyy";
    private Calendar myCalendar;
    private Context context;
    private SQLiteDatabase database;
    private String latitude, longitude;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mdashboard = (DashboardActivity) getActivity();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profilefragment, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        setToolbar(view);
        rl_email = (RelativeLayout) view.findViewById(R.id.rl_email);
        rl_pwd = (RelativeLayout) view.findViewById(R.id.rl_pwd);
        rl_favorites = (RelativeLayout) view.findViewById(R.id.rl_favorites);
        rl_review = (RelativeLayout) view.findViewById(R.id.rl_review);
        rl_deleteprofile = (RelativeLayout) view.findViewById(R.id.rl_deleteprofile);
        rl_nickname = (RelativeLayout) view.findViewById(R.id.rl_nickname);
        rl_bday = (RelativeLayout) view.findViewById(R.id.rl_bday);
        rl_country = (RelativeLayout) view.findViewById(R.id.rl_country);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        nickname = (TextView) view.findViewById(R.id.nickname);
        emaildetail = (TextView) view.findViewById(R.id.emaildetail);
        pwddetail = (TextView) view.findViewById(R.id.pwddetail);
        bday_detail = (TextView) view.findViewById(R.id.bday_detail);
        bday = (TextView) view.findViewById(R.id.bday);
        countrydetail = (TextView) view.findViewById(R.id.countrydetail);
        ttl_fvrt = (TextView) view.findViewById(R.id.ttl_fvrt);
        ttl_review = (TextView) view.findViewById(R.id.ttl_review);
        checkbox_newsletter = (CheckBox) view.findViewById(R.id.checkbox_newsletter);
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();

        editor = preferences.edit();
        rl_email.setOnClickListener(this);
        rl_pwd.setOnClickListener(this);
        rl_favorites.setOnClickListener(this);
        rl_review.setOnClickListener(this);
        rl_deleteprofile.setOnClickListener(this);
        rl_nickname.setOnClickListener(this);
        rl_bday.setOnClickListener(this);
        rl_country.setOnClickListener(this);

        checkbox_newsletter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkbox_newsletter.isChecked()) {
                    SwitchNewsletter_Task("Enabled");
                } else {
                    SwitchNewsletter_Task("Disable");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.rl_email:
                com.vadevelopment.RedAppetite.profilesection.EmailProfile_Fragment email_frg = new com.vadevelopment.RedAppetite.profilesection.EmailProfile_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString(Consts.PROFILE_EMAIL, emaildetail.getText().toString());
                email_frg.setArguments(bundle);
                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(email_frg);
                break;
            case R.id.rl_pwd:
                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(new PwdProfile_Fragment());
                break;
            case R.id.rl_favorites:
                String ttlfvrt = ttl_fvrt.getText().toString();
                if (ttlfvrt != null && !ttlfvrt.isEmpty()) {
                    if (ttlfvrt.equalsIgnoreCase("0")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getString(R.string.noadded_fvrt));
                    } else {
                        com.vadevelopment.RedAppetite.profilesection.FavoritesProfile_Fragment fp_frgm = new com.vadevelopment.RedAppetite.profilesection.FavoritesProfile_Fragment();
                        Bundle bundle_p = new Bundle();
                        bundle_p.putString(Consts.PROFILE_TTLFVRT, ttl_fvrt.getText().toString());
                        fp_frgm.setArguments(bundle_p);
                        mdashboard.displayWithoutViewFragmentWithanimWithoutv4(fp_frgm);
                    }
                }
                break;
            case R.id.rl_review:
                String ttlreview = ttl_review.getText().toString();
                //  mdashboard.displayWithoutViewFragment(new ReviewProfile_Fragment());
                if (ttlreview != null && !ttlreview.isEmpty()) {
                    if (ttlreview.equalsIgnoreCase("0")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getString(R.string.noadded_review));
                    } else {
                        mdashboard.displayWithoutViewFragmentWithanimWithoutv4(new ReviewProfile_Fragment());
                    }
                }
                break;
            case R.id.rl_deleteprofile:
                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(new com.vadevelopment.RedAppetite.profilesection.DeleteProfile_Fragment());
                break;
            case R.id.rl_nickname:
                com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), getString(R.string.contactus_fredit));
                break;
            case R.id.rl_bday:
                com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), getString(R.string.contactus_fredit));
                break;
            case R.id.rl_country:
                com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), getString(R.string.contactus_fredit));
                break;
        }
    }

    private void setToolbar(View view) {
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        if (getArguments() != null && getArguments().getString("fromskip").equalsIgnoreCase("yup")) {
            slideToAbove();
        }
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.searchicon.setImageResource(R.drawable.srchicon);
       // mdashboard.searchicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colortext));
        mdashboard.searchtext.setTextColor(new Color().parseColor("#410202"));
       // mdashboard.moreicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colortext));
        mdashboard.moreicon.setImageResource(R.drawable.more);
        mdashboard.moretext.setTextColor(new Color().parseColor("#410202"));
     //   mdashboard.profileicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.newcolortext));
        mdashboard.profileicon.setImageResource(R.drawable.profilebtm_white);
        mdashboard.profiletext.setTextColor(new Color().parseColor("#e8dddd"));
        mdashboard.newsfeedicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mdashboard.newstext.setTextColor(new Color().parseColor("#410202"));
        mdashboard.adverticeicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mdashboard.adverticetext.setTextColor(new Color().parseColor("#410202"));

        mdashboard.setting.setVisibility(View.GONE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.rl_headingwithcount.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText("Profile");
        mdashboard.mapButton.setVisibility(View.VISIBLE);
        mdashboard.mapButton.setText("Logout");
        mdashboard.settingicon.setVisibility(View.GONE);
        mdashboard.mapicon.setVisibility(View.GONE);

        mdashboard.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latitude = preferences.getString(Consts.CURRENT_LATITUDE, "");
                longitude = preferences.getString(Consts.CURRENT_LONGITUDE, "");
                editor.clear();
                editor.commit();
                com.vadevelopment.RedAppetite.HandyObjects.deleteAllDatabase(context);
                com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getString(R.string.logout));
                Cursor cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, null, null, null, null, null, null);
                Cursor cursor_event = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, null, null, null, null, null, null);
                Cursor cursor_favrt = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORFAVORITES, null, null, null, null, null, null);
                Cursor cursor_review = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, null, null, null, null, null, null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                    } else {
                        ContentValues cv = new ContentValues();
                        cv.put("search_category", "All");
                        cv.put("search_stars", "Any");
                        cv.put("search_type", "Free");
                        cv.put("search_reviews", "0");
                        cv.put("search_favorites", "0");
                        cv.put("search_modeposi", "Current Position");
                        cv.put("search_radius", "100");
                        cv.put("search_sortby", "Distance");
                        cv.put("search_whichlatlong", "current");
                        cv.put("search_whichlatlongaddrs", "current");
                        long idd = database.insert("forsearch", null, cv);
                        Log.e("table", String.valueOf(idd));
                    }
                    cursor.close();
                }

                if (cursor_event != null) {
                    if (cursor_event.getCount() > 0) {
                    } else {
                        ContentValues cv_event = new ContentValues();
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_CATEGORY, "All");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_STARS, "Any");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_TYPE, "Normal");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_TYPEONE, "Free");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_REVIEWS, "0");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_FAVORITES, "0");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_MODEPOSI, "Current Position");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_RADIUS, "100");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_SORTBY, "Distance");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_WHICHLATLONG, "current");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_WHICHLATLONGADDRS, "current");
                        long idd = database.insert(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, null, cv_event);
                        Log.e("table", String.valueOf(idd));
                    }
                    cursor_event.close();
                }

                if (cursor_favrt != null) {
                    if (cursor_favrt.getCount() > 0) {
                    } else {
                        ContentValues cv_fvrt = new ContentValues();
                        cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_CATEGORY, "All");
                        cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_STARS, "Any");
                        cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_TYPE, "Free");
                        cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_REVIEWS, "0");
                        cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_SORTBY, "Distance");
                        long idd = database.insert(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORFAVORITES, null, cv_fvrt);
                        Log.e("table", String.valueOf(idd));
                    }
                    cursor_favrt.close();
                }

                if (cursor_review != null) {
                    if (cursor_review.getCount() > 0) {
                    } else {
                        ContentValues cv_review = new ContentValues();
                        cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_CATEGORY, "All");
                        cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_STARS, "Any");
                        cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_TYPE, "Free");
                        cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_FAVORITE, "0");
                        cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_SORTBY, "Date");
                        long idd = database.insert(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, null, cv_review);
                        Log.e("table", String.valueOf(idd));
                    }
                    cursor_review.close();
                }
                // mdashboard.displayWithoutViewFragmentWithoutBackstack(new SearchFirstFragment());
                editor.putString(Consts.CURRENT_LATITUDE, latitude);
                editor.putString(Consts.CURRENT_LONGITUDE, longitude);
                editor.putBoolean(Consts.WELCOME_SCREEN, true);
                editor.commit();
                Intent i = new Intent(getActivity(), DashboardActivity.class);
                i.putExtra("fromskip", "yup");
                startActivity(i);
                getActivity().finish();
            }
        });

        if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
            com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
                    GetProfile_Task();
                }
            }, 80);

        }
    }

    private void slideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
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
                ll_main.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void GetProfile_Task() {
        String tag_json_obj = "json_obj_req";
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.PROFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();
                        JSONObject json_obj = jboj.getJSONObject("all_list");
                        nickname.setText(json_obj.getString("name"));
                        emaildetail.setText(json_obj.getString("email"));
                        countrydetail.setText(json_obj.getString("country"));
                        bday_detail.setText(json_obj.getString("birthday"));
                        pwddetail.setText(json_obj.getString("password"));
                        ttl_fvrt.setText(json_obj.getString("favorites"));
                        ttl_review.setText(json_obj.getString("reviews"));
                        bday.setText(getString(R.string.profile_bday) + " " + "(" + String.valueOf(getDiffere_year(json_obj.getString("birthday"))) + ")");
                      //  HandyObjects.showAlert(getActivity(),json_obj.getString("newsletter"));
                        if (json_obj.getString("newsletter").equalsIgnoreCase("Enabled")) {
                            checkbox_newsletter.setChecked(true);
                        } else if (json_obj.getString("newsletter").equalsIgnoreCase("Disable")){
                            checkbox_newsletter.setChecked(false);
                        }
                    }
                } catch (Exception e) {
                }
                com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", preferences.getString(Consts.USER_ID, ""));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private int getDiffere_year(String showing_date) {
        int differ = 7;

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        myCalendar = Calendar.getInstance();
        String currentdate = sdf.format(myCalendar.getTime());
        int diff = Integer.parseInt(currentdate.split("-")[2]) - Integer.parseInt(showing_date.split("-")[2]);
        return diff;
    }

    private void SwitchNewsletter_Task(final String type) {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.NEWSLETTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        if (jboj.getString("all_list").equalsIgnoreCase("Enabled")) {
                            checkbox_newsletter.setChecked(true);
                            com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), getString(R.string.succenabled));
                        } else if (jboj.getString("all_list").equalsIgnoreCase("Disable")) {
                            checkbox_newsletter.setChecked(false);
                            com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), getString(R.string.succdisabled));
                        }
                    }
                } catch (Exception e) {
                }
                com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", preferences.getString(Consts.USER_ID, ""));
                params.put("newsletter", type);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


}
