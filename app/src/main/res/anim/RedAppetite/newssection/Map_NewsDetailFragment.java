package com.vadevelopment.RedAppetite.newssection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

/**
 * Created by vibrantappz on 4/4/2017.
 */

public class Map_NewsDetailFragment extends Fragment implements View.OnClickListener {

    DashboardActivity mdashboard;
    private TextView normalview, sateliteview, hybridview;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_LOCATION = 1;
    private MapView mapView;
    private GoogleMap googleMap;
    private View mLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_mapsearch, container, false);
        initViews(view, savedInstanceState);
        return view;
    }

    private void initViews(View view, Bundle bundle) {
        checkAllRequestPermission();
        normalview = (TextView) view.findViewById(R.id.normalview);
        sateliteview = (TextView) view.findViewById(R.id.sateliteview);
        hybridview = (TextView) view.findViewById(R.id.hybridview);
        setToolbar(view, bundle);
        normalview.setOnClickListener(this);
        sateliteview.setOnClickListener(this);
        hybridview.setOnClickListener(this);
    }


    private void setToolbar(View view, Bundle bundle) {
       /* mdashboard.profileicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colortext));
        mdashboard.profiletext.setTextColor(new Color().parseColor("#541c1d"));
        mdashboard.moreicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colortext));
        mdashboard.moretext.setTextColor(new Color().parseColor("#541c1d"));
        mdashboard.searchicon.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.white));
        mdashboard.searchtext.setTextColor(new Color().parseColor("#ffffff"));
        mdashboard.newsfeedicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colortext));
        mdashboard.newstext.setTextColor(new Color().parseColor("#541c1d"));
        mdashboard.adverticeicon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colortext));
        mdashboard.adverticetext.setTextColor(new Color().parseColor("#541c1d"));*/
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText("Details");
        mdashboard.setting.setText("Back");
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        mdashboard.mapButton.setVisibility(View.VISIBLE);
        mdashboard.mapButton.setText("Close");

        mLayout = view.findViewById(R.id.sample_main_layout);
        mapView = (MapView) view.findViewById(R.id.map);

        mapView.onCreate(bundle);
        //mapView.getMapAsync(this);
        mapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(new NewsDetail_Fragment());
            }
        });

        mdashboard.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.normalview:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                normalview.setTextColor(Color.parseColor("#ffffff"));
                normalview.setBackgroundColor(Color.parseColor("#780505"));

                sateliteview.setTextColor(Color.parseColor("#960100"));
                sateliteview.setBackgroundColor(Color.parseColor("#e2d6d5"));
                hybridview.setTextColor(Color.parseColor("#960100"));
                hybridview.setBackgroundColor(Color.parseColor("#e2d6d5"));
                break;
            case R.id.sateliteview:
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                sateliteview.setTextColor(Color.parseColor("#ffffff"));
                sateliteview.setBackgroundColor(Color.parseColor("#780505"));

                normalview.setTextColor(Color.parseColor("#960100"));
                normalview.setBackgroundColor(Color.parseColor("#e2d6d5"));
                hybridview.setTextColor(Color.parseColor("#960100"));
                hybridview.setBackgroundColor(Color.parseColor("#e2d6d5"));
                break;
            case R.id.hybridview:
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                hybridview.setTextColor(Color.parseColor("#ffffff"));
                hybridview.setBackgroundColor(Color.parseColor("#780505"));

                normalview.setTextColor(Color.parseColor("#960100"));
                normalview.setBackgroundColor(Color.parseColor("#e2d6d5"));
                sateliteview.setTextColor(Color.parseColor("#960100"));
                sateliteview.setBackgroundColor(Color.parseColor("#e2d6d5"));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final LatLng ltlng = new LatLng(Double.parseDouble("30.7046"), Double.parseDouble("76.7179"));
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(false);
                }
                googleMap.addMarker(new MarkerOptions().position(ltlng).title("Marker Title").snippet("Marker Description").icon(BitmapDescriptorFactory.fromResource(R.drawable.clubicon)));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(ltlng).zoom(14).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    private void checkAllRequestPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(mLayout, R.string.permission_location_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat
                                        .requestPermissions(getActivity(), PERMISSIONS_LOCATION, REQUEST_LOCATION);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_LOCATION, REQUEST_LOCATION);
            }
        } else {
        }
    }
}
