package com.vadevelopment.RedAppetite.searchsection;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.HandyObjects;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_filtercategorySearch;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;
import com.vadevelopment.RedAppetite.database.ParseOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by vibrantappz on 3/22/2017.
 */

public class FilterCategory_SearchFragment extends android.app.Fragment {

    private static String TAG = "FilterCategory";
    DashboardActivity mdashboard;
    private RelativeLayout rl_mode;
    private LinearLayout ll_main;
    private ArrayList<Category_Skeleton> arraylist;
    private Adapter_filtercategorySearch adapter;
    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private SQLiteDatabase database;
    private Context context;
    Cursor cursor;
    private String categoriesid;
    public static CheckBox checkbox_selectall;
    public static String finalcatgid_string = "";
    private ArrayList<String> selected_catgid, only_catgid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_filtercategorysearch, container, false);
        rl_mode = (RelativeLayout) view.findViewById(R.id.rl_mode);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        checkbox_selectall = (CheckBox) view.findViewById(R.id.checkbox_selectall);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        database = ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();
        mdashboard.footer.setVisibility(View.GONE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText(getString(R.string.Category));
        mdashboard.setting.setText(getString(R.string.back));
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        mdashboard.mapButton.setVisibility(View.GONE);
        //  slideToAbove();

        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mdashboard.displayWithoutViewFragment(new Setting_SearchFragment());
                if (checkbox_selectall.isChecked()) {
                    finalcatgid_string = "All";
                    getActivity().getFragmentManager().popBackStack();
                } else {
                    if (Adapter_filtercategorySearch.checkbox_checkedcatgid.size() > 0) {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < Adapter_filtercategorySearch.checkbox_checkedcatgid.size(); i++) {
                            builder.append(Adapter_filtercategorySearch.checkbox_checkedcatgid.get(i) + ",");
                        }
                        if (builder.toString().endsWith(",")) {
                            finalcatgid_string = builder.toString().substring(0, builder.toString().length() - 1);
                        }
                        //  HandyObjects.showAlert(getActivity(), finalcatgid_string);
                        getActivity().getFragmentManager().popBackStack();
                    } else {
                        finalcatgid_string = "";
                        HandyObjects.showAlert(getActivity(), getString(R.string.selectcategory));
                    }
                }

            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setData_Fromdatabase();
            }
        });


        checkbox_selectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arraylist.size() > 0) {
                    if (checkbox_selectall.isChecked() == true) {
                        selected_catgid = new ArrayList<>();
                        setAdapter(arraylist, false);
                        // HandyObjects.showAlert(getActivity(), "checked");
                    } else {
                        setAdapter(arraylist, true);
                        // HandyObjects.showAlert(getActivity(), "Unchecked");
                    }
                }
            }
        });
        return view;
    }

    private void slideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(1600);
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

    private void GetCategory_Task() {
        String tag_json_obj = "json_obj_req";
        HandyObjects.showProgressDialog(context);
        final StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                HandyObjects.SEARCHCATEGORY_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success")) {
                        arraylist = new ArrayList<>();
                        only_catgid = new ArrayList<>();
                        JSONArray jarry = jboj.getJSONArray("all_list");
                        for (int i = 0; i < jarry.length(); i++) {
                            Category_Skeleton cat_ske = new Category_Skeleton();
                            cat_ske.setId(jarry.getJSONObject(i).getString("id"));
                            cat_ske.setName(jarry.getJSONObject(i).getString("name"));
                            cat_ske.setFree_image(jarry.getJSONObject(i).getString("free"));
                            arraylist.add(cat_ske);
                            only_catgid.add(jarry.getJSONObject(i).getString("id"));
                        }
                        setAdapter(arraylist, false);
                    } else {
                        HandyObjects.showAlert(context, jboj.getString("message"));
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
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void setAdapter(ArrayList<Category_Skeleton> arraylist, boolean b) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Adapter_filtercategorySearch(getActivity(), arraylist, selected_catgid, only_catgid, b);
        recyclerView.setAdapter(adapter);
    }

    private void setData_Fromdatabase() {
        if (getArguments() != null && getArguments().getString(Consts.ONCATEGORY_FROM).equalsIgnoreCase("favorite")) {
            cursor = database.query(ParseOpenHelper.TABLE_FORFAVORITES, null, null, null, null, null, null);
        } else if (getArguments() != null && getArguments().getString(Consts.ONCATEGORY_FROM).equalsIgnoreCase("review")) {
            cursor = database.query(ParseOpenHelper.TABLE_FORREVIEW, null, null, null, null, null, null);
        } else if (getArguments() != null && getArguments().getString(Consts.ONCATEGORY_FROM).equalsIgnoreCase("event")) {
            cursor = database.query(ParseOpenHelper.TABLE_FOREVENT, null, null, null, null, null, null);
        } else {
            cursor = database.query(ParseOpenHelper.TABLE_FORSEARCH, null, null, null, null, null, null);
        }

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    if (getArguments() != null && getArguments().getString(Consts.ONCATEGORY_FROM).equalsIgnoreCase("favorite")) {
                        categoriesid = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.FAVORITE_CATEGORY));
                    } else if (getArguments() != null && getArguments().getString(Consts.ONCATEGORY_FROM).equalsIgnoreCase("review")) {
                        categoriesid = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.REVIEW_CATEGORY));
                    } else if (getArguments() != null && getArguments().getString(Consts.ONCATEGORY_FROM).equalsIgnoreCase("event")) {
                        categoriesid = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.EVENT_CATEGORY));
                    } else {
                        categoriesid = cursor.getString(cursor.getColumnIndex("search_category"));
                    }
                    cursor.moveToNext();
                }
                cursor.close();
            }
            selected_catgid = new ArrayList<>();
            if (categoriesid.equalsIgnoreCase("All")) {
                checkbox_selectall.setChecked(true);
            } else {
                checkbox_selectall.setChecked(false);
                String[] split = categoriesid.split(",");
                selected_catgid = new ArrayList<>(Arrays.asList(split));
            }
        }
        if (!HandyObjects.isNetworkAvailable(context)) {
            HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    GetCategory_Task();
                }
            }, 100);

        }
    }
}


