package teq.development.seatech.Utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import teq.development.seatech.R;
import teq.development.seatech.ServerCall.apiCall;
import teq.development.seatech.ServerCall.apiManager;
import teq.development.seatech.database.ParseOpenHelper;

import static android.content.Context.MODE_PRIVATE;

public class HandyObject {
    private static SharedPreferences sharedPrefs;
    public static KProgressHUD hud;
    private static ProgressDialog progressDialog;
    public static int dateCount = 0;
    public static int weekcount = 0;
    public static int monthcount = 0;
    public static ImageLoader imageLoader;
    public static Calendar calendar = Calendar.getInstance(Locale.UK);

    public static apiCall getApiManagerType() {
        return apiManager.getApiManager().create(apiCall.class);
    }

    public static apiCall getApiManagerTypeAdmin() {
        return apiManager.getApiManagerAdmin().create(apiCall.class);
    }

    public static apiCall getApiManagerTypeJobs() {
        return apiManager.getApiAddressjobs().create(apiCall.class);
    }

    public static apiCall getApiManagerMain() {
        return apiManager.getApiManagerMain().create(apiCall.class);
    }

    public static apiCall getApiManagerMainRx() {
        return apiManager.getApiManagerMainRx().create(apiCall.class);
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

    public static boolean putbooleanPrams(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean getbooleanPrams(Context context, String key) {
        SharedPreferences shared = getPrefs(context);
        return (shared.getBoolean(key, false));
    }

    public static boolean clearpref(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.clear();
        return editor.commit();
    }

    public static void deleteAllDatabase(Context context) {
        SQLiteDatabase database = ParseOpenHelper.getInstance(context).getWritableDatabase();
        database.delete(ParseOpenHelper.TABLENAME_ALLJOBS, null, null);
        database.delete(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, null, null);
        database.delete(ParseOpenHelper.TABLENAME_MANUFACTURER, null, null);

        database.delete(ParseOpenHelper.TABLE_PICKUPJOBS, null, null);
        database.delete(ParseOpenHelper.TABLENAME_NEEDESTIMATE, null, null);
        database.delete(ParseOpenHelper.TABLE_SUBMITMYLABOR_NEWOFFRECORD, null, null);

        database.delete(ParseOpenHelper.TABLE_LCCHANGE, null, null);
        database.delete(ParseOpenHelper.TABLE_JOBSTATUS, null, null);
        database.delete(ParseOpenHelper.TABLE_UPLOADIMAGES, null, null);
        database.delete(ParseOpenHelper.TABLE_ADDPART, null, null);

        database.delete(ParseOpenHelper.TABLE_UPLOADIMAGES, null, null);
        database.delete(ParseOpenHelper.TABLE_URGENTMSG, null, null);
        database.delete(ParseOpenHelper.TABLE_NOTIFICATIONS, null, null);
        database.delete(ParseOpenHelper.TABLE_CHATPARENTLEFT, null, null);
        database.delete(ParseOpenHelper.TABLE_CHATMSGS, null, null);
        database.delete(ParseOpenHelper.TABLE_SCHEDULEDATA, null, null);
        database.delete(ParseOpenHelper.TABLE_SCHEDULECALENDARDATA, null, null);
        database.delete(ParseOpenHelper.TABLE_SCHEDULEWEEKDATA, null, null);
        database.delete(ParseOpenHelper.TABLE_SCHEDULEDAYDATA, null, null);
        database.delete(ParseOpenHelper.TABLE_STARTJOB, null, null);
        stopAlarm(context);
       /* String deviceToken = HandyObject.getPrams(context, AppConstants.DEVICE_TOKEN);
        HandyObject.putPrams(context, AppConstants.DEVICE_TOKEN, deviceToken);*/
    }

    private static SharedPreferences getPrefs(Context context) {
        if (sharedPrefs == null) {
            sharedPrefs = context.getSharedPreferences("com_example_teq_seatech", MODE_PRIVATE);
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

    public static void showProgressDialogDash(Context context,String text) {
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
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

    public static void stopProgressDialogDash() {
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

    public static String getDateFromPicker(Date date) {
        dateCount = 0;
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public static String getDateFromPickerNew(Date date) {
        dateCount = 0;
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public static String getDateFromPickerDayStatus(Date date) {
        //   dateCount = 0;
        SimpleDateFormat df = new SimpleDateFormat("MM dd yyyy");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public static String getDateFromPickerDayStatusNew(Date date) {
        //   dateCount = 0;
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public static String getDateFromCalendar(Date date) {
        //   dateCount = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public static String getDayFromDate(String input) {
        String goal = "";
        try {
            SimpleDateFormat inFormat = new SimpleDateFormat("MM dd yyyy");
            Date date = inFormat.parse(input);
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            goal = outFormat.format(date);
        } catch (Exception e) {
        }
        return goal;
    }

    public static String getDayFromDateNew(String input) {
        String goal = "";
        try {
            SimpleDateFormat inFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = inFormat.parse(input);
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            goal = outFormat.format(date);
        } catch (Exception e) {
        }
        return goal;
    }

    public static String ParseDateJobTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public static String ParseDateSchedule(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public static String ParseDateJobTimeNew(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public static String ParseDateTimeForNotes(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public static String getCurrentDate() {
        dateCount = 0;
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getCurrentDateNew() {
        dateCount = 0;
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getCurrentWeek_FirstDate(Context context) {
        weekcount = 0;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.add(Calendar.WEEK_OF_YEAR, weekcount);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        sb.append(HandyObject.ParseDateJobTime(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("," + HandyObject.ParseDateJobTime(calendar.getTime()));
        return sb.toString();
    }

    public static String getCurrentWeek_FirstDateSchedule(Context context) {
        weekcount = 0;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.add(Calendar.WEEK_OF_YEAR, weekcount);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        sb.append(HandyObject.ParseDateSchedule(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("-" + HandyObject.ParseDateSchedule(calendar.getTime()));
        return sb.toString();
    }

    public static String getCurrentWeek_FirstDateNew(Context context) {
        weekcount = 0;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.add(Calendar.WEEK_OF_YEAR, weekcount);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        sb.append(HandyObject.ParseDateJobTimeNew(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("," + HandyObject.ParseDateJobTimeNew(calendar.getTime()));
        return sb.toString();
    }

    public static String getSelectedWeek_FirstDate(Context context, String date) {
        weekcount = 0;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        // calendar.set(2018, 06, 4);
        calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) - 1, Integer.parseInt(date.split("-")[2]));
        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        sb.append(HandyObject.ParseDateJobTime(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("," + HandyObject.ParseDateJobTime(calendar.getTime()));
        return sb.toString();
    }

    public static String getSelectedWeek_FirstDateslash(Context context, String date) {
        weekcount = 0;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        // calendar.set(2018, 06, 4);
        // calendar.set(Integer.parseInt(date.split("/")[0]), Integer.parseInt(date.split("/")[1]) - 1, Integer.parseInt(date.split("/")[2]));
        calendar.set(Integer.parseInt(date.split("/")[2]), Integer.parseInt(date.split("/")[0]) - 1, Integer.parseInt(date.split("/")[1]));
        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        sb.append(HandyObject.ParseDateJobTime(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("," + HandyObject.ParseDateJobTime(calendar.getTime()));
        return sb.toString();
    }

    public static String getSelectedWeek_FirstDateslashNew(Context context, String date) {
        weekcount = 0;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        // calendar.set(2018, 06, 4);
        // calendar.set(Integer.parseInt(date.split("/")[0]), Integer.parseInt(date.split("/")[1]) - 1, Integer.parseInt(date.split("/")[2]));
        calendar.set(Integer.parseInt(date.split("/")[2]), Integer.parseInt(date.split("/")[0]) - 1, Integer.parseInt(date.split("/")[1]));
        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        sb.append(HandyObject.ParseDateJobTimeNew(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("," + HandyObject.ParseDateJobTimeNew(calendar.getTime()));
        return sb.toString();
    }

    public static String getPreviousWeek_FirstDate(Context context) {
        weekcount--;
        //  Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.add(Calendar.WEEK_OF_YEAR, weekcount);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        //  HandyObject.showAlert(context, HandyObject.ParseDateJobTime(calendar.getTime()));
        return HandyObject.ParseDateJobTime(calendar.getTime());
    }

    public static String getSelectedPreviousWeek_FirstDate(Context context, String date) {
        weekcount--;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) - 1, Integer.parseInt(date.split("-")[2]));
        //  calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) + 1, Integer.parseInt(date.split("-")[2]));
        // HandyObject.showAlert(context, String.valueOf(weekcount));
        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        calendar.add(Calendar.WEEK_OF_YEAR, weekcount);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        sb.append(HandyObject.ParseDateJobTime(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("," + HandyObject.ParseDateJobTime(calendar.getTime()));
        return sb.toString();
    }

    public static String getSelectedPreviousWeek_FirstDateSlash(Context context, String date) {
        weekcount--;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.set(Integer.parseInt(date.split("/")[2]), Integer.parseInt(date.split("/")[0]) - 1, Integer.parseInt(date.split("/")[1]));
        //  calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) + 1, Integer.parseInt(date.split("-")[2]));
        // HandyObject.showAlert(context, String.valueOf(weekcount));
        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        calendar.add(Calendar.WEEK_OF_YEAR, weekcount);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        sb.append(HandyObject.ParseDateJobTime(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("," + HandyObject.ParseDateJobTime(calendar.getTime()));
        return sb.toString();
    }

    public static String getSelectedPreviousWeek_FirstLastSchedule(Context context, String date) {
        weekcount--;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.set(Integer.parseInt(date.split("/")[2]), Integer.parseInt(date.split("/")[0]) - 1, Integer.parseInt(date.split("/")[1]));
        //  calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) + 1, Integer.parseInt(date.split("-")[2]));
        // HandyObject.showAlert(context, String.valueOf(weekcount));
        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        calendar.add(Calendar.WEEK_OF_YEAR, weekcount);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        sb.append(HandyObject.ParseDateSchedule(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("-" + HandyObject.ParseDateSchedule(calendar.getTime()));
        return sb.toString();
    }

    public static String getSelectedNextWeek_FirstDateSlash(Context context, String date) {
        weekcount++;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.set(Integer.parseInt(date.split("/")[2]), Integer.parseInt(date.split("/")[0]) - 1, Integer.parseInt(date.split("/")[1]));
        //  calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) + 1, Integer.parseInt(date.split("-")[2]));
        // HandyObject.showAlert(context, String.valueOf(weekcount));
        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        calendar.add(Calendar.WEEK_OF_YEAR, weekcount);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        sb.append(HandyObject.ParseDateJobTime(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("," + HandyObject.ParseDateJobTime(calendar.getTime()));
        return sb.toString();
    }

    public static String getSelectedNextWeek_FirstLastSchedule(Context context, String date) {
        weekcount++;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.set(Integer.parseInt(date.split("/")[2]), Integer.parseInt(date.split("/")[0]) - 1, Integer.parseInt(date.split("/")[1]));
        //  calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) + 1, Integer.parseInt(date.split("-")[2]));
        // HandyObject.showAlert(context, String.valueOf(weekcount));
        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        calendar.add(Calendar.WEEK_OF_YEAR, weekcount);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        sb.append(HandyObject.ParseDateSchedule(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("-" + HandyObject.ParseDateSchedule(calendar.getTime()));
        return sb.toString();
    }



    public static String getNextWeek_FirstDate(Context context) {
        weekcount++;
        Calendar calendar = Calendar.getInstance(Locale.UK);
        HandyObject.showAlert(context, String.valueOf(weekcount));
        calendar.add(Calendar.WEEK_OF_YEAR, weekcount);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        // HandyObject.showAlert(context, HandyObject.ParseDateJobTime(calendar.getTime()));
        return HandyObject.ParseDateJobTime(calendar.getTime());
    }

    public static String getSelectedNextWeek_FirstDate(Context context, String date) {
        weekcount++;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) - 1, Integer.parseInt(date.split("-")[2]));
        //    HandyObject.showAlert(context, String.valueOf(weekcount));
        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        calendar.add(Calendar.WEEK_OF_YEAR, weekcount);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        sb.append(HandyObject.ParseDateJobTime(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 8);
        sb.append("," + HandyObject.ParseDateJobTime(calendar.getTime()));
        return sb.toString();
    }

    public static String getNextDate(Calendar c) {
        dateCount++;
        c.add(c.DATE, 1);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy");
        String tomdate = df.format(c.getTime());
        return tomdate;
    }

    public static String getNextDateNew(Calendar c) {
        dateCount++;
        c.add(c.DATE, 1);
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String tomdate = df.format(c.getTime());
        return tomdate;
    }

    public static String getPreviousDate(Calendar c) {
        dateCount--;
        c.add(c.DATE, -1);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy");
        String prevdate = df.format(c.getTime());
        return prevdate;
    }

    public static String getPreviousDateNew(Calendar c) {
        dateCount--;
        c.add(c.DATE, -1);
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyy");
        String prevdate = df.format(c.getTime());
        return prevdate;
    }

    public static String parseDateToMM(String time) {
        String inputPattern = "MMMM yyyy";
        String outputPattern = "MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToYMD(String time) {
        String inputPattern = "MMM dd yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToYMDNew(String time) {
        String inputPattern = "MM/dd/yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToYMDSchedule(String time) {
        String inputPattern = "MMM dd,yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToMDYSchedule(String time) {
        String inputPattern = "MMM dd,yyyy";
        String outputPattern = "MM/dd/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToDMSCalendar(String time) {
        String inputPattern = "MM/dd/yyyy";
        String outputPattern = "dd MMM";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    public static String parseDateToMDY(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MM dd yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToMDYNew(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MM/dd/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToDayStatusText(String time) {
        String inputPattern = "MM dd yyyy";
        String outputPattern = "MMM dd yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static Date CurrentMonthFirstDate(String date) {
        monthcount = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) - 1, Integer.parseInt(date.split("-")[2]));
        int firstDate = calendar.getActualMinimum(Calendar.DATE);
        calendar.set(Calendar.DATE, firstDate);
        return calendar.getTime();
    }

    public static Date CurrentMonthLastDate(String date) {
        monthcount = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) - 1, Integer.parseInt(date.split("-")[2]));
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, lastDate);
        return calendar.getTime();
    }


    public static Date NextMonthFirstDate(String date) {
        monthcount++;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) - 1, Integer.parseInt(date.split("-")[2]));
        calendar.add(Calendar.MONTH, monthcount);
        int firstDate = calendar.getActualMinimum(Calendar.DATE);
        calendar.set(Calendar.DATE, firstDate);
        return calendar.getTime();
    }

    public static Date NextMonthLastDate(String date) {
        //  monthcount;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) - 1, Integer.parseInt(date.split("-")[2]));
        calendar.add(Calendar.MONTH, monthcount);
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, lastDate);
        return calendar.getTime();
    }

    public static Date PreviousMonthFirstDate(String date) {
        monthcount--;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) - 1, Integer.parseInt(date.split("-")[2]));
        calendar.add(Calendar.MONTH, monthcount);
        int firstDate = calendar.getActualMinimum(Calendar.DATE);
        calendar.set(Calendar.DATE, firstDate);
        return calendar.getTime();
    }

    public static Date PreviousMonthLastDate(String date) {
        //  monthcount;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]) - 1, Integer.parseInt(date.split("-")[2]));
        calendar.add(Calendar.MONTH, monthcount);
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, lastDate);
        return calendar.getTime();
    }


    public static int getNear15MinuteLB(int minutes) {
        //   Calendar calendar = Calendar.getInstance();
        //   int minutes = calendar.get(Calendar.MINUTE);
        int mod = minutes % 15;
        int res = 0;
        if ((mod) >= 8) {
            res = minutes + (15 - mod);
        } else {
            res = minutes - mod;
        }
        return (res % 60);
    }

    public static String getLaborcode(int posi) {
        String lcname = "";
        /*if (posi == 0) {
            lcname = "Travel Time";
        } else if (posi == 1) {
            lcname = "Service Time";
        } else if (posi == 2) {
            lcname = "Lunch";
        } else if (posi == 3) {
            lcname = "Shop Work Time";
        } else if (posi == 4) {
            lcname = "DONE FOR THE DAY";
        }*/
//        if (posi == 0) {
//            lcname = "Travel Time";
//        } else if (posi == 1) {
//            lcname = "Service Time";
//        } else if (posi == 2) {
//            lcname = "Lunch";
//        } else if (posi == 3) {
//            lcname = "Shop Work Time";
//        } else if (posi == 4) {
//            lcname = "Office Staff Labor";
//        } else if (posi == 5) {
//            lcname = "Tech Time Non-Productive";
//        } else if (posi == 6) {
//            lcname = "Tech Betterment" + " & " + "Training";
//        }
//        <item>TIME FIELD SERVICE BILLABLE</item>
//        <item>TIME FIELD SERVICE NON BILLABLE</item>
//        <item>TIME HOLIDAY PAID</item>
//        <item>TIME HOLIDAY UNPAID</item>
//        <item>TIME OFFICE LABOR</item>
//        <item>TIME SHOP WORK BILLABLE</item>
//        <item>TIME SHOP WORK NON BILLABLE</item>
//        <item>TIME SICK PAID</item>
//        <item>TIME SICK UNPAID</item>
//        <item>TIME TRAVELBILLABLE</item>
//        <item>TIME TRAVELNONBILLABLE</item>
//        <item>TIME VACATION PAID</item>
//        <item>TIME VACATION UNPAID</item>
//        if (posi == 0) {
//            lcname = "TIME FIELD SERVICE BILLABLE";
//        } else if (posi == 1) {
//            lcname = "TIME FIELD SERVICE NON BILLABLE";
//        } else if (posi == 2) {
//            lcname = "TIME HOLIDAY PAID";
//        } else if (posi == 3) {
//            lcname = "TIME HOLIDAY UNPAID";
//        } else if (posi == 4) {
//            lcname = "TIME OFFICE LABOR";
//        } else if (posi == 5) {
//            lcname = "TIME SHOP WORK BILLABLE";
//        } else if (posi == 6) {
//            lcname = "TIME SHOP WORK NON BILLABLE";
//        } else if (posi == 7) {
//            lcname = "TIME SICK PAID";
//        }  else if (posi == 8) {
//            lcname = "TIME SICK UNPAID";
//        } else if (posi == 9) {
//            lcname = "LUNCH";
//        } else if (posi == 10) {
//            lcname = "TIME TRAVELBILLABLE";
//        } else if (posi == 11) {
//            lcname = "TIME TRAVELNONBILLABLE";
//        } else if (posi == 12) {
//            lcname = "TIME VACATION PAID";
//        } else if (posi == 13) {
//            lcname = "TIME SHOP WORK NON BILLABLE";
//        }


        if (posi == 0) {
            lcname = "TIME TRAVELNONBILLABLE";
        } else if (posi == 1) {
            lcname = "TIME TRAVELBILLABLE";
        } else if (posi == 2) {
            lcname = "TIME FIELD SERVICE BILLABLE";
        } else if (posi == 3) {
            lcname = "TIME FIELD SERVICE NON BILLABLE";
        } else if (posi == 4) {
            lcname = "LUNCH";
        } else if (posi == 5) {
            lcname = "TIME SHOP WORK BILLABLE";
        }else if (posi == 6) {
            lcname = "TIME SHOP WORK NON BILLABLE";
        } else if (posi == 7) {
            lcname = "TIME OFFICE LABOR";
        }else if (posi == 8) {
            lcname = "TIME TECH BETTERMENT AND TRAINING";
        }




        return lcname;
    }

    public static String getLaborcodeNew(int posi) {
        String lcname = "";
        if (posi == 0) {
            lcname = "Office Staff Labor";
        } else if (posi == 1) {
            lcname = "Tech Time Non-Productive";
        } else if (posi == 2) {
            lcname = "Tech Betterment & Training";
        }
        return lcname;
    }

    public static void stopAlarm(Context context) {
        try {
            Intent myIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        } catch (Exception e){}

    }

    public static void stopStartAlarm(Context context) {
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    5 * 60 * 1000,
                    pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    5 * 60 * 1000,
                    pendingIntent);
        }
    }

    public static int CheckLeftMarginDay(String check) {

        int marginleft = 0;

        if(check.equalsIgnoreCase("12:00AM")) {
            marginleft = 0;
        } else if(check.equalsIgnoreCase("12:30AM")) {
            marginleft = 25;
        } else if(check.equalsIgnoreCase("01:00AM")) {
            marginleft = 50;
        } else if(check.equalsIgnoreCase("01:30AM")) {
            marginleft = 75;
        } else if(check.equalsIgnoreCase("02:00AM")) {
            marginleft = 100;
        } else if(check.equalsIgnoreCase("02:30AM")) {
            marginleft = 125;
        } else if(check.equalsIgnoreCase("03:00AM")) {
            marginleft = 150;
        } else if(check.equalsIgnoreCase("03:30AM")) {
            marginleft = 175;
        } else if(check.equalsIgnoreCase("04:00AM")) {
            marginleft = 200;
        } else if(check.equalsIgnoreCase("04:30AM")) {
            marginleft = 225;
        } else if(check.equalsIgnoreCase("05:00AM")) {
            marginleft = 250;
        } else if(check.equalsIgnoreCase("05:30AM")) {
            marginleft = 275;
        } else if(check.equalsIgnoreCase("06:00AM")) {
            marginleft = 300;
        } else if(check.equalsIgnoreCase("06:30AM")) {
            marginleft = 325;
        } else if(check.equalsIgnoreCase("07:00AM")) {
            marginleft = 350;
        } else if(check.equalsIgnoreCase("07:30AM")) {
            marginleft = 375;
        } else if(check.equalsIgnoreCase("08:00AM")) {
            marginleft = 400;
        } else if(check.equalsIgnoreCase("08:30AM")) {
            marginleft = 425;
        } else if(check.equalsIgnoreCase("09:00AM")) {
            marginleft = 450;
        } else if(check.equalsIgnoreCase("09:30AM")) {
            marginleft = 475;
        } else if(check.equalsIgnoreCase("10:00AM")) {
            marginleft = 500;
        } else if(check.equalsIgnoreCase("10:30AM")) {
            marginleft = 525;
        } else if(check.equalsIgnoreCase("11:00AM")) {
            marginleft = 550;
        } else if(check.equalsIgnoreCase("11:30AM")) {
            marginleft = 575;
        } else if(check.equalsIgnoreCase("12:00PM")) {
            marginleft = 600;
        } else if(check.equalsIgnoreCase("12:30AM")) {
            marginleft = 625;
        } else if(check.equalsIgnoreCase("01:00PM")) {
            marginleft = 650;
        } else if(check.equalsIgnoreCase("01:30AM")) {
            marginleft = 675;
        } else if(check.equalsIgnoreCase("02:00PM")) {
            marginleft = 700;
        } else if(check.equalsIgnoreCase("02:30AM")) {
            marginleft = 725;
        } else if(check.equalsIgnoreCase("03:00PM")) {
            marginleft = 750;
        } else if(check.equalsIgnoreCase("03:30AM")) {
            marginleft = 775;
        } else if(check.equalsIgnoreCase("04:00PM")) {
            marginleft = 800;
        } else if(check.equalsIgnoreCase("04:30AM")) {
            marginleft = 825;
        } else if(check.equalsIgnoreCase("05:00PM")) {
            marginleft = 850;
        } else if(check.equalsIgnoreCase("05:30AM")) {
            marginleft = 875;
        } else if(check.equalsIgnoreCase("06:00PM")) {
            marginleft = 900;
        } else if(check.equalsIgnoreCase("06:30AM")) {
            marginleft = 925;
        } else if(check.equalsIgnoreCase("07:00PM")) {
            marginleft = 950;
        } else if(check.equalsIgnoreCase("07:30AM")) {
            marginleft = 975;
        } else if(check.equalsIgnoreCase("08:00PM")) {
            marginleft = 1000;
        }
        return marginleft;
    }
}
