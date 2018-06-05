package com.vadevelopment.RedAppetite.login;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.contact.ContactFragment;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vibrant Android on 08-03-2017.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static String TAG = "LoginFragment";
    private TextView bottomtext, toptext;
    private Button btn_login, btn_register, btn_frgtpwd;
    private String get_email, get_pwd;
    private EditText et_email, et_pwd;
    private Dialog openDialog;
    private LinearLayout ll_main;
    com.vadevelopment.RedAppetite.login.ContainerActivity mContainerActivity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    SQLiteDatabase database;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContainerActivity = (com.vadevelopment.RedAppetite.login.ContainerActivity) getActivity();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frgm_login, container, false);
        getActivity().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bottomtext = (TextView) view.findViewById(R.id.textBottom);
        btn_frgtpwd = (Button) view.findViewById(R.id.btn_frgtpwd);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_register = (Button) view.findViewById(R.id.btn_register);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_pwd = (EditText) view.findViewById(R.id.et_pwd);
        toptext = (TextView) view.findViewById(R.id.toptext);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        mContainerActivity.cancelText.setBackgroundResource(R.drawable.cancelbutton);
        mContainerActivity.cancelText.setVisibility(View.VISIBLE);
        mContainerActivity.cancelText.setText("Abbrechen");

        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(), btn_frgtpwd);
        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(), btn_login);
        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(), btn_register);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_frgtpwd.setOnClickListener(this);
        slideToAbove();
        if (getArguments() != null && getArguments().getString("fromdashboard").equalsIgnoreCase("yup")) {
            if (getArguments().getString("forwhich").equalsIgnoreCase("review")) {
                toptext.setText(getString(R.string.loginfirst_addreview));
            } else if (getArguments().getString("forwhich").equalsIgnoreCase("fvrt")) {
                toptext.setText(getString(R.string.loginfirst_addfvrt));
            } else if (getArguments().getString("forwhich").equalsIgnoreCase("normal")) {
                toptext.setText(getString(R.string.loginText));
            }
           /* slideToAbove();*/
        } else if (getArguments() != null && getArguments().getString("fromdashboard").equalsIgnoreCase("nop")) {
            if (getArguments().getString("forwhich").equalsIgnoreCase("normal")) {
                toptext.setText(getString(R.string.loginText));
            }
        } else if (getArguments() != null && getArguments().getString("fromdashboard").equalsIgnoreCase("nop_fromregister")) {
            if (ContactFragment.from_contact == false) {
                showDialog(getArguments().getString("email"));
            }

        }

        mContainerActivity.cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  if (getArguments() != null && getArguments().getString("fromdashboard").equalsIgnoreCase("yup")) {
                slideToBottom();
                /*} else {
                    getActivity().finish();
                }*/
            }
        });

        mContainerActivity.centertext.setText("Login");
        mContainerActivity.next.setVisibility(View.GONE);
        bottomtext.setText("Nach der Registrierung können Sie\n• Bewertungen abgeben\n• den Newsletter erhalten\n (Events, News und Updates über das  Rotlicht und die App),\n• Ihre Favoriten speichern.");
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //    showDialog();
                get_email = et_email.getText().toString();
                get_pwd = et_pwd.getText().toString();
                if (get_email.length() == 0) {
                    et_email.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.enteremail_orpwd));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(get_email).matches()) {
                    et_email.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.invalidemail));
                } else if (get_pwd.length() == 0) {
                    et_pwd.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.enteremail_orpwd));
                } else if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
                } else {
                    Login_Task();
                }
                break;
            case R.id.btn_frgtpwd:
                frgtpwdDialog();
                break;
            case R.id.btn_register:
                if (getArguments() != null && getArguments().getString("fromdashboard").equalsIgnoreCase("yup")) {
                    com.vadevelopment.RedAppetite.login.RegisterFragment af = new com.vadevelopment.RedAppetite.login.RegisterFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "login");
                    bundle.putString("fromdashboard", "yup");
                    af.setArguments(bundle);
                    mContainerActivity.displayWithViewFragment(af);
                } else {
                    com.vadevelopment.RedAppetite.login.RegisterFragment af = new com.vadevelopment.RedAppetite.login.RegisterFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "login");
                    bundle.putString("fromdashboard", "nop");
                    af.setArguments(bundle);
                    mContainerActivity.displayWithViewFragment(af);
                }
                // mContainerActivity.displayWithoutViewFragment(new RegisterFragment());
                break;
        }
    }

    private void showDialog(final String email) {
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
        openDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View vview = inflater.inflate(R.layout.verifyemail_dialog, null, false);
        openDialog.setContentView(vview);
        LinearLayout approx_lay = (LinearLayout) openDialog.findViewById(R.id.approx_lay);
        int valueinpix = (int) getResources().getDimension(R.dimen.newforcheck);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width - valueinpix, (height / 3) + 60);
        approx_lay.setLayoutParams(params);
        TextView text_ok = (TextView) vview.findViewById(R.id.text_ok);
        TextView resendemail = (TextView) vview.findViewById(R.id.resendemail);
        TextView text_contact = (TextView) vview.findViewById(R.id.text_contact);

        resendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                ResendMail_Task(email);
            }
        });

        text_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
            }
        });

        text_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                mContainerActivity.displayWithViewFragment(new ContactFragment());
            }
        });
        openDialog.show();
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


        openDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
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

    private void slideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(640);
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
              /*  LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ll_main.getWidth(), ll_main.getHeight());
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rl_main.setLayoutParams(lp);*/
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void ResendMail_Task(final String getemail) {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.RESEND_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, jboj.getString("mail"));
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
                params.put("email", getemail);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void Login_Task() {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success")) {
                        Cursor cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, null, null, null, null, null, null);
                        Cursor cursor_event = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, null, null, null, null, null, null);
                        Cursor cursor_favrt = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORFAVORITES, null, null, null, null, null, null);
                        Cursor cursor_review = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, null, null, null, null, null, null);
                        if (cursor != null) {
                            if (cursor.getCount() > 0) {
                            } else {
                                ContentValues cv = new ContentValues();
                                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_CATEGORY, "All");
                                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_STARS, "Any");
                                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_TYPE, "Free");
                                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_REVIEWS, "0");
                                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_FAVORITES, "0");
                                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_MODEPOSI, "Current Position");
                                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_RADIUS, "100");
                                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_SORTBY, "Distance");
                                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_WHICHLATLONG, "current");
                                cv.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.SEARCH_WHICHLATLONGADDRS, "current");
                                long idd = database.insert(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, null, cv);
                                Log.e("table", String.valueOf(idd));
                            }
                            cursor.close();
                        }

                        if (cursor_event != null) {
                            if (cursor_event.getCount() > 0) {
                            } else {
                                ContentValues cv_event = new ContentValues();
                                cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_CATEGORY, "All");
                                cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_STARS, "Any");
                                cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_TYPE, "Normal");
                                cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_TYPEONE, "Free");
                                cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_REVIEWS, "0");
                                cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_FAVORITES, "0");
                                cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_MODEPOSI, "Current Position");
                                cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_RADIUS, "100");
                                cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_SORTBY, "Distance");
                                cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_WHICHLATLONG, "current");
                                cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_WHICHLATLONGADDRS, "current");
                                long idd = database.insert(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, null, cv_event);
                                Log.e("table", String.valueOf(idd));
                            }
                            cursor_event.close();
                        }

                        if (cursor_favrt != null) {
                            if (cursor_favrt.getCount() > 0) {
                            } else {
                                ContentValues cv_fvrt = new ContentValues();
                                cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_CATEGORY, "All");
                                cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_STARS, "Any");
                                cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_TYPE, "Free");
                                cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_REVIEWS, "0");
                                cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_SORTBY, "Distance");
                                long idd = database.insert(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORFAVORITES, null, cv_fvrt);
                                Log.e("table", String.valueOf(idd));
                            }
                            cursor_favrt.close();
                        }

                        if (cursor_review != null) {
                            if (cursor_review.getCount() > 0) {
                            } else {
                                ContentValues cv_review = new ContentValues();
                                cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_CATEGORY, "All");
                                cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_STARS, "Any");
                                cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_TYPE, "Free");
                                cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_FAVORITE, "0");
                                cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_SORTBY, "Date");
                                long idd = database.insert(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, null, cv_review);
                                Log.e("table", String.valueOf(idd));
                            }
                            cursor_review.close();
                        }
                        editor.putString(Consts.SEARCH_ORDERTYPE, "ascending");
                        editor.putString(Consts.SEARCH_ORDERTYPESTARS, "descending");
                        editor.putString(Consts.SEARCH_ORDERTYPEREVIEWS, "descending");
                        editor.putString(Consts.EVENT_ORDERTYPESTARS, "descending");
                        editor.putString(Consts.EVENT_ORDERTYPEREVIEWS, "descending");

                        editor.putString(Consts.FAVORITE_ORDERTYPESTARS, "descending");
                        editor.putString(Consts.FAVORITE_ORDERTYPEREVIEWS, "descending");
                        editor.putString(Consts.REVIEW_ORDERTYPESTARS, "descending");
                        editor.putString(Consts.REVIEW_ORDERTYPEDATE, "descending");
                        editor.putString(Consts.EVENT_ORDERTYPE, "ascending");
                        editor.putString(Consts.FAVORITE_ORDERTYPE, "ascending");
                        editor.putString(Consts.REVIEW_ORDERTYPE, "ascending");
                        editor.putBoolean("login", true);
                        editor.putString(Consts.USER_ID, jboj.getJSONObject("all_list").getString("uid"));
                        editor.commit();

                        if (getArguments().getString("forwhich").equalsIgnoreCase("fvrt")) {
                            /*Intent i = new Intent(getActivity(), DashboardActivity.class);
                            i.putExtra("fromskip", "for_fvrt");
                            startActivity(i);*/
                            getActivity().finish();
                        } else if (getArguments().getString("forwhich").equalsIgnoreCase("review")) {
                           /* Intent i = new Intent(getActivity(), DashboardActivity.class);
                            i.putExtra("fromskip", "for_review");
                            startActivity(i);*/
                            getActivity().finish();
                        } else {
                            Intent i = new Intent(getActivity(), DashboardActivity.class);
                            i.putExtra("fromskip", "nop_ftime");
                            startActivity(i);
                            getActivity().finish();
                        }

                    } else if (jboj.getString("message").equalsIgnoreCase("fail")) {
                        if (jboj.getString("alert").equalsIgnoreCase("Please verify your account in order to login")) {
                            showDialog(et_email.getText().toString());
                        }
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
                params.put("email", get_email);
                params.put("password", get_pwd);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
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
                getActivity().finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ContactFragment.from_contact = false;
    }
}

