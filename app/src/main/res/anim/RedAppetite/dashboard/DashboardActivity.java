package com.vadevelopment.RedAppetite.dashboard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;

/**
 * Created by Vibrant Android on 08-03-2017.
 */

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    public RelativeLayout searchrelativeLayout, textsearchimage, rl_headingwithcount;
    public LinearLayout searchdatatab, profiletab, moretab, newstab, advertisetab, lltool_top;
    public ImageView searchicon, profileicon, moreicon, newsfeedicon, adverticeicon, settingicon, mapicon;
    public TextView searchtext, profiletext, moretext, setting, centertextsearch, mapButton, newstext, search, adverticetext, text_headingcount, text_heading;
    public LinearLayout footer;
    private static final int REQUEST_CHECK_SETTINGS = 1000;
    public View btm_line;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private LocationRequest mLocationRequest;
    private GoogleApiClient client;
    public static double currentLatitude;
    public static double currentLongitude;
    boolean enabled, stopresume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_after_skip_main);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            stopresume = true;
            Intent ilogin = new Intent(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this, com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.class);
            startActivity(ilogin);
        }
        searchrelativeLayout = (RelativeLayout) findViewById(R.id.searchtoolbar);
        rl_headingwithcount = (RelativeLayout) findViewById(R.id.rl_headingwithcount);
        searchrelativeLayout.setVisibility(View.VISIBLE);
        setting = (TextView) findViewById(R.id.setting);
        textsearchimage = (RelativeLayout) findViewById(R.id.textsearchcenter);
        mapButton = (TextView) findViewById(R.id.mapButton);
        centertextsearch = (TextView) findViewById(R.id.centertextsearch);
        searchdatatab = (LinearLayout) findViewById(R.id.searchdata);
        profiletab = (LinearLayout) findViewById(R.id.profiletab);
        moretab = (LinearLayout) findViewById(R.id.moretab);
        newstab = (LinearLayout) findViewById(R.id.newstab);
        lltool_top = (LinearLayout) findViewById(R.id.lltool_top);
        advertisetab = (LinearLayout) findViewById(R.id.advertisetab);
        profileicon = (ImageView) findViewById(R.id.profileicon);
        profiletext = (TextView) findViewById(R.id.profiletext);
        searchicon = (ImageView) findViewById(R.id.searchicon);
        searchtext = (TextView) findViewById(R.id.searchtext);
        moreicon = (ImageView) findViewById(R.id.moreicon);
        moretext = (TextView) findViewById(R.id.moretext);
        newsfeedicon = (ImageView) findViewById(R.id.newsfeedicon);
        adverticeicon = (ImageView) findViewById(R.id.adverticeicon);
        newstext = (TextView) findViewById(R.id.newstext);
        search = (TextView) findViewById(R.id.search);
        adverticetext = (TextView) findViewById(R.id.adverticetext);
        text_heading = (TextView) findViewById(R.id.text_heading);
        text_headingcount = (TextView) findViewById(R.id.text_headingcount);
        footer = (LinearLayout) findViewById(R.id.footer);
        settingicon = (ImageView) findViewById(R.id.settingicon);
        mapicon = (ImageView) findViewById(R.id.mapicon);
        btm_line = (View) findViewById(R.id.btm_line);
        preferences = PreferenceManager.getDefaultSharedPreferences(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this);
        editor = preferences.edit();

        searchdatatab.setOnClickListener(this);
        profiletab.setOnClickListener(this);
        moretab.setOnClickListener(this);
        newstab.setOnClickListener(this);
        advertisetab.setOnClickListener(this);

        if (getIntent().getExtras().getString("fromskip").equalsIgnoreCase("nop_ftime")) {
            com.vadevelopment.RedAppetite.profilesection.ProfileFragment pf = new com.vadevelopment.RedAppetite.profilesection.ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString("fromskip", "nop");
            pf.setArguments(bundle);
            displayWithoutViewFragmentWithoutv4backstack(pf);
        } /*else if (getIntent().getExtras().getString("fromskip").equalsIgnoreCase("for_fvrt") || getIntent().getExtras().getString("fromskip").equalsIgnoreCase("for_review")) {
            finish();
        }*/ else {
            displayWithoutViewFragmentWithoutv4backstack(new com.vadevelopment.RedAppetite.searchsection.SearchFirstFragment());
        }
    }

    public void displayWithoutViewFragment(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame1, mFragment)
                .addToBackStack(null)
                .commit();
    }

    public void displayWithoutViewFragmentWithoutv4(android.app.Fragment mFragment) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame1, mFragment)
                .addToBackStack(null)
                .commit();
    }

    public void adddisplayWithoutViewFragmentWithoutv4(android.app.Fragment mFragment) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.frame1, mFragment)
                .addToBackStack(null)
                .commit();
    }

    public void displayWithoutViewFragmentWithoutv4backstack(android.app.Fragment mFragment) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame1, mFragment)
                .commit();
    }

    public void addWithoutViewFragment(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.frame1, mFragment)
                .addToBackStack(null)
                .commit();
    }

    public void flipViewFragmentWithanim(android.app.Fragment mFragment) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.animator.card_flip_right_in,
                R.animator.card_flip_right_out,
                R.animator.card_flip_left_in,
                R.animator.card_flip_left_out)
                .replace(R.id.frame1, mFragment)
                .addToBackStack(null)
                .commit();
    }

    public void flipViewFragmentWithanimWithoutBack(android.app.Fragment mFragment) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.animator.card_flip_right_in,
                R.animator.card_flip_right_out,
                R.animator.card_flip_left_in,
                R.animator.card_flip_left_out)
                .replace(R.id.frame1, mFragment)
                .commit();
    }

    public void displayWithoutViewFragmentWithanim(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.frame1, mFragment)
                .addToBackStack(null)
                .commit();
    }

    public void displayWithoutViewFragmentWithanimWithoutv4(android.app.Fragment mFragment) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.pop_in_left, R.animator.pop_out_right)
                .replace(R.id.frame1, mFragment)
                .addToBackStack(null)
                .commit();
    }

    public void displayWithoutViewFragmentWithanimwithoutback(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                // fragmentManager.beginTransaction().setCustomAnimations(R.anim.left_to_right,R.anim.right_to_left,R.anim.left_to_right,R.anim.right_to_left)
                .replace(R.id.frame1, mFragment)
                .commit();
    }

    public void displayWithoutViewFragmentWithanimwithoutbackReverse(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.exit, R.anim.enter, 0, 0)
                // fragmentManager.beginTransaction().setCustomAnimations(R.anim.left_to_right,R.anim.right_to_left,R.anim.left_to_right,R.anim.right_to_left)
                .replace(R.id.frame1, mFragment)
                .commit();
    }


    public void displayWithoutViewFragmentWithoutBackstack(Fragment mFragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame1, mFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.searchdata:
                if (!enabled) {
                    Intent ilogin = new Intent(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this, com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.class);
                    startActivity(ilogin);
                }
                displayWithoutViewFragmentWithoutv4(new com.vadevelopment.RedAppetite.searchsection.SearchFirstFragment());
                break;
            case R.id.profiletab:
                try {
                    if (preferences.getBoolean("login", false)) {
                        com.vadevelopment.RedAppetite.profilesection.ProfileFragment pf = new com.vadevelopment.RedAppetite.profilesection.ProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("fromskip", "nop");
                        pf.setArguments(bundle);
                        displayWithoutViewFragmentWithoutv4(pf);
                    } else {
                        if (getIntent().getExtras().getString("fromskip").equalsIgnoreCase("yup")) {
                            Intent ilogin = new Intent(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this, com.vadevelopment.RedAppetite.login.ContainerActivity.class);
                            ilogin.putExtra("Login", true);
                            ilogin.putExtra("fromdashboard", "yup");
                            ilogin.putExtra("forwhich", "normal");
                            startActivity(ilogin);
                        } else if (getIntent().getExtras().getString("fromskip").equalsIgnoreCase("nop") ||
                                getIntent().getExtras().getString("fromskip").equalsIgnoreCase("nop_ftime") ||
                                getIntent().getExtras().getString("fromskip").equalsIgnoreCase("loc") ||
                                getIntent().getExtras().getString("fromskip").equalsIgnoreCase("for_fvrt") || getIntent().getExtras().getString("fromskip").equalsIgnoreCase("for_review")) {
                            com.vadevelopment.RedAppetite.profilesection.ProfileFragment pf = new com.vadevelopment.RedAppetite.profilesection.ProfileFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("fromskip", "nop");
                            pf.setArguments(bundle);
                            displayWithoutViewFragmentWithoutv4(pf);
                        }
                    }

                } catch (Exception e) {
                }

                break;
            case R.id.moretab:
                displayWithoutViewFragmentWithoutv4(new com.vadevelopment.RedAppetite.moresection.MoreFirstFragment());
                break;
            case R.id.newstab:
                if (!enabled) {
                    Intent ilogin = new Intent(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this, com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.class);
                    startActivity(ilogin);
                }
                displayWithoutViewFragmentWithoutv4(new com.vadevelopment.RedAppetite.newssection.NewsFragment());
                break;
            case R.id.advertisetab:
                /*AdvertisementFragment af = new AdvertisementFragment();
                Bundle bundle = new Bundle();
                bundle.putString("fromdashboard", "yup");
                af.setArguments(bundle);
                displayWithoutViewFragmentWithoutv4(af);*/
               // adddisplayWithoutViewFragmentWithoutv4(af);
                Intent ilogin = new Intent(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this, com.vadevelopment.RedAppetite.dashboard.AdvertiseWithUs.class);
                startActivity(ilogin);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Activonresume", "onresume");
        if (stopresume == false) {
            // HandyObjects.showAlert(DashboardActivity.this,"settingAPIIIIIIIIIIIII");
            settingApitest();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Activonstart", "onstart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopresume = false;
        Log.e("ActivonPause", "onPause");
    }

    private void settingApitest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setFastestInterval(0);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        client = com.vadevelopment.RedAppetite.AppController.getGoogleApiHelper().getGoogleApiClient();
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("dds", "All location settings are satisfied.");
                        if (ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            client = com.vadevelopment.RedAppetite.AppController.getGoogleApiHelper().getGoogleApiClient();
                            Location location = LocationServices.FusedLocationApi.getLastLocation(client);
                            if (location == null) {
                                // Toast.makeText(LocationServiceDisabled.this, "nullocation", Toast.LENGTH_LONG).show();
                            } else {
                                currentLatitude = location.getLatitude();
                                currentLongitude = location.getLongitude();
                                editor.putString(Consts.CURRENT_LATITUDE, String.valueOf(currentLatitude));
                                editor.putString(Consts.CURRENT_LONGITUDE, String.valueOf(currentLongitude));
                                editor.commit();
                                // Toast.makeText(DashboardActivity.this, "updatelatlong", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("sssss", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            status.startResolutionForResult(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("vdd", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("dcvsdfs", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            Location location = LocationServices.FusedLocationApi.getLastLocation(client);
                            if (location == null) {
                                LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        currentLatitude = location.getLatitude();
                                        currentLongitude = location.getLongitude();
                                        editor.putString(Consts.CURRENT_LATITUDE, String.valueOf(currentLatitude));
                                        editor.putString(Consts.CURRENT_LONGITUDE, String.valueOf(currentLongitude));
                                        editor.commit();
                                        //  Toast.makeText(DashboardActivity.this, "updatelatlong", Toast.LENGTH_LONG).show();
                                        // Toast.makeText(LocationServiceDisabled.this, String.valueOf(currentLatitude), Toast.LENGTH_LONG).show();
                                        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
                                    }
                                });
                            } else {
                                currentLatitude = location.getLatitude();
                                currentLongitude = location.getLongitude();
                                editor.putString(Consts.CURRENT_LATITUDE, String.valueOf(currentLatitude));
                                editor.putString(Consts.CURRENT_LONGITUDE, String.valueOf(currentLongitude));
                                editor.commit();
                                // Toast.makeText(DashboardActivity.this, "updatelatlong", Toast.LENGTH_LONG).show();
                                //  Toast.makeText(LocationServiceDisabled.this, String.valueOf(currentLatitude), Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        editor.putString(Consts.SETTINGAPI_DIALOG, "notdone");
                        editor.commit();
                        settingApitest();
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(com.vadevelopment.RedAppetite.dashboard.DashboardActivity.this, "Location is required");
                        break;
                    default:
                        break;
                }
                break;
        }
    }
}
