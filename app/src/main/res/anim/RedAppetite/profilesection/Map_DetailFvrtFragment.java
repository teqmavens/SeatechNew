package com.vadevelopment.RedAppetite.profilesection;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;
import com.vadevelopment.RedAppetite.searchsection.MyItem;
import com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by vibrantappz on 4/4/2017.
 */

public class Map_DetailFvrtFragment extends android.app.Fragment implements View.OnClickListener, ClusterManager.OnClusterItemClickListener<MyItem> {

    DashboardActivity mdashboard;
    private TextView normalview, sateliteview, hybridview;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_LOCATION = 1;
    private MapView mapView;
    private GoogleMap googleMap;
    private ClusterManager mClusterManager;
    private MyItem clickedClusterdeItem;
    private ArrayList<Skeleton_SearchFirst> arraylist_custom;
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
        if (getArguments() != null) {
            arraylist_custom = getArguments().getParcelableArrayList("detail_arraylist");
        }

    }


    private void setToolbar(View view, Bundle bundle) {
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText("Details");
        mdashboard.setting.setText("Back");
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        mdashboard.mapButton.setVisibility(View.VISIBLE);
        mdashboard.mapicon.setVisibility(View.GONE);
        //   mdashboard.mapicon.setImageResource(R.drawable.crossmap);
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
                if (getArguments() != null && getArguments().getString(Consts.ONDETAIL_FROM).equalsIgnoreCase("fvrt")) {
                    getActivity().getFragmentManager().popBackStack();
                    //   mdashboard.displayWithoutViewFragmentWithoutv4backstack(new FavoritesProfile_Fragment());
                } else {
                    getActivity().getFragmentManager().popBackStack();
                    //   mdashboard.displayWithoutViewFragmentWithoutv4backstack(new ReviewProfile_Fragment());
                }

            }
        });

        mdashboard.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
                /*if (getArguments() != null && getArguments().getString(Consts.ONDETAIL_FROM).equalsIgnoreCase("fvrt")) {
                    if (arraylist_custom.size() > 0) {
                        DetailDltFrmFvrt_EdtRvw_Fragment map_sdetail = new DetailDltFrmFvrt_EdtRvw_Fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Consts.PROFILE_CLICKLID, getArguments().getString(Consts.PROFILE_CLICKLID));
                        bundle.putString(Consts.CHECKCLICK_FROM, "fvrt");
                        map_sdetail.setArguments(bundle);
                        // mdashboard.displayWithoutViewFragment(map_sdetail);
                        mdashboard.displayWithoutViewFragmentWithoutBackstack(map_sdetail);
                    }
                } else {
                    if (arraylist_custom.size() > 0) {
                        DetailDltFrmFvrt_EdtRvw_Fragment map_sdetail = new DetailDltFrmFvrt_EdtRvw_Fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Consts.PROFILE_CLICKLID, getArguments().getString(Consts.PROFILE_CLICKLID));
                        bundle.putString(Consts.CHECKCLICK_FROM, "review");
                        map_sdetail.setArguments(bundle);
                        // mdashboard.displayWithoutViewFragment(map_sdetail);
                        mdashboard.displayWithoutViewFragmentWithoutBackstack(map_sdetail);
                    }
                }*/
                // mdashboard.displayWithoutViewFragmentWithoutBackstack(new DetailDltFrmFvrt_EdtRvw_Fragment());
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
        final LatLng ltlng = new LatLng(Double.parseDouble(arraylist_custom.get(0).getLatitude()), Double.parseDouble(arraylist_custom.get(0).getLongitude()));
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(false);
                }
                //   googleMap.addMarker(new MarkerOptions().position(ltlng).title("Marker Title").snippet("Marker Description").icon(BitmapDescriptorFactory.fromResource(R.drawable.clubicon)));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(ltlng).zoom(14).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mClusterManager = new ClusterManager<>(getActivity(), googleMap);
                googleMap.setOnCameraIdleListener(mClusterManager);
                googleMap.setOnMarkerClickListener(mClusterManager);
                googleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
                googleMap.setOnInfoWindowClickListener(mClusterManager);

                mClusterManager.setOnClusterItemClickListener(com.vadevelopment.RedAppetite.profilesection.Map_DetailFvrtFragment.this);
                mClusterManager.setRenderer(new MyClusterRenderer(getActivity(), googleMap, mClusterManager));
                addItems(arraylist_custom);
                mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForItems());
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

    private void addItems(ArrayList<Skeleton_SearchFirst> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            MyItem offsetitem = new MyItem(arrayList.get(i).getVisited_date(), arrayList.get(i).getIsreviewed(), arrayList.get(i).getIsfav(), arrayList.get(i).getLid(), Double.parseDouble(arrayList.get(i).getLatitude()), Double.parseDouble(arrayList.get(i).getLongitude()), arrayList.get(i).getBuildingname(), arrayList.get(i).getAddress(),
                    arrayList.get(i).getAvgrating(), arrayList.get(i).getDistance(),
                    arrayList.get(i).getHappy(), arrayList.get(i).getSad(), arrayList.get(i).getTotal(), arrayList.get(i).getType(), arrayList.get(i).getFreeimage(), arrayList.get(i).getPremiumimage());
            mClusterManager.addItem(offsetitem);
        }
    }

    @Override
    public boolean onClusterItemClick(MyItem myItem) {
        clickedClusterdeItem = myItem;
        return false;
    }

    public class MyClusterRenderer extends DefaultClusterRenderer<MyItem> {

        public MyClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);
            // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.clubicon));
        }

        @Override
        protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
            loadMarkerIcon(marker, clusterItem);
        }
    }

    private void loadMarkerIcon(final Marker markerOptions, MyItem item) {
        String Imageurl;
        if (item.getType().equalsIgnoreCase("Premium")) {
            Imageurl = item.getPremiumimage();
        } else {
            Imageurl = item.getFreeimage();
        }
        Glide.with(this).load(Imageurl)
                .asBitmap().fitCenter().override(160, 160).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                markerOptions.setIcon(icon);
                //  markerOptions.setIcon(icon);
            }
        });
    }

    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyCustomAdapterForItems() {
            myContentsView = getActivity().getLayoutInflater().inflate(R.layout.searchinfo_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            TextView tvTitle = ((TextView) myContentsView
                    .findViewById(R.id.name));
            TextView tvSnippet = ((TextView) myContentsView
                    .findViewById(R.id.address));
            TextView distance = ((TextView) myContentsView
                    .findViewById(R.id.distance));
            TextView users = ((TextView) myContentsView
                    .findViewById(R.id.users));
            TextView likes = ((TextView) myContentsView
                    .findViewById(R.id.likes));
            TextView dislikes = ((TextView) myContentsView
                    .findViewById(R.id.dislikes));
            TextView ratingtext = ((TextView) myContentsView
                    .findViewById(R.id.ratingtext));
            RatingBar ratingBar = ((RatingBar) myContentsView
                    .findViewById(R.id.ratingBar));
            ImageView image_isreviewd = ((ImageView) myContentsView.findViewById(R.id.image_isreviewd));
            ImageView image_isfav = ((ImageView) myContentsView.findViewById(R.id.image_isfav));
            ImageView iconarrow = ((ImageView) myContentsView.findViewById(R.id.iconarrow));
            iconarrow.setVisibility(View.INVISIBLE);
            if (Integer.parseInt(clickedClusterdeItem.getIsreviewd()) > 0) {
                image_isreviewd.setVisibility(View.VISIBLE);
            }
            if (Integer.parseInt(clickedClusterdeItem.getIsfvrt()) > 0) {
                image_isfav.setVisibility(View.VISIBLE);
            }

            tvTitle.setText(clickedClusterdeItem.getB_name());
            tvSnippet.setText(clickedClusterdeItem.getB_address().split(",")[0]);
            distance.setText(clickedClusterdeItem.getDistance());
            users.setText(clickedClusterdeItem.getTotal());
            likes.setText(clickedClusterdeItem.getLike());
            dislikes.setText(clickedClusterdeItem.getDislike());
            ratingBar.setRating(Float.parseFloat(clickedClusterdeItem.getAvg_rating()));
            double ii = Double.parseDouble(clickedClusterdeItem.getAvg_rating());
            ratingtext.setText(String.valueOf(new DecimalFormat("##.#").format(ii)));
            //  ratingtext.setText(clickedClusterdeItem.getAvg_rating());
            return myContentsView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}


