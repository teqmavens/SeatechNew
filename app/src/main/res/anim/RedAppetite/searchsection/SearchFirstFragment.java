package com.vadevelopment.RedAppetite.searchsection;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vibrant Android on 08-03-2017.
 */

public class SearchFirstFragment extends android.app.Fragment implements View.OnClickListener {

    private static String TAG = "SearchFirstFragment";
    private RecyclerView recyclerViewSearch;
    DashboardActivity mdashboard;
    private com.vadevelopment.RedAppetite.adapters.Adapters.AdapterSearch adapterForSearch;
    private ArrayList<com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst> arrayList;
    private ImageView sorting_img;
    private Dialog dialog;
    private EditText search;
    public static boolean fromfirstsearch;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    SQLiteDatabase database;
    private Context context;
    String radius, sortby, categoriesid, stars, type, reviews, favorites, modeposition, sortbyValue, which_latlong;
    private LinearLayout ll_nolocfound, activity_search;
    TextView textview_sortby, resettext;
    private int max;
    public static int start = 0;

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);
       // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();
        setToolbar();
        recyclerViewSearch = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        search = (EditText) view.findViewById(R.id.search);
        sorting_img = (ImageView) view.findViewById(R.id.sorting_img);
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();
        textview_sortby = (TextView) view.findViewById(R.id.textview_sortby);
        resettext = (TextView) view.findViewById(R.id.resettext);
        ll_nolocfound = (LinearLayout) view.findViewById(R.id.ll_nolocfound);
        activity_search = (LinearLayout) view.findViewById(R.id.activity_search);
        recyclerViewSearch.setHasFixedSize(true);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.supportsPredictiveItemAnimations();
        recyclerViewSearch.setLayoutManager(mLayoutManager);
        recyclerViewSearch.setItemAnimator(new DefaultItemAnimator());
        textview_sortby.setOnClickListener(this);
        sorting_img.setOnClickListener(this);
        activity_search.setOnClickListener(this);
        resettext.setOnClickListener(this);
        search.setOnClickListener(this);
        //  search.setFocusable(false);

        recyclerViewSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("scrolling", String.valueOf(newState));
                if (newState == 0) {
                    sorting_img.setVisibility(View.VISIBLE);
                } else {
                    sorting_img.setVisibility(View.INVISIBLE);
                }
            }
        });

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setData_FromDatabase();
            }
        });
        textview_sortby.setText(sortby);
        if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
            com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
        } else {
            if (sortby.equalsIgnoreCase("Distance")) {
                if (isAscendingOrder() == true) {
                    sortbyValue = "distance ASC";
                } else {
                    sortbyValue = "distance DESC";
                }
            } else if (sortby.equalsIgnoreCase("A-Z")) {
                if (isAscendingOrder() == true) {
                    sortbyValue = "buildingname ASC";
                } else {
                    sortbyValue = "buildingname DESC";
                }
            } else if (sortby.equalsIgnoreCase("Stars")) {
                if (isDescendingOrderStars() == true) {
                    sortbyValue = "avgRate DESC";
                } else {
                    sortbyValue = "avgRate ASC";
                }
            } else if (sortby.equalsIgnoreCase("Reviews")) {
                if (isDescendingOrderReviews() == true) {
                    sortbyValue = "total DESC";
                } else {
                    sortbyValue = "total ASC";
                }
            } else {
                if (isAscendingOrder() == true) {
                    sortbyValue = "happy DESC";
                } else {
                    sortbyValue = "sad DESC";
                }
            }
            if (stars.equalsIgnoreCase("Any")) {
            } else {
                stars = stars.split("\\s+")[0];
            }
            if (com.vadevelopment.RedAppetite.searchsection.Filter_SearchFragment.searchfilter_fromcancel == true || com.vadevelopment.RedAppetite.searchsection.Setting_SearchFragment.searchsetting_fromcancel || com.vadevelopment.RedAppetite.searchsection.Map_SearchFragment.frommap_search) {
                try {
                    if (!arrayList.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setAdapter(arrayList);
                            }
                        });
                    } else {
                        ll_nolocfound.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                }

            } else {
                ll_nolocfound.setVisibility(View.INVISIBLE);
                arrayList = new ArrayList<>();
                start = 0;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetSearchTask_Task(search.getText().toString());
                    }
                }, 80);
            }
        }

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) || (i == EditorInfo.IME_ACTION_DONE)) {

                    if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
                    } else {
                        resettext.setVisibility(View.INVISIBLE);
                        com.vadevelopment.RedAppetite.HandyObjects.expand(search, 160, com.vadevelopment.RedAppetite.HandyObjects.getEditextWidth(context));
                        arrayList = new ArrayList<>();
                        start = 0;
                        GetSearchTask_Task(search.getText().toString());
                    }
                }
                return false;
            }
        });

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b == true) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resettext.setVisibility(View.VISIBLE);
                        }
                    }, 160);
                    com.vadevelopment.RedAppetite.HandyObjects.collapse(search, 160, com.vadevelopment.RedAppetite.HandyObjects.getEditextWidth(context) - (2 * resettext.getWidth()));
                }
            }
        });

       /* search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (search.getRight() - search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        search.setText("");
                        return false;
                    }
                }
                return false;
            }
        });*/
    }

    private void setData_FromDatabase() {
        Cursor cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    categoriesid = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_CATEGORY));
                    stars = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_STARS));
                    type = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_TYPE));
                    reviews = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_REVIEWS));
                    favorites = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_FAVORITES));
                    modeposition = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_MODEPOSI));
                    radius = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_RADIUS));
                    sortby = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_SORTBY));
                    which_latlong = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_WHICHLATLONG));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
    }

    private boolean isAscendingOrder() {
        boolean type = false;
        if (preferences.getString(Consts.SEARCH_ORDERTYPE, "").equalsIgnoreCase("ascending")) {
            type = true;
        }
        return type;
    }

    private boolean isDescendingOrderStars() {
        boolean type = false;
        if (preferences.getString(Consts.SEARCH_ORDERTYPESTARS, "").equalsIgnoreCase("descending")) {
            type = true;
        }
        return type;
    }

    private boolean isDescendingOrderReviews() {
        boolean type = false;
        if (preferences.getString(Consts.SEARCH_ORDERTYPEREVIEWS, "").equalsIgnoreCase("descending")) {
            type = true;
        }
        return type;
    }

    private void setToolbar() {
        mdashboard.footer.setVisibility(View.VISIBLE);
       // mdashboard.profileicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mdashboard.profileicon.setImageResource(R.drawable.prfliconunselect);
        mdashboard.profiletext.setTextColor(new Color().parseColor("#410202"));
        mdashboard.moreicon.setImageResource(R.drawable.more);
        // mdashboard.moreicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mdashboard.moretext.setTextColor(new Color().parseColor("#410202"));
        //  mdashboard.searchicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.newcolortext));
        mdashboard.searchicon.setImageResource(R.drawable.srchicon_white);
        mdashboard.searchtext.setTextColor(new Color().parseColor("#e8dddd"));
        mdashboard.newsfeedicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mdashboard.newstext.setTextColor(new Color().parseColor("#410202"));
        mdashboard.adverticeicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mdashboard.adverticetext.setTextColor(new Color().parseColor("#410202"));
        mdashboard.setting.setVisibility(View.GONE);
        mdashboard.settingicon.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.VISIBLE);
        mdashboard.rl_headingwithcount.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.GONE);
        mdashboard.search.setText(getString(R.string.search));
        mdashboard.setting.setBackgroundResource(R.drawable.cancelbutton);
        mdashboard.mapButton.setVisibility(View.GONE);
        mdashboard.mapicon.setVisibility(View.VISIBLE);
        mdashboard.mapicon.setImageResource(R.drawable.searchheadermap);
        mdashboard.btm_line.setVisibility(View.VISIBLE);

        mdashboard.settingicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromfirstsearch = true;
                com.vadevelopment.RedAppetite.searchsection.Setting_SearchFragment setting_frgmt = new com.vadevelopment.RedAppetite.searchsection.Setting_SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Consts.TOSETTING_FROM, "search");
                setting_frgmt.setArguments(bundle);
                mdashboard.displayWithoutViewFragmentWithoutv4(setting_frgmt);
            }
        });

        mdashboard.textsearchimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromfirstsearch = true;
                mdashboard.displayWithoutViewFragmentWithoutv4(new com.vadevelopment.RedAppetite.searchsection.Filter_SearchFragment());
            }
        });

        mdashboard.mapicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.vadevelopment.RedAppetite.searchsection.Filter_SearchFragment.searchfilter_fromcancel = false;
                com.vadevelopment.RedAppetite.searchsection.Setting_SearchFragment.searchsetting_fromcancel = false;
                com.vadevelopment.RedAppetite.searchsection.Map_SearchFragment.frommap_search = false;
                mdashboard.flipViewFragmentWithanim(new com.vadevelopment.RedAppetite.searchsection.Map_SearchFragment());
            }
        });

        mdashboard.lltool_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    View vieww = getActivity().getCurrentFocus();
                    if (vieww != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(vieww.getWindowToken(), 0);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void displayBottomDialog() {
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.y = 30;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window.setAttributes(wlp);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_sortbysearch);
        LinearLayout approx_lay = (LinearLayout) dialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 60, (h / 2) - 10);
        approx_lay.setLayoutParams(params);

        RelativeLayout rl_cancel = (RelativeLayout) dialog.findViewById(R.id.rl_cancel);
        final TextView atoz = (TextView) dialog.findViewById(R.id.atoz);
        final TextView distance = (TextView) dialog.findViewById(R.id.distance);
        final TextView stars = (TextView) dialog.findViewById(R.id.stars);
        final TextView review = (TextView) dialog.findViewById(R.id.review);
        final TextView recommend = (TextView) dialog.findViewById(R.id.recommend);

        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        atoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAscendingOrder() == true) {
                    sortbyValue = "buildingname ASC";
                } else {
                    sortbyValue = "buildingname DESC";
                }
                ContentValues cv = new ContentValues();
                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_SORTBY, "A-Z");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, cv, null, null);
                textview_sortby.setText(atoz.getText().toString());
                sortby = "A-Z";
                arrayList.clear();
                start = 0;
                GetSearchTask_Task(search.getText().toString());
                dialog.dismiss();
            }
        });

        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAscendingOrder() == true) {
                    sortbyValue = "distance ASC";
                } else {
                    sortbyValue = "distance DESC";
                }
                ContentValues cv = new ContentValues();
                cv.put("search_sortby", "Distance");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, cv, null, null);
                textview_sortby.setText(distance.getText().toString());
                sortby = "Distance";
                arrayList.clear();
                start = 0;
                GetSearchTask_Task(search.getText().toString());
                dialog.dismiss();
            }
        });

        stars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDescendingOrderStars() == true) {
                    sortbyValue = "avgRate DESC";
                } else {
                    sortbyValue = "avgRate ASC";
                }
                ContentValues cv = new ContentValues();
                cv.put("search_sortby", "Stars");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, cv, null, null);
                textview_sortby.setText(stars.getText().toString());
                sortby = "Stars";
                arrayList.clear();
                start = 0;
                GetSearchTask_Task(search.getText().toString());
                dialog.dismiss();
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDescendingOrderReviews() == true) {
                    sortbyValue = "total DESC";
                } else {
                    sortbyValue = "total ASC";
                }
                ContentValues cv = new ContentValues();
                cv.put("search_sortby", "Reviews");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, cv, null, null);
                textview_sortby.setText(review.getText().toString());
                sortby = "Reviews";
                arrayList.clear();
                start = 0;
                GetSearchTask_Task(search.getText().toString());
                dialog.dismiss();
            }
        });

        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAscendingOrder() == true) {
                    sortbyValue = "happy DESC";
                } else {
                    sortbyValue = "sad DESC";
                }
                ContentValues cv = new ContentValues();
                cv.put("search_sortby", "Recommendation");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, cv, null, null);
                textview_sortby.setText(recommend.getText().toString());
                sortby = "Recommendation";
                arrayList.clear();
                start = 0;
                GetSearchTask_Task(search.getText().toString());
                dialog.dismiss();
            }
        });

        if (dialog.isShowing()) {
        } else {
            dialog.show();
        }
    }

    private void GetSearchTask_Task(final String search) {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.SEARCHTASK_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success")) {
                        JSONArray jarry = jboj.getJSONArray("all_list");
                        max = Integer.parseInt(jboj.getString("max"));
                        if (jarry.length() == 0) {
                            arrayList.clear();
                            setAdapter(arrayList);
                            ll_nolocfound.setVisibility(View.VISIBLE);
                        } else {
                            ll_nolocfound.setVisibility(View.INVISIBLE);
                            for (int i = 0; i < jarry.length(); i++) {
                                com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst ske_search = new com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst();
                                JSONObject jobj_inside = jarry.getJSONObject(i);
                                ske_search.setLid(jobj_inside.getString("lid"));
                                ske_search.setBuildingname(jobj_inside.getString("buildingname"));
                                ske_search.setAddress(jobj_inside.getString("address"));
                                ske_search.setDistance(jobj_inside.getString("distance"));
                                ske_search.setType(jobj_inside.getString("type"));
                                ske_search.setTotal(jobj_inside.getString("total"));
                                ske_search.setIsreviewed(jobj_inside.getString("isReviewed"));
                                ske_search.setIsfav(jobj_inside.getString("isFav"));
                                ske_search.setAvgrating(jobj_inside.getString("avgRate"));
                                ske_search.setFreeimage(jobj_inside.getString("free"));
                                ske_search.setPremiumimage(jobj_inside.getString("premium"));
                                ske_search.setHappy(jobj_inside.getString("happy"));
                                ske_search.setSad(jobj_inside.getString("sad"));
                                arrayList.add(ske_search);
                            }
                            setAdapter(arrayList);
                        }
                    } else if (jboj.getString("message").equalsIgnoreCase("fail")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, jboj.getString("alert"));
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
                if (which_latlong.equalsIgnoreCase("current")) {
                    params.put("latitude", preferences.getString(Consts.CURRENT_LATITUDE, ""));
                    params.put("longitude", preferences.getString(Consts.CURRENT_LONGITUDE, ""));
                } else {
                    params.put("latitude", String.valueOf(which_latlong.split(",")[0]));
                    params.put("longitude", String.valueOf(which_latlong.split(",")[1]));
                }
                params.put("uid", preferences.getString(Consts.USER_ID, ""));
                params.put("type", type);
                params.put("star", stars);
                params.put("start", String.valueOf(start));
                if (radius.equalsIgnoreCase("Show all")) {
                    params.put("distance", "10000");
                } else {
                    params.put("distance", radius);
                }
                params.put("categoriesid", categoriesid);
                params.put("reviews", reviews);
                params.put("favorites", favorites);
                params.put("order", sortbyValue);
                params.put("search", search);
                return params;
            }
        };
        com.vadevelopment.RedAppetite.AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void setAdapter(final ArrayList<com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst> arraylist) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //  HandyObjects.showAlert(getActivity(), String.valueOf(max));
                try {
                    if (arrayList.size() < 51 && max < 51) {
                        adapterForSearch = new com.vadevelopment.RedAppetite.adapters.Adapters.AdapterSearch(context, getFragmentManager(), arraylist, 0, com.vadevelopment.RedAppetite.searchsection.SearchFirstFragment.this, false);
                        recyclerViewSearch.setAdapter(adapterForSearch);
                    } else if (arrayList.size() < 51 && max > 50) {
                        adapterForSearch = new com.vadevelopment.RedAppetite.adapters.Adapters.AdapterSearch(context, getFragmentManager(), arraylist, 0, com.vadevelopment.RedAppetite.searchsection.SearchFirstFragment.this, true);
                        recyclerViewSearch.setAdapter(adapterForSearch);
                    } else if (com.vadevelopment.RedAppetite.searchsection.Filter_SearchFragment.searchfilter_fromcancel == true || com.vadevelopment.RedAppetite.searchsection.Setting_SearchFragment.searchsetting_fromcancel || com.vadevelopment.RedAppetite.searchsection.Map_SearchFragment.frommap_search == true) {
                        if (max > 50 && (max - start) < 50) {
                            adapterForSearch = new com.vadevelopment.RedAppetite.adapters.Adapters.AdapterSearch(context, getFragmentManager(), arraylist, 0, com.vadevelopment.RedAppetite.searchsection.SearchFirstFragment.this, false);
                            recyclerViewSearch.setAdapter(adapterForSearch);
                        } else if ((max > 50 && (max - start) > 50)) {
                            adapterForSearch = new com.vadevelopment.RedAppetite.adapters.Adapters.AdapterSearch(context, getFragmentManager(), arraylist, 0, com.vadevelopment.RedAppetite.searchsection.SearchFirstFragment.this, true);
                            recyclerViewSearch.setAdapter(adapterForSearch);
                        }

                    } else {
                        // adapterForSearch.showfooter();
                        adapterForSearch.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.e("exception", "exception" + e);
                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        com.vadevelopment.RedAppetite.searchsection.Filter_SearchFragment.searchfilter_fromcancel = false;
        com.vadevelopment.RedAppetite.searchsection.Setting_SearchFragment.searchsetting_fromcancel = false;
        com.vadevelopment.RedAppetite.searchsection.Map_SearchFragment.frommap_search = false;
        search.setText("");
    }

    public void onClickLoadMore() {
        if (start == 0) {
            start = start + 51;
        } else {
            start = start + 50;
        }
        com.vadevelopment.RedAppetite.searchsection.Filter_SearchFragment.searchfilter_fromcancel = false;
        com.vadevelopment.RedAppetite.searchsection.Setting_SearchFragment.searchsetting_fromcancel = false;
        com.vadevelopment.RedAppetite.searchsection.Map_SearchFragment.frommap_search = false;
        if (start > max) {
            adapterForSearch.hideFooter();
            adapterForSearch.notifyDataSetChanged();
        } else if ((max - start) < 50) {
            adapterForSearch.hideFooter();
            GetSearchTask_Task(search.getText().toString());
        } else {
            GetSearchTask_Task(search.getText().toString());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resettext:
                resettext.setVisibility(View.INVISIBLE);
                search.setText("");
                com.vadevelopment.RedAppetite.HandyObjects.expand(search, 160, com.vadevelopment.RedAppetite.HandyObjects.getEditextWidth(context));
                arrayList = new ArrayList<>();
                start = 0;
                GetSearchTask_Task("");
                break;
            case R.id.textview_sortby:
                displayBottomDialog();
                break;
            case R.id.sorting_img:
                if (isAscendingOrder() == true) {
                    editor.putString(Consts.SEARCH_ORDERTYPE, "descending");
                } else {
                    editor.putString(Consts.SEARCH_ORDERTYPE, "ascending");
                }
                if (isDescendingOrderStars() == true) {
                    editor.putString(Consts.SEARCH_ORDERTYPESTARS, "ascending");
                } else {
                    editor.putString(Consts.SEARCH_ORDERTYPESTARS, "descending");
                }
                if (isDescendingOrderReviews() == true) {
                    editor.putString(Consts.SEARCH_ORDERTYPEREVIEWS, "ascending");
                } else {
                    editor.putString(Consts.SEARCH_ORDERTYPEREVIEWS, "descending");
                }
                editor.commit();
                if (sortby.equalsIgnoreCase("Distance")) {
                    if (isAscendingOrder() == true) {
                        sortbyValue = "distance ASC";
                    } else {
                        sortbyValue = "distance DESC";
                    }
                } else if (sortby.equalsIgnoreCase("A-Z")) {
                    if (isAscendingOrder() == true) {
                        sortbyValue = "buildingname ASC";
                    } else {
                        sortbyValue = "buildingname DESC";
                    }
                } else if (sortby.equalsIgnoreCase("Stars")) {
                    if (isDescendingOrderStars() == true) {
                        sortbyValue = "avgRate DESC";
                    } else {
                        sortbyValue = "avgRate ASC";
                    }
                } else if (sortby.equalsIgnoreCase("Reviews")) {
                    if (isDescendingOrderReviews() == true) {
                        sortbyValue = "total DESC";
                    } else {
                        sortbyValue = "total ASC";
                    }
                } else {
                    if (isAscendingOrder() == true) {
                        sortbyValue = "happy DESC";
                    } else {
                        sortbyValue = "sad DESC";
                    }
                }
                // arrayList.clear();
                arrayList = new ArrayList<>();
                start = 0;
                GetSearchTask_Task(search.getText().toString());
                break;
            case R.id.activity_search:
                /*try {
                    View vieww = getActivity().getCurrentFocus();
                    if (vieww != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(vieww.getWindowToken(), 0);
                    }
                } catch (Exception e) {
                }*/
                break;
            case R.id.search:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resettext.setVisibility(View.VISIBLE);
                    }
                }, 160);
                com.vadevelopment.RedAppetite.HandyObjects.collapse(search, 160, com.vadevelopment.RedAppetite.HandyObjects.getEditextWidth(context) - (2 * resettext.getWidth()));
                break;
        }
    }
}
