package teq.development.seatech.Dashboard;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Chat.ChatActivity;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.OfflineSync.DemoSyncJob;
import teq.development.seatech.OfflineSync.SyncAddPart;
import teq.development.seatech.OfflineSync.SyncJobStatus;
import teq.development.seatech.OfflineSync.SyncLCChange;
import teq.development.seatech.OfflineSync.SyncNeedEstimate;
import teq.development.seatech.OfflineSync.SyncNeeddPart;
import teq.development.seatech.OfflineSync.SyncSubmitLaborPerf;
import teq.development.seatech.OfflineSync.SyncUploadImages;
import teq.development.seatech.Profile.ChangePwdFragment;
import teq.development.seatech.Profile.MyProfileFragment;
import teq.development.seatech.R;
import teq.development.seatech.Timesheet.DayJobStatusDetailFragment;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    public Boolean mABoolean = false;
    public ImageView menu_icon, sick_icon, cdhour_icon,chaticon;
    SimpleDraweeView userimage;
    TextView username;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;
    public static boolean onbackppress;
    // ImageView navigation_icon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        replaceFragmentWithoutBack(new DashBoardFragment());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        menu_icon = (ImageView) findViewById(R.id.menu_icon);
        sick_icon = (ImageView) findViewById(R.id.sick_icon);
        cdhour_icon = (ImageView) findViewById(R.id.cdhour_icon);
        chaticon = (ImageView) findViewById(R.id.chaticon);
        userimage = (SimpleDraweeView) findViewById(R.id.userimage);
        username = (TextView) findViewById(R.id.username);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.navigation_icon);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userimage.setImageURI(HandyObject.getPrams(this, AppConstants.LOGINTEQ_IMAGE));
        username.setText(HandyObject.getPrams(this, AppConstants.LOGINTEQ_USERNAME));
        displayLeft(new LeftDrawer());
        menu_icon.setOnClickListener(this);
        sick_icon.setOnClickListener(this);
        cdhour_icon.setOnClickListener(this);
        chaticon.setOnClickListener(this);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.navigation_icon,
                R.string.app_name,
                R.string.detailpage
        ) {
            public void onDrawerClosed(View view) {
                if (mABoolean == false) {
                    toolbar.setNavigationIcon(R.drawable.navigation_icon);
                    invalidateOptionsMenu();
                } else {
                    toolbar.setNavigationIcon(R.drawable.navigation_icon);
                    invalidateOptionsMenu();
                }
            }

            public void onDrawerOpened(View drawerView) {
                if (mABoolean == false) {
                    toolbar.setNavigationIcon(R.drawable.cross);
                    invalidateOptionsMenu();
                } else {
                    toolbar.setNavigationIcon(R.drawable.cross);
                    invalidateOptionsMenu();
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (view.isShown()) {
                    long selectedMilli = myCalendar.getTimeInMillis();
                    Date datePickerDate = new Date(selectedMilli);
                    if (datePickerDate.after(new Date())) {
                        HandyObject.showAlert(DashBoardActivity.this, "Can't Select Future date");
                    } else {
                        DayJobStatusDetailFragment frgm = new DayJobStatusDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(AppConstants.SELECTED_JOBDAYSTATUS, HandyObject.getDateFromPickerDayStatus(myCalendar.getTime()));
                        frgm.setArguments(bundle);
                        replaceFragment(frgm);
                    }
                }
            }
        };
    }

    // Display Navigation Drawer
    public void displayLeft(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.leftDrawer, mFragment)
                .commit();
    }

    public void replaceFragmentWithoutBack(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, mFragment)
                .commit();
    }

    public void replaceFragment(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, mFragment).addToBackStack(null)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//         boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//         menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_icon:
                displyPopup(menu_icon);
                break;
            case R.id.sick_icon:
                displaypopupLeave(sick_icon);
                break;
            case R.id.cdhour_icon:
                if (HandyObject.checkInternetConnection(this)) {
                    new DatePickerDialog(this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                } else {
                    // Toast.makeText(this, R.string.withoutinter_noprenextjob, Toast.LENGTH_SHORT).show();
                    HandyObject.showAlert(DashBoardActivity.this, getString(R.string.withoutinter_nodailyreport));
                }
                break;
            case R.id.chaticon:
                Intent intent_reg = new Intent(DashBoardActivity.this, ChatActivity.class);
                startActivity(intent_reg);
              //  finish();
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                break;
        }
    }

    private void displaypopupLeave(View anchorview) {
        final PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.popup_leave, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        Button submit = (Button) layout.findViewById(R.id.submit);
        final CheckBox cbx_vacation = (CheckBox) layout.findViewById(R.id.cbx_vacation);
        final CheckBox cbx_sick = (CheckBox) layout.findViewById(R.id.cbx_sick);
        final CheckBox cbx_doneforday = (CheckBox) layout.findViewById(R.id.cbx_doneforday);
        cbx_vacation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_sick.setChecked(false);
                    cbx_doneforday.setChecked(false);
                }
            }
        });

        cbx_sick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_vacation.setChecked(false);
                    cbx_doneforday.setChecked(false);
                }
            }
        });

        cbx_doneforday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_vacation.setChecked(false);
                    cbx_sick.setChecked(false);
                }
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbx_sick.isChecked() == false && cbx_vacation.isChecked() == false && cbx_doneforday.isChecked() == false) {
                    HandyObject.showAlert(DashBoardActivity.this, getString(R.string.pleaseselectcode));
                } else if (cbx_sick.isChecked() == true || cbx_vacation.isChecked() == true) {
                    popup.dismiss();
                } else if (cbx_doneforday.isChecked() == true) {
                    popup.dismiss();
                    if (isJobRunning() == true) {
                        HandyObject.showAlert(DashBoardActivity.this, getString(R.string.cannt_logoutjobrunning));
                    } else {
                        if (HandyObject.checkInternetConnection(DashBoardActivity.this)) {
                            LogoutTask();
                        } else {
                            HandyObject.showAlert(DashBoardActivity.this, getString(R.string.check_internet_connection));
                        }
                    }
                }

            }
        });
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }

    private void displyPopup(View view) {
        final PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.popup_menu, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView myprofile = (TextView) layout.findViewById(R.id.myprofile);
        TextView changepwd = (TextView) layout.findViewById(R.id.changepwd);
        TextView logout = (TextView) layout.findViewById(R.id.logout);

        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                replaceFragment(new MyProfileFragment());
            }
        });

        changepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                replaceFragment(new ChangePwdFragment());
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                if (isJobRunning() == true) {
                    HandyObject.showAlert(DashBoardActivity.this, getString(R.string.cannt_logoutjobrunning));
                } else {
                    if (HandyObject.checkInternetConnection(DashBoardActivity.this)) {
                        LogoutTask();
                    } else {
                        HandyObject.showAlert(DashBoardActivity.this, getString(R.string.check_internet_connection));
                    }
                }

            }
        });
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(view);
    }

    private void LogoutTask() {
        HandyObject.showProgressDialog(this);
        HandyObject.getApiManagerType().logout(HandyObject.getPrams(this, AppConstants.LOGINTEQ_ID), HandyObject.getPrams(this, AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("response", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                HandyObject.showAlert(DashBoardActivity.this, jsonObject.getString("message"));
                                App.appInstance.stopTimer();
                                HandyObject.clearpref(DashBoardActivity.this);
                                Intent intent_reg = new Intent(DashBoardActivity.this, LoginActivity.class);
                                startActivity(intent_reg);
                                finish();
                                // overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                            } else {
                                HandyObject.showAlert(DashBoardActivity.this, jsonObject.getString("message"));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            HandyObject.stopProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        HandyObject.stopProgressDialog();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Activity OnStop", "Activity OnStop");
        //HandyObject.putPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING, "no");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Activity onPause", "Activity onPause");
        //HandyObject.putPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING, "no");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Activity OnDestroy", "Activity OnDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("Activity OnRestart", "Activity OnRestart");
        onbackppress = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("Acvity onSaveInseState", "Activity onSaveInseState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("Acvity onRestornceState", "Activity onRestornceState");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("Acvity onBackPressed", "Activity onBackPressed");
        onbackppress = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (onbackppress == true) {
            if (HandyObject.getPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
                // onbackppress = true;
                HandyObject.putPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING, "yes");
            } else {
                HandyObject.putPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING, "no");
            }
            onbackppress = false;
        } else {
            HandyObject.putPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING, "no");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        DemoSyncJob.scheduleJob();
        SyncNeeddPart.scheduleJob();
        SyncSubmitLaborPerf.scheduleJob();
        SyncNeedEstimate.scheduleJob();
        SyncLCChange.scheduleJob();
        SyncJobStatus.scheduleJob();
        SyncUploadImages.scheduleJob();
        SyncAddPart.scheduleJob();
    }

    boolean isJobRunning() {
        if (HandyObject.getPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
    }

}
