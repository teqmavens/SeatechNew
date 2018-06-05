package com.vadevelopment.RedAppetite.newssection;

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
import android.database.sqlite.SQLiteDatabase;
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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

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

public class NewsDetail_Fragment extends android.app.Fragment implements View.OnClickListener {

    private static String TAG = "NewsDetail_Fragment";
    DashboardActivity mdashboard;
    private LinearLayout ll_top;
    private ImageView img_share, imageview, fwd_arrow;
    private Dialog tellfrnd_Dialog, openDialog;
    private RatingBar ratingBar;
    private Context context;
    private SQLiteDatabase database;
    private SharedPreferences preferences;
    ArrayList<String> premiumall_images;
    CustomPagerAdapter mCustomPagerAdapter;
    private LinearLayout ll_viewpager;
    private ViewPager mViewPager;
    String nid, lid, which_latlong;
    public static final int PICK_CONTACT = 4;
    private String phone_no;
    private static final int MY_PERMISSIONS_REQUEST_CONTACT_PHONE = 2;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 3;
    private static Facebook facebook = new Facebook("1881398585516116");
    private static final String PERMISSION = "user_friends";
    TextView name, address, ratingstars, distance, users, likes, dislikes, text_newsdate, text_newsheadline, news_decription,
            moveto_prev, moveto_next;
    String make_sharetext, from, fullimage_url;
    TextView dynamic_number, fix_number;
    Bitmap bm_image;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_newsdetail, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        setToolBar();
        check_permission();
        context = getActivity();
        ll_top = (LinearLayout) view.findViewById(R.id.ll_top);
        img_share = (ImageView) view.findViewById(R.id.img_share);
        fwd_arrow = (ImageView) view.findViewById(R.id.fwd_arrow);
        name = (TextView) view.findViewById(R.id.name);
        address = (TextView) view.findViewById(R.id.address);
        ratingstars = (TextView) view.findViewById(R.id.ratingstars);
        distance = (TextView) view.findViewById(R.id.distance);
        users = (TextView) view.findViewById(R.id.users);
        likes = (TextView) view.findViewById(R.id.likes);
        dislikes = (TextView) view.findViewById(R.id.dislikes);
        text_newsdate = (TextView) view.findViewById(R.id.text_newsdate);
        text_newsheadline = (TextView) view.findViewById(R.id.text_newsheadline);
        news_decription = (TextView) view.findViewById(R.id.news_decription);
        moveto_prev = (TextView) view.findViewById(R.id.moveto_prev);
        moveto_next = (TextView) view.findViewById(R.id.moveto_next);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        imageview = (ImageView) view.findViewById(R.id.imageview);
        ll_viewpager = (LinearLayout) view.findViewById(R.id.ll_viewpager);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        moveto_prev.setOnClickListener(this);
        moveto_next.setOnClickListener(this);
        ll_top.setOnClickListener(this);
        img_share.setOnClickListener(this);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setData_Fromdatabase();
            }
        });
        if (getArguments() != null) {
            nid = getArguments().getString(Consts.NEWS_CLICKNID);
            from = getArguments().getString(Consts.ONNEWSDETAIL_FROM);
            if (from.equalsIgnoreCase("list_inside")) {
                fwd_arrow.setVisibility(View.INVISIBLE);
            }
            if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
                com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // GetSearchTask_Task(search.getText().toString());
                        SearchDetail_Task();
                    }
                }, 100);

            }
        }

       /* ll_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!from.equals("list_inside")) {
                    SearchDetail_Fragment sdetailk_frgm = new SearchDetail_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Consts.SEARCH_CLICKLID, lid);
                    bundle.putString(Consts.CHECKCLICK_FROM, "news");
                    sdetailk_frgm.setArguments(bundle);
                    mdashboard.displayWithoutViewFragmentWithanimWithoutv4(sdetailk_frgm);
                }
            }
        });*/

      /*  img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayBottomDialog();
            }
        });*/
    }

    private void setToolBar() {
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText(getString(R.string.Events));
        mdashboard.setting.setText(getString(R.string.back));
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        mdashboard.mapButton.setVisibility(View.GONE);
        mdashboard.mapicon.setVisibility(View.GONE);
        mdashboard.settingicon.setVisibility(View.GONE);

        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  mdashboard.displayWithoutViewFragment(new Setting_SearchFragment());
                getActivity().getFragmentManager().popBackStack();
            }
        });
    }


    private void SearchDetail_Task() {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.NEWSDETAILTASK_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("Success")) {
                        JSONObject jobj_loclist = jboj.getJSONObject("location_list");
                        JSONObject jobj_newslist = jboj.getJSONObject("news_list");
                        // JSONObject jobj_newsimages = jboj.getJSONObject("news_images");

                        name.setText(jobj_loclist.getString("buildingname"));
                        address.setText(jobj_loclist.getString("address") + "," + jobj_loclist.getString("country"));
                        ratingBar.setRating(Float.parseFloat(jobj_loclist.getString("avgRate")));
                        double ii = Double.parseDouble(jobj_loclist.getString("avgRate"));
                        ratingstars.setText(String.valueOf(new DecimalFormat("##.#").format(ii)));
                        distance.setText(String.valueOf(Math.round(Float.parseFloat(jobj_loclist.getString("distance")))));
                        users.setText(jobj_loclist.getString("total"));
                        likes.setText(jobj_loclist.getString("happy"));
                        dislikes.setText(jobj_loclist.getString("sad"));
                        lid = jobj_loclist.getString("lid");

                        text_newsdate.setText(jobj_newslist.getString("date"));
                        text_newsheadline.setText(jobj_newslist.getString("headline"));
                        news_decription.setText(jobj_newslist.getString("description"));

                        make_sharetext = getString(R.string.irecomend_you) + "\n\n" + jobj_newslist.getString("headline") + "/" + jobj_newslist.getString("date") + "\n\n" +
                                jobj_loclist.getString("buildingname") + "\n" + jobj_loclist.getString("address") + "\n" + jobj_loclist.getString("country") + "\n\n" +
                                getString(R.string.telephone) + " " + jobj_loclist.getString("telephone") + "\n" + getString(R.string.email) + " " + jobj_loclist.getString("email") +
                                "\n" + getString(R.string.internet) + " " + jobj_loclist.getString("website") + "\n\n" + jobj_loclist.getString("avgRate") + " " +
                                getString(R.string.stars) + " / " + jobj_loclist.getString("total") + " " + getString(R.string.reviews) + "\n\n" + getString(R.string.get_latestnews) +
                                "\n\n" + getString(R.string.androidios_link) + "\n\n" + getString(R.string.internet) + "\n" + jobj_loclist.getString("website");

                        if (jobj_loclist.getString("type").equalsIgnoreCase("Free") || jobj_loclist.getString("type").equalsIgnoreCase("free")) {
                            Glide.with(context).load(jobj_loclist.getString("free")).placeholder(R.drawable.clubicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(imageview);
                        } else {
                            Glide.with(context).load(jobj_loclist.getString("premium")).placeholder(R.drawable.clubicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(imageview);
                        }
                        JSONArray jarryimages = jboj.getJSONArray("news_images");
                        if (jarryimages.length() == 0) {
                            ll_viewpager.setVisibility(View.GONE);
                        } else {
                            ll_viewpager.setVisibility(View.VISIBLE);
                        }
                        premiumall_images = new ArrayList<>();
                        for (int i = 0; i < jarryimages.length(); i++) {
                            premiumall_images.add(jarryimages.getString(i));
                        }
                        mCustomPagerAdapter = new CustomPagerAdapter(getActivity());
                        mViewPager.setAdapter(mCustomPagerAdapter);
                        // makeshareText(jobj_alllist);
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
                if (which_latlong.equalsIgnoreCase("current")) {
                    params.put("latitude", preferences.getString(Consts.CURRENT_LATITUDE, ""));
                    params.put("longitude", preferences.getString(Consts.CURRENT_LONGITUDE, ""));
                } else {
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), which_latlong.split(",")[1]);
                    params.put("latitude", String.valueOf(which_latlong.split(",")[0]));
                    params.put("longitude", String.valueOf(which_latlong.split(",")[1]));
                }
                params.put("uid", preferences.getString(Consts.USER_ID, ""));
                params.put("nid", nid);
                return params;
            }
        };
        com.vadevelopment.RedAppetite.AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void setData_Fromdatabase() {
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();
        Cursor cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    which_latlong = cursor.getString(cursor.getColumnIndex("event_whichlatlong"));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
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

        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellfrnd_Dialog.dismiss();
            }
        });

        rl_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.redappetite_news));
                    intent.putExtra(Intent.EXTRA_TEXT, make_sharetext);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    //TODO smth
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
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), "Please login");
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
                shareTwitter(getActivity(), make_sharetext, "url", "via", "hashtags");
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
                    //startActivity(Intent.createChooser(waIntent, "Share with"));
                    startActivity(waIntent);

                } catch (PackageManager.NameNotFoundException e) {
                    com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), "WhatsApp not Installed");
                }
            }
        });

        if (tellfrnd_Dialog.isShowing()) {
        } else {
            tellfrnd_Dialog.show();
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.moveto_prev:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
                break;
            case R.id.moveto_next:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                break;
            case R.id.ll_top:
                if (!from.equals("list_inside")) {
                    com.vadevelopment.RedAppetite.searchsection.SearchDetail_Fragment sdetailk_frgm = new com.vadevelopment.RedAppetite.searchsection.SearchDetail_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Consts.SEARCH_CLICKLID, lid);
                    bundle.putString(Consts.CHECKCLICK_FROM, "news");
                    sdetailk_frgm.setArguments(bundle);
                    mdashboard.displayWithoutViewFragmentWithanimWithoutv4(sdetailk_frgm);
                }
                break;
            case R.id.img_share:
                displayBottomDialog();
                break;
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
            // fullimage_url = premiumall_images.get(0);
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

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  p_dialog = new ProgressDialog(FullImageHorizontal.this);
            //   p_dialog.setMessage("Loading Image ....");
            //  p_dialog.show();
            com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(getActivity());

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
                com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();

            } else {

                //  p_dialog.dismiss();
                com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();
                Toast.makeText(getActivity(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
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
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), getString(R.string.no_messageapp));
                    }
                }
                break;
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
            com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), "loginerror");
        }

        public void onCancel() {
            com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), "logincancel");
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
                                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), getString(R.string.postedsucc));
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

