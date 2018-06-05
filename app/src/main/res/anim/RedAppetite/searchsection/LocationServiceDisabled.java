package com.vadevelopment.RedAppetite.searchsection;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.HandyObjects;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

/**
 * Created by vibrantappz on 9/23/2017.
 */

public class LocationServiceDisabled extends AppCompatActivity {

    Button button;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 1000;
    public static double currentLatitude;
    public static double currentLongitude;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private LocationRequest mLocationRequest;
    private GoogleApiClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationservice_disabled);
        button = (Button) findViewById(R.id.button);
        preferences = PreferenceManager.getDefaultSharedPreferences(com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.this);
        editor = preferences.edit();
        HandyObjects.SetButton_TEXGYREHEROES_BOLD(com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.this, button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAllRequestPermission();
            }
        });
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.this);
                    builder.setMessage("Permission is needed")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityCompat.requestPermissions(com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                }
            } else {
                if (AppController.getGoogleApiHelper().isConnected()) {
                    settingApitest();
                } else {
                    //Toast.makeText(LocationServiceDisabled.this, "notconnected", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
        }
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
                        if (ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            client = AppController.getGoogleApiHelper().getGoogleApiClient();
                            Location location = LocationServices.FusedLocationApi.getLastLocation(client);
                            if (location == null) {
                                // Toast.makeText(LocationServiceDisabled.this, "nullocation", Toast.LENGTH_LONG).show();
                            } else {
                                currentLatitude = location.getLatitude();
                                currentLongitude = location.getLongitude();
                                editor.putString(Consts.CURRENT_LATITUDE, String.valueOf(currentLatitude));
                                editor.putString(Consts.CURRENT_LONGITUDE, String.valueOf(currentLongitude));
                                editor.commit();
                                // Toast.makeText(LocationServiceDisabled.this, String.valueOf(currentLatitude), Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("sssss", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            status.startResolutionForResult(com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.this, REQUEST_CHECK_SETTINGS);
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
                        if (ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                                        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
                                    }
                                });
                            } else {
                                currentLatitude = location.getLatitude();
                                currentLongitude = location.getLongitude();
                                editor.putString(Consts.CURRENT_LATITUDE, String.valueOf(currentLatitude));
                                editor.putString(Consts.CURRENT_LONGITUDE, String.valueOf(currentLongitude));
                                editor.commit();
                                //  Toast.makeText(LocationServiceDisabled.this, String.valueOf(currentLatitude), Toast.LENGTH_LONG).show();
                            }
                            editor.putString(Consts.SETTINGAPI_DIALOG, "done");
                            editor.commit();
                            Intent i = new Intent(com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.this, DashboardActivity.class);
                            i.putExtra("fromskip", "loc");
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        editor.putString(Consts.SETTINGAPI_DIALOG, "notdone");
                        editor.commit();
                        settingApitest();
                        HandyObjects.showAlert(com.vadevelopment.RedAppetite.searchsection.LocationServiceDisabled.this, "Location is required");
                        break;
                    default:
                        break;
                }
                break;
        }
    }
}
