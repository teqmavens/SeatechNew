package com.vadevelopment.RedAppetite;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    TextView LeftsidecancelTexttoolbar, centertext, nextbutton, welcometext;
    RelativeLayout relativeapptoolbar;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private GoogleApiClient client;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 1000;
    public static double currentLatitude;
    public static double currentLongitude;
    private LocationRequest mLocationRequest;
    private View mLayout;
    Dialog dialog_loccheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        preferences = PreferenceManager.getDefaultSharedPreferences(com.vadevelopment.RedAppetite.MainActivity.this);
        editor = preferences.edit();
        mLayout = findViewById(R.id.myrootlayout);
        checkAllRequestPermission();
        relativeapptoolbar = (RelativeLayout) findViewById(R.id.cancelSkipLayout);
        relativeapptoolbar.setVisibility(View.VISIBLE);
        LeftsidecancelTexttoolbar = (TextView) findViewById(R.id.cancelText);
        LeftsidecancelTexttoolbar.setVisibility(View.GONE);
        centertext = (TextView) findViewById(R.id.centertext);
        centertext.setText("Welcome");
        nextbutton = (TextView) findViewById(R.id.skipText);
        welcometext = (TextView) findViewById(R.id.welcometext);
        nextbutton.setVisibility(View.VISIBLE);
        nextbutton.setText("Next");
        com.vadevelopment.RedAppetite.HandyObjects.SetOPENSANS_LIGHT(com.vadevelopment.RedAppetite.MainActivity.this, welcometext);

        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(com.vadevelopment.RedAppetite.MainActivity.this, SelectLoginRegister.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void settingApitest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setFastestInterval(0);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        client = AppController.getGoogleApiHelper().getGoogleApiClient();
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("dds", "All location settings are satisfied.");
                        if (ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            client = AppController.getGoogleApiHelper().getGoogleApiClient();
                            Location location = LocationServices.FusedLocationApi.getLastLocation(client);
                            if (location == null) {
                                //Toast.makeText(MainActivity.this, "nullocation", Toast.LENGTH_LONG).show();
                            } else {
                                currentLatitude = location.getLatitude();
                                currentLongitude = location.getLongitude();
                                editor.putString(Consts.CURRENT_LATITUDE, String.valueOf(currentLatitude));
                                editor.putString(Consts.CURRENT_LONGITUDE, String.valueOf(currentLongitude));
                                editor.commit();
                                //Toast.makeText(MainActivity.this, String.valueOf(currentLatitude), Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("sssss", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            status.startResolutionForResult(com.vadevelopment.RedAppetite.MainActivity.this, REQUEST_CHECK_SETTINGS);
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
                        if (ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            Location location = LocationServices.FusedLocationApi.getLastLocation(client);
                            if (location == null) {
                                // isGPS = false;
                                LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        currentLatitude = location.getLatitude();
                                        currentLongitude = location.getLongitude();
                                        editor.putString(Consts.CURRENT_LATITUDE, String.valueOf(currentLatitude));
                                        editor.putString(Consts.CURRENT_LONGITUDE, String.valueOf(currentLongitude));
                                        editor.commit();
                                        //   Toast.makeText(MainActivity.this, String.valueOf(currentLatitude), Toast.LENGTH_LONG).show();
                                        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
                                    }
                                });
                            } else {
                                currentLatitude = location.getLatitude();
                                currentLongitude = location.getLongitude();
                                editor.putString(Consts.CURRENT_LATITUDE, String.valueOf(currentLatitude));
                                editor.putString(Consts.CURRENT_LONGITUDE, String.valueOf(currentLongitude));
                                editor.commit();
                                // Toast.makeText(MainActivity.this, String.valueOf(currentLatitude), Toast.LENGTH_LONG).show();
                            }
                            editor.putString(Consts.SETTINGAPI_DIALOG, "done");
                            editor.commit();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        editor.putString(Consts.SETTINGAPI_DIALOG, "notdone");
                        editor.commit();
                        //   settingApitest();
                        //  HandyObjects.showAlert(MainActivity.this, "Location is required");
                        break;
                    default:
                        break;
                }
                break;
        }

    }

    private void checkAllRequestPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // locationmandatoryDialog();
                    AlertDialog.Builder builder = new AlertDialog.Builder(com.vadevelopment.RedAppetite.MainActivity.this);
                    builder.setMessage("Permission is needed")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityCompat.requestPermissions(com.vadevelopment.RedAppetite.MainActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                }
            }
           else {
                if (AppController.getGoogleApiHelper().isConnected()) {
                    settingApitest();
                } else {
                    Toast.makeText(com.vadevelopment.RedAppetite.MainActivity.this, "notconnected", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mLayout, R.string.permission_location_rationale,
                        Snackbar.LENGTH_SHORT).show();
                if (AppController.getGoogleApiHelper().isConnected()) {
                    settingApitest();
                } else {
                    Toast.makeText(com.vadevelopment.RedAppetite.MainActivity.this, String.valueOf("notconnected"), Toast.LENGTH_LONG).show();
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(com.vadevelopment.RedAppetite.MainActivity.this);
                    builder.setMessage("Permission is needed to access Location")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    ActivityCompat.requestPermissions(com.vadevelopment.RedAppetite.MainActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    // Toast.makeText(MainActivity.this, "Manually turn on camera permission", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putBoolean(Consts.WELCOME_SCREEN, true);
        editor.commit();
    }
}
