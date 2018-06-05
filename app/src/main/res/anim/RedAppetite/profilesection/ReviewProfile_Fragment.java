package com.vadevelopment.RedAppetite.profilesection;

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
import com.vadevelopment.RedAppetite.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vibrantappz on 3/22/2017.
 */

public class ReviewProfile_Fragment extends android.app.Fragment implements View.OnClickListener {

    com.vadevelopment.RedAppetite.dashboard.DashboardActivity mdashboard;
    private RecyclerView recyclerView;
    private static String TAG = "ReviewProfile_Fragment";
    private String categoriesid, stars, type, favorites, sortby, sortbyValue;
    private ArrayList<com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst> arrayList;
    private com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_ReviewProfile adapter;
    private TextView textview_sortby, resettext;
    private SQLiteDatabase database;
    private ImageView sorting_img;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private LinearLayout ll_nolocfound, activity_search;
    private Dialog dialog;
    private EditText search;
    private Context context;
    private int max;
    public static boolean from_reviewlisting;
    public static int start = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (com.vadevelopment.RedAppetite.dashboard.DashboardActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_reviewprofile, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        setToolbar();
        sorting_img = (ImageView) view.findViewById(R.id.sorting_img);
        ll_nolocfound = (LinearLayout) view.findViewById(R.id.ll_nolocfound);
        activity_search = (LinearLayout) view.findViewById(R.id.activity_search);
        search = (EditText) view.findViewById(R.id.search);
        resettext = (TextView) view.findViewById(R.id.resettext);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        sorting_img.setOnClickListener(this);
        activity_search.setOnClickListener(this);
        resettext.setOnClickListener(this);
        search.setOnClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                setData_FromDatabase(view);
            }
        });
    }

    private void setToolbar() {
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);

        mdashboard.rl_headingwithcount.setVisibility(View.VISIBLE);
        mdashboard.text_heading.setText(getString(R.string.reviews));
        mdashboard.text_headingcount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.filter, 0);
        //   mdashboard.search.setText(getString(R.string.reviews));
        mdashboard.centertextsearch.setVisibility(View.GONE);
     /*   mdashboard.mapButton.setVisibility(View.VISIBLE);
        mdashboard.mapButton.setText(getString(R.string.map));*/
        mdashboard.mapButton.setVisibility(View.GONE);
        mdashboard.mapicon.setVisibility(View.VISIBLE);
        mdashboard.mapicon.setImageResource(R.drawable.searchheadermap);

        mdashboard.setting.setText(getString(R.string.back));
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        mdashboard.btm_line.setVisibility(View.VISIBLE);
        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        mdashboard.rl_headingwithcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                from_reviewlisting = true;
                mdashboard.displayWithoutViewFragmentWithoutv4(new com.vadevelopment.RedAppetite.profilesection.ReviewSrchFilter_ProfileFragment());
            }
        });

        mdashboard.mapicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdashboard.flipViewFragmentWithanim(new com.vadevelopment.RedAppetite.profilesection.Map_ReviewFragment());
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

    private void setData_FromDatabase(View view) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(context).getWritableDatabase();
        Cursor cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    categoriesid = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_CATEGORY));
                    stars = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_STARS));
                    type = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_TYPE));
                    favorites = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_FAVORITE));
                    sortby = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_SORTBY));
                    cursor.moveToNext();
                }
                cursor.close();
            }
            textview_sortby = (TextView) view.findViewById(R.id.textview_sortby);

            textview_sortby.setText(sortby);
            textview_sortby.setOnClickListener(this);
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
                } else if (sortby.equalsIgnoreCase("Date")) {
                    if (isDescendingOrderDate() == true) {
                        sortbyValue = "visited_date DESC";
                    } else {
                        sortbyValue = "visited_date ASC";
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
                if (com.vadevelopment.RedAppetite.profilesection.ReviewSrchFilter_ProfileFragment.reviewfilter_fromcancel == true || com.vadevelopment.RedAppetite.profilesection.Map_ReviewFragment.from_reviewsearch == true) {
                    if (!arrayList.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mdashboard.text_headingcount.setText(String.valueOf(arrayList.size()));
                                setAdapter(arrayList);
                            }
                        });
                    } else {
                        ll_nolocfound.setVisibility(View.VISIBLE);
                    }
                } else {
                    arrayList = new ArrayList<>();
                    start = 0;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GetReview_Task(search.getText().toString());
                        }
                    }, 80);

                }
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
                        GetReview_Task(search.getText().toString());
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

        /*search.setOnTouchListener(new View.OnTouchListener() {
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

    private void GetReview_Task(final String search) {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.REVIEWBUILDING_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success")) {
                        max = Integer.parseInt(jboj.getString("max"));
                        JSONArray jarry = jboj.getJSONArray("all_list");
                        // mdashboard.text_headingcount.setText(String.valueOf(jarry.length()));
                        mdashboard.text_headingcount.setText(jboj.getString("max"));
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
                                ske_search.setIsfav(jobj_inside.getString("isFav"));
                                ske_search.setAvgrating(jobj_inside.getString("avgRate"));
                                ske_search.setFreeimage(jobj_inside.getString("free"));
                                ske_search.setPremiumimage(jobj_inside.getString("premium"));
                                ske_search.setVisited_date(jobj_inside.getString("visited_date"));
                                ske_search.setHappy(jobj_inside.getString("happy"));
                                ske_search.setSad(jobj_inside.getString("sad"));
                                ske_search.setRvid(jobj_inside.getString("rvid"));
                                ske_search.setRecommendation(jobj_inside.getString("recommendation"));
                                ske_search.setInd_rating(jobj_inside.getString("rating"));
                                arrayList.add(ske_search);
                            }
                            // HandyObjects.showAlert(getActivity(), String.valueOf(jboj.getString("max")));
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
                params.put("latitude", preferences.getString(com.vadevelopment.RedAppetite.Consts.CURRENT_LATITUDE, ""));
                params.put("longitude", preferences.getString(com.vadevelopment.RedAppetite.Consts.CURRENT_LONGITUDE, ""));
                params.put("uid", preferences.getString(com.vadevelopment.RedAppetite.Consts.USER_ID, ""));
                params.put("type", type);
                params.put("star", stars);
                params.put("start", String.valueOf(start));
                params.put("distance", "");
                params.put("categoriesid", categoriesid);
                params.put("favorites", favorites);
                params.put("order", sortbyValue);
                params.put("search", search);
                return params;
            }
        };
        com.vadevelopment.RedAppetite.AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void setAdapter(ArrayList<com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst> arraylist) {
        if (arrayList.size() < 51 && max < 51) {
            adapter = new com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_ReviewProfile(context, getFragmentManager(), arraylist, com.vadevelopment.RedAppetite.profilesection.ReviewProfile_Fragment.this, false);
            recyclerView.setAdapter(adapter);
        } else if (arrayList.size() < 51 && max > 50) {
            adapter = new com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_ReviewProfile(context, getFragmentManager(), arraylist, com.vadevelopment.RedAppetite.profilesection.ReviewProfile_Fragment.this, true);
            recyclerView.setAdapter(adapter);
        } else if (com.vadevelopment.RedAppetite.profilesection.ReviewSrchFilter_ProfileFragment.reviewfilter_fromcancel == true || com.vadevelopment.RedAppetite.profilesection.Map_ReviewFragment.from_reviewsearch == true) {
            if (max > 50 && (max - start) < 50) {
                adapter = new com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_ReviewProfile(context, getFragmentManager(), arraylist, com.vadevelopment.RedAppetite.profilesection.ReviewProfile_Fragment.this, false);
                recyclerView.setAdapter(adapter);
            } else if ((max > 50 && (max - start) > 50)) {
                adapter = new com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_ReviewProfile(context, getFragmentManager(), arraylist, com.vadevelopment.RedAppetite.profilesection.ReviewProfile_Fragment.this, true);
                recyclerView.setAdapter(adapter);
            }
        } else {
            adapter.notifyDataSetChanged();
        }
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
        dialog.setContentView(R.layout.dialog_sortbysearch);
        LinearLayout approx_lay = (LinearLayout) dialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 40, (h / 2) - 10);
        approx_lay.setLayoutParams(params);

        RelativeLayout rl_cancel = (RelativeLayout) dialog.findViewById(R.id.rl_cancel);
        final TextView atoz = (TextView) dialog.findViewById(R.id.atoz);
        final TextView distance = (TextView) dialog.findViewById(R.id.distance);
        final TextView date = (TextView) dialog.findViewById(R.id.stars);
        date.setText(getString(R.string.date));
        final TextView stars = (TextView) dialog.findViewById(R.id.review);
        stars.setText(getString(R.string.stars));
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
                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_SORTBY, "A-Z");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, cv, null, null);
                textview_sortby.setText(atoz.getText().toString());
                sortby = "A-Z";
                arrayList.clear();
                start = 0;
                GetReview_Task(search.getText().toString());
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
                cv.put("review_sortby", "Distance");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, cv, null, null);
                textview_sortby.setText(distance.getText().toString());
                sortby = "Distance";
                arrayList.clear();
                start = 0;
                GetReview_Task(search.getText().toString());
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
                cv.put("review_sortby", "Stars");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, cv, null, null);
                textview_sortby.setText(stars.getText().toString());
                sortby = "Stars";
                arrayList.clear();
                start = 0;
                GetReview_Task(search.getText().toString());
                dialog.dismiss();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDescendingOrderDate() == true) {
                    sortbyValue = "visited_date DESC";
                } else {
                    sortbyValue = "visited_date ASC";
                }
                ContentValues cv = new ContentValues();
                cv.put("review_sortby", "Date");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, cv, null, null);
                textview_sortby.setText(date.getText().toString());
                sortby = "Date";
                arrayList.clear();
                start = 0;
                GetReview_Task(search.getText().toString());
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
                cv.put("review_sortby", "Recommendation");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, cv, null, null);
                textview_sortby.setText(recommend.getText().toString());
                sortby = "Recommendation";
                arrayList.clear();
                start = 0;
                GetReview_Task(search.getText().toString());
                dialog.dismiss();
            }
        });

        if (dialog.isShowing()) {
        } else {
            dialog.show();
        }
    }

    public void onClickLoadMore() {
        if (start == 0) {
            start = start + 51;
        } else {
            start = start + 50;
        }
        com.vadevelopment.RedAppetite.profilesection.ReviewSrchFilter_ProfileFragment.reviewfilter_fromcancel = false;
        com.vadevelopment.RedAppetite.profilesection.Map_ReviewFragment.from_reviewsearch = false;
        if (start > max) {
            adapter.hideFooter();
            adapter.notifyDataSetChanged();
        } else if ((max - start) < 50) {
            adapter.hideFooter();
            GetReview_Task(search.getText().toString());
        } else {
            GetReview_Task(search.getText().toString());
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
                GetReview_Task("");
                break;
            case R.id.sorting_img:
                if (isAscendingOrder() == true) {
                    editor.putString(com.vadevelopment.RedAppetite.Consts.REVIEW_ORDERTYPE, "descending");
                } else {
                    editor.putString(com.vadevelopment.RedAppetite.Consts.REVIEW_ORDERTYPE, "ascending");
                }
                if (isDescendingOrderStars() == true) {
                    editor.putString(com.vadevelopment.RedAppetite.Consts.REVIEW_ORDERTYPESTARS, "ascending");
                } else {
                    editor.putString(com.vadevelopment.RedAppetite.Consts.REVIEW_ORDERTYPESTARS, "descending");
                }
                if (isDescendingOrderDate() == true) {
                    editor.putString(com.vadevelopment.RedAppetite.Consts.REVIEW_ORDERTYPEDATE, "ascending");
                } else {
                    editor.putString(com.vadevelopment.RedAppetite.Consts.REVIEW_ORDERTYPEDATE, "descending");
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
                } else if (sortby.equalsIgnoreCase("Date")) {
                    if (isDescendingOrderDate() == true) {
                        sortbyValue = "visited_date DESC";
                    } else {
                        sortbyValue = "visited_date ASC";
                    }
                } else {
                    if (isAscendingOrder() == true) {
                        sortbyValue = "happy DESC";
                    } else {
                        sortbyValue = "sad DESC";
                    }
                }
                arrayList = new ArrayList<>();
                start = 0;
                GetReview_Task(search.getText().toString());
                break;
            case R.id.textview_sortby:
                displayBottomDialog();
                break;
            case R.id.activity_search:
               /* try {
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

    private boolean isAscendingOrder() {
        boolean type = false;
        if (preferences.getString(com.vadevelopment.RedAppetite.Consts.REVIEW_ORDERTYPE, "").equalsIgnoreCase("ascending")) {
            type = true;
        }
        return type;
    }

    private boolean isDescendingOrderStars() {
        boolean type = false;
        if (preferences.getString(com.vadevelopment.RedAppetite.Consts.REVIEW_ORDERTYPESTARS, "").equalsIgnoreCase("descending")) {
            type = true;
        }
        return type;
    }

    private boolean isDescendingOrderDate() {
        boolean type = false;
        if (preferences.getString(com.vadevelopment.RedAppetite.Consts.REVIEW_ORDERTYPEDATE, "").equalsIgnoreCase("descending")) {
            type = true;
        }
        return type;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        com.vadevelopment.RedAppetite.profilesection.ReviewSrchFilter_ProfileFragment.reviewfilter_fromcancel = false;
        com.vadevelopment.RedAppetite.profilesection.Map_ReviewFragment.from_reviewsearch = false;
        //  mdashboard.rl_headingwithcount.setVisibility(View.GONE);
    }
}
