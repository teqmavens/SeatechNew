package com.vadevelopment.RedAppetite.profilesection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vibrantappz on 3/21/2017.
 */

public class EmailProfile_Fragment extends android.app.Fragment {

    private static final String TAG = "EmailProfile_Fragment";
    private TextView email;
    private EditText et_newemail, et_confrmnewemail, et_password;
    DashboardActivity mdashboard;
    private String get_newemail, get_confirmemail, get_pwd;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_emailprofile, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText("Email");
        mdashboard.mapButton.setVisibility(View.VISIBLE);
        mdashboard.mapButton.setText("save");
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.setting.setText("Back");
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        email = (TextView) view.findViewById(R.id.email);
        et_newemail = (EditText) view.findViewById(R.id.et_newemail);
        et_confrmnewemail = (EditText) view.findViewById(R.id.et_confrmnewemail);
        et_password = (EditText) view.findViewById(R.id.et_password);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        if (getArguments() != null) {
            email.setText(getArguments().getString(Consts.PROFILE_EMAIL));
        }

        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        mdashboard.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_newemail = et_newemail.getText().toString();
                get_confirmemail = et_confrmnewemail.getText().toString();
                get_pwd = et_password.getText().toString();

                if (get_newemail.length() == 0) {
                    et_newemail.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(get_newemail).matches()) {
                    et_newemail.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.invalidemail));
                } else if (get_confirmemail.length() == 0) {
                    et_confrmnewemail.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(get_confirmemail).matches()) {
                    et_confrmnewemail.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.invalidemail));
                } else if (!get_confirmemail.equalsIgnoreCase(get_newemail)) {
                    et_confrmnewemail.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.email_notmatching));
                } else if (get_pwd.length() == 0) {
                    et_password.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
                } else {
                    ChangeEmail_Task();
                }
            }
        });
    }

    private void ChangeEmail_Task() {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.CHANGEEMAIL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getString(R.string.emailsuccs_changed));
                        editor.clear();
                        editor.commit();
                        com.vadevelopment.RedAppetite.HandyObjects.deleteAllDatabase(getActivity());
                        Intent ilogin = new Intent(context, com.vadevelopment.RedAppetite.login.ContainerActivity.class);
                        ilogin.putExtra("Login", true);
                        ilogin.putExtra("fromdashboard", "yup");
                        startActivity(ilogin);
                        ((Activity) getActivity()).finish();
                    } else {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, jboj.getString("message"));
                    }

                    /*else if (jboj.getString("message").equalsIgnoreCase("fail")) {
                        HandyObjects.showAlert(context, jboj.getString("alert"));
                    }*/
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
                params.put("password", get_pwd);
                params.put("newemail", get_newemail);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
