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
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.HandyObjects;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_FavoriteProfile;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;
import com.vadevelopment.RedAppetite.database.ParseOpenHelper;
import com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vibrantappz on 3/21/2017.
 */

public class FavoritesProfile_Fragment extends android.app.Fragment implements View.OnClickListener {

    private static String TAG = "FavoritesPro_Fragment";
    DashboardActivity mdashboard;
    private RecyclerView recyclerView;
    private Adapter_FavoriteProfile adapter;
    private ImageView sorting_img;
    private TextView textview_sortby, resettext;
    private Dialog dialog;
    private Context context;
    private EditText search;
    private SQLiteDatabase database;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ArrayList<Skeleton_SearchFirst> arrayList;
    private LinearLayout ll_nolocfound, activity_search;
    private String categoriesid, stars, type, reviews, sortby, sortbyValue;
    public static int start = 0;
    public static boolean fromfvrt_list;
    private int max;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
        context = getActivity();
        Log.e("onCreate", "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_favoritesprofile, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        setToolbar();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        sorting_img = (ImageView) view.findViewById(R.id.sorting_img);
        ll_nolocfound = (LinearLayout) view.findViewById(R.id.ll_nolocfound);
        activity_search = (LinearLayout) view.findViewById(R.id.activity_search);
        resettext = (TextView) view.findViewById(R.id.resettext);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
        sorting_img.setOnClickListener(this);
        textview_sortby.setOnClickListener(this);
        activity_search.setOnClickListener(this);
        resettext.setOnClickListener(this);
        search.setOnClickListener(this);
    }

    private void setData_FromDatabase(View view) {
        database = ParseOpenHelper.getmInstance(context).getWritableDatabase();
        Cursor cursor = database.query(ParseOpenHelper.TABLE_FORFAVORITES, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    categoriesid = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.FAVORITE_CATEGORY));
                    stars = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.FAVORITE_STARS));
                    type = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.FAVORITE_TYPE));
                    reviews = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.FAVORITE_REVIEWS));
                    sortby = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.FAVORITE_SORTBY));
                    cursor.moveToNext();
                }
                cursor.close();
            }
            textview_sortby = (TextView) view.findViewById(R.id.textview_sortby);
            search = (EditText) view.findViewById(R.id.search);
            textview_sortby.setText(sortby);
            if (!HandyObjects.isNetworkAvailable(context)) {
                HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
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
                if (FvrtSrchFilter_ProfileFragment.fvrtfilter_frmcancel == true || Map_FvrtFragment.from_mapfvrt == true) {
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
                    ll_nolocfound.setVisibility(View.INVISIBLE);
                    arrayList = new ArrayList<>();
                    start = 0;
                    //GetFavrt_Task(search.getText().toString());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GetFavrt_Task(search.getText().toString());
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
                    if (!HandyObjects.isNetworkAvailable(context)) {
                        HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
                    } else {
                        resettext.setVisibility(View.INVISIBLE);
                        HandyObjects.expand(search, 160, HandyObjects.getEditextWidth(context));
                        arrayList = new ArrayList<>();
                        start = 0;
                        GetFavrt_Task(search.getText().toString());
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
                    HandyObjects.collapse(search, 160, HandyObjects.getEditextWidth(context) - (2 * resettext.getWidth()));
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
                cv.put(ParseOpenHelper.FAVORITE_SORTBY, "A-Z");
                database.update(ParseOpenHelper.TABLE_FORFAVORITES, cv, null, null);
                textview_sortby.setText(atoz.getText().toString());
                sortby = "A-Z";
                arrayList.clear();
                start = 0;
                GetFavrt_Task(search.getText().toString());
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
                cv.put("favorite_sortby", "Distance");
                database.update(ParseOpenHelper.TABLE_FORFAVORITES, cv, null, null);
                textview_sortby.setText(distance.getText().toString());
                sortby = "Distance";
                arrayList.clear();
                start = 0;
                GetFavrt_Task(search.getText().toString());
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
                cv.put("favorite_sortby", "Stars");
                database.update(ParseOpenHelper.TABLE_FORFAVORITES, cv, null, null);
                textview_sortby.setText(stars.getText().toString());
                sortby = "Stars";
                arrayList.clear();
                start = 0;
                GetFavrt_Task(search.getText().toString());
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
                cv.put("favorite_sortby", "Reviews");
                database.update(ParseOpenHelper.TABLE_FORFAVORITES, cv, null, null);
                textview_sortby.setText(review.getText().toString());
                sortby = "Reviews";
                arrayList.clear();
                start = 0;
                GetFavrt_Task(search.getText().toString());
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
                cv.put("favorite_sortby", "Recommendation");
                database.update(ParseOpenHelper.TABLE_FORFAVORITES, cv, null, null);
                textview_sortby.setText(recommend.getText().toString());
                sortby = "Recommendation";
                arrayList.clear();
                start = 0;
                GetFavrt_Task(search.getText().toString());
                dialog.dismiss();
            }
        });

        if (dialog.isShowing()) {
        } else {
            dialog.show();
        }
    }

    private void setToolbar() {
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);

        mdashboard.rl_headingwithcount.setVisibility(View.VISIBLE);
        mdashboard.text_heading.setText(getString(R.string.profile_favorites));
        mdashboard.centertextsearch.setVisibility(View.GONE);
        mdashboard.text_headingcount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.filter, 0);
        mdashboard.mapButton.setVisibility(View.GONE);
        mdashboard.mapicon.setVisibility(View.VISIBLE);
        mdashboard.mapicon.setImageResource(R.drawable.searchheadermap);
        mdashboard.setting.setVisibility(View.VISIBLE);
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
                fromfvrt_list = true;
                FvrtSrchFilter_ProfileFragment fvrtfilter_frg = new FvrtSrchFilter_ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Consts.FROM_FVRTLISTING, "yup");
                fvrtfilter_frg.setArguments(bundle);
                mdashboard.displayWithoutViewFragmentWithoutv4(fvrtfilter_frg);
            }
        });

        mdashboard.mapicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   mdashboard.displayWithoutViewFragment(new Map_FvrtFragment());
                mdashboard.flipViewFragmentWithanim(new Map_FvrtFragment());

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resettext:
                resettext.setVisibility(View.INVISIBLE);
                search.setText("");
                HandyObjects.expand(search, 160, HandyObjects.getEditextWidth(context));
                arrayList = new ArrayList<>();
                start = 0;
                GetFavrt_Task("");
                break;
            case R.id.sorting_img:
                if (isAscendingOrder() == true) {
                    editor.putString(Consts.FAVORITE_ORDERTYPE, "descending");
                } else {
                    editor.putString(Consts.FAVORITE_ORDERTYPE, "ascending");
                }

                if (isDescendingOrderStars() == true) {
                    editor.putString(Consts.FAVORITE_ORDERTYPESTARS, "ascending");
                } else {
                    editor.putString(Consts.FAVORITE_ORDERTYPESTARS, "descending");
                }
                if (isDescendingOrderReviews() == true) {
                    editor.putString(Consts.FAVORITE_ORDERTYPEREVIEWS, "ascending");
                } else {
                    editor.putString(Consts.FAVORITE_ORDERTYPEREVIEWS, "descending");
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
                arrayList = new ArrayList<>();
                start = 0;
                GetFavrt_Task(search.getText().toString());
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
                HandyObjects.collapse(search, 160, HandyObjects.getEditextWidth(context) - (2 * resettext.getWidth()));
                break;
        }
    }

    private boolean isAscendingOrder() {
        boolean type = false;
        if (preferences.getString(Consts.FAVORITE_ORDERTYPE, "").equalsIgnoreCase("ascending")) {
            type = true;
        }
        return type;
    }

    private boolean isDescendingOrderStars() {
        boolean type = false;
        if (preferences.getString(Consts.FAVORITE_ORDERTYPESTARS, "").equalsIgnoreCase("descending")) {
            type = true;
        }
        return type;
    }

    private boolean isDescendingOrderReviews() {
        boolean type = false;
        if (preferences.getString(Consts.FAVORITE_ORDERTYPEREVIEWS, "").equalsIgnoreCase("descending")) {
            type = true;
        }
        return type;
    }

    public void onClickLoadMore() {
        if (start == 0) {
            start = start + 51;
        } else {
            start = start + 50;
        }
        FvrtSrchFilter_ProfileFragment.fvrtfilter_frmcancel = false;
        Map_FvrtFragment.from_mapfvrt = false;
        if (start > max) {
            adapter.hideFooter();
            adapter.notifyDataSetChanged();
        } else if ((max - start) < 50) {
            adapter.hideFooter();
            GetFavrt_Task(search.getText().toString());
        } else {
            GetFavrt_Task(search.getText().toString());
        }
    }


    private void GetFavrt_Task(final String search) {
        String tag_json_obj = "json_obj_req";
        HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                HandyObjects.FAVBUILDING_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success")) {
                        JSONArray jarry = jboj.getJSONArray("all_list");
                        max = Integer.parseInt(jboj.getString("max"));
                        // mdashboard.text_headingcount.setText(String.valueOf(jarry.length()));
                        //HandyObjects.showAlert();
                        mdashboard.text_headingcount.setText(jboj.getString("max"));
                        if (jarry.length() == 0) {
                            arrayList.clear();
                            setAdapter(arrayList);
                            ll_nolocfound.setVisibility(View.VISIBLE);
                        } else {
                            ll_nolocfound.setVisibility(View.INVISIBLE);
                            for (int i = 0; i < jarry.length(); i++) {
                                Skeleton_SearchFirst ske_search = new Skeleton_SearchFirst();
                                JSONObject jobj_inside = jarry.getJSONObject(i);
                                ske_search.setLid(jobj_inside.getString("lid"));
                                ske_search.setBuildingname(jobj_inside.getString("buildingname"));
                                ske_search.setAddress(jobj_inside.getString("address"));
                                ske_search.setDistance(jobj_inside.getString("distance"));
                                ske_search.setType(jobj_inside.getString("type"));
                                ske_search.setTotal(jobj_inside.getString("total"));
                                ske_search.setIsreviewed(jobj_inside.getString("isReviewed"));
                                ske_search.setAvgrating(jobj_inside.getString("avgRate"));
                                ske_search.setFreeimage(jobj_inside.getString("free"));
                                ske_search.setPremiumimage(jobj_inside.getString("premium"));
                                ske_search.setHappy(jobj_inside.getString("happy"));
                                ske_search.setSad(jobj_inside.getString("sad"));
                                arrayList.add(ske_search);
                            }
                            setAdapter(arrayList);
                            // HandyObjects.showAlert(getActivity(), String.valueOf(jboj.getString("max")));
                        }

                    } else if (jboj.getString("message").equalsIgnoreCase("fail")) {
                        HandyObjects.showAlert(context, jboj.getString("alert"));
                    }
                } catch (Exception e) {
                }
                HandyObjects.stopProgressDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                HandyObjects.stopProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("latitude", preferences.getString(Consts.CURRENT_LATITUDE, ""));
                params.put("longitude", preferences.getString(Consts.CURRENT_LONGITUDE, ""));
                params.put("uid", preferences.getString(Consts.USER_ID, ""));
                params.put("type", type);
                params.put("star", stars);
                params.put("distance", "");
                params.put("start", String.valueOf(start));
                params.put("categoriesid", categoriesid);
                params.put("reviews", reviews);
                params.put("order", sortbyValue);
                params.put("search", search);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    private void setAdapter(ArrayList<Skeleton_SearchFirst> arraylist) {
        //HandyObjects.showAlert(getActivity(), String.valueOf(max));
        if (arrayList.size() < 51 && max < 51) {
            adapter = new Adapter_FavoriteProfile(context, getFragmentManager(), arraylist, com.vadevelopment.RedAppetite.profilesection.FavoritesProfile_Fragment.this, false);
            recyclerView.setAdapter(adapter);

        } else if (arrayList.size() < 51 && max > 50) {
            adapter = new Adapter_FavoriteProfile(context, getFragmentManager(), arraylist, com.vadevelopment.RedAppetite.profilesection.FavoritesProfile_Fragment.this, true);
            recyclerView.setAdapter(adapter);
        } else if (FvrtSrchFilter_ProfileFragment.fvrtfilter_frmcancel == true || Map_FvrtFragment.from_mapfvrt == true) {
            if (max > 50 && (max - start) < 50) {
                adapter = new Adapter_FavoriteProfile(context, getFragmentManager(), arraylist, com.vadevelopment.RedAppetite.profilesection.FavoritesProfile_Fragment.this, false);
                recyclerView.setAdapter(adapter);
            } else if ((max > 50 && (max - start) > 50)) {
                adapter = new Adapter_FavoriteProfile(context, getFragmentManager(), arraylist, com.vadevelopment.RedAppetite.profilesection.FavoritesProfile_Fragment.this, true);
                recyclerView.setAdapter(adapter);
            }

        } else {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FvrtSrchFilter_ProfileFragment.fvrtfilter_frmcancel = false;
        Map_FvrtFragment.from_mapfvrt = false;
        // mdashboard.rl_headingwithcount.setVisibility(View.GONE);
    }
}
