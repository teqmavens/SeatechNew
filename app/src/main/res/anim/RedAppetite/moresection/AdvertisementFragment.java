package com.vadevelopment.RedAppetite.moresection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vibrant Android on 11-03-2017.
 */

public class AdvertisementFragment extends android.app.Fragment {

    private static String TAG = "AdvertisementFragment";
    com.vadevelopment.RedAppetite.dashboard.AdvertiseWithUs mdashboard;
    private RelativeLayout rl_main;
    private Button sendmsgbutton;
    private EditText et_message, et_name, et_email;
    private String get_email, get_msg, get_name;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (com.vadevelopment.RedAppetite.dashboard.AdvertiseWithUs) getActivity();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.advertisementfragment, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        setToolbar(view);
    }

    private void setToolbar(View view) {
        mdashboard.footer.setVisibility(View.GONE);
        rl_main = (RelativeLayout) view.findViewById(R.id.rl_main);
        et_message = (EditText) view.findViewById(R.id.et_message);
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_email = (EditText) view.findViewById(R.id.et_email);
        sendmsgbutton = (Button) view.findViewById(R.id.sendmsgbutton);
        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(), sendmsgbutton);
        slideToAbove();
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText(getString(R.string.advertisewith_us));
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.rl_headingwithcount.setVisibility(View.GONE);
        mdashboard.mapButton.setVisibility(View.GONE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.setting.setText(getString(R.string.cancel));
        mdashboard.setting.setBackgroundResource(R.drawable.cancelbutton);
        mdashboard.mapicon.setVisibility(View.GONE);
        mdashboard.settingicon.setVisibility(View.GONE);
        mdashboard.btm_line.setVisibility(View.GONE);
        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideToBottom();
                // getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        sendmsgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_email = et_email.getText().toString();
                get_msg = et_message.getText().toString();
                get_name = et_name.getText().toString();
                if (get_msg.length() == 0) {
                    et_message.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (get_name.length() == 0) {
                    et_name.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (get_email.length() == 0) {
                    et_email.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(get_email).matches()) {
                    et_email.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.invalidemail));
                } else if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
                } else {
                    Advertisement_Task();
                }
            }
        });
    }

    private void slideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(640);
        slide.setFillAfter(true);
        slide.setFillBefore(true);
        rl_main.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rl_main.clearAnimation();
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        rl_main.getWidth(), rl_main.getHeight());
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rl_main.setLayoutParams(lp);
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
        rl_main.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    View vieww = getActivity().getCurrentFocus();
                    if (vieww != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(vieww.getWindowToken(), 0);
                    }
                } catch (Exception e) {
                }
                // getActivity().getFragmentManager().popBackStack();
                ((Activity) getActivity()).finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }


    private void Advertisement_Task() {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.ADVERTISE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success")) {
                        getActivity().getFragmentManager().popBackStack();
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, jboj.getString("mail"));
                    } else {
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
                params.put("message", get_msg);
                params.put("name", get_name);
                params.put("email", get_email);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
