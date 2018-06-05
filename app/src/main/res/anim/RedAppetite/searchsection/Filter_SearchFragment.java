package com.vadevelopment.RedAppetite.searchsection;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

/**
 * Created by vibrantappz on 3/22/2017.
 */

public class Filter_SearchFragment extends android.app.Fragment {

    DashboardActivity mdashboard;
    private RelativeLayout rl_category, rl_stars, rl_myfvrt, rl_myreviews;
    private LinearLayout ll_main;
    private Dialog openDialog;
    private CheckBox checkbox_premium, checkbox_fvrt, checkbox_review;
    private SQLiteDatabase database;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String stars, type, categoriesid, myfavourites, myreviews;
    private TextView starsvalue, categoryvalue;
    public static boolean searchfilter_fromcancel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mdashboard = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_filtersearch, container, false);
        rl_category = (RelativeLayout) view.findViewById(R.id.rl_category);
        rl_stars = (RelativeLayout) view.findViewById(R.id.rl_stars);
        rl_myfvrt = (RelativeLayout) view.findViewById(R.id.rl_myfvrt);
        rl_myreviews = (RelativeLayout) view.findViewById(R.id.rl_myreviews);
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        checkbox_premium = (CheckBox) view.findViewById(R.id.checkbox_premium);
        checkbox_fvrt = (CheckBox) view.findViewById(R.id.checkbox_fvrt);
        checkbox_review = (CheckBox) view.findViewById(R.id.checkbox_review);
        starsvalue = (TextView) view.findViewById(R.id.starsvalue);
        categoryvalue = (TextView) view.findViewById(R.id.categoryvalue);
        database = com.vadevelopment.RedAppetite.database.ParseOpenHelper.getmInstance(getActivity()).getWritableDatabase();
        mdashboard.settingicon.setVisibility(View.GONE);
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.footer.setVisibility(View.GONE);
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.centertextsearch.setVisibility(View.VISIBLE);
        mdashboard.centertextsearch.setText(getString(R.string.search));
        mdashboard.setting.setText(getString(R.string.cancel));
        mdashboard.setting.setBackgroundResource(R.drawable.cancelbutton);
        mdashboard.mapButton.setVisibility(View.VISIBLE);
        mdashboard.mapButton.setText(getString(R.string.done));
        mdashboard.mapicon.setVisibility(View.GONE);
        mdashboard.btm_line.setVisibility(View.GONE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        if (preferences.getString(Consts.USER_ID, "").equalsIgnoreCase("")) {
            rl_myfvrt.setVisibility(View.INVISIBLE);
            rl_myreviews.setVisibility(View.INVISIBLE);
        } else {
            rl_myfvrt.setVisibility(View.VISIBLE);
            rl_myreviews.setVisibility(View.VISIBLE);
        }
        if (SearchFirstFragment.fromfirstsearch == true) {
            slideToAbove();
        }

        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchfilter_fromcancel = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getFragmentManager().popBackStack();
                    }
                }, 430);
                slideToBottom();
            }
        });

        rl_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment fc_frgm = new com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Consts.ONCATEGORY_FROM, "search");
                fc_frgm.setArguments(bundle);
                mdashboard.displayWithoutViewFragmentWithanimWithoutv4(fc_frgm);
            }
        });

        rl_stars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StarsDialog();
            }
        });

        mdashboard.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues();
                if (checkbox_premium.isChecked()) {
                    cv.put("search_type", "Premium");
                } else {
                    cv.put("search_type", "Free");
                }
                if (checkbox_fvrt.isChecked()) {
                    cv.put("search_favorites", "1");
                } else {
                    cv.put("search_favorites", "0");
                }
                if (checkbox_review.isChecked()) {
                    cv.put("search_reviews", "1");
                } else {
                    cv.put("search_reviews", "0");
                }
                if (categoryvalue.getText().toString().equalsIgnoreCase("All")) {
                    cv.put("search_category", categoryvalue.getText().toString());
                } else {
                    if (com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.finalcatgid_string != null && !com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.finalcatgid_string.isEmpty()) {
                        cv.put("search_category", com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.finalcatgid_string);
                    } else {
                        cv.put("search_category", categoriesid);
                    }
                }
                cv.put("search_stars", starsvalue.getText().toString());
                database.update(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, cv, null, null);
                searchfilter_fromcancel = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getFragmentManager().popBackStack();
                    }
                }, 390);
                slideToBottom();

            }
        });
        setData_Fromdatabase();
        return view;
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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SearchFirstFragment.fromfirstsearch = false;
        com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.finalcatgid_string = "";
    }

    private void setData_Fromdatabase() {
        Cursor cursor = database.query(com.vadevelopment.RedAppetite.database.ParseOpenHelper.TABLE_FORSEARCH, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    categoriesid = cursor.getString(cursor.getColumnIndex("search_category"));
                    stars = cursor.getString(cursor.getColumnIndex("search_stars"));
                    type = cursor.getString(cursor.getColumnIndex("search_type"));
                    myfavourites = cursor.getString(cursor.getColumnIndex("search_favorites"));
                    myreviews = cursor.getString(cursor.getColumnIndex("search_reviews"));
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

        if (com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.finalcatgid_string.equalsIgnoreCase("All")) {
            categoryvalue.setText("All");
        } else if (com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.finalcatgid_string != null && !com.vadevelopment.RedAppetite.searchsection.FilterCategory_SearchFragment.finalcatgid_string.isEmpty()) {
            categoryvalue.setText("Selected");
        } else {
            if (categoriesid.equalsIgnoreCase("All")) {
                categoryvalue.setText("All");
            } else {
                categoryvalue.setText("Selected");
            }
        }

        if (myfavourites.equalsIgnoreCase("1")) {
            checkbox_fvrt.setChecked(true);
        } else {
            checkbox_fvrt.setChecked(false);
        }
        if (myreviews.equalsIgnoreCase("1")) {
            checkbox_review.setChecked(true);
        } else {
            checkbox_review.setChecked(false);
        }
    }
}


