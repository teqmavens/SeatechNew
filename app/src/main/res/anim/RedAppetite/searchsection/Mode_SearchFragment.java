package com.vadevelopment.RedAppetite.searchsection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by vibrantappz on 3/22/2017.
 */

public class Mode_SearchFragment extends android.app.Fragment {

    private static String TAG = "Mode_SearchFragment";
    DashboardActivity mdashboard;
    private RelativeLayout rl_mode, rl_address, rl_currentposi;
    private CheckBox checkbox_myaddress, checkbox_curntposi;
    private LinearLayout ll_main;
    private EditText et_searchaddr;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private SQLiteDatabase database;
    private SharedPreferences preferences;
    private Cursor cursor;
    public static String searchedlatlong, searched_address = "";
    private String which_latlng, which_latlngaddrs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_modesearch, container, false);
        rl_currentposi = (RelativeLayout) view.findViewById(R.id.rl_currentposi);
        rl_address = (RelativeLayout) view.findViewById(R.id.rl_address);
        rl_mode = (RelativeLayout) view.findViewById(R.id.rl_mode);
        et_searchaddr = (EditText) view.findViewById(R.id.et_searchaddr);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        checkbox_myaddress = (CheckBox) view.findViewById(R.id.checkbox_myaddress);
        checkbox_curntposi = (CheckBox) view.findViewById(R.id.checkbox_curntposi);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mdashboard.footer.setVisibility(View.GONE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText("Mode");
        mdashboard.setting.setText("Back");
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        mdashboard.mapButton.setVisibility(View.GONE);
        rl_currentposi.setBackgroundColor(Color.parseColor("#5a0807"));
        rl_address.setBackgroundColor(Color.parseColor("#780505"));
        et_searchaddr.setVisibility(View.GONE);

        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkbox_curntposi.isChecked()) {
                    searchedlatlong = "Current Position";
                    getActivity().getFragmentManager().popBackStack();
                } else if (checkbox_myaddress.isChecked()) {
                    if (et_searchaddr.getText().toString().length() == 0) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(getActivity(), getString(R.string.addressreq));
                    } else {
                        getActivity().getFragmentManager().popBackStack();
                    }

                }
            }
        });

        checkbox_myaddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    rl_address.setBackground(getResources().getDrawable(R.drawable.adresss_fld));
                    rl_currentposi.setBackgroundColor(Color.parseColor("#780505"));
                    et_searchaddr.setVisibility(View.VISIBLE);
                    checkbox_curntposi.setChecked(false);
                    checkbox_myaddress.setChecked(true);
                } else {
                    if (!checkbox_curntposi.isChecked()) {
                        checkbox_myaddress.setChecked(true);
                    }
                }
            }
        });

        checkbox_curntposi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    rl_currentposi.setBackgroundColor(Color.parseColor("#5a0807"));
                    rl_address.setBackgroundColor(Color.parseColor("#780505"));
                    et_searchaddr.setVisibility(View.GONE);
                    checkbox_curntposi.setChecked(true);
                    checkbox_myaddress.setChecked(false);
                } else {
                    if (!checkbox_myaddress.isChecked()) {
                        checkbox_curntposi.setChecked(true);
                    }
                    //   HandyObjects.showAlert(getActivity(), "alreadycheed");
                }

            }
        });

        et_searchaddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException me) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setData_Fromdatabase();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //googleMap.
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.e(TAG, "Place: " + place.getName());
                final LatLng ltlng = place.getLatLng();
                et_searchaddr.setText(place.getAddress().toString());
                searched_address = place.getAddress().toString();
                searchedlatlong = String.valueOf(ltlng.latitude + "," + ltlng.longitude);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.e(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.e(TAG, "Cancel");
            }
        }
    }

    private void setData_Fromdatabase() {

        if (getArguments() != null && getArguments().getString(Consts.TOMODE_FROM).equalsIgnoreCase("search")) {
            cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, null, null, null, null, null, null);
        } else {
            cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, null, null, null, null, null, null);
        }
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    if (getArguments() != null && getArguments().getString(Consts.TOMODE_FROM).equalsIgnoreCase("search")) {
                        which_latlng = cursor.getString(cursor.getColumnIndex("search_whichlatlong"));
                        which_latlngaddrs = cursor.getString(cursor.getColumnIndex("search_whichlatlongaddrs"));
                    } else {
                        which_latlng = cursor.getString(cursor.getColumnIndex("event_whichlatlong"));
                        which_latlngaddrs = cursor.getString(cursor.getColumnIndex("event_whichlatlongaddrs"));
                    }
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }

        if (which_latlngaddrs.equalsIgnoreCase("current")) {
            checkbox_curntposi.setChecked(true);
            checkbox_myaddress.setChecked(false);
            et_searchaddr.setVisibility(View.GONE);
        } else {
            checkbox_curntposi.setChecked(false);
            checkbox_myaddress.setChecked(true);
            et_searchaddr.setVisibility(View.VISIBLE);
            et_searchaddr.setText(which_latlngaddrs);
        }
    }
}


