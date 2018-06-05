package com.vadevelopment.RedAppetite;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

public class SelectLoginRegister extends AppCompatActivity implements View.OnClickListener {
    Button login, register;
    TextView skip, centertext;
    RelativeLayout relativeapptoolbar;
    private boolean wasShaken = false;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_login_register);
        initViews();
    }

    private void initViews() {
        relativeapptoolbar = (RelativeLayout) findViewById(R.id.cancelSkipLayout);
        relativeapptoolbar.setVisibility(View.VISIBLE);
        skip = (TextView) findViewById(R.id.skipText);
        skip.setVisibility(View.VISIBLE);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        centertext = (TextView) findViewById(R.id.centertext);
        preferences = PreferenceManager.getDefaultSharedPreferences(com.vadevelopment.RedAppetite.SelectLoginRegister.this);
        editor = preferences.edit();
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(com.vadevelopment.RedAppetite.SelectLoginRegister.this).getWritableDatabase();
        centertext.setText("Login/Register");
        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/texgyreheros-bold.otf");
        ViewGroup root = (ViewGroup) findViewById(R.id.myrootlayout);
        setFont(root, mFont);
        skip.setOnClickListener(this);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        check_permission();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skipText:
                Cursor cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, null, null, null, null, null, null);
                Cursor cursor_event = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, null, null, null, null, null, null);
                Cursor cursor_favrt = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORFAVORITES, null, null, null, null, null, null);
                Cursor cursor_review = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, null, null, null, null, null, null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                    } else {
                        ContentValues cv = new ContentValues();
                        cv.put("search_category", "All");
                        cv.put("search_stars", "Any");
                        cv.put("search_type", "Free");
                        cv.put("search_reviews", "0");
                        cv.put("search_favorites", "0");
                        cv.put("search_modeposi", "Current Position");
                        cv.put("search_radius", "100");
                        cv.put("search_sortby", "Distance");
                        cv.put("search_whichlatlong", "current");
                        cv.put("search_whichlatlongaddrs", "current");
                        long idd = database.insert("forsearch", null, cv);
                        Log.e("table", String.valueOf(idd));
                    }
                    cursor.close();
                }

                if (cursor_event != null) {
                    if (cursor_event.getCount() > 0) {
                    } else {
                        ContentValues cv_event = new ContentValues();
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_CATEGORY, "All");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_STARS, "Any");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_TYPE, "Normal");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_TYPEONE, "Free");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_REVIEWS, "0");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_FAVORITES, "0");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_MODEPOSI, "Current Position");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_RADIUS, "100");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_SORTBY, "Distance");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_WHICHLATLONG, "current");
                        cv_event.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.EVENT_WHICHLATLONGADDRS, "current");
                        long idd = database.insert(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FOREVENT, null, cv_event);
                        Log.e("table", String.valueOf(idd));
                    }
                    cursor_event.close();
                }

                if (cursor_favrt != null) {
                    if (cursor_favrt.getCount() > 0) {
                    } else {
                        ContentValues cv_fvrt = new ContentValues();
                        cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_CATEGORY, "All");
                        cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_STARS, "Any");
                        cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_TYPE, "Free");
                        cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_REVIEWS, "0");
                        cv_fvrt.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.FAVORITE_SORTBY, "Distance");
                        long idd = database.insert(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORFAVORITES, null, cv_fvrt);
                        Log.e("table", String.valueOf(idd));
                    }
                    cursor_favrt.close();
                }

                if (cursor_review != null) {
                    if (cursor_review.getCount() > 0) {
                    } else {
                        ContentValues cv_review = new ContentValues();
                        cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_CATEGORY, "All");
                        cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_STARS, "Any");
                        cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_TYPE, "Free");
                        cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_FAVORITE, "0");
                        cv_review.put(com.vadevelopment.RedAppetite.database.ParseOpenHelper.REVIEW_SORTBY, "Distance");
                        long idd = database.insert(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORREVIEW, null, cv_review);
                        Log.e("table", String.valueOf(idd));
                    }
                    cursor_review.close();
                }
                //  editor.putString(Consts)
                editor.putString(Consts.SEARCH_ORDERTYPE, "ascending");
                editor.putString(Consts.SEARCH_ORDERTYPESTARS, "descending");
                editor.putString(Consts.SEARCH_ORDERTYPEREVIEWS, "descending");
                editor.putString(Consts.EVENT_ORDERTYPESTARS, "descending");
                editor.putString(Consts.EVENT_ORDERTYPEREVIEWS, "descending");

                editor.putString(Consts.FAVORITE_ORDERTYPESTARS, "descending");
                editor.putString(Consts.FAVORITE_ORDERTYPEREVIEWS, "descending");
                editor.putString(Consts.REVIEW_ORDERTYPESTARS, "descending");
                editor.putString(Consts.REVIEW_ORDERTYPEDATE, "descending");
                editor.putString(Consts.EVENT_ORDERTYPE, "ascending");
                editor.putString(Consts.FAVORITE_ORDERTYPE, "ascending");
                editor.putString(Consts.REVIEW_ORDERTYPE, "ascending");
                editor.commit();
                Intent i = new Intent(com.vadevelopment.RedAppetite.SelectLoginRegister.this, DashboardActivity.class);
                i.putExtra("fromskip", "yup");
                startActivity(i);
                break;
            case R.id.login:
                Intent ilogin = new Intent(com.vadevelopment.RedAppetite.SelectLoginRegister.this, com.vadevelopment.RedAppetite.login.ContainerActivity.class);
                ilogin.putExtra("Login", true);
                ilogin.putExtra("fromdashboard", "nop");
                startActivity(ilogin);
                break;
            case R.id.register:
                Intent iregister = new Intent(com.vadevelopment.RedAppetite.SelectLoginRegister.this, com.vadevelopment.RedAppetite.login.ContainerActivity.class);
                iregister.putExtra("Register", false);
                iregister.putExtra("from", "main");
                startActivity(iregister);
                break;
        }
    }

    public void setFont(ViewGroup group, Typeface font) {
        int count = group.getChildCount();
        View v;
        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView || v instanceof Button)
                ((TextView) v).setTypeface(font);
            else if (v instanceof ViewGroup)
                setFont((ViewGroup) v, font);
        }
    }

    private void check_permission() {
        if (ContextCompat.checkSelfPermission(com.vadevelopment.RedAppetite.SelectLoginRegister.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(com.vadevelopment.RedAppetite.SelectLoginRegister.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(com.vadevelopment.RedAppetite.SelectLoginRegister.this);
                builder.setMessage("Permission is needed")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                ActivityCompat.requestPermissions(com.vadevelopment.RedAppetite.SelectLoginRegister.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        2);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(com.vadevelopment.RedAppetite.SelectLoginRegister.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }
}
