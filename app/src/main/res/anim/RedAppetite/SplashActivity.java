package com.vadevelopment.RedAppetite;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences = PreferenceManager.getDefaultSharedPreferences(com.vadevelopment.RedAppetite.SplashActivity.this);
        editor = preferences.edit();
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(com.vadevelopment.RedAppetite.SplashActivity.this).getWritableDatabase();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (preferences.getBoolean(Consts.WELCOME_SCREEN, false)) {
                        // if (preferences.getBoolean("login", false)) {
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
                        Intent i = new Intent(com.vadevelopment.RedAppetite.SplashActivity.this, DashboardActivity.class);
                        i.putExtra("fromskip", "yup");
                        startActivity(i);

                        finish();
                        /*} else {
                            Intent intent = new Intent(SplashActivity.this, SelectLoginRegister.class);
                            startActivity(intent);
                        }*/
                    } else {
                        Intent intent = new Intent(com.vadevelopment.RedAppetite.SplashActivity.this, com.vadevelopment.RedAppetite.MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}

