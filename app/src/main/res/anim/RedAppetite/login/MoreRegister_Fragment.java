package com.vadevelopment.RedAppetite.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.vadevelopment.RedAppetite.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by vibrantappz on 9/9/2017.
 */

public class MoreRegister_Fragment extends Fragment {

    private static String TAG = "TermsOfUseFragment";
    private TextView text;
    private Context context;
    private LinearLayout ll_main;
    com.vadevelopment.RedAppetite.login.ContainerActivity mcContainerActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcContainerActivity = (com.vadevelopment.RedAppetite.login.ContainerActivity) getActivity();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//layout tell friend pending
        View view = inflater.inflate(R.layout.privacy, container, false);
        mcContainerActivity.cancelText.setVisibility(View.VISIBLE);
        mcContainerActivity.cancelText.setText("Back");
        mcContainerActivity.cancelText.setBackgroundResource(R.drawable.backbtn);
        mcContainerActivity.centertext.setVisibility(View.VISIBLE);
        mcContainerActivity.next.setVisibility(View.GONE);
        mcContainerActivity.centertext.setText(getString(R.string.terms_ofuse));
        text = (TextView) view.findViewById(R.id.text);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        slideToAbove();
        mcContainerActivity.cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().getSupportFragmentManager().popBackStack();
                slideToBottom();
            }
        });

        if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
            com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
        } else {
            GetData_Task();
        }
        return view;
    }

    private void GetData_Task() {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.TERMS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        JSONArray jarry = jboj.getJSONArray("all_list");
                        for (int i = 0; i < jarry.length(); i++) {
                            JSONObject jobj = jarry.getJSONObject(i);
                            if (jobj.getString("type").equalsIgnoreCase("termsofuse")) {
                                text.setText(jobj.getString("editor"));
                            }
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
        };
        com.vadevelopment.RedAppetite.AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
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
                getActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}