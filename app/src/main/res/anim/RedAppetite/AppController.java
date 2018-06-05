package com.vadevelopment.RedAppetite;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by online computers on 4/19/2016.
 */
public class AppController extends MultiDexApplication {
    public static final String TAG = com.vadevelopment.RedAppetite.AppController.class
            .getSimpleName();
    private RequestQueue mRequestQueue;
    private static com.vadevelopment.RedAppetite.AppController mInstance;
    private com.vadevelopment.RedAppetite.GoogleApiHelper googleApiHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        googleApiHelper = new com.vadevelopment.RedAppetite.GoogleApiHelper(mInstance);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized com.vadevelopment.RedAppetite.AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public com.vadevelopment.RedAppetite.GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }

    public static com.vadevelopment.RedAppetite.GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }
}
