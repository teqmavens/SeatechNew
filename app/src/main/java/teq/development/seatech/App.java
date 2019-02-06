package teq.development.seatech;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.facebook.drawee.backends.pipeline.Fresco;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.fabric.sdk.android.Fabric;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.OfflineSync.DemoJobCreator;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.ConnectivityReceiver;
import teq.development.seatech.Utils.HandyObject;

public class App extends MultiDexApplication {

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    public static App appInstance;
    public static Calendar calendar;
    public static boolean timer_running,timer_pause;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        calendar = Calendar.getInstance();
        Fresco.initialize(this);
        Fabric.with(getApplicationContext(), new Crashlytics());
        JobManager.create(this).addJobCreator(new DemoJobCreator());
        HandyObject.putPrams(appInstance, AppConstants.JOBRUNNING_TOTALTIME, "0");
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        appInstance.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.e("OnActivityCreated", "onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.e("OnActivityStarted", "onActivityStarted");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.e("OActivityResumed", "onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.e("OnActivityPaused", "onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.e("OnActivityStopped", "onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.e("OnMAINNDestroyed", "OnMAINNDestroyed");
               /* if (DashBoardActivity.onbackppress == true) {
                    DashBoardActivity.onbackppress = false;
                    HandyObject.putPrams(appInstance, AppConstants.ISJOB_RUNNING, "yes");
                } else {
                    HandyObject.putPrams(appInstance, AppConstants.ISJOB_RUNNING, "no");
                }*/

            }
        });
    }

    public static synchronized App getInstance() {
        return appInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void startTimer() {
      //  startTime = SystemClock.uptimeMillis();
        if(timer_pause == true) {
            timer_pause = false;
        } else {
            if (HandyObject.getPrams(this, AppConstants.ISJOB_RUNNINGCLOSE).equalsIgnoreCase("yes")) {

                long diff = new Date().getTime() - Long.parseLong(HandyObject.getPrams(this, AppConstants.JOBRUNNING_CLOSETIMEDATE));
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                Log.e("LASTCLOSEDATE",String.valueOf(minutes) +"-----"+ String.valueOf(seconds));
                Log.e("LASTCLOSEDATEDIFFF",String.valueOf(diff));
                Log.e("COMPLETED TIME",HandyObject.getPrams(this, AppConstants.JOBRUNNING_CLOSETIME));
                    String myTime = HandyObject.getPrams(this, AppConstants.JOBRUNNING_CLOSETIME);
                    Long hg = Long.parseLong(myTime.split(":")[0]) * 3600000 + Long.parseLong(myTime.split(":")[1]) * 60000 + Long.parseLong(myTime.split(":")[2]) * 1000;
                    long secondsnew = hg / 1000;
                    long minutesnew = secondsnew / 60;
                    long hrsnew = minutesnew / 60;
                    secondsnew = secondsnew % 60;
                    minutesnew = minutesnew % 60;
                Log.e("NEW TIMEEEEEEE",String.valueOf(hrsnew) +"-----"+String.valueOf(minutesnew) +"-----"+ String.valueOf(secondsnew));
                timeSwapBuff = diff+hg;
            } else {
                timeSwapBuff = 0L;
            }
        }

        startTime = SystemClock.elapsedRealtime();
        customHandler.postDelayed(updateTimerThread, 0);
        timer_running = true;
    }

    public Runnable updateTimerThread = new Runnable() {

        public void run() {
            timeInMilliseconds = SystemClock.elapsedRealtime() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hrs = mins / 60;
            secs = secs % 60;
            mins = mins % 60;

            // binding.uploadimage.setText(String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
          //  Log.e("MILLLLLLIITIME", String.valueOf(timeInMilliseconds));
            Log.e("TIME", String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            Intent intent = new Intent("load_message");
            intent.putExtra("secss", String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            LocalBroadcastManager.getInstance(appInstance).sendBroadcast(intent);
            customHandler.postDelayed(this, 1000);
            //257211
        }
    };

    public void stopTimer() {
        timer_running = false;
      //  timeSwapBuff = 0L;
        if (HandyObject.getPrams(this, AppConstants.ISJOB_RUNNINGCLOSE).equalsIgnoreCase("yes")) {
          //  timeSwapBuff = 125877L;
            long diff = new Date().getTime() - Long.parseLong(HandyObject.getPrams(this, AppConstants.JOBRUNNING_CLOSETIMEDATE));
            String myTime = HandyObject.getPrams(this, AppConstants.JOBRUNNING_CLOSETIME);
            Long hg = Long.parseLong(myTime.split(":")[0]) * 3600000 + Long.parseLong(myTime.split(":")[1]) * 60000 + Long.parseLong(myTime.split(":")[2]) * 1000;
            long secondsnew = hg / 1000;
            long minutesnew = secondsnew / 60;
            long hrsnew = minutesnew / 60;
            secondsnew = secondsnew % 60;
            minutesnew = minutesnew % 60;
            Log.e("NEW TIMEEEEEEE",String.valueOf(hrsnew) +"-----"+String.valueOf(minutesnew) +"-----"+ String.valueOf(secondsnew));
            timeSwapBuff = diff+hg;
        } else {
            timeSwapBuff = 0L;
        }
        customHandler.removeCallbacks(updateTimerThread);
    }

    public void pauseTimer() {
        timer_pause = true;
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }

    public Socket mSocket;
    {
        try {
            mSocket = IO.socket(AppConstants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
