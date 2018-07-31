package teq.development.seatech;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.Calendar;

import io.fabric.sdk.android.Fabric;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.OfflineSync.DemoJobCreator;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;

public class App extends MultiDexApplication {

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    public static App appInstance;
    public static Calendar calendar;

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
                Log.e("OnActivityDestroyed", "onActivityDestroyed");
               /* if (DashBoardActivity.onbackppress == true) {
                    DashBoardActivity.onbackppress = false;
                    HandyObject.putPrams(appInstance, AppConstants.ISJOB_RUNNING, "yes");
                } else {
                    HandyObject.putPrams(appInstance, AppConstants.ISJOB_RUNNING, "no");
                }*/

            }
        });
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public Runnable updateTimerThread = new Runnable() {

        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hrs = mins / 60;
            secs = secs % 60;
            mins = mins % 60;

            // binding.uploadimage.setText(String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            Log.e("TIME", String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            Intent intent = new Intent("load_message");
            intent.putExtra("secss", String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            LocalBroadcastManager.getInstance(appInstance).sendBroadcast(intent);
            customHandler.postDelayed(this, 1000);
        }
    };

    public void stopTimer() {
        timeSwapBuff = 0L;
        customHandler.removeCallbacks(updateTimerThread);
    }

    public void pauseTimer() {
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);

    }

}
