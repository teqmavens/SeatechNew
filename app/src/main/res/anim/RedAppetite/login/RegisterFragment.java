package com.vadevelopment.RedAppetite.login;

import android.app.DatePickerDialog;
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
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_NewCountry;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vibrant Android on 08-03-2017.
 */

public class RegisterFragment extends Fragment {

    public static String TAG = "RegisterFragment";
    TextView resendemail;
    private Button registerbutton;
    private LinearLayout ll_main;
    private TextView textnewline, textnewline_new;
    private EditText bday, et_country, et_name, et_pwd, et_cpwd, et_email;
    ContainerActivity mcContainerActivity;
    DatePickerDialog.OnDateSetListener date;
    Dialog openDialog;
    Calendar myCalendar;
    private String get_name, get_pwd, get_cpwd, get_email, get_country, get_bday;
    private Context context;
    private ArrayList<CountrySkeleton> country_arraylist;
    private Adapter_NewCountry adaptercountry;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private SQLiteDatabase database;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcContainerActivity = (ContainerActivity) getActivity();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_register, container, false);
        bday = (EditText) view.findViewById(R.id.bday);
        et_country = (EditText) view.findViewById(R.id.et_country);
        registerbutton = (Button) view.findViewById(R.id.registerbutton);
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_pwd = (EditText) view.findViewById(R.id.et_pwd);
        et_cpwd = (EditText) view.findViewById(R.id.et_cpwd);
        et_email = (EditText) view.findViewById(R.id.et_email);
        textnewline = (TextView) view.findViewById(R.id.textnewline);
        textnewline_new = (TextView) view.findViewById(R.id.textnewline_new);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        myCalendar = Calendar.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Calendar prevYear = Calendar.getInstance();
                prevYear.add(Calendar.YEAR, -18);
                prevYear.getTime();
                if (myCalendar.getTime().equals(prevYear.getTime()) || myCalendar.getTime().before(prevYear.getTime())) {
                    updateLabel();
                } else {
                    HandyObjects.showAlert(context, getResources().getString(R.string.mustbe_older));
                }


            }
        };
        slideToAbove();
        if (getArguments() != null && getArguments().getString("from").equalsIgnoreCase("settings")) {
            mcContainerActivity.cancelText.setVisibility(View.VISIBLE);
            mcContainerActivity.cancelText.setText("Cancel");
            mcContainerActivity.centertext.setText("Register");
            mcContainerActivity.next.setVisibility(View.GONE);
            mcContainerActivity.cancelText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        } else if (getArguments() != null && getArguments().getString("from").equalsIgnoreCase("login")) {
            mcContainerActivity.cancelText.setVisibility(View.VISIBLE);
            mcContainerActivity.cancelText.setText("Back");
            mcContainerActivity.cancelText.setBackgroundResource(R.drawable.backbtn);
            mcContainerActivity.centertext.setText("Register");

            mcContainerActivity.next.setBackgroundResource(R.drawable.cancelbutton);
            mcContainerActivity.next.setVisibility(View.VISIBLE);
            mcContainerActivity.next.setText("Cancel");


            // if (getArguments() != null && getArguments().getString("fromdashboard").equalsIgnoreCase("yup")) {
            //   slideToAbove();
            //}

            mcContainerActivity.next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
            mcContainerActivity.cancelText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //getActivity().finish();
                    slideToBottom();
                }
            });
        } else if (getArguments() != null && getArguments().getString("from").equalsIgnoreCase("main")) {
            mcContainerActivity.cancelText.setVisibility(View.GONE);
            // mcContainerActivity.cancelText.setText("Cancel");
            mcContainerActivity.centertext.setText("Register");
            mcContainerActivity.next.setVisibility(View.VISIBLE);
            mcContainerActivity.next.setText("Skip");
            mcContainerActivity.next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, null, null, null, null, null, null);
                    Cursor cursor_event = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, null, null, null, null, null, null);
                    Cursor cursor_favrt = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORFAVORITES, null, null, null, null, null, null);
                    Cursor cursor_review = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, null, null, null, null, null, null);
                    if (cursor != null) {
                        if (cursor.getCount() > 0) {
                        } else {
                            ContentValues cv = new ContentValues();
                            cv.put("search_category", "All");
                            cv.put("search_stars", "Any");
                            cv.put("search_type", "Free");
                            cv.put("search_reviews", "0");
                            cv.put("search_favorites", "0");
                            cv.put("search_modeposi", "Current Position");
                            cv.put("search_radius", "100");
                            cv.put("search_sortby", "Distance");
                            cv.put("search_whichlatlong", "current");
                            cv.put("search_whichlatlongaddrs", "current");
                            long idd = database.insert("forsearch", null, cv);
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
                            cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_SORTBY, "Distance");
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
                    editor.commit();
                    Intent i = new Intent(getActivity(), DashboardActivity.class);
                    i.putExtra("fromskip", "yup");
                    startActivity(i);
                }
            });
        }

        HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(), registerbutton);
        if (!HandyObjects.isNetworkAvailable(context)) {
            HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
        } else {
            GetCountry_Task();
        }

        bday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        et_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CountryDialog();
            }
        });

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_name = et_name.getText().toString();
                get_pwd = et_pwd.getText().toString();
                get_cpwd = et_cpwd.getText().toString();
                get_email = et_email.getText().toString();
                get_country = et_country.getText().toString();
                get_bday = bday.getText().toString();

                if (get_name.length() == 0) {
                    et_name.requestFocus();
                    HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (get_name.length() < 5) {
                    et_email.requestFocus();
                    HandyObjects.showAlert(context, getResources().getString(R.string.nickname_atleast5));
                } else if (get_pwd.length() == 0) {
                    et_pwd.requestFocus();
                    HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (get_cpwd.length() == 0) {
                    et_cpwd.requestFocus();
                    HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (get_pwd.length() < 5) {
                    HandyObjects.showAlert(context, getResources().getString(R.string.pwdshould));
                } else if (!get_cpwd.equalsIgnoreCase(get_pwd)) {
                    HandyObjects.showAlert(context, getResources().getString(R.string.pwdnotmatch));
                } else if (get_email.length() == 0) {
                    et_email.requestFocus();
                    HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(get_email).matches()) {
                    et_email.requestFocus();
                    HandyObjects.showAlert(context, getResources().getString(R.string.invalidemail));
                } else if (get_country.length() == 0) {
                    et_country.requestFocus();
                    HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (get_bday.length() == 0) {
                    bday.requestFocus();
                    HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (!HandyObjects.isNetworkAvailable(context)) {
                    HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
                } else {
                    Register_Task();
                }
            }
        });

        SpannableString ss = new SpannableString(getString(R.string.textprivacy));
        SpannableString ss_new = new SpannableString(getString(R.string.textprivacy_new));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                mcContainerActivity.displayWithViewFragment(new MoreRegister_Fragment());
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#e8dddd"));
                ds.setUnderlineText(true);
            }
        };
        ClickableSpan clickableSpan_new = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                mcContainerActivity.displayWithViewFragment(new com.vadevelopment.RedAppetite.login.PrivacyPolicyRegister_Fragment());
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#e8dddd"));
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, 48, 67, 0);
        ss_new.setSpan(clickableSpan_new, 1, 23, 0);

        textnewline.setText(ss);
        textnewline.setMovementMethod(LinkMovementMethod.getInstance());
        textnewline.setHighlightColor(Color.TRANSPARENT);

        textnewline_new.setText(ss_new);
        textnewline_new.setMovementMethod(LinkMovementMethod.getInstance());
        textnewline_new.setHighlightColor(Color.TRANSPARENT);
        return view;
    }

    private void slideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(540);
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

    private void updateLabel() {
        String myFormat = "dd.MM.yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        bday.setText(sdf.format(myCalendar.getTime()));
    }

    private void CountryDialog() {
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
        View vview = inflater.inflate(R.layout.dialog_country, null, false);
        openDialog.setContentView(vview);
        LinearLayout approx_lay = (LinearLayout) openDialog.findViewById(R.id.approx_lay);
        int valueinpix = (int) getResources().getDimension(R.dimen.newforcheck);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width - valueinpix, (height / 3) + 60);
        approx_lay.setLayoutParams(params);

        TextView cancel = (TextView) vview.findViewById(R.id.cancel);
        ListView listView = (ListView) vview.findViewById(R.id.listView);
        setAdapter(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openDialog.hide();
                country_arraylist.get(i).getName();
                et_country.setText(country_arraylist.get(i).getName());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
            }
        });
        openDialog.show();
    }

    private void Register_Task() {
        String tag_json_obj = "json_obj_req";
        HandyObjects.showProgressDialog(context);
        //   HandyObjects.startProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                HandyObjects.REGISTER_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success")) {
                        HandyObjects.showAlert(context, getResources().getString(R.string.registersucc));
                        //  verifyEmailDialog();
                        LoginFragment lf = new LoginFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("fromdashboard", "nop_fromregister");
                        bundle.putString("email", jboj.getJSONObject("all_list").getString("email"));
                        lf.setArguments(bundle);
                        mcContainerActivity.displayWithoutViewFragment(lf);
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

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", get_name);
                params.put("password", get_pwd);
                params.put("repeatpassword", get_cpwd);
                params.put("email", get_email);
                params.put("country", get_country);
                params.put("birthday", get_bday);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    private void GetCountry_Task() {
        String tag_json_obj = "json_obj_req";
        HandyObjects.showProgressDialog(context);
        //   HandyObjects.startProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                HandyObjects.COUNTRY_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success")) {
                        JSONArray jarry = jboj.getJSONArray("all_list");
                        // HandyObjects.showAlert(context, getResources().getString(R.string.registersucc));
                        // verifyEmailDialog();
                        country_arraylist = new ArrayList<>();
                        for (int i = 0; i < jarry.length(); i++) {
                            CountrySkeleton ske = new CountrySkeleton();
                            ske.setCid(jarry.getJSONObject(i).getString("cid"));
                            ske.setName(jarry.getJSONObject(i).getString("country"));
                            country_arraylist.add(ske);
                        }
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

    private void setAdapter(ListView listView) {
        try {
            if (country_arraylist.size() > 0) {
                adaptercountry = new Adapter_NewCountry(getActivity(), country_arraylist);
                listView.setAdapter(adaptercountry);
            }
        } catch (Exception e) {
        }
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
