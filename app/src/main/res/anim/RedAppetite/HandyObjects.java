package com.vadevelopment.RedAppetite;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Created by vibrantappz on 5/8/2017.
 */

public class HandyObjects {

    public static final String BASE_URL = "http://redappetite.de/Api/";
    public static final String REGISTER_URL = BASE_URL + "register.php";
    public static final String COUNTRY_URL = BASE_URL + "country.php";
    public static final String RESEND_URL = BASE_URL + "resend.php";
    public static final String LOGIN_URL = BASE_URL + "login.php";
    public static final String CONTACT_URL = BASE_URL + "contact.php";
    public static final String FRGTPWD_URL = BASE_URL + "forget.php";
    public static final String SEARCHTASK_URL = BASE_URL + "location.php";
    public static final String SEARCHDETAILTASK_URL = BASE_URL + "locationdetail.php";
    public static final String SEARCHCATEGORY_URL = BASE_URL + "category.php";
    public static final String ADDTOFVRT = BASE_URL + "add_favorites.php?";
    public static final String REPORTERROR_URL = BASE_URL + "report.php?";

    //Event section
    public static final String EVENT_URL = BASE_URL + "event.php";
    public static final String NEWSDETAILTASK_URL = BASE_URL + "eventdetail.php";

    //profile section
    public static final String PROFILE_URL = BASE_URL + "profile_view.php";
    public static final String NEWSLETTER_URL = BASE_URL + "newsletter.php";
    public static final String CHANGEEMAIL_URL = BASE_URL + "edit_email.php";
    public static final String CHANGEPWD_URL = BASE_URL + "edit_password.php";
    public static final String DELETEPROFILE_URL = BASE_URL + "deleteprofile.php";
    public static final String FAVBUILDING_URL = BASE_URL + "fav_building.php";
    public static final String DELETEFVRT = BASE_URL + "delete_fav_building.php?";
    public static final String DELETEFVRT_BUILDING = BASE_URL + "delete_favorites.php?";
    public static final String ADD_REVIEW = BASE_URL + "add_reviews.php?";
    public static final String EDIT_REVIEW = BASE_URL + "edit_reviews.php?";
    public static final String REMOVE_REVIEW = BASE_URL + "delete_reviews.php?";
    public static final String REVIEWBUILDING_URL = BASE_URL + "fav_reviews.php?";
    //More Section
    public static final String TERMS_URL = BASE_URL + "terms.php";
    //Advertise Screen
    public static final String ADVERTISE_URL = BASE_URL + "advertise.php";
    //https://developers.facebook.com/docs/graph-api/reference/v2.10/user/feed


    public static KProgressHUD hud;
    public static SQLiteDatabase database;

    public static void SetTEXGYREHEROES_BOLD(Context mContext, TextView textView) {
        Typeface textBody = Typeface.createFromAsset(mContext.getAssets(), "fonts/texgyreheros-bold.otf");
        textView.setTypeface(textBody);
    }

    public static void SetButton_TEXGYREHEROES_BOLD(Context mContext, Button button) {
        Typeface textBody = Typeface.createFromAsset(mContext.getAssets(), "fonts/texgyreheros-bold.otf");
        button.setTypeface(textBody);
    }

    public static void SetOPENSANS_LIGHT(Context mContext, TextView textView) {
        Typeface textBody = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Light.ttf");
        textView.setTypeface(textBody);
    }

    public static void SetOPENSANS_REGULAR(Context mContext, TextView textView) {
        Typeface textBody = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Regular.ttf");
        textView.setTypeface(textBody);
    }

    public static void showAlert(final Context ctx, final String text) {
        ((Activity) ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
                // Call toast.xml file for toast layout
                View toastRoot = inflater.inflate(R.layout.custom_toast, null);
                TextView txtMessage = (TextView) toastRoot.findViewById(R.id.txtMessage);
                txtMessage.setText(text);
                Toast toast = new Toast(ctx);
                toast.setView(toastRoot);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showProgressDialog(Context context) {
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please Wait")
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
        if (hud != null) {
            try {
                hud.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void stopProgressDialog() {
        if (hud != null && hud.isShowing()) {
            try {
                hud.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteAllDatabase(Context context) {
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(context).getWritableDatabase();
        database.delete("forsearch", null, null);
    }

    public static void collapse(final View v, int duration, int targetHeight) {
        int prevHeight = v.getWidth();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static void expand(final View v, int duration, int targetHeight) {

        int prevHeight = v.getWidth();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static int getEditextWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }
}
