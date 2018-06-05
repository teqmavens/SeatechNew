package com.vadevelopment.RedAppetite.profilesection;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.adapters.Adapters.Adapter_FavoriteProfile;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;
import com.vadevelopment.RedAppetite.database.ParseOpenHelper;
import com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment;

/**
 * Created by vibrantappz on 3/22/2017.
 */

public class FvrtSrchFilter_ProfileFragment extends android.app.Fragment implements View.OnClickListener {

    DashboardActivity mdashboard;
    private LinearLayout ll_main;
    private Context context;
    private Dialog openDialog;
    private Adapter_FavoriteProfile adapter;
    private SharedPreferences preferences;
    private CheckBox checkbox_premium, checkbox_myreviews;
    private TextView starsvalue, categoryvalue;
    private SQLiteDatabase database;
    private RelativeLayout rl_category, rl_stars;
    private String categoriesid, stars, type, myreviews;
    public static boolean fvrtfilter_frmcancel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_fvrtsrchfilterprofile, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        setToolbar();
        //FavoritesProfile_Fragment.filterclick = false;
        checkbox_premium = (CheckBox) view.findViewById(R.id.checkbox_premium);
        checkbox_myreviews = (CheckBox) view.findViewById(R.id.checkbox_myreviews);
        starsvalue = (TextView) view.findViewById(R.id.starsvalue);
        categoryvalue = (TextView) view.findViewById(R.id.categoryvalue);
        rl_category = (RelativeLayout) view.findViewById(R.id.rl_category);
        rl_stars = (RelativeLayout) view.findViewById(R.id.rl_stars);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        rl_category.setOnClickListener(this);
        rl_stars.setOnClickListener(this);
        if (com.vadevelopment.RedAppetite.profilesection.FavoritesProfile_Fragment.fromfvrt_list == true) {
            slideToAbove();
        }

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setData_Fromdatabase();
            }
        });
    }

    private void slideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillBefore(true);
        ll_main.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_main.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void slideToBottom() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        slide.setDuration(460);
        slide.setFillAfter(true);
        slide.setFillBefore(true);
        ll_main.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getActivity().getFragmentManager().popBackStack();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void setToolbar() {
        mdashboard.footer.setVisibility(View.GONE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.rl_headingwithcount.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText(getString(R.string.profile_favorites));
        mdashboard.mapButton.setVisibility(View.VISIBLE);
        mdashboard.mapButton.setText(getString(R.string.done));
        mdashboard.mapicon.setVisibility(View.GONE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.setting.setBackgroundResource(R.drawable.cancelbutton);
        mdashboard.setting.setText(getString(R.string.cancel));
        mdashboard.btm_line.setVisibility(View.GONE);
        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fvrtfilter_frmcancel = true;
                slideToBottom();
                // getActivity().getSupportFragmentManager().popBackStack();
                //  getActivity().getSupportFragmentManager().beginTransaction().remove(FvrtSrchFilter_ProfileFragment.this).commit();
            }
        });

        mdashboard.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues();
                if (checkbox_premium.isChecked()) {
                    cv.put(ParseOpenHelper.FAVORITE_TYPE, "Premium");
                } else {
                    cv.put(ParseOpenHelper.FAVORITE_TYPE, "Free");
                }
                if (checkbox_myreviews.isChecked()) {
                    cv.put(ParseOpenHelper.FAVORITE_REVIEWS, "1");
                } else {
                    cv.put(ParseOpenHelper.FAVORITE_REVIEWS, "0");
                }
                if (categoryvalue.getText().toString().equalsIgnoreCase("All")) {
                    cv.put(ParseOpenHelper.FAVORITE_CATEGORY, categoryvalue.getText().toString());
                } else {
                    if (FilterCategory_SearchFragment.finalcatgid_string != null && !FilterCategory_SearchFragment.finalcatgid_string.isEmpty()) {
                        cv.put(ParseOpenHelper.FAVORITE_CATEGORY, FilterCategory_SearchFragment.finalcatgid_string);
                    } else {
                        cv.put(ParseOpenHelper.FAVORITE_CATEGORY, categoriesid);
                    }
                }
                cv.put(ParseOpenHelper.FAVORITE_STARS, starsvalue.getText().toString());
                database.update(ParseOpenHelper.TABLE_FORFAVORITES, cv, null, null);
                getActivity().getFragmentManager().popBackStack();
            }
        });
    }

    private void setData_Fromdatabase() {
        database = ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();
        Cursor cursor = database.query(ParseOpenHelper.TABLE_FORFAVORITES, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    categoriesid = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.FAVORITE_CATEGORY));
                    stars = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.FAVORITE_STARS));
                    type = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.FAVORITE_TYPE));
                    myreviews = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.FAVORITE_REVIEWS));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
        starsvalue.setText(stars);
        if (type.equalsIgnoreCase("Free")) {
            checkbox_premium.setChecked(false);
        } else {
            checkbox_premium.setChecked(true);
        }

        if (FilterCategory_SearchFragment.finalcatgid_string.equalsIgnoreCase("All")) {
            categoryvalue.setText("All");
        } else if (FilterCategory_SearchFragment.finalcatgid_string != null && !FilterCategory_SearchFragment.finalcatgid_string.isEmpty()) {
            categoryvalue.setText("Selected");
        } else {
            if (categoriesid.equalsIgnoreCase("All")) {
                categoryvalue.setText("All");
            } else {
                categoryvalue.setText("Selected");
            }
        }
        if (myreviews.equalsIgnoreCase("1")) {
            checkbox_myreviews.setChecked(true);
        } else {
            checkbox_myreviews.setChecked(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_category:
                FilterCategory_SearchFragment fc_frgm = new FilterCategory_SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Consts.ONCATEGORY_FROM, "favorite");
                fc_frgm.setArguments(bundle);
                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(fc_frgm);
                break;
            case R.id.rl_stars:
                StarsDialog();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FilterCategory_SearchFragment.finalcatgid_string = "";
        com.vadevelopment.RedAppetite.profilesection.FavoritesProfile_Fragment.fromfvrt_list = false;
    }

    private void StarsDialog() {
        openDialog = new Dialog(getActivity());
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setCanceledOnTouchOutside(false);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        openDialog.show();
        Window window = openDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window.setAttributes(wlp);
        openDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View vview = inflater.inflate(R.layout.dialog_stars, null, false);
        openDialog.setContentView(vview);
        LinearLayout approx_lay = (LinearLayout) openDialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width - 60, (height / 3) + 20);
        approx_lay.setLayoutParams(params);
        TextView cancel = (TextView) vview.findViewById(R.id.cancel);
        final TextView any = (TextView) vview.findViewById(R.id.any);
        final TextView firststar = (TextView) vview.findViewById(R.id.firststar);
        final TextView secondstar = (TextView) vview.findViewById(R.id.secondstar);
        final TextView thirdstar = (TextView) vview.findViewById(R.id.thirdstar);
        final TextView fourthstar = (TextView) vview.findViewById(R.id.fourthstar);
        final TextView fifthstar = (TextView) vview.findViewById(R.id.fifthstar);

        any.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                starsvalue.setText(any.getText().toString());
            }
        });
        firststar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                starsvalue.setText(firststar.getText().toString());
            }
        });

        secondstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                starsvalue.setText(secondstar.getText().toString());
            }
        });

        thirdstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                starsvalue.setText(thirdstar.getText().toString());
            }
        });

        fourthstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                starsvalue.setText(fourthstar.getText().toString());
            }
        });

        fifthstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
                starsvalue.setText(fifthstar.getText().toString());
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.hide();
            }
        });
        openDialog.show();
    }
}
