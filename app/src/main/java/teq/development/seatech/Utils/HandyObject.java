package teq.development.seatech.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import teq.development.seatech.R;
import teq.development.seatech.ServerCall.apiCall;
import teq.development.seatech.ServerCall.apiManager;

import static android.content.Context.MODE_PRIVATE;

public class HandyObject {
    private static SharedPreferences sharedPrefs;
    public static KProgressHUD hud;
    private static ProgressDialog progressDialog;

    public static apiCall getApiManagerType() {
        return apiManager.getApiManager().create(apiCall.class);
    }
    public static apiCall getApiManagerTypeAdmin() {
        return apiManager.getApiManagerAdmin().create(apiCall.class);
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        boolean isConnected = info != null && info.isConnectedOrConnecting();
        return isConnected;
    }

    public static boolean putPrams(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getPrams(Context context, String key) {
        SharedPreferences shared = getPrefs(context);
        return (shared.getString(key, ""));
    }

    public static boolean putIntPrams(Context context, String key, int value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static int getIntPrams(Context context, String key) {
        SharedPreferences shared = getPrefs(context);
        return (shared.getInt(key, 0));
    }

    public static boolean clearpref(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.clear();
        return editor.commit();
    }

    private static SharedPreferences getPrefs(Context context) {
        if (sharedPrefs == null) {
            sharedPrefs = context.getSharedPreferences("com_example_vibrant_beautysalon", MODE_PRIVATE);
        }
        return sharedPrefs;
    }

    public static void showAlert(final Context ctx, final String text) {
        try {
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
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                            0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        } catch (Exception e) {
        }

    }


   /* public static void showHideProgressBar(Context context) {

        showHideProgressBar(context, "Loading...");

    }

    public static void showHideProgressBar(Context context, String message) {
        showHideProgressBar(context, message, "Loading...");

    }*/

/*    public static void showHideProgressBar(Context context, String message, String Title) {


        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(message);
            progressDialog.setTitle(Title);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        } else {
            if (progressDialog != null) {
                try {
                    progressDialog.show();
                } catch (Exception e) {
                }

            }

        }
    }*/

    public static void myToast(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_LONG).show();
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

    public static void hideSoftKeyboard(Context ctx) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) ctx;
        if (appCompatActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) appCompatActivity.getSystemService(appCompatActivity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(appCompatActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void showSoftKeyboard(Context ctx, EditText edittext) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) ctx;
        if (appCompatActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) appCompatActivity.getSystemService(appCompatActivity.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(edittext, InputMethodManager.SHOW_FORCED);
        }
    }

}
