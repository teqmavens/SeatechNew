package com.vadevelopment.RedAppetite.profilesection;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vibrantappz on 3/22/2017.
 */

public class DetailReviewProfile_Fragment extends android.app.Fragment implements View.OnClickListener {

    DashboardActivity mdashboard;
    private Button edit_btn, remove_btn, submit_review;
    private LinearLayout ll_editremove;
    private SharedPreferences preferences;
    private RatingBar ratingbar, ratingBar_avrg;
    private TextView text_date, name, address, ratingstars, distance, users, likes, dislikes;
    private Context context;
    private ImageView image_bigsmiley, image_bigsad, image_isreviewd, image_isfvrt, imageview;
    ;
    DatePickerDialog.OnDateSetListener date;
    String myFormat = "dd/MM/yyyy"; //In which you need put here
    SimpleDateFormat sdf;
    Calendar myCalendar;
    private String recommendation = "";
    private String rvid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_detailreviewprofile, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        setTooolbar();
        sdf = new SimpleDateFormat(myFormat);
        edit_btn = (Button) view.findViewById(R.id.edit_btn);
        remove_btn = (Button) view.findViewById(R.id.remove_btn);
        submit_review = (Button) view.findViewById(R.id.submit_review);
        ll_editremove = (LinearLayout) view.findViewById(R.id.ll_editremove);
        text_date = (TextView) view.findViewById(R.id.text_date);
        name = (TextView) view.findViewById(R.id.name);
        address = (TextView) view.findViewById(R.id.address);
        ratingstars = (TextView) view.findViewById(R.id.ratingstars);
        distance = (TextView) view.findViewById(R.id.distance);
        users = (TextView) view.findViewById(R.id.users);
        likes = (TextView) view.findViewById(R.id.likes);
        dislikes = (TextView) view.findViewById(R.id.dislikes);
        ratingbar = (RatingBar) view.findViewById(R.id.ratingbar);
        ratingBar_avrg = (RatingBar) view.findViewById(R.id.ratingBar_avrg);
        image_bigsmiley = (ImageView) view.findViewById(R.id.image_bigsmiley);
        image_bigsad = (ImageView) view.findViewById(R.id.image_bigsad);
        image_isreviewd = (ImageView) view.findViewById(R.id.image_isreviewd);
        image_isfvrt = (ImageView) view.findViewById(R.id.image_isfvrt);
        imageview = (ImageView) view.findViewById(R.id.imageview);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        myCalendar = Calendar.getInstance();


        edit_btn.setOnClickListener(this);
        remove_btn.setOnClickListener(this);
        submit_review.setOnClickListener(this);
        text_date.setOnClickListener(this);
        image_bigsmiley.setOnClickListener(this);
        image_bigsad.setOnClickListener(this);
        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(), edit_btn);
        com.vadevelopment.RedAppetite.HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(), remove_btn);


        // ratingbar.setRating(Float.parseFloat(getArguments().getString(Consts.REVIEW_TOTALRATING)));

        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        if (getArguments() != null && getArguments().getString(Consts.REVIEW_TYPE).equalsIgnoreCase("add")) {
            submit_review.setVisibility(View.VISIBLE);
            ll_editremove.setVisibility(View.INVISIBLE);
            name.setText(getArguments().getString(Consts.REVIEW_BUILDINGNAME));
            address.setText(getArguments().getString(Consts.REVIEW_BUILDINGADDRESS));
            ratingBar_avrg.setRating(Float.parseFloat(getArguments().getString(Consts.REVIEW_TOTALRATING)));

            if (getArguments().getString(Consts.REVIEW_TOTALRATING).length() == 0) {
                ratingstars.setText(getArguments().getString(Consts.REVIEW_TOTALRATING) + ".0");
            } else {
                double ii = Double.parseDouble(getArguments().getString(Consts.REVIEW_TOTALRATING));
                ratingstars.setText(String.valueOf(new DecimalFormat("##.#").format(ii)));
            }
            //    ratingstars.setText(getArguments().getString(Consts.REVIEW_TOTALRATING));
            distance.setText(getArguments().getString(Consts.REVIEW_DISTANCE) + " km");
            users.setText(getArguments().getString(Consts.REVIEW_USERS));
            likes.setText(getArguments().getString(Consts.REVIEW_LIKES));
            dislikes.setText(getArguments().getString(Consts.REVIEW_DISLIKES));
            text_date.setText(sdf.format(myCalendar.getTime()));
            text_date.setClickable(true);
            ratingbar.setIsIndicator(false);
            image_bigsmiley.setClickable(true);
            image_bigsad.setClickable(true);
            image_bigsmiley.setImageResource(R.drawable.big_smileywhite);
            image_bigsad.setImageResource(R.drawable.big_sad);
            recommendation = "happy";
            checkforfav_review();
        } else {
            try {
                submit_review.setVisibility(View.INVISIBLE);
                ll_editremove.setVisibility(View.VISIBLE);
                name.setText(getArguments().getString(Consts.REVIEW_BUILDINGNAME));
                address.setText(getArguments().getString(Consts.REVIEW_BUILDINGADDRESS));
                ratingBar_avrg.setRating(Float.parseFloat(getArguments().getString(Consts.REVIEW_TOTALRATING)));
                ratingstars.setText(getArguments().getString(Consts.REVIEW_TOTALRATING));
                ratingbar.setRating(Float.parseFloat(getArguments().getString(Consts.REVIEW_TOTALRATING)));
                distance.setText(getArguments().getString(Consts.REVIEW_DISTANCE));
                users.setText(getArguments().getString(Consts.REVIEW_USERS));
                likes.setText(getArguments().getString(Consts.REVIEW_LIKES));
                dislikes.setText(getArguments().getString(Consts.REVIEW_DISLIKES));
                if (Integer.parseInt(getArguments().getString(Consts.REVIEW_LIKES)) > Integer.parseInt(getArguments().getString(Consts.REVIEW_DISLIKES)) ||
                        Integer.parseInt(getArguments().getString(Consts.REVIEW_LIKES)) == Integer.parseInt(getArguments().getString(Consts.REVIEW_DISLIKES))) {
                    likes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileyicon_copy), null, null, null);
                    dislikes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileydislikes_copy), null, null, null);
                } else if (Integer.parseInt(getArguments().getString(Consts.REVIEW_LIKES)) < Integer.parseInt(getArguments().getString(Consts.REVIEW_DISLIKES))) {
                    likes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileyicon), null, null, null);
                    dislikes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileydislikes), null, null, null);
                }

                edit_btn.setText(getString(R.string.review_edit));
                remove_btn.setText(getString(R.string.review_remove));
                // REVIEW_EDIT_JSONOBJECT
                JSONObject jobj = new JSONObject(getArguments().getString(Consts.REVIEW_EDIT_JSONOBJECT));
                ratingbar.setRating(Float.parseFloat(jobj.getString("rating")));
                text_date.setText(jobj.getString("visited_date"));
                if (jobj.getString("recommendation").equalsIgnoreCase("happy")) {
                    image_bigsmiley.setImageResource(R.drawable.big_smileywhite);
                    image_bigsad.setImageResource(R.drawable.big_sad);
                    recommendation = "happy";
                } else {
                    image_bigsmiley.setImageResource(R.drawable.big_smiley);
                    image_bigsad.setImageResource(R.drawable.big_sadwhite);
                    recommendation = "sad";
                }
                rvid = jobj.getString("rvid");
                text_date.setClickable(false);
                ratingbar.setIsIndicator(true);
                image_bigsmiley.setClickable(false);
                image_bigsad.setClickable(false);
                checkforfav_review();
            } catch (Exception e) {
            }
        }

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Calendar prevYear = Calendar.getInstance();
                if (myCalendar.getTime().equals(prevYear.getTime()) || myCalendar.getTime().before(prevYear.getTime())) {
                    updateLabel();
                } else {
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.cannotchoose_futdate));
                }
            }
        };
    }

    private void updateLabel() {
        text_date.setText(sdf.format(myCalendar.getTime()));
    }

    private void checkforfav_review() {
        Glide.with(context).load(getArguments().getString(Consts.REVIEW_IMAGEURL)).placeholder(R.drawable.clubicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(imageview);
        if (Integer.parseInt(getArguments().getString(Consts.REVIEW_ISREVIEWD)) > 0 && Integer.parseInt(getArguments().getString(Consts.REVIEW_ISFAV)) > 0) {
            image_isreviewd.setVisibility(View.VISIBLE);
            image_isfvrt.setVisibility(View.VISIBLE);
        } else if (Integer.parseInt(getArguments().getString(Consts.REVIEW_ISREVIEWD)) > 0 && Integer.parseInt(getArguments().getString(Consts.REVIEW_ISFAV)) == 0) {
            image_isreviewd.setVisibility(View.VISIBLE);
            image_isfvrt.setVisibility(View.INVISIBLE);
        } else if (Integer.parseInt(getArguments().getString(Consts.REVIEW_ISREVIEWD)) == 0 && Integer.parseInt(getArguments().getString(Consts.REVIEW_ISFAV)) > 0) {
            image_isreviewd.setVisibility(View.INVISIBLE);
            image_isfvrt.setVisibility(View.VISIBLE);
        } else {
            image_isreviewd.setVisibility(View.INVISIBLE);
            image_isfvrt.setVisibility(View.INVISIBLE);
        }
    }

    private void setTooolbar() {
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.rl_headingwithcount.setVisibility(View.GONE);
        mdashboard.centertextsearch.setText(getString(R.string.review));
        mdashboard.mapButton.setVisibility(View.GONE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.mapicon.setVisibility(View.GONE);
        mdashboard.setting.setText(getString(R.string.back));
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_btn:
                if (edit_btn.getText().toString().equalsIgnoreCase("Edit")) {
                    edit_btn.setText(getString(R.string.review_save));
                    remove_btn.setText(getString(R.string.cancel));
                    text_date.setClickable(true);
                    ratingbar.setIsIndicator(false);
                    image_bigsmiley.setClickable(true);
                    image_bigsad.setClickable(true);
                } else {
                    //   EditReview_Task();
                    SubmitReview_task(String.valueOf(Math.round(ratingbar.getRating())), text_date.getText().toString(), "edit");
                    // getActivity().getSupportFragmentManager().popBackStack();
                }
                break;
            case R.id.remove_btn:
                if (remove_btn.getText().toString().equalsIgnoreCase("Cancel")) {
                    edit_btn.setText(getString(R.string.review_edit));
                    remove_btn.setText(getString(R.string.review_remove));
                    text_date.setClickable(false);
                    ratingbar.setIsIndicator(true);
                    image_bigsmiley.setClickable(false);
                    image_bigsad.setClickable(false);
                } else {
                    removeReviewDialog();

                }
                break;
            case R.id.submit_review:
                SubmitReview_task(String.valueOf(Math.round(ratingbar.getRating())), text_date.getText().toString(), "submit");
                break;
            case R.id.text_date:
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.image_bigsmiley:
                image_bigsmiley.setImageResource(R.drawable.big_smileywhite);
                image_bigsad.setImageResource(R.drawable.big_sad);
                recommendation = "happy";
                break;
            case R.id.image_bigsad:
                image_bigsmiley.setImageResource(R.drawable.big_smiley);
                image_bigsad.setImageResource(R.drawable.big_sadwhite);
                recommendation = "sad";
                break;
        }
    }

    private void removeReviewDialog() {
        final Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        final Dialog mediaDialog = new Dialog(context);
        mediaDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mediaDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        mediaDialog.setContentView(R.layout.dialog_dltfavorite);
        LinearLayout approx_lay = (LinearLayout) mediaDialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 80, (h / 4) - 20);
        approx_lay.setLayoutParams(params);
        TextView heading_text = (TextView) mediaDialog.findViewById(R.id.heading_text);
        TextView yes = (TextView) mediaDialog.findViewById(R.id.yes);
        TextView no = (TextView) mediaDialog.findViewById(R.id.no);
        heading_text.setText(context.getString(R.string.want_delete) + " " + context.getString(R.string.review_for) + " " + name.getText().toString() + "?");

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
                removeReview_Task();

            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
            }
        });
        mediaDialog.show();
    }

    private void SubmitReview_task(final String ratingvalue, final String visited_date, final String from) {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        String url;
        if (from.equalsIgnoreCase("submit")) {
            url = com.vadevelopment.RedAppetite.HandyObjects.ADD_REVIEW;
        } else {
            url = com.vadevelopment.RedAppetite.HandyObjects.EDIT_REVIEW;
        }

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Submitreview", response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        if (from.equalsIgnoreCase("submit")) {
                            com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, name.getText().toString() + " " + getString(R.string.succ_reviewd));
                        } else {
                            com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getString(R.string.succ_editreview) + " " + name.getText().toString());
                        }
                        getActivity().getFragmentManager().popBackStack();
                    } else if (jboj.getString("message").equalsIgnoreCase("fail")) {
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
                params.put("lid", getArguments().getString(Consts.REVIEW_LID));
                if (from.equalsIgnoreCase("submit")) {
                } else {
                    params.put("rvid", rvid);
                }
                params.put("rating", ratingvalue);
                params.put("recommendation", recommendation);
                params.put("visited_date", visited_date);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void removeReview_Task() {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.REMOVE_REVIEW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("removereview", response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getString(R.string.succ_deletereview) + " " + name.getText().toString());
                        getActivity().getFragmentManager().popBackStack();
                    } else if (jboj.getString("message").equalsIgnoreCase("fail")) {
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
                params.put("rvid", rvid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
