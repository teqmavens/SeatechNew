package com.vadevelopment.RedAppetite.newssection;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.adapters.Adapters.AdapterNews;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;
import com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vibrant Android on 14-03-2017.
 */

public class NewsFragment extends android.app.Fragment implements View.OnClickListener {

    private static String TAG = "NewsFragment";
    private DashboardActivity mdashboard;
    private RecyclerView recyclerView;
    private AdapterNews adapter;
    private ImageView sorting_img;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    SQLiteDatabase database;
    private Context context;
    private EditText search;
    private ArrayList<Skeleton_SearchFirst> arrayList;
    String radius, sortby, categoriesid, stars, type, typeone, reviews, favorites, modeposition, sortbyValue, which_latlong;
    TextView textview_sortby, resettext;
    private LinearLayout ll_nolocfound, activity_search;
    private Dialog dialog;
    private int max;
    Calendar myCalendar;
    EditText et_date;
    DatePickerDialog.OnDateSetListener date;
    public static int start = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newsmainlayout, container, false);
        mdashboard = (DashboardActivity) getActivity();
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        setToolbar();
        context = getActivity();
        sorting_img = (ImageView) view.findViewById(R.id.sorting_img);
        search = (EditText) view.findViewById(R.id.search);
        textview_sortby = (TextView) view.findViewById(R.id.textview_sortby);
        ll_nolocfound = (LinearLayout) view.findViewById(R.id.ll_nolocfound);
        activity_search = (LinearLayout) view.findViewById(R.id.activity_search);
        resettext = (TextView) view.findViewById(R.id.resettext);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        sorting_img.setOnClickListener(this);
        textview_sortby.setOnClickListener(this);
        activity_search.setOnClickListener(this);
        search.setOnClickListener(this);
        resettext.setOnClickListener(this);
        //recyclerView.setAdapter(adapter);
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

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

        setData_FromDatabase();


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
            if (com.vadevelopment.RedAppetite.newssection.Filter_NewsFragment.newsfilter_fromcancel == true || Setting_EventFragment.eventsetting_fromcancel) {
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
            } else {
                ll_nolocfound.setVisibility(View.INVISIBLE);
                arrayList = new ArrayList<>();
                start = 0;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetSearchTask_Task(search.getText().toString(), "");
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
                        GetSearchTask_Task(search.getText().toString(), "");
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

      /*  search.setOnTouchListener(new View.OnTouchListener() {
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
        Cursor cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    categoriesid = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_CATEGORY));
                    stars = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_STARS));
                    type = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_TYPE));
                    typeone = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_TYPEONE));
                    reviews = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_REVIEWS));
                    favorites = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_FAVORITES));
                    modeposition = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_MODEPOSI));
                    radius = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_RADIUS));
                    sortby = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_SORTBY));
                    which_latlong = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_WHICHLATLONG));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
    }


    private boolean isAscendingOrder() {
        boolean type = false;
        if (preferences.getString(Consts.EVENT_ORDERTYPE, "").equalsIgnoreCase("ascending")) {
            type = true;
        }
        return type;
    }

    private boolean isDescendingOrderStars() {
        boolean type = false;
        if (preferences.getString(Consts.EVENT_ORDERTYPESTARS, "").equalsIgnoreCase("descending")) {
            type = true;
        }
        return type;
    }

    private boolean isDescendingOrderReviews() {
        boolean type = false;
        if (preferences.getString(Consts.EVENT_ORDERTYPEREVIEWS, "").equalsIgnoreCase("descending")) {
            type = true;
        }
        return type;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        com.vadevelopment.RedAppetite.newssection.Filter_NewsFragment.newsfilter_fromcancel = false;
        Setting_EventFragment.eventsetting_fromcancel = false;
        search.setText("");
    }

    private void GetSearchTask_Task(final String search, final String date) {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.EVENT_URL, new Response.Listener<String>() {

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
                            ll_nolocfound.setVisibility(View.VISIBLE);
                            setAdapter(arrayList);
                        } else {
                            ll_nolocfound.setVisibility(View.INVISIBLE);
                            for (int i = 0; i < jarry.length(); i++) {
                                Skeleton_SearchFirst ske_search = new Skeleton_SearchFirst();
                                JSONObject jobj_inside = jarry.getJSONObject(i);
                                ske_search.setLid(jobj_inside.getString("nid"));
                                ske_search.setBuildingname(jobj_inside.getString("building"));
                                ske_search.setAddress(jobj_inside.getString("address"));
                                ske_search.setDistance(jobj_inside.getString("distance"));
                                ske_search.setTypeone(jobj_inside.getString("type1"));
                                ske_search.setType(jobj_inside.getString("type"));
                                ske_search.setTotal(jobj_inside.getString("total"));
                                ske_search.setAvgrating(jobj_inside.getString("avgRate"));
                                ske_search.setIsreviewed(jobj_inside.getString("isReviewed"));
                                ske_search.setIsfav(jobj_inside.getString("isFav"));
                                ske_search.setFreeimage(jobj_inside.getString("free"));
                                ske_search.setPremiumimage(jobj_inside.getString("premium"));
                                ske_search.setHappy(jobj_inside.getString("happy"));
                                ske_search.setSad(jobj_inside.getString("sad"));
                                ske_search.setVisited_date(jobj_inside.getString("date"));
                                ske_search.setHeading(jobj_inside.getString("headline"));
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
                params.put("type1", typeone);
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
                params.put("date", date);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void setAdapter(ArrayList<Skeleton_SearchFirst> arraylist) {
        //  adapterForSearch = new AdapterSearch(getContext(), getFragmentManager(), arraylist);
        if (arrayList.size() < 50) {
            adapter = new AdapterNews(context, getFragmentManager(), arraylist, com.vadevelopment.RedAppetite.newssection.NewsFragment.this, false);
            recyclerView.setAdapter(adapter);
        } else if (arraylist.size() > 49 && arraylist.size() < 51) {
            adapter = new AdapterNews(context, getFragmentManager(), arraylist, com.vadevelopment.RedAppetite.newssection.NewsFragment.this, true);
            recyclerView.setAdapter(adapter);
        } else if (com.vadevelopment.RedAppetite.newssection.Filter_NewsFragment.newsfilter_fromcancel == true || Setting_EventFragment.eventsetting_fromcancel == true) {
            adapter = new AdapterNews(context, getFragmentManager(), arraylist, com.vadevelopment.RedAppetite.newssection.NewsFragment.this, true);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    private void setToolbar() {
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.searchicon.setImageResource(R.drawable.srchicon);
       // mdashboard.searchicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mdashboard.searchtext.setTextColor(new Color().parseColor("#410202"));
     //   mdashboard.profileicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mdashboard.profileicon.setImageResource(R.drawable.prfliconunselect);
        mdashboard.profiletext.setTextColor(new Color().parseColor("#410202"));
       // mdashboard.moreicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mdashboard.moreicon.setImageResource(R.drawable.more);
        mdashboard.moretext.setTextColor(new Color().parseColor("#410202"));
       // mdashboard.newsfeedicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
       // mdashboard.newsfeedicon.setImageResource(R.drawable.calendar_ftr);
        mdashboard.newsfeedicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.newcolortext));
        mdashboard.newstext.setTextColor(new Color().parseColor("#e8dddd"));
        mdashboard.adverticeicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mdashboard.adverticetext.setTextColor(new Color().parseColor("#410202"));

        mdashboard.setting.setVisibility(View.GONE);
        mdashboard.textsearchimage.setVisibility(View.VISIBLE);
        mdashboard.rl_headingwithcount.setVisibility(View.GONE);
        mdashboard.search.setText(getString(R.string.Events));
        mdashboard.centertextsearch.setVisibility(View.GONE);
        mdashboard.mapButton.setVisibility(View.GONE);
        mdashboard.mapicon.setVisibility(View.VISIBLE);
        mdashboard.mapicon.setImageResource(R.drawable.calendar_events);
        mdashboard.settingicon.setVisibility(View.VISIBLE);
        mdashboard.settingicon.setImageResource(R.drawable.settingiconnew);
        mdashboard.btm_line.setVisibility(View.VISIBLE);

        mdashboard.textsearchimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdashboard.displayWithoutViewFragmentWithoutv4(new com.vadevelopment.RedAppetite.newssection.Filter_NewsFragment());
            }
        });

        mdashboard.settingicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdashboard.displayWithoutViewFragmentWithoutv4(new Setting_EventFragment());
            }
        });

        mdashboard.mapicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog_Date();
              /*  new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/
            }
        });

        mdashboard.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mdashboard.displayWithoutViewFragment(new AdvertisementFragment());
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

    private void updateLabel() {
        String myFormat = "dd.MM.yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
       /* arrayList.clear();
        start = 0;
        GetSearchTask_Task(search.getText().toString(), sdf.format(myCalendar.getTime()));*/
        // displayDialog_Date();
        et_date.setText(sdf.format(myCalendar.getTime()));
    }

    private void displayDialog_Date() {
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        final Dialog dialog_date = new Dialog(getActivity());
        dialog_date.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_date.setCanceledOnTouchOutside(false);
        Window window = dialog_date.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.y = 30;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window.setAttributes(wlp);
        dialog_date.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        dialog_date.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_date.setContentView(R.layout.dialog_afterdateselect);
        LinearLayout approx_lay = (LinearLayout) dialog_date.findViewById(R.id.approx_lay);
        int valueinpix = (int) getResources().getDimension(R.dimen.newforcheck);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - valueinpix, (h / 3) - 10);
        approx_lay.setLayoutParams(params);
        et_date = (EditText) dialog_date.findViewById(R.id.et_date);
        final TextView showall = (TextView) dialog_date.findViewById(R.id.showall);
        final TextView search_new = (TextView) dialog_date.findViewById(R.id.search);
        final TextView cancel = (TextView) dialog_date.findViewById(R.id.cancel);
        // et_date.setText(date);
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_date.dismiss();
                arrayList = new ArrayList<>();
                start = 0;
                GetSearchTask_Task(search.getText().toString(), "");
            }
        });

        search_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_date.dismiss();
                arrayList.clear();
                start = 0;
                GetSearchTask_Task(search.getText().toString(), et_date.getText().toString());
            }
        });

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        et_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b == true) {
                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_date.dismiss();
            }
        });
        dialog_date.show();
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 44, (h / 2) - 10);
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
                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_SORTBY, "A-Z");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, cv, null, null);
                textview_sortby.setText(atoz.getText().toString());
                sortby = "A-Z";
                arrayList.clear();
                start = 0;
                GetSearchTask_Task(search.getText().toString(), "");
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
                cv.put("event_sortby", "Distance");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, cv, null, null);
                textview_sortby.setText(distance.getText().toString());
                sortby = "Distance";
                arrayList.clear();
                start = 0;
                GetSearchTask_Task(search.getText().toString(), "");
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
                cv.put("event_sortby", "Stars");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, cv, null, null);
                textview_sortby.setText(stars.getText().toString());
                sortby = "Stars";
                arrayList.clear();
                start = 0;
                GetSearchTask_Task(search.getText().toString(), "");
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
                cv.put("event_sortby", "Reviews");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, cv, null, null);
                textview_sortby.setText(review.getText().toString());
                sortby = "Reviews";
                arrayList.clear();
                start = 0;
                GetSearchTask_Task(search.getText().toString(), "");
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
                cv.put("event_sortby", "Recommendation");
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, cv, null, null);
                textview_sortby.setText(recommend.getText().toString());
                sortby = "Recommendation";
                arrayList.clear();
                start = 0;
                GetSearchTask_Task(search.getText().toString(), "");
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
        com.vadevelopment.RedAppetite.newssection.Filter_NewsFragment.newsfilter_fromcancel = false;
        Setting_EventFragment.eventsetting_fromcancel = false;
        if (start > max) {
            adapter.hideFooter();
            adapter.notifyDataSetChanged();
        } else {
            GetSearchTask_Task(search.getText().toString(), "");
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
                GetSearchTask_Task("", "");
                break;
            case R.id.textview_sortby:
                displayBottomDialog();
                break;
            case R.id.sorting_img:
                if (isAscendingOrder() == true) {
                    editor.putString(Consts.EVENT_ORDERTYPE, "descending");
                } else {
                    editor.putString(Consts.EVENT_ORDERTYPE, "ascending");
                }
                if (isDescendingOrderStars() == true) {
                    editor.putString(Consts.EVENT_ORDERTYPESTARS, "ascending");
                } else {
                    editor.putString(Consts.EVENT_ORDERTYPESTARS, "descending");
                }
                if (isDescendingOrderReviews() == true) {
                    editor.putString(Consts.EVENT_ORDERTYPEREVIEWS, "ascending");
                } else {
                    editor.putString(Consts.EVENT_ORDERTYPEREVIEWS, "descending");
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
                //arrayList.clear();
                arrayList = new ArrayList<>();
                start = 0;
                GetSearchTask_Task(search.getText().toString(), "");
                break;
            case R.id.activity_search:
              /*  try {
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
