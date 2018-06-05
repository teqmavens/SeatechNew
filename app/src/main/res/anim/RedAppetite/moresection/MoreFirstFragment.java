package com.vadevelopment.RedAppetite.moresection;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * Created by Vibrant Android on 11-03-2017.
 */

public class MoreFirstFragment extends android.app.Fragment implements View.OnClickListener {
    RelativeLayout apppurchases, help, contact, tellafriend, rl_facebook, twitter, rate, legal, termsofuse, privacypolicy;
    DashboardActivity mDashboardActivity;
    private Dialog tellfrnd_Dialog;
    public static final int PICK_CONTACT = 4;
    private static final int MY_PERMISSIONS_REQUEST_CONTACT_PHONE = 2;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 3;
    private static Facebook facebook = new Facebook("1881398585516116");
    private static final String PERMISSION = "user_friends";
    private String phone_no;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDashboardActivity = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.more_homefragment, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        setToolbar();
        apppurchases = (RelativeLayout) view.findViewById(R.id.apppurchasestab);
        help = (RelativeLayout) view.findViewById(R.id.help);
        contact = (RelativeLayout) view.findViewById(R.id.contact);
        tellafriend = (RelativeLayout) view.findViewById(R.id.tellafriend);
        rl_facebook = (RelativeLayout) view.findViewById(R.id.rl_facebook);
        twitter = (RelativeLayout) view.findViewById(R.id.twitter);
        rate = (RelativeLayout) view.findViewById(R.id.rate);
        legal = (RelativeLayout) view.findViewById(R.id.legal);
        termsofuse = (RelativeLayout) view.findViewById(R.id.termsofuse);
        privacypolicy = (RelativeLayout) view.findViewById(R.id.privacypolicy);

        apppurchases.setOnClickListener(this);
        help.setOnClickListener(this);
        contact.setOnClickListener(this);
        tellafriend.setOnClickListener(this);
        rl_facebook.setOnClickListener(this);
        twitter.setOnClickListener(this);
        rate.setOnClickListener(this);
        legal.setOnClickListener(this);
        termsofuse.setOnClickListener(this);
        privacypolicy.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.apppurchasestab:
                mDashboardActivity.displayWithoutViewFragmentWithanimWithoutv4(new com.vadevelopment.RedAppetite.moresection.AppPurchaseFragment());
                break;
            case R.id.help:
                mDashboardActivity.displayWithoutViewFragmentWithanimWithoutv4(new HelpFragment());
                break;
            case R.id.contact:
                mDashboardActivity.displayWithoutViewFragmentWithanimWithoutv4(new ContactMoreFragment());
                break;
            case R.id.tellafriend:
                displayBottomDialog();
                break;
            case R.id.legal:
                mDashboardActivity.displayWithoutViewFragmentWithanimWithoutv4(new com.vadevelopment.RedAppetite.moresection.LegalNoticeFragment());
                break;
            case R.id.termsofuse:
                mDashboardActivity.displayWithoutViewFragmentWithanimWithoutv4(new com.vadevelopment.RedAppetite.moresection.TermsOfUseFragment());
                break;
            case R.id.privacypolicy:
                mDashboardActivity.displayWithoutViewFragmentWithanimWithoutv4(new com.vadevelopment.RedAppetite.moresection.PrivacyPolicyFragment());
                break;
            case R.id.rl_facebook:
                String url = "http://" + "www.facebook.com";
                Intent in = new Intent(Intent.ACTION_VIEW);
                in.setData(Uri.parse(url));
                startActivity(in);
                break;
            case R.id.twitter:
                String url_twiter = "http://" + "twitter.com";
                Intent in_twitter = new Intent(Intent.ACTION_VIEW);
                in_twitter.setData(Uri.parse(url_twiter));
                startActivity(in_twitter);
                break;
            case R.id.rate:
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
        }
    }

    private void setToolbar() {
        mDashboardActivity.footer.setVisibility(View.VISIBLE);
       // mDashboardActivity.searchicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colortext));
        mDashboardActivity.searchicon.setImageResource(R.drawable.srchicon);
        mDashboardActivity.searchtext.setTextColor(new Color().parseColor("#410202"));
        mDashboardActivity.profileicon.setImageResource(R.drawable.prfliconunselect);
       // mDashboardActivity.profileicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mDashboardActivity.profiletext.setTextColor(new Color().parseColor("#410202"));
        mDashboardActivity.newsfeedicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mDashboardActivity.newstext.setTextColor(new Color().parseColor("#410202"));
       // mDashboardActivity.moreicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.newcolortext));
        mDashboardActivity.moreicon.setImageResource(R.drawable.more_white);
        mDashboardActivity.moretext.setTextColor(new Color().parseColor("#e8dddd"));
        mDashboardActivity.adverticeicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.btmviewcolor));
        mDashboardActivity.adverticetext.setTextColor(new Color().parseColor("#410202"));

        mDashboardActivity.setting.setVisibility(View.GONE);
        mDashboardActivity.rl_headingwithcount.setVisibility(View.GONE);
        mDashboardActivity.textsearchimage.setVisibility(View.GONE);
        mDashboardActivity.centertextsearch.setVisibility(View.VISIBLE);
        mDashboardActivity.centertextsearch.setText(getString(R.string.more));
        mDashboardActivity.mapButton.setVisibility(View.GONE);
        mDashboardActivity.mapicon.setVisibility(View.GONE);
        mDashboardActivity.settingicon.setVisibility(View.GONE);
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
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.redappetite_app));
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.moreshare_text));
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
                shareTwitter(getActivity(), getString(R.string.moreshare_text), "url", "via", "hashtags");
            }
        });

        rl_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pmm = getActivity().getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = getString(R.string.moreshare_text);
                    PackageInfo info = pmm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

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
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                    sendIntent.putExtra("sms_body", getString(R.string.moreshare_text));
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
            // HandyObjects.showAlert(getActivity(), "logincancel");
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
                        params.putString("message", getString(R.string.moreshare_text));
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