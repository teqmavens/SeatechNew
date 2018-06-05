package com.vadevelopment.RedAppetite.profilesection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.SelectLoginRegister;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vibrantappz on 3/21/2017.
 */

public class DeleteProfile_Fragment extends android.app.Fragment {

    private static String TAG = "DeleteProfile_Fragment";
    DashboardActivity mdashboard;
    private EditText et_message, et_pwd;
    private String get_message, get_pwd;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    Button sendmsgbutton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_deleteprofile, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText("Delete Profile");
        mdashboard.mapButton.setVisibility(View.GONE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.setting.setText("Back");
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        sendmsgbutton = (Button) view.findViewById(R.id.sendmsgbutton);
        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(), sendmsgbutton);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        et_message = (EditText) view.findViewById(R.id.et_message);
        et_pwd = (EditText) view.findViewById(R.id.et_pwd);

        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        sendmsgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_message = et_message.getText().toString();
                get_pwd = et_pwd.getText().toString();

                if (get_pwd.length() == 0) {
                    et_pwd.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
                } else {
                    DeleteProfile_Task();
                }
            }
        });
    }

    private void DeleteProfile_Task() {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.DELETEPROFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getString(R.string.deleteuser_profile));
                        editor.clear();
                        editor.commit();
                        com.vadevelopment.RedAppetite.HandyObjects.deleteAllDatabase(context);
                        Intent ilogin = new Intent(context, SelectLoginRegister.class);
                        /*ilogin.putExtra("Login", true);
                        ilogin.putExtra("fromdashboard", "yup");*/
                        startActivity(ilogin);
                        getActivity().finish();
                    } else {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, jboj.getString("message"));
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
                params.put("message", get_message);
                params.put("password", get_pwd);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
