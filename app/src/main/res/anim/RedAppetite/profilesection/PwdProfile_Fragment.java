package com.vadevelopment.RedAppetite.profilesection;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vibrantappz on 3/21/2017.
 */

public class PwdProfile_Fragment extends android.app.Fragment {

    private static String TAG = "PwdProfile_Fragment";
    DashboardActivity mdashboard;
    private Button btn_frgtpwd;
    private Dialog openDialog;
    private EditText et_currpwd, et_newpwd, et_cnfpwd;
    private String get_currpwd, get_newpwd, get_cnfpwd;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private SQLiteDatabase database;
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
        View view = inflater.inflate(R.layout.frgm_pwdprofile, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        btn_frgtpwd = (Button) view.findViewById(R.id.btn_frgtpwd);
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText("Password");
        mdashboard.mapButton.setVisibility(View.VISIBLE);
        mdashboard.mapButton.setText("save");
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.setting.setText("Back");
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(), btn_frgtpwd);
        et_currpwd = (EditText) view.findViewById(R.id.et_currpwd);
        et_newpwd = (EditText) view.findViewById(R.id.et_newpwd);
        et_cnfpwd = (EditText) view.findViewById(R.id.et_cnfpwd);
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        btn_frgtpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frgtpwdDialog();
            }
        });

        mdashboard.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_currpwd = et_currpwd.getText().toString();
                get_newpwd = et_newpwd.getText().toString();
                get_cnfpwd = et_cnfpwd.getText().toString();

                if (get_currpwd.length() == 0) {
                    et_currpwd.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (get_newpwd.length() == 0) {
                    et_newpwd.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (get_cnfpwd.length() == 0) {
                    et_cnfpwd.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (!get_newpwd.equalsIgnoreCase(get_cnfpwd)) {
                    et_newpwd.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.pwdnotmatch));
                } else if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
                } else {
                    ChangePwd_Task();
                }
            }
        });

    }


    private void ChangePwd_Task() {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.CHANGEPWD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getString(R.string.pwdsuccs_changed));
                        editor.clear();
                        editor.commit();
                        com.vadevelopment.RedAppetite.HandyObjects.deleteAllDatabase(context);
                        Intent ilogin = new Intent(context, com.vadevelopment.RedAppetite.login.ContainerActivity.class);
                        ilogin.putExtra("Login", true);
                        ilogin.putExtra("fromdashboard", "yup");
                        startActivity(ilogin);
                        getActivity().finish();
                    } /*else if (jboj.getString("message").equalsIgnoreCase("fail")) {
                        HandyObjects.showAlert(context, jboj.getString("alert"));
                    }*/ else {
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
                params.put("currentpassword", get_currpwd);
                params.put("newpassword", get_newpwd);
                return params;
            }
        };
        com.vadevelopment.RedAppetite.AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void frgtpwdDialog() {
        openDialog = new Dialog(getActivity());
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setCanceledOnTouchOutside(false);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        openDialog.show();
        Window window = openDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window.setAttributes(wlp);
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View vview = inflater.inflate(R.layout.forgotpasswordalert, null, false);
        openDialog.setContentView(vview);
        LinearLayout approx_lay = (LinearLayout) openDialog.findViewById(R.id.approx_lay);
      /*  Double d = new Double(2.99);
        int hei = d.intValue();*/
        int valueinpix = (int) getResources().getDimension(R.dimen.newforcheck);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width - valueinpix, (height / 3) + 20);
        approx_lay.setLayoutParams(params);

        TextView text_ok = (TextView) vview.findViewById(R.id.text_ok);
        TextView text_cancel = (TextView) vview.findViewById(R.id.text_cancel);
        final EditText et_email = (EditText) vview.findViewById(R.id.et_email);

        text_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getemail = et_email.getText().toString().trim();
                if (getemail.length() == 0) {
                    et_email.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.emailempty));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(getemail).matches()) {
                    et_email.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.invalidemail));
                } else if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
                    openDialog.hide();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
                } else {
                    openDialog.hide();
                    FrgtPwd_Task(getemail);
                }
            }
        });

        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.dismiss();
            }
        });
        openDialog.show();
    }

    private void FrgtPwd_Task(final String getemail) {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.FRGTPWD_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, jboj.getString("message"));
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
                params.put("email", getemail);
                return params;
            }
        };
        com.vadevelopment.RedAppetite.AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
