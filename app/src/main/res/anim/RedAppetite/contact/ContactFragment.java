package com.vadevelopment.RedAppetite.contact;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
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
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vibrant Android on 09-03-2017.
 */

public class ContactFragment extends Fragment {

    private static String TAG = "ContactFragment";
    private com.vadevelopment.RedAppetite.login.ContainerActivity mcContainerActivity;
    private Button sendmsgbutton;
    private Context context;
    private EditText et_message, et_name, et_email;
    private String get_email, get_msg, get_name;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static boolean from_contact;
    private SQLiteDatabase database;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcContainerActivity = (com.vadevelopment.RedAppetite.login.ContainerActivity) getActivity();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_contact, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        sendmsgbutton = (Button) view.findViewById(R.id.sendmsgbutton);
        et_message = (EditText) view.findViewById(R.id.et_message);
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_email = (EditText) view.findViewById(R.id.et_email);
        mcContainerActivity.centertext.setText(getString(R.string.contact));
        mcContainerActivity.cancelText.setVisibility(View.VISIBLE);
        mcContainerActivity.cancelText.setText(R.string.cancel);
        mcContainerActivity.next.setVisibility(View.GONE);
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();
        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(), sendmsgbutton);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        mcContainerActivity.cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                from_contact = true;
                getActivity().getSupportFragmentManager().popBackStack();
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
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.pwdshould));
                } else if (get_email.length() == 0) {
                    et_email.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.allfields_req));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(get_email).matches()) {
                    et_email.requestFocus();
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.invalidemail));
                } else if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
                } else {
                    Contact_Task();
                }
            }
        });
    }

    private void Contact_Task() {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        //   HandyObjects.startProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.CONTACT_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, jboj.getString("mail"));
                        skipthis();
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
                params.put("email", get_email);
                params.put("message", get_msg);
                params.put("name", get_name);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void skipthis() {
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
        //  editor.putString(Consts)
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
}
