package com.vadevelopment.RedAppetite.profilesection;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.HandyObjects;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_Newsinsidenew;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;
import com.vadevelopment.RedAppetite.login.ContainerActivity;
import com.vadevelopment.RedAppetite.newssection.NewsSeemore_Fragment;
import com.vadevelopment.RedAppetite.newssection.NewsSkeletonInside;
import com.vadevelopment.RedAppetite.searchsection.ReportError_Fragment;
import com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vibrantappz on 3/23/2017.
 */

public class DetailDltFrmFvrt_EdtRvw_Fragment extends android.app.Fragment implements View.OnClickListener {

    private static String TAG = "DetailFvrt_Fragment";
    DashboardActivity mdashboard;
    public static final int PICK_CONTACT = 4;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CONTACT_PHONE = 2;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 3;
    private static Facebook facebook = new Facebook("1881398585516116");
    private static final String PERMISSION = "user_friends";
    private TextView text_editreview, text_addtofvrt, seemore, allnews, moveto_prev, moveto_next, buildingname, ratingstars,
            distance, users, likes, dislikes, mondaykey, mondayvalue, tuesdaykey, tuesdayvalue, wednesdaykey, wednesdayvalue, thrusdaykey,
            thrusdayvalue, fridaykey, fridayvalue, saturdaykey, saturdayvalue, sundaykey, sundayvalue, telephone_value, telepcall,
            websiteopen, website, text_email, text_sendemail, address_valuetext, textnews_value, text_1thcount, text_2thcount,
            text_3thcount, text_4thcount, text_5thcount, text_totalrating, text_totalcount, textreview_value, description_value,
            specialoffr_value, navigate, playvideo_btn, textOpeningtimes;
    private Dialog tellfrnd_Dialog;
    private Context context;
    ArrayList<String> premiumall_images;
    CustomPagerAdapter mCustomPagerAdapter;
    private Adapter_Newsinsidenew adapter_newsinside;
    Dialog openDialog;
    private ViewPager mViewPager;
    private SharedPreferences preferences;
    private String lid, make_sharetext;
    private RatingBar ratingBar;
    com.hedgehog.ratingbar.RatingBar allreview_ratingbar;
    private ArrayList<Skeleton_SearchFirst> arraylist_custom;
    private ArrayList<NewsSkeletonInside> arraylist_news;
    private ImageView imageView, img_share, image_isreviewd, image_isfvrt, image_reporterror;
    private LinearLayout ll_viewpager;
    private RelativeLayout rl_news;
    private String phone_no, building_address, isReviewed, isfav, image_url, uf1, fullimage_url, latitude, longitude, categorytype, video_url;
    private String edit_jsonobject = "";
    private ViewStub viewstub;
    private RecyclerView listview_news;
    TextView dynamic_number, fix_number;
    private Bitmap bm_image;
    private RelativeLayout rl_description, rl_specialoffr, rl_openingtimes, rl_video, rl_days;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_favoritedetail, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        setToolbar();
        check_permission();
        viewstub = (ViewStub) view.findViewById(R.id.stub);
        View inflated = viewstub.inflate();
        buildingname = (TextView) view.findViewById(R.id.buildingname);
        text_editreview = (TextView) view.findViewById(R.id.text_editreview);
        text_addtofvrt = (TextView) view.findViewById(R.id.text_addtofvrt);
        img_share = (ImageView) view.findViewById(R.id.img_share);
        image_reporterror = (ImageView) view.findViewById(R.id.image_reporterror);
        seemore = (TextView) view.findViewById(R.id.seemore);
        allnews = (TextView) view.findViewById(R.id.allnews);
        playvideo_btn = (TextView) view.findViewById(R.id.playvideo_btn);
        moveto_prev = (TextView) view.findViewById(R.id.moveto_prev);
        moveto_next = (TextView) view.findViewById(R.id.moveto_next);
        ratingstars = (TextView) view.findViewById(R.id.ratingstars);
        distance = (TextView) view.findViewById(R.id.distance);
        users = (TextView) view.findViewById(R.id.users);
        likes = (TextView) view.findViewById(R.id.likes);
        dislikes = (TextView) view.findViewById(R.id.dislikes);
        mondaykey = (TextView) view.findViewById(R.id.mondaykey);
        mondayvalue = (TextView) view.findViewById(R.id.mondayvalue);
        tuesdaykey = (TextView) view.findViewById(R.id.tuesdaykey);
        tuesdayvalue = (TextView) view.findViewById(R.id.tuesdayvalue);
        wednesdaykey = (TextView) view.findViewById(R.id.wednesdaykey);
        wednesdayvalue = (TextView) view.findViewById(R.id.wednesdayvalue);
        thrusdaykey = (TextView) view.findViewById(R.id.thrusdaykey);
        thrusdayvalue = (TextView) view.findViewById(R.id.thrusdayvalue);
        textOpeningtimes = (TextView) view.findViewById(R.id.textOpeningtimes);
        fridaykey = (TextView) view.findViewById(R.id.fridaykey);
        fridayvalue = (TextView) view.findViewById(R.id.fridayvalue);
        saturdaykey = (TextView) view.findViewById(R.id.saturdaykey);
        saturdayvalue = (TextView) view.findViewById(R.id.saturdayvalue);
        sundaykey = (TextView) view.findViewById(R.id.sundaykey);
        sundayvalue = (TextView) view.findViewById(R.id.sundayvalue);
        telephone_value = (TextView) view.findViewById(R.id.telephone_value);
        telepcall = (TextView) view.findViewById(R.id.telepcall);
        website = (TextView) view.findViewById(R.id.website);
        websiteopen = (TextView) view.findViewById(R.id.websiteopen);
        text_email = (TextView) view.findViewById(R.id.text_email);
        text_sendemail = (TextView) view.findViewById(R.id.text_sendemail);
        address_valuetext = (TextView) view.findViewById(R.id.address_valuetext);
        textnews_value = (TextView) view.findViewById(R.id.textnews_value);
        text_1thcount = (TextView) view.findViewById(R.id.text_1thcount);
        text_2thcount = (TextView) view.findViewById(R.id.text_2thcount);
        text_3thcount = (TextView) view.findViewById(R.id.text_3thcount);
        text_4thcount = (TextView) view.findViewById(R.id.text_4thcount);
        text_5thcount = (TextView) view.findViewById(R.id.text_5thcount);
        text_totalrating = (TextView) view.findViewById(R.id.text_totalrating);
        text_totalcount = (TextView) view.findViewById(R.id.text_totalcount);
        textreview_value = (TextView) view.findViewById(R.id.textreview_value);
        description_value = (TextView) view.findViewById(R.id.description_value);
        specialoffr_value = (TextView) view.findViewById(R.id.specialoffr_value);
        navigate = (TextView) view.findViewById(R.id.navigate);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        allreview_ratingbar = (com.hedgehog.ratingbar.RatingBar) view.findViewById(R.id.allreview_ratingbar);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        image_isreviewd = (ImageView) view.findViewById(R.id.image_isreviewd);
        image_isfvrt = (ImageView) view.findViewById(R.id.image_isfvrt);
        ll_viewpager = (LinearLayout) view.findViewById(R.id.ll_viewpager);
        rl_description = (RelativeLayout) view.findViewById(R.id.rl_description);
        rl_specialoffr = (RelativeLayout) view.findViewById(R.id.rl_specialoffr);
        rl_openingtimes = (RelativeLayout) view.findViewById(R.id.rl_openingtimes);
        rl_days = (RelativeLayout) view.findViewById(R.id.rl_days);
        rl_video = (RelativeLayout) view.findViewById(R.id.rl_video);
        listview_news = (RecyclerView) view.findViewById(R.id.listview_news);
        rl_news = (RelativeLayout) view.findViewById(R.id.rl_news);
        listview_news.setHasFixedSize(true);
        listview_news.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listview_news.setLayoutManager(mLayoutManager);
        listview_news.setItemAnimator(new DefaultItemAnimator());

        mCustomPagerAdapter = new CustomPagerAdapter(getActivity());
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //allnews.setOnClickListener(this);
        text_editreview.setOnClickListener(this);
        text_addtofvrt.setOnClickListener(this);
        moveto_prev.setOnClickListener(this);
        moveto_next.setOnClickListener(this);
        img_share.setOnClickListener(this);
        seemore.setOnClickListener(this);
        telepcall.setOnClickListener(this);
        websiteopen.setOnClickListener(this);
        text_sendemail.setOnClickListener(this);
        image_reporterror.setOnClickListener(this);
        navigate.setOnClickListener(this);
        playvideo_btn.setOnClickListener(this);
        text_addtofvrt.setText(getString(R.string.deletefrm_fvrt));

        if (getArguments() != null) {
            lid = getArguments().getString(Consts.PROFILE_CLICKLID);
            if (!HandyObjects.isNetworkAvailable(context)) {
                HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SearchDetail_Task();
                    }
                }, 100);
            }
        }
    }

    private void setToolbar() {
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.rl_headingwithcount.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText(getString(R.string.details));
        mdashboard.mapButton.setVisibility(View.GONE);
        mdashboard.mapicon.setVisibility(View.VISIBLE);
        mdashboard.mapicon.setImageResource(R.drawable.searchheadermap);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.setting.setText(getString(R.string.back));
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        mdashboard.mapicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (arraylist_custom.size() > 0) {
                    Map_DetailFvrtFragment map_sdetail = new Map_DetailFvrtFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(Consts.DETAIL_ARRAYLIST, arraylist_custom);
                    if (getArguments().getString(Consts.CHECKCLICK_FROM).equalsIgnoreCase("fvrt")) {
                        bundle.putString(Consts.ONDETAIL_FROM, "fvrt");
                    } else {
                        bundle.putString(Consts.ONDETAIL_FROM, "review");
                    }
                    bundle.putString(Consts.PROFILE_CLICKLID, getArguments().getString(Consts.PROFILE_CLICKLID));
                    map_sdetail.setArguments(bundle);
                    // mdashboard.displayWithoutViewFragment(map_sdetail);
                    mdashboard.flipViewFragmentWithanim(map_sdetail);
                }
                // mdashboard.displayWithoutViewFragment(new Map_DetailFvrtFragment());
            }
        });
    }

    private void SearchDetail_Task() {
        String tag_json_obj = "json_obj_req";
        HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                HandyObjects.SEARCHDETAILTASK_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("Success")) {
                        arraylist_custom = new ArrayList<>();
                        arraylist_news = new ArrayList<>();
                        JSONObject jobj_alllist = jboj.getJSONObject("all_list");
                        //JSONObject jobj_opentime = jboj.getJSONObject("opening times");
                        JSONArray jarry_news = jboj.getJSONArray("news");
                        JSONArray jarry_reviews = jboj.getJSONArray("reviews");
                        JSONArray jarry_favorites = jboj.getJSONArray("favorites");
                        categorytype = jobj_alllist.getString("category");
                        make_sharetext = getString(R.string.irecomend_you) + "\n\n" + jobj_alllist.getString("buildingname") + "\n" + jobj_alllist.getString("address") + "\n" + jobj_alllist.getString("country") + "\n\n" +
                                getString(R.string.telephone) + " " + jobj_alllist.getString("telephone") + "\n" + getString(R.string.email) + " " + jobj_alllist.getString("email") +
                                "\n" + getString(R.string.internet) + " " + jobj_alllist.getString("website") + "\n\n" + jobj_alllist.getString("avgRate") + " " +
                                getString(R.string.stars) + " / " + jobj_alllist.getString("total") + " " + getString(R.string.reviews) + "\n\n" + getString(R.string.get_latestnews) +
                                "\n\n" + getString(R.string.androidios_link) + "\n\n" + getString(R.string.internet) + "\n" + jobj_alllist.getString("website");
                        if (jobj_alllist.getJSONArray("reviews").length() == 0) {
                            text_editreview.setText(R.string.addreview);
                        } else {
                            edit_jsonobject = String.valueOf(jobj_alllist.getJSONArray("reviews").getJSONObject(0));
                            text_editreview.setText(R.string.editreview);
                        }

                        if (jarry_favorites.length() == 0) {
                            text_addtofvrt.setText(getString(R.string.addtofvrt));
                        } else {
                            uf1 = jarry_favorites.getJSONObject(0).getString("uf1");
                            text_addtofvrt.setText(getString(R.string.deletefrm_fvrt));
                        }
                        text_1thcount.setText(jarry_reviews.getJSONObject(0).getString("conuts"));
                        text_2thcount.setText(jarry_reviews.getJSONObject(1).getString("conuts"));
                        text_3thcount.setText(jarry_reviews.getJSONObject(2).getString("conuts"));
                        text_4thcount.setText(jarry_reviews.getJSONObject(3).getString("conuts"));
                        text_5thcount.setText(jarry_reviews.getJSONObject(4).getString("conuts"));
                        if (jarry_news.length() == 0) {
                            allnews.setVisibility(View.VISIBLE);
                            allnews.setText(getString(R.string.noevent_yet));
                        } else {
                            allnews.setVisibility(View.GONE);
                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < jarry_news.length(); i++) {
                                if (i < 6) {
                                    builder.append(jarry_news.getJSONObject(i).getString("date") + " " + jarry_news.getJSONObject(i).getString("headline") + "\n");
                                }
                                if (builder.toString().endsWith("\n")) {
                                    builder.toString().substring(0, builder.toString().length() - 1);
                                }
                            }
                        }
                        textnews_value.setText(String.valueOf(jarry_news.length()));
                        //sadaa
                        if (jobj_alllist.getString("type").equalsIgnoreCase("Free") || jobj_alllist.getString("type").equalsIgnoreCase("free")) {
                            Glide.with(context).load(jobj_alllist.getString("free")).placeholder(R.drawable.clubicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
                            ll_viewpager.setVisibility(View.GONE);
                            rl_description.setVisibility(View.GONE);
                            rl_specialoffr.setVisibility(View.GONE);
                            rl_openingtimes.setVisibility(View.GONE);
                            rl_video.setVisibility(View.GONE);
                            image_url = jobj_alllist.getString("free");
                        } else {
                            video_url = jobj_alllist.getString("video");
                            if (video_url != null && !video_url.isEmpty()) {
                                rl_video.setVisibility(View.VISIBLE);
                            } else {
                                rl_video.setVisibility(View.GONE);
                            }
                            rl_description.setVisibility(View.VISIBLE);
                            rl_specialoffr.setVisibility(View.VISIBLE);
                            rl_openingtimes.setVisibility(View.VISIBLE);
                            if (jobj_alllist.getString("description") != null && !jobj_alllist.getString("specialoffer").isEmpty()) {
                                description_value.setText(jobj_alllist.getString("description"));
                            } else {
                                description_value.setText("-");
                            }
                            if (jobj_alllist.getString("specialoffer") != null && !jobj_alllist.getString("specialoffer").isEmpty()) {
                                specialoffr_value.setText(jobj_alllist.getString("specialoffer"));
                            } else {
                                specialoffr_value.setText("-");
                            }
                            image_url = jobj_alllist.getString("premium");
                            ll_viewpager.setVisibility(View.VISIBLE);
                            premiumall_images = new ArrayList<>();
                            Glide.with(context).load(jobj_alllist.getString("premium")).placeholder(R.drawable.clubicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
                            JSONArray jarryimages = jboj.getJSONArray("images");
                            if (jarryimages.length() == 0) {
                                ll_viewpager.setVisibility(View.GONE);
                            } else {
                                ll_viewpager.setVisibility(View.VISIBLE);
                                for (int i = 0; i < jarryimages.length(); i++) {
                                    premiumall_images.add(jarryimages.getString(i));
                                }
                                mViewPager.setAdapter(mCustomPagerAdapter);
                            }

                        }
                        buildingname.setText(jobj_alllist.getString("buildingname"));
                        ratingBar.setRating(Float.parseFloat(jobj_alllist.getString("avgRate")));
                        allreview_ratingbar.setStar(Float.parseFloat(jobj_alllist.getString("avgRate")));
                        if (jobj_alllist.getString("avgRate").length() == 1) {
                            ratingstars.setText(jobj_alllist.getString("avgRate") + ".0");
                            text_totalrating.setText(jobj_alllist.getString("avgRate") + ".0");
                        } else {
                            double ii = Double.parseDouble(jobj_alllist.getString("avgRate"));
                            ratingstars.setText(String.valueOf(new DecimalFormat("##.#").format(ii)));
                            text_totalrating.setText(String.valueOf(new DecimalFormat("##.#").format(ii)));
                        }
                        distance.setText(jobj_alllist.getString("distance") + " km");
                        users.setText(jobj_alllist.getString("total"));
                        if (Integer.parseInt(jobj_alllist.getString("happy")) > Integer.parseInt(jobj_alllist.getString("sad")) ||
                                Integer.parseInt(jobj_alllist.getString("happy")) == Integer.parseInt(jobj_alllist.getString("sad"))) {
                            likes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileyicon_copy), null, null, null);
                            dislikes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileydislikes_copy), null, null, null);
                        } else if (Integer.parseInt(jobj_alllist.getString("happy")) < Integer.parseInt(jobj_alllist.getString("sad"))) {
                            likes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileyicon), null, null, null);
                            dislikes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileydislikes), null, null, null);
                        }
                        likes.setText(jobj_alllist.getString("happy"));
                        dislikes.setText(jobj_alllist.getString("sad"));

                        for (int k = 0; k < jarry_news.length(); k++) {
                            NewsSkeletonInside news_ske = new NewsSkeletonInside();
                            news_ske.setNid(jarry_news.getJSONObject(k).getString("nid"));
                            news_ske.setHeadline(jarry_news.getJSONObject(k).getString("headline"));
                            news_ske.setDate(jarry_news.getJSONObject(k).getString("date"));
                            news_ske.setImage(jarry_news.getJSONObject(k).getString("image"));
                            arraylist_news.add(news_ske);
                        }
                        //  HandyObjects.showAlert(context, String.valueOf(arraylist_news.size()));
                        adapter_newsinside = new Adapter_Newsinsidenew(context, arraylist_news);
                        listview_news.setAdapter(adapter_newsinside);

                        Skeleton_SearchFirst ske_search = new Skeleton_SearchFirst();
                        ske_search.setLid(jobj_alllist.getString("lid"));
                        ske_search.setBuildingname(jobj_alllist.getString("buildingname"));
                        ske_search.setAddress(jobj_alllist.getString("address"));
                        ske_search.setDistance(jobj_alllist.getString("distance"));
                        ske_search.setType(jobj_alllist.getString("type"));
                        ske_search.setTotal(jobj_alllist.getString("total"));
                        ske_search.setIsreviewed(jobj_alllist.getString("isReviewed"));
                        ske_search.setIsfav(jobj_alllist.getString("isFav"));
                        ske_search.setAvgrating(jobj_alllist.getString("avgRate"));
                        ske_search.setHappy(jobj_alllist.getString("happy"));
                        ske_search.setSad(jobj_alllist.getString("sad"));
                        ske_search.setLatitude(jobj_alllist.getString("latitude"));
                        ske_search.setLongitude(jobj_alllist.getString("longitude"));
                        ske_search.setFreeimage(jobj_alllist.getString("map_free"));
                        ske_search.setPremiumimage(jobj_alllist.getString("map_premium"));
                        arraylist_custom.add(ske_search);

                        if (jobj_alllist.getString("telephone") != null && !jobj_alllist.getString("telephone").isEmpty()) {
                            telephone_value.setText(jobj_alllist.getString("telephone"));
                        } else {
                            telephone_value.setText("-");
                        }
                        if (jobj_alllist.getString("website") != null && !jobj_alllist.getString("website").isEmpty()) {
                            website.setText(jobj_alllist.getString("website"));
                        } else {
                            website.setText("-");
                        }
                        if (jobj_alllist.getString("email") != null && !jobj_alllist.getString("email").isEmpty()) {
                            text_email.setText(jobj_alllist.getString("email"));
                        } else {
                            text_email.setText("-");
                        }
                        latitude = jobj_alllist.getString("latitude");
                        longitude = jobj_alllist.getString("longitude");

                        if (jobj_alllist.getString("address").contains(",")) {
                            String[] addr_string = jobj_alllist.getString("address").split(",");
                            address_valuetext.setText(addr_string[1].trim() + "\n" + addr_string[0] + "\n" + jobj_alllist.getString("country"));
                            /*for (int i = 0; i < addr_string.length; i++) {
                                address_builder.append(addr_string[0] + "\n");
                                if (address_builder.toString().endsWith("\n")) {
                                    address_builder.toString().substring(0, address_builder.toString().length() - 1);
                                }
                            }*/
                        } else {
                            if (jobj_alllist.getString("country") != null && !jobj_alllist.getString("country").isEmpty()) {
                                address_valuetext.setText(jobj_alllist.getString("address") + "\n" + jobj_alllist.getString("country"));
                            }
                        }

                        text_totalcount.setText(jobj_alllist.getString("total"));
                        textreview_value.setText(jobj_alllist.getString("total"));
                        building_address = jobj_alllist.getString("address");
                        isReviewed = jobj_alllist.getString("isReviewed");
                        isfav = jobj_alllist.getString("isFav");
                        if (Integer.parseInt(jobj_alllist.getString("isReviewed")) > 0 && Integer.parseInt(jobj_alllist.getString("isFav")) > 0) {
                            image_isreviewd.setVisibility(View.VISIBLE);
                            image_isfvrt.setVisibility(View.VISIBLE);
                        } else if (Integer.parseInt(jobj_alllist.getString("isReviewed")) > 0 && Integer.parseInt(jobj_alllist.getString("isFav")) == 0) {
                            image_isreviewd.setVisibility(View.VISIBLE);
                            image_isfvrt.setVisibility(View.INVISIBLE);
                        } else if (Integer.parseInt(jobj_alllist.getString("isReviewed")) == 0 && Integer.parseInt(jobj_alllist.getString("isFav")) > 0) {
                            image_isreviewd.setVisibility(View.INVISIBLE);
                            image_isfvrt.setVisibility(View.VISIBLE);
                        } else {
                            image_isreviewd.setVisibility(View.INVISIBLE);
                            image_isfvrt.setVisibility(View.INVISIBLE);
                        }

                        //set opening times
                        if (jboj.has("opening times")) {
                            if (jboj.getString("opening times").equalsIgnoreCase("")) {
                                textOpeningtimes.setText("Openeing Times" + "\n-");
                                rl_days.setVisibility(View.GONE);
                            } else {
                                textOpeningtimes.setText("Openeing Times");
                                // HandyObjects.showAlert(getActivity(), "withobject");
                                JSONObject jobj_opentime = jboj.getJSONObject("opening times");
                                rl_days.setVisibility(View.VISIBLE);
                                mondaykey.setText(jobj_opentime.getString("day1"));
                                mondayvalue.setText(jobj_opentime.getString("opentime1") + "-" + jobj_opentime.getString("closetime1"));
                                tuesdaykey.setText(jobj_opentime.getString("day2"));
                                tuesdayvalue.setText(jobj_opentime.getString("opentime2") + "-" + jobj_opentime.getString("closetime2"));
                                wednesdaykey.setText(jobj_opentime.getString("day3"));
                                wednesdayvalue.setText(jobj_opentime.getString("opentime3") + "-" + jobj_opentime.getString("closetime3"));
                                thrusdaykey.setText(jobj_opentime.getString("day4"));
                                thrusdayvalue.setText(jobj_opentime.getString("opentime4") + "-" + jobj_opentime.getString("closetime4"));
                                fridaykey.setText(jobj_opentime.getString("day5"));
                                fridayvalue.setText(jobj_opentime.getString("opentime5") + "-" + jobj_opentime.getString("closetime5"));
                                saturdaykey.setText(jobj_opentime.getString("day6"));
                                saturdayvalue.setText(jobj_opentime.getString("opentime6") + "-" + jobj_opentime.getString("closetime6"));
                                sundaykey.setText(jobj_opentime.getString("day7"));
                                sundayvalue.setText(jobj_opentime.getString("opentime7") + "-" + jobj_opentime.getString("closetime7"));
                            }

                        }
                        /*mondaykey.setText(jobj_opentime.getString("day1"));
                        mondayvalue.setText(jobj_opentime.getString("opentime1") + "-" + jobj_opentime.getString("closetime1"));
                        tuesdaykey.setText(jobj_opentime.getString("day2"));
                        tuesdayvalue.setText(jobj_opentime.getString("opentime2") + "-" + jobj_opentime.getString("closetime2"));
                        wednesdaykey.setText(jobj_opentime.getString("day3"));
                        wednesdayvalue.setText(jobj_opentime.getString("opentime3") + "-" + jobj_opentime.getString("closetime3"));
                        thrusdaykey.setText(jobj_opentime.getString("day4"));
                        thrusdayvalue.setText(jobj_opentime.getString("opentime4") + "-" + jobj_opentime.getString("closetime4"));
                        fridaykey.setText(jobj_opentime.getString("day5"));
                        fridayvalue.setText(jobj_opentime.getString("opentime5") + "-" + jobj_opentime.getString("closetime5"));
                        saturdaykey.setText(jobj_opentime.getString("day6"));
                        saturdayvalue.setText(jobj_opentime.getString("opentime6") + "-" + jobj_opentime.getString("closetime6"));
                        sundaykey.setText(jobj_opentime.getString("day7"));
                        sundayvalue.setText(jobj_opentime.getString("opentime7") + "-" + jobj_opentime.getString("closetime7"));*/

                        if (arraylist_news.size() >= 5) {
                            seemore.setVisibility(View.VISIBLE);
                        }
                    } else if (jboj.getString("message").equalsIgnoreCase("fail")) {
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
                params.put("latitude", preferences.getString(Consts.CURRENT_LATITUDE, ""));
                params.put("longitude", preferences.getString(Consts.CURRENT_LONGITUDE, ""));
                params.put("uid", preferences.getString(Consts.USER_ID, ""));
                params.put("lid", lid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    private void displayBottomDialog() {
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        tellfrnd_Dialog = new Dialog(getActivity());
        tellfrnd_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tellfrnd_Dialog.setCanceledOnTouchOutside(false);
        Window window = tellfrnd_Dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.y = 30;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window.setAttributes(wlp);
        tellfrnd_Dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        tellfrnd_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tellfrnd_Dialog.setContentView(R.layout.dialog_tellafrnd);
        LinearLayout approx_lay = (LinearLayout) tellfrnd_Dialog.findViewById(R.id.approx_lay);
        int valueinpix = (int) getResources().getDimension(R.dimen.newforcheck);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - valueinpix, (h / 2) - 10);
        approx_lay.setLayoutParams(params);

        RelativeLayout rl_email = (RelativeLayout) tellfrnd_Dialog.findViewById(R.id.rl_email);
        RelativeLayout rl_cancel = (RelativeLayout) tellfrnd_Dialog.findViewById(R.id.rl_cancel);
        RelativeLayout rl_sms = (RelativeLayout) tellfrnd_Dialog.findViewById(R.id.rl_sms);
        RelativeLayout rl_fb = (RelativeLayout) tellfrnd_Dialog.findViewById(R.id.rl_fb);
        RelativeLayout rl_twitter = (RelativeLayout) tellfrnd_Dialog.findViewById(R.id.rl_twitter);
        RelativeLayout rl_whatsapp = (RelativeLayout) tellfrnd_Dialog.findViewById(R.id.rl_whatsapp);

        rl_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.redappetite_reviewndfvrt));
                    intent.putExtra(Intent.EXTRA_TEXT, make_sharetext);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                }
            }
        });

        rl_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // int permissionCheck = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT < 23) {
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_CONTACT_PHONE);
                    }
                }
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT < 23) {
                    } else {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                }
            }
        });

        rl_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (facebook.getAccessToken() == null) {
                    //HandyObjects.showAlert(getActivity(), "Please login");
                    facebook.authorize(getActivity(), new String[]{"email", "public_profile", PERMISSION}, Facebook.FORCE_DIALOG_AUTH,
                            new LoginDialogListener());
                } else {
                    shareTexttoFb();
                }
            }
        });

        rl_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareTwitter(getActivity(), make_sharetext, "", "", "");
            }
        });

        rl_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pmm = getActivity().getPackageManager();
                try {
                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = make_sharetext;
                    PackageInfo info = pmm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    // startActivity(Intent.createChooser(waIntent, "Share with"));
                    startActivity(waIntent);
                } catch (PackageManager.NameNotFoundException e) {
                    HandyObjects.showAlert(getActivity(), "WhatsApp not Installed");
                }
            }
        });

        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellfrnd_Dialog.dismiss();
            }
        });

        if (tellfrnd_Dialog.isShowing()) {
        } else {
            tellfrnd_Dialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.moveto_prev:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
                break;
            case R.id.moveto_next:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                break;
            case R.id.seemore:
                if (arraylist_news.size() > 0) {
                    NewsSeemore_Fragment ndetail = new NewsSeemore_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(Consts.NEWS_INSIDEARRAYLIST, arraylist_news);
                    bundle.putString(Consts.NEWS_CATEGORYTYPE, categorytype);
                    bundle.putString(Consts.NEWS_BUILDINGNAME, buildingname.getText().toString());
                    ndetail.setArguments(bundle);
                    mdashboard.displayWithoutViewFragmentWithanimWithoutv4(ndetail);
                }
                // mdashboard.displayWithoutViewFragmentWithanimWithoutv4(new NewsSeemore_Fragment());
                break;
            case R.id.allnews:
                // mdashboard.displayWithoutViewFragmentWithanim(new NewsDetail_Fragment());
                break;
            case R.id.text_editreview:
                if (preferences.getBoolean("login", false)) {
                    if (text_editreview.getText().toString().equalsIgnoreCase(getString(R.string.addreview))) {
                        DetailReviewProfile_Fragment review_frgm = new DetailReviewProfile_Fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Consts.REVIEW_TYPE, "add");
                        bundle.putString(Consts.REVIEW_LID, lid);
                        bundle.putString(Consts.REVIEW_BUILDINGNAME, buildingname.getText().toString());
                        bundle.putString(Consts.REVIEW_TOTALRATING, ratingstars.getText().toString());
                        bundle.putString(Consts.REVIEW_DISTANCE, distance.getText().toString());
                        bundle.putString(Consts.REVIEW_USERS, users.getText().toString());
                        bundle.putString(Consts.REVIEW_LIKES, likes.getText().toString());
                        bundle.putString(Consts.REVIEW_DISLIKES, dislikes.getText().toString());
                        bundle.putString(Consts.REVIEW_BUILDINGADDRESS, building_address);
                        bundle.putString(Consts.REVIEW_IMAGEURL, image_url);
                        if (isReviewed != null && !isReviewed.isEmpty()) {
                            bundle.putString(Consts.REVIEW_ISREVIEWD, isReviewed);
                            bundle.putString(Consts.REVIEW_ISFAV, isfav);
                        }
                        review_frgm.setArguments(bundle);
                        mdashboard.displayWithoutViewFragmentWithanimWithoutv4(review_frgm);
                    } else {
                        DetailReviewProfile_Fragment review_frgm = new DetailReviewProfile_Fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Consts.REVIEW_TYPE, "edit");
                        bundle.putString(Consts.REVIEW_LID, lid);
                        bundle.putString(Consts.REVIEW_BUILDINGNAME, buildingname.getText().toString());
                        bundle.putString(Consts.REVIEW_TOTALRATING, ratingstars.getText().toString());
                        bundle.putString(Consts.REVIEW_DISTANCE, distance.getText().toString());
                        bundle.putString(Consts.REVIEW_USERS, users.getText().toString());
                        bundle.putString(Consts.REVIEW_LIKES, likes.getText().toString());
                        bundle.putString(Consts.REVIEW_DISLIKES, dislikes.getText().toString());
                        bundle.putString(Consts.REVIEW_BUILDINGADDRESS, building_address);
                        bundle.putString(Consts.REVIEW_IMAGEURL, image_url);
                        if (edit_jsonobject != null && !edit_jsonobject.isEmpty()) {
                            bundle.putString(Consts.REVIEW_EDIT_JSONOBJECT, edit_jsonobject);
                        }
                        if (isReviewed != null && !isReviewed.isEmpty()) {
                            bundle.putString(Consts.REVIEW_ISREVIEWD, isReviewed);
                            bundle.putString(Consts.REVIEW_ISFAV, isfav);
                        }
                        review_frgm.setArguments(bundle);
                        mdashboard.displayWithoutViewFragmentWithanimWithoutv4(review_frgm);
                    }
                } else {
                    Intent ilogin = new Intent(getActivity(), ContainerActivity.class);
                    ilogin.putExtra("Login", true);
                    ilogin.putExtra("fromdashboard", "yup");
                    ilogin.putExtra("forwhich", "review");
                    startActivity(ilogin);
                }

                break;
            case R.id.text_addtofvrt:
                if (preferences.getBoolean("login", false)) {
                    if (text_addtofvrt.getText().toString().equalsIgnoreCase(getString(R.string.addtofvrt))) {
                        Addtofvrt_task();
                    } else {
                        if (uf1 != null && !uf1.isEmpty()) {
                            Deletefvrt_task();
                        }
                    }
                } else {
                    Intent ilogin = new Intent(getActivity(), ContainerActivity.class);
                    ilogin.putExtra("Login", true);
                    ilogin.putExtra("fromdashboard", "yup");
                    ilogin.putExtra("forwhich", "fvrt");
                    startActivity(ilogin);
                }
                break;
            case R.id.img_share:
                displayBottomDialog();
                break;
            case R.id.telepcall:
                int permissionCheck = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT < 23) {
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                } else {
                    if (telephone_value.getText().toString() != null && !telephone_value.getText().toString().isEmpty()) {
                        callPhone();
                    }
                }
                break;
            case R.id.websiteopen:
                if (website.getText().toString() != null && !website.getText().toString().isEmpty()) {
                    String url = "";
                    if (!website.getText().toString().startsWith("http://") && !website.getText().toString().startsWith("https://")) {
                        url = "http://" + website.getText().toString();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } else {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(website.getText().toString()));
                        startActivity(i);
                    }
                }
                break;
            case R.id.text_sendemail:
                if (Patterns.EMAIL_ADDRESS.matcher(text_email.getText().toString()).matches()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + text_email.getText().toString()));
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.redappetite_app));
                        intent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //TODO smth
                    }
                } else {
                    HandyObjects.showAlert(getActivity(), getString(R.string.notemail_type));
                }
                break;
            case R.id.image_reporterror:
                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(new ReportError_Fragment());
                break;
            case R.id.navigate:
                if (latitude != null && !latitude.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude));
                    startActivity(intent);
                }
                break;
            case R.id.playvideo_btn:
                if (video_url != null && !video_url.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video_url));
                    intent.setDataAndType(Uri.parse(video_url), "video/mp4");
                    startActivity(intent);
                } else {
                    HandyObjects.showAlert(context, getString(R.string.nourl));
                }
                break;
        }
    }

    private void Addtofvrt_task() {
        String tag_json_obj = "json_obj_req";
        HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                HandyObjects.ADDTOFVRT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        HandyObjects.showAlert(context, getString(R.string.succadded) + " " + buildingname.getText().toString() + " " + getString(R.string.toyour_fvrt));
                        uf1 = jboj.getString("Id");
                        text_addtofvrt.setText(getString(R.string.deletefrm_fvrt));
                        //   getActivity().getFragmentManager().popBackStack();
                    } else if (jboj.getString("message").equalsIgnoreCase("fail")) {
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
                params.put("uid", preferences.getString(Consts.USER_ID, ""));
                params.put("lid", lid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    private void Deletefvrt_task() {
        String tag_json_obj = "json_obj_req";
        HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                HandyObjects.DELETEFVRT_BUILDING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        HandyObjects.showAlert(context, context.getString(R.string.succdelete_fvrt) + " " + buildingname.getText().toString() + " " + context.getString(R.string.frm_fvrt));
                        text_addtofvrt.setText(getString(R.string.addtofvrt));
                        //getActivity().getFragmentManager().popBackStack();
                    } else if (jboj.getString("message").equalsIgnoreCase("fail")) {
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
                params.put("uf1", uf1);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void shareTwitter(Activity activity, String text, String url, String via, String hashtags) {
        StringBuilder tweetUrl = new StringBuilder("https://twitter.com/intent/tweet?text=");
        tweetUrl.append(TextUtils.isEmpty(text) ? urlEncode(" ") : urlEncode(text));
        if (!TextUtils.isEmpty(url)) {
            tweetUrl.append("&url=");
            tweetUrl.append(urlEncode(url));
        }
        if (!TextUtils.isEmpty(via)) {
            tweetUrl.append("&via=");
            tweetUrl.append(urlEncode(via));
        }
        if (!TextUtils.isEmpty(hashtags)) {
            tweetUrl.append("&hastags=");
            tweetUrl.append(urlEncode(hashtags));
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl.toString()));
        List<ResolveInfo> matches = activity.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }
        activity.startActivity(intent);
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf("wtf", "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            //  return mResources.length;
            return premiumall_images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.row_searchfirstfrgmt, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.centerimage);
            //  imageView.setImageResource(mResources[position]);
            //imageView.setImageResource(premiumall_images.get(position));
            Glide.with(context).load(premiumall_images.get(position)).placeholder(R.drawable.clubicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
            container.addView(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SeeFullImageDialog(position);
                }
            });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }

    class CustomPagerAdapterDialog extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapterDialog(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return premiumall_images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.row_searchfirstfrgmtdialog, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.centerimage);
            //  fullimage_url = premiumall_images.get(0);
            fix_number.setText("/" + String.valueOf(premiumall_images.size()));
            Glide.with(context).load(premiumall_images.get(position)).placeholder(R.drawable.clubicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }

    private void SeeFullImageDialog(int posi) {
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
        //openDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View vview = inflater.inflate(R.layout.fullimage_dialog, null, false);
        openDialog.setContentView(vview);
        final ImageView cross = (ImageView) vview.findViewById(R.id.cross);
        TextView moveto_prev = (TextView) vview.findViewById(R.id.moveto_prev);
        TextView moveto_next = (TextView) vview.findViewById(R.id.moveto_next);
        dynamic_number = (TextView) vview.findViewById(R.id.dynamic_number);
        fix_number = (TextView) vview.findViewById(R.id.fix_number);
        ImageView share_img = (ImageView) vview.findViewById(R.id.share_img);
        // ImageView fullimage = (ImageView) vview.findViewById(R.id.fullimage);

        // Glide.with(context).load(premiumall_images.get(posi)).placeholder(R.drawable.clubicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(fullimage);
        final ViewPager dialog_viewpager = (ViewPager) vview.findViewById(R.id.dialog_viewpager);
        dialog_viewpager.setAdapter(new CustomPagerAdapterDialog(getActivity()));
        dialog_viewpager.setCurrentItem(posi);
        fullimage_url = premiumall_images.get(posi);
        dynamic_number.setText(String.valueOf(posi + 1));
        dialog_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int po = position + 1;
                dynamic_number.setText(String.valueOf(po));
                fullimage_url = premiumall_images.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
            }
        });

        moveto_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_viewpager.setCurrentItem(dialog_viewpager.getCurrentItem() - 1, true);
            }
        });

        moveto_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_viewpager.setCurrentItem(dialog_viewpager.getCurrentItem() + 1, true);
            }
        });

        share_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadImage().execute(fullimage_url);
            }
        });
        openDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (telephone_value.getText().toString() != null && !telephone_value.getText().toString().isEmpty()) {
                    callPhone();
                }
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_CONTACT_PHONE) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT < 23) {
                } else {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        }
        if (requestCode == 2) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void callPhone() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + telephone_value.getText().toString()));
        startActivity(callIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == getActivity().RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor contactCursor = getActivity().getContentResolver()
                            .query(contactData,
                                    new String[]{ContactsContract.Contacts._ID},
                                    null, null, null);
                    String id = null;
                    if (contactCursor.moveToFirst()) {
                        id = contactCursor.getString(contactCursor
                                .getColumnIndex(ContactsContract.Contacts._ID));
                    }
                    contactCursor.close();
                    Cursor phoneCursor = getActivity()
                            .getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + "= ? ", new String[]{id}, null);
                    if (phoneCursor.moveToFirst()) {
                        phone_no = phoneCursor
                                .getString(phoneCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.e("Phone no", phone_no);
                    }
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("sms_body", make_sharetext);
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    sendIntent.putExtra("address", phone_no);
                    try {
                        startActivity(sendIntent);
                    } catch (ActivityNotFoundException ex) {
                        HandyObjects.showAlert(getActivity(), getString(R.string.no_messageapp));
                    }
                }
                break;
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  p_dialog = new ProgressDialog(FullImageHorizontal.this);
            //   p_dialog.setMessage("Loading Image ....");
            //  p_dialog.show();
            HandyObjects.showProgressDialog(getActivity());

        }

        protected Bitmap doInBackground(String... args) {
            try {
                bm_image = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm_image;
        }

        protected void onPostExecute(Bitmap image) {

            if (image != null) {
                //  img.setImageBitmap(image);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Hey view/download this image");
                String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), image, "RedApp", null);
                // MediaStore.Images.Media.in
                Uri screenshotUri = Uri.parse(path);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                //intent.setType("image*//*");
                startActivity(Intent.createChooser(intent, "Share image via..."));
                // p_dialog.dismiss();
                HandyObjects.stopProgressDialog();

            } else {

                //  p_dialog.dismiss();
                HandyObjects.stopProgressDialog();
                Toast.makeText(getActivity(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void check_permission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Permission is needed")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        2);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);
            }
        }
    }

    private final class LoginDialogListener implements Facebook.DialogListener {
        public void onComplete(final Bundle values) {
            shareTexttoFb();
        }

        public void onFacebookError(FacebookError error) {
        }

        public void onError(DialogError error) {
            //  CommonObject.showAlert(getActivity(), "Login error");
            HandyObjects.showAlert(getActivity(), "loginerror");
        }

        public void onCancel() {
            //  HandyObjects.showAlert(getActivity(), "logincancel");
        }
    }

    private void shareTexttoFb() {
        final AccessToken accessToken = new AccessToken(facebook.getAccessToken(), facebook.getAppId(), facebook.getAppId(), null, null, null, new Date(facebook.getAccessExpires()), null);
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Bundle params = new Bundle();
                        params.putString("message", make_sharetext);
                        new GraphRequest(
                                accessToken,
                                "/me/feed",
                                params,
                                HttpMethod.POST,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
                                        HandyObjects.showAlert(getActivity(), getString(R.string.postedsucc));
                                    }
                                }
                        ).executeAsync();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email,gender, birthday,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
