package com.vadevelopment.RedAppetite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vibrantappz on 7/28/2017.
 */

public class ParseOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "REDAPP";
    public static final int VERSION = 1;
    private static com.vadevelopment.RedAppetite.database.ParseOpenHelper mInstance = null;
    public static final String TABLE_FORSEARCH = "forsearch";
    public static final String SEARCH_CATEGORY = "search_category";
    public static final String SEARCH_STARS = "search_stars";
    public static final String SEARCH_TYPE = "search_type";
    public static final String SEARCH_REVIEWS = "search_reviews";
    public static final String SEARCH_FAVORITES = "search_favorites";
    public static final String SEARCH_MODEPOSI = "search_modeposi";
    public static final String SEARCH_RADIUS = "search_radius";
    public static final String SEARCH_SORTBY = "search_sortby";
    public static final String SEARCH_WHICHLATLONG = "search_whichlatlong";
    public static final String SEARCH_WHICHLATLONGADDRS = "search_whichlatlongaddrs";

    public static final String TABLE_FOREVENT = "forevent";
    public static final String EVENT_CATEGORY = "event_category";
    public static final String EVENT_STARS = "event_stars";
    public static final String EVENT_TYPE = "event_type";
    public static final String EVENT_TYPEONE = "event_typeone";
    public static final String EVENT_REVIEWS = "event_reviews";
    public static final String EVENT_FAVORITES = "event_favorites";
    public static final String EVENT_MODEPOSI = "event_modeposi";
    public static final String EVENT_RADIUS = "event_radius";
    public static final String EVENT_SORTBY = "event_sortby";
    public static final String EVENT_WHICHLATLONG = "event_whichlatlong";
    public static final String EVENT_WHICHLATLONGADDRS = "event_whichlatlongaddrs";

    public static final String TABLE_FORFAVORITES = "forfavorite";
    public static final String FAVORITE_CATEGORY = "favorite_category";
    public static final String FAVORITE_STARS = "favorite_stars";
    public static final String FAVORITE_TYPE = "favorite_type";
    public static final String FAVORITE_REVIEWS = "favorite_reviews";
    public static final String FAVORITE_SORTBY = "favorite_sortby";

    public static final String TABLE_FORREVIEW = "forreview";
    public static final String REVIEW_CATEGORY = "review_category";
    public static final String REVIEW_STARS = "review_stars";
    public static final String REVIEW_TYPE = "review_type";
    public static final String REVIEW_FAVORITE = "review_favorite";
    public static final String REVIEW_SORTBY = "review_sortby";

    // public static final String TABLE_FORFVRT = "forfvrt";

    public synchronized static com.vadevelopment.RedAppetite.database.ParseOpenHelper getmInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new com.vadevelopment.RedAppetite.database.ParseOpenHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public ParseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table forsearch(search_category TEXT,search_stars TEXT,search_type TEXT,search_reviews TEXT,search_favorites TEXT,search_modeposi TEXT,search_radius TEXT,search_sortby TEXT,search_whichlatlong TEXT,search_whichlatlongaddrs TEXT);");
        sqLiteDatabase.execSQL("create table forevent(event_category TEXT,event_stars TEXT,event_type TEXT,event_typeone TEXT,event_reviews TEXT,event_favorites TEXT,event_modeposi TEXT,event_radius TEXT,event_sortby TEXT,event_whichlatlong TEXT,event_whichlatlongaddrs TEXT);");
        sqLiteDatabase.execSQL("create table forfavorite(favorite_category TEXT,favorite_stars TEXT,favorite_type TEXT,favorite_reviews TEXT,favorite_sortby TEXT);");
        sqLiteDatabase.execSQL("create table forreview(review_category TEXT,review_stars TEXT,review_type TEXT,review_favorite TEXT,review_sortby TEXT);");
        // sqLiteDatabase.execSQL("create table forsearch(search_uid TEXT,search_category TEXT,search_stars TEXT,search_type TEXT,search_reviews TEXT,search_favorites TEXT,search_modeposi TEXT,search_radius TEXT,search_sortby TEXT,search_whichlatlong TEXT,search_whichlatlongaddrs TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table" + TABLE_FORSEARCH);
        sqLiteDatabase.execSQL("drop table" + TABLE_FOREVENT);
        sqLiteDatabase.execSQL("drop table" + TABLE_FORFAVORITES);
        sqLiteDatabase.execSQL("drop table" + TABLE_FORREVIEW);
        onCreate(sqLiteDatabase);
    }
}
