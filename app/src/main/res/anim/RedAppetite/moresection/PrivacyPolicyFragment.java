package com.vadevelopment.RedAppetite.moresection;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.HandyObjects;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Vibrant Android on 11-03-2017.
 */

public class PrivacyPolicyFragment extends android.app.Fragment {

    private static String TAG = "PrivacyPolicyFragment";
    DashboardActivity mDashboardActivity;
    private TextView text;
    private Context context;
    private LinearLayout ll_main;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDashboardActivity = (DashboardActivity) getActivity();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//layout tell friend pending
        View view = inflater.inflate(R.layout.privacy, container, false);
        mDashboardActivity.footer.setVisibility(View.VISIBLE);
        mDashboardActivity.centertextsearch.setVisibility(View.VISIBLE);
        mDashboardActivity.centertextsearch.setText(getString(R.string.privacy_policy));
        mDashboardActivity.setting.setVisibility(View.VISIBLE);
        mDashboardActivity.setting.setText(getString(R.string.back));
        mDashboardActivity.setting.setBackgroundResource(R.drawable.backbtn);
        text = (TextView) view.findViewById(R.id.text);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        // slideToAbove();
        mDashboardActivity.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
                //slideToBottom();
            }
        });

        if (!HandyObjects.isNetworkAvailable(context)) {
            HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // HandyObjects.showProgressDialog(context);
                    GetData_Task();
                }
            }, 80);
        }
        return view;
    }

    private void GetData_Task() {
        String tag_json_obj = "json_obj_req";
        HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                HandyObjects.TERMS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        JSONArray jarry = jboj.getJSONArray("all_list");
                        for (int i = 0; i < jarry.length(); i++) {
                            JSONObject jobj = jarry.getJSONObject(i);
                            if (jobj.getString("type").equalsIgnoreCase("privacypolicy")) {
                                text.setText(jobj.getString("editor"));
                            }
                        }
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
                getActivity().getFragmentManager().popBackStack();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

}