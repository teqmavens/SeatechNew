package com.vadevelopment.RedAppetite.profilesection;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
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
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.MainActivity;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;
import com.vadevelopment.RedAppetite.searchsection.MyItem;
import com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vibrantappz on 4/4/2017.
 */

public class Map_FvrtFragment extends android.app.Fragment implements View.OnClickListener, ClusterManager.OnClusterItemClickListener<MyItem> {

    DashboardActivity mdashboard;
    private static String TAG = "Map_FvrtFragment";
    private TextView normalview, sateliteview, hybridview;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_LOCATION = 1;
    private MapView mapView;
    private GoogleMap googleMap;
    private SQLiteDatabase database;
    private SharedPreferences preferences;
    private ArrayList<Skeleton_SearchFirst> arrayList;
    private SharedPreferences.Editor editor;
    private String categoriesid, stars, type, reviews, sortby, sortbyValue;
    public static int start = 0;
    private View mLayout;
    private Context context;
    private ClusterManager mClusterManager;
    private MyItem clickedClusterdeItem;
    public static boolean from_mapfvrt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_mapsearch, container, false);
        initViews(view, savedInstanceState);
        return view;
    }

    private void initViews(final View view, Bundle bundle) {
        checkAllRequestPermission();
        normalview = (TextView) view.findViewById(R.id.normalview);
        sateliteview = (TextView) view.findViewById(R.id.sateliteview);
        hybridview = (TextView) view.findViewById(R.id.hybridview);
        setToolbar(view, bundle);
        normalview.setOnClickListener(this);
        sateliteview.setOnClickListener(this);
        hybridview.setOnClickListener(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setData_FromDatabase(view);
            }
        });
    }

    private void setData_FromDatabase(View view) {
        com.vadevelopment.RedAppetite.profilesection.FvrtSrchFilter_ProfileFragment.fvrtfilter_frmcancel = false;
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(context).getWritableDatabase();
        Cursor cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORFAVORITES, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    categoriesid = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_CATEGORY));
                    stars = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_STARS));
                    type = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_TYPE));
                    reviews = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_REVIEWS));
                    sortby = cursor.getString(cursor.getColumnIndex(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_SORTBY));
                    cursor.moveToNext();
                }
                cursor.close();
            }
            if (!com.vadevelopment.RedAppetite.HandyObjects.isNetworkAvailable(context)) {
                com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, getResources().getString(R.string.application_network_error));
            } else {
                if (sortby.equalsIgnoreCase("Distance")) {
                    if (isAscendingOrder() == true) {
                        sortbyValue = "distance ASC";
                    } else {
                        sortbyValue = "distance DESC";
                    }
                } else if (sortby.equalsIgnoreCase("A-Z")) {
                    if (isAscendingOrder() == true) {
                        sortbyValue = "buildingname ASC";
                    } else {
                        sortbyValue = "buildingname DESC";
                    }
                } else if (sortby.equalsIgnoreCase("Stars")) {
                    if (isAscendingOrder() == true) {
                        sortbyValue = "avgRate ASC";
                    } else {
                        sortbyValue = "avgRate DESC";
                    }
                } else if (sortby.equalsIgnoreCase("Reviews")) {
                    if (isAscendingOrder() == true) {
                        sortbyValue = "total ASC";
                    } else {
                        sortbyValue = "total DESC";
                    }
                } else {
                    if (isAscendingOrder() == true) {
                        sortbyValue = "happy DESC";
                    } else {
                        sortbyValue = "sad DESC";
                    }
                }
                if (stars.equalsIgnoreCase("Any")) {
                } else {
                    stars = stars.split("\\s+")[0];
                }
                arrayList = new ArrayList<>();
                start = 0;
                GetFavrt_Task();
            }
        }

    }

    private boolean isAscendingOrder() {
        boolean type = false;
        if (preferences.getString(Consts.FAVORITE_ORDERTYPE, "").equalsIgnoreCase("ascending")) {
            type = true;
        }
        return type;
    }

    private void setToolbar(View view, Bundle bundle) {
        mdashboard.footer.setVisibility(View.VISIBLE);
        mdashboard.textsearchimage.setVisibility(View.GONE);

        mdashboard.rl_headingwithcount.setVisibility(View.VISIBLE);
        mdashboard.text_heading.setText(getString(R.string.profile_favorites));
        mdashboard.centertextsearch.setVisibility(View.GONE);

        mdashboard.setting.setText(getString(R.string.back));
        //   mdashboard.search.setText(getString(R.string.profile_favorites));
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        mdashboard.mapButton.setVisibility(View.GONE);
        mdashboard.mapicon.setVisibility(View.VISIBLE);
        mdashboard.mapicon.setImageResource(R.drawable.listaftermap);
        mLayout = view.findViewById(R.id.sample_main_layout);
        mapView = (MapView) view.findViewById(R.id.map);

        mapView.onCreate(bundle);
        mapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
                // mdashboard.displayWithoutViewFragmentWithoutBackstack(new FavoritesProfile_Fragment());
            }
        });

        mdashboard.rl_headingwithcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdashboard.displayWithoutViewFragmentWithoutv4(new com.vadevelopment.RedAppetite.profilesection.FvrtSrchFilter_ProfileFragment());
            }
        });

        mdashboard.mapicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                from_mapfvrt = true;
                getActivity().getFragmentManager().popBackStack();
                // mdashboard.displayWithoutViewFragmentWithoutBackstack(new FavoritesProfile_Fragment());
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
        //  final LatLng ltlng = new LatLng(Double.parseDouble("30.7046"), Double.parseDouble("76.7179"));
        final LatLng ltlng = new LatLng(Double.parseDouble(preferences.getString(Consts.CURRENT_LATITUDE, "")), Double.parseDouble(preferences.getString(Consts.CURRENT_LONGITUDE, "")));
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(false);
                }
                //  googleMap.addMarker(new MarkerOptions().position(ltlng).title("Marker Title").snippet("Marker Description").icon(BitmapDescriptorFactory.fromResource(R.drawable.clubicon)));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(ltlng).zoom(8).build();
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

    private void GetFavrt_Task() {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.FAVBUILDING_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success")) {
                        JSONArray jarry = jboj.getJSONArray("all_list");
                        //max = Integer.parseInt(jboj.getString("max"));
                        mdashboard.text_headingcount.setText(String.valueOf(jarry.length()));
                        if (jarry.length() == 0) {
                        } else {
                        }
                        for (int i = 0; i < jarry.length(); i++) {
                            Skeleton_SearchFirst ske_search = new Skeleton_SearchFirst();
                            JSONObject jobj_inside = jarry.getJSONObject(i);
                            ske_search.setLid(jobj_inside.getString("lid"));
                            ske_search.setBuildingname(jobj_inside.getString("buildingname"));
                            ske_search.setAddress(jobj_inside.getString("address"));
                            ske_search.setDistance(jobj_inside.getString("distance"));
                            ske_search.setType(jobj_inside.getString("type"));
                            ske_search.setTotal(jobj_inside.getString("total"));
                            ske_search.setLatitude(jobj_inside.getString("latitude"));
                            ske_search.setLongitude(jobj_inside.getString("longitude"));
                            ske_search.setIsreviewed(jobj_inside.getString("isReviewed"));
                            ske_search.setAvgrating(jobj_inside.getString("avgRate"));
                            ske_search.setFreeimage(jobj_inside.getString("map_free"));
                            ske_search.setPremiumimage(jobj_inside.getString("map_premium"));
                            ske_search.setHappy(jobj_inside.getString("happy"));
                            ske_search.setSad(jobj_inside.getString("sad"));
                            arrayList.add(ske_search);
                        }
                        mClusterManager = new ClusterManager<>(getActivity(), googleMap);
                        googleMap.setOnCameraIdleListener(mClusterManager);
                        googleMap.setOnMarkerClickListener(mClusterManager);
                        googleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
                        googleMap.setOnInfoWindowClickListener(mClusterManager);

                        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener() {
                            @Override
                            public void onClusterItemInfoWindowClick(ClusterItem clusterItem) {
                                // clusterItem.getPosition().
                                com.vadevelopment.RedAppetite.profilesection.DetailDltFrmFvrt_EdtRvw_Fragment fvrtdeatil_frgm = new com.vadevelopment.RedAppetite.profilesection.DetailDltFrmFvrt_EdtRvw_Fragment();
                                Bundle bundle = new Bundle();
                                bundle.putString(Consts.PROFILE_CLICKLID, clickedClusterdeItem.getLid());
                                bundle.putString(Consts.CHECKCLICK_FROM, "fvrt");
                                fvrtdeatil_frgm.setArguments(bundle);
                                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(fvrtdeatil_frgm);
                            }
                        });
                        mClusterManager.setOnClusterItemClickListener(com.vadevelopment.RedAppetite.profilesection.Map_FvrtFragment.this);
                        mClusterManager.setRenderer(new MyClusterRenderer(getActivity(), googleMap, mClusterManager));
                        addItems(arrayList);
                        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForItems());
                    } else if (jboj.getString("message").equalsIgnoreCase("fail")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, jboj.getString("alert"));
                    }
                } catch (Exception e) {
                }
                com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("latitude", String.valueOf(MainActivity.currentLatitude));
                params.put("longitude", String.valueOf(MainActivity.currentLongitude));
                params.put("uid", preferences.getString(Consts.USER_ID, ""));
                params.put("type", type);
                params.put("star", stars);
                params.put("distance", "");
                params.put("start", "0");
                params.put("categoriesid", categoriesid);
                params.put("reviews", reviews);
                params.put("order", sortbyValue);
                params.put("search", "");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public boolean onClusterItemClick(MyItem myItem) {
        clickedClusterdeItem = myItem;
        return false;
    }

    private void addItems(ArrayList<Skeleton_SearchFirst> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            MyItem offsetitem = new MyItem(arrayList.get(i).getVisited_date(), arrayList.get(i).getIsreviewed(), "0", arrayList.get(i).getLid(), Double.parseDouble(arrayList.get(i).getLatitude()), Double.parseDouble(arrayList.get(i).getLongitude()), arrayList.get(i).getBuildingname(), arrayList.get(i).getAddress(),
                    arrayList.get(i).getAvgrating(), arrayList.get(i).getDistance(),
                    arrayList.get(i).getHappy(), arrayList.get(i).getSad(), arrayList.get(i).getTotal(), arrayList.get(i).getType(), arrayList.get(i).getFreeimage(), arrayList.get(i).getPremiumimage());
            mClusterManager.addItem(offsetitem);
        }
    }

    public class MyClusterRenderer extends DefaultClusterRenderer<MyItem> {

        private final IconGenerator mClusterIconGenerator = new IconGenerator(context);

        public MyClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.clubicon));
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions);
            final Drawable clustericon = getResources().getDrawable(R.drawable.circle_bg);
            // mClusterIconGenerator.setColor(Color.parseColor("#950000"));
            mClusterIconGenerator.setBackground(clustericon);
            mClusterIconGenerator.setTextAppearance(R.style.iconGenText);
            if (cluster.getSize() < 10) {
                mClusterIconGenerator.setContentPadding(36, 25, 0, 0);
            } else {
                mClusterIconGenerator.setContentPadding(30, 25, 0, 0);
            }
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
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
            ImageView image_isreviewd = ((ImageView) myContentsView
                    .findViewById(R.id.image_isreviewd));
            ImageView icondelete = ((ImageView) myContentsView
                    .findViewById(R.id.icondelete));
            icondelete.setVisibility(View.VISIBLE);
            if (Integer.parseInt(clickedClusterdeItem.getIsreviewd()) > 0) {
                image_isreviewd.setVisibility(View.VISIBLE);
            }
            tvTitle.setText(clickedClusterdeItem.getB_name());
            tvSnippet.setText(clickedClusterdeItem.getB_address().split(",")[0]);
            distance.setText(clickedClusterdeItem.getDistance());
            users.setText(clickedClusterdeItem.getTotal());
            likes.setText(clickedClusterdeItem.getLike());
            dislikes.setText(clickedClusterdeItem.getDislike());
            ratingBar.setRating(Float.parseFloat(clickedClusterdeItem.getAvg_rating()));
            // ratingtext.setText(clickedClusterdeItem.getAvg_rating());
            double ii = Double.parseDouble(clickedClusterdeItem.getAvg_rating());
            ratingtext.setText(String.valueOf(new DecimalFormat("##.#").format(ii)));
            return myContentsView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        com.vadevelopment.RedAppetite.profilesection.FvrtSrchFilter_ProfileFragment.fvrtfilter_frmcancel = false;
    }
}

