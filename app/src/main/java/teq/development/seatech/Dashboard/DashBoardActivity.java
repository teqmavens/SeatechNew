package teq.development.seatech.Dashboard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.github.nkzawa.socketio.client.Socket;

import android.os.Handler;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Chat.ChatActivity;
import teq.development.seatech.JobDetail.Skeleton.LCChangeSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.OfflineSync.DemoSyncJob;
import teq.development.seatech.OfflineSync.SyncAddPart;
import teq.development.seatech.OfflineSync.SyncJobStart;
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
import teq.development.seatech.Utils.ConnectivityReceiver;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    public Boolean mABoolean = false;
    public ImageView menu_icon, sick_icon, cdhour_icon, chaticon, notificationicon;
    public static SimpleDraweeView userimage;
    public static TextView username;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;
    public static boolean onbackppress;
    private ConnectivityReceiver receiver;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jobj = new JSONObject();
                    jobj.put("customId", Integer.parseInt(HandyObject.getPrams(DashBoardActivity.this, AppConstants.LOGINTEQPARENT_ID)));
                    jobj.put("device_id", HandyObject.getPrams(DashBoardActivity.this, AppConstants.DEVICE_TOKEN));
                    App.appInstance.getSocket().emit("storeClientInfo", jobj);
                } catch (Exception e) {
                }

            }
        }, 1000);
        replaceFragmentWithoutBack(new DashBoardFragment());
        // replaceFragmentWithoutBack(new DashBoardFragment());
        toolbar = findViewById(R.id.toolbar);
        menu_icon = findViewById(R.id.menu_icon);
        sick_icon = findViewById(R.id.sick_icon);
        cdhour_icon = findViewById(R.id.cdhour_icon);
        chaticon = findViewById(R.id.chaticon);
        notificationicon = findViewById(R.id.notificationicon);
        userimage = findViewById(R.id.userimage);
        username = findViewById(R.id.username);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.navigation_icon);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userimage.setImageURI(HandyObject.getPrams(this, AppConstants.LOGINTEQ_IMAGE));
        username.setText(HandyObject.getPrams(this, AppConstants.LOGINTEQ_USERNAME));
        displayLeft(new LeftDrawer());
        menu_icon.setOnClickListener(this);
        sick_icon.setOnClickListener(this);
        cdhour_icon.setOnClickListener(this);
        chaticon.setOnClickListener(this);
        notificationicon.setOnClickListener(this);

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
                        //     bundle.putString(AppConstants.SELECTED_JOBDAYSTATUS, HandyObject.getDateFromPickerDayStatus(myCalendar.getTime()));
                        bundle.putString(AppConstants.SELECTED_JOBDAYSTATUS, HandyObject.getDateFromPickerDayStatusNew(myCalendar.getTime()));
                        frgm.setArguments(bundle);
                        replaceFragment(frgm);
                    }
                }
            }
        };


        receiver = new ConnectivityReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    // Display Navigation Drawer
    public void displayLeft(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.leftDrawer, mFragment)
                .commit();
    }

    //Replace fragment Without backstack
    public void replaceFragmentWithoutBack(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, mFragment)
                .commit();
    }

    //Replace fragment With backstack
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
                Intent intent_chat = new Intent(DashBoardActivity.this, ChatActivity.class);
                startActivity(intent_chat);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                break;
            case R.id.notificationicon:
                Intent intent_reg = new Intent(DashBoardActivity.this, Notifications.class);
                startActivity(intent_reg);
                //  finish();
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                break;
        }
    }

    //Display popup for internal job(JOBID:111111) labor code like sick,vacation etc
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
        final CheckBox cbx_vacationunpaid = (CheckBox) layout.findViewById(R.id.cbx_vacationunpaid);
        final CheckBox cbx_sick = (CheckBox) layout.findViewById(R.id.cbx_sick);
        final CheckBox cbx_sickunpaid = (CheckBox) layout.findViewById(R.id.cbx_sickunpaid);
        final CheckBox cbx_doneforday = (CheckBox) layout.findViewById(R.id.cbx_doneforday);
        cbx_vacation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_sick.setChecked(false);
                    cbx_doneforday.setChecked(false);
                    cbx_vacationunpaid.setChecked(false);
                    cbx_sickunpaid.setChecked(false);
                }
            }
        });

        cbx_vacationunpaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_sick.setChecked(false);
                    cbx_doneforday.setChecked(false);
                    cbx_sickunpaid.setChecked(false);
                    cbx_vacation.setChecked(false);
                }
            }
        });

        cbx_sick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_vacation.setChecked(false);
                    cbx_vacationunpaid.setChecked(false);
                    cbx_doneforday.setChecked(false);
                    cbx_sickunpaid.setChecked(false);
                }
            }
        });

        cbx_sickunpaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbx_sick.setChecked(false);
                    cbx_vacationunpaid.setChecked(false);
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
                    cbx_vacationunpaid.setChecked(false);
                    cbx_sick.setChecked(false);
                    cbx_sickunpaid.setChecked(false);
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
                if (cbx_sick.isChecked() == false && cbx_sickunpaid.isChecked() == false && cbx_vacation.isChecked() == false && cbx_vacationunpaid.isChecked() == false && cbx_doneforday.isChecked() == false) {
                    HandyObject.showAlert(DashBoardActivity.this, getString(R.string.pleaseselectcode));
                } else if (cbx_sick.isChecked() == true && cbx_vacation.isChecked() == false) {
                    if (App.timer_running || isJobRunning() == true) {
                        HandyObject.showAlert(DashBoardActivity.this, getString(R.string.cannt_logoutjobrunningnew));
                    } else {
                        TaskLCChange("TIME SICK PAID");
                    }
                    popup.dismiss();
                } else if (cbx_sickunpaid.isChecked() == true && cbx_vacation.isChecked() == false) {
                    if (App.timer_running || isJobRunning() == true) {
                        HandyObject.showAlert(DashBoardActivity.this, getString(R.string.cannt_logoutjobrunningnew));
                    } else {
                        TaskLCChange("TIME SICK UNPAID");
                    }
                    popup.dismiss();
                } else if (cbx_vacation.isChecked() == true && cbx_sick.isChecked() == false) {
                    if (App.timer_running || isJobRunning() == true) {
                        HandyObject.showAlert(DashBoardActivity.this, getString(R.string.cannt_logoutjobrunningnew));
                    } else {
                        TaskLCChange("TIME VACATION PAID");
                    }
                    popup.dismiss();
                } else if (cbx_vacationunpaid.isChecked() == true && cbx_sick.isChecked() == false) {
                    if (App.timer_running || isJobRunning() == true) {
                        HandyObject.showAlert(DashBoardActivity.this, getString(R.string.cannt_logoutjobrunningnew));
                    } else {
                        TaskLCChange("TIME VACATION UNPAID");
                    }
                    popup.dismiss();
                } else if (cbx_doneforday.isChecked() == true) {
                    popup.dismiss();
                    if (App.timer_running || isJobRunning() == true) {
                        HandyObject.showAlert(DashBoardActivity.this, getString(R.string.cannt_logoutjobrunning));
                    } else if (HandyObject.getPrams(DashBoardActivity.this, AppConstants.ISALLMSG_ACKNOWLEDGE).equalsIgnoreCase("1")) {
                        HandyObject.showAlert(DashBoardActivity.this, getString(R.string.cannt_logoutacknowledge));
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

    //Display menu poopup for profile and logout
    private void displyPopup(View view) {
        final PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.popup_menu, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView myprofile = (TextView) layout.findViewById(R.id.myprofile);
        //   TextView changepwd = (TextView) layout.findViewById(R.id.changepwd);
        TextView logout = (TextView) layout.findViewById(R.id.logout);

        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                replaceFragment(new MyProfileFragment());
            }
        });

        /*changepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                replaceFragment(new ChangePwdFragment());
            }
        });*/

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                if (App.timer_running || isJobRunning() == true) {
                    HandyObject.showAlert(DashBoardActivity.this, getString(R.string.cannt_logoutjobrunning));
                } else if (HandyObject.getPrams(DashBoardActivity.this, AppConstants.ISALLMSG_ACKNOWLEDGE).equalsIgnoreCase("1")) {
                    HandyObject.showAlert(DashBoardActivity.this, getString(R.string.cannt_logoutacknowledge));
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

    // LogOut Api for logged in technician.
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
                                // String deviceToken = HandyObject.getPrams(DashBoardActivity.this, AppConstants.DEVICE_TOKEN);
                                HandyObject.clearpref(DashBoardActivity.this);
                                HandyObject.deleteAllDatabase(DashBoardActivity.this);


                                //  HandyObject.putPrams(getApplicationContext(), AppConstants.DEVICE_TOKEN, deviceToken);
                                Intent intent_reg = new Intent(DashBoardActivity.this, LoginActivity.class);
                                startActivity(intent_reg);
                                finish();
                                // overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                            } else {
                                HandyObject.showAlert(DashBoardActivity.this, jsonObject.getString("message"));
                                if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                    HandyObject.clearpref(DashBoardActivity.this);
                                    HandyObject.deleteAllDatabase(DashBoardActivity.this);
                                    App.appInstance.stopTimer();
                                    Intent intent_reg = new Intent(DashBoardActivity.this, LoginActivity.class);
                                    startActivity(intent_reg);
                                    finish();
                                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                                }

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
        Log.e("onStopDASSHHH", "onStopDASSHHH");
        //HandyObject.putPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING, "no");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("OnPauseDASSHHH", "OnPauseDASSHHH");
        //HandyObject.putPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING, "no");
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();

            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
        Log.e("OnDestroyDASSHHH", "OnDestroyDASSHHH");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("OnRestartDASSHHH", "OnRestartDASSHHH");
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
        overridePendingTransition(R.anim.activity_lefttoright, R.anim.activity_righttoleft);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.getInstance().setConnectivityListener(this);
        Log.e("onResumeDASHBBB", "onResumeDASHBBB");
        if (onbackppress == true) {
            if (HandyObject.getPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
                HandyObject.putPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING, "yes");
            } else {
                HandyObject.putPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING, "no");
            }
            onbackppress = false;
        } else {
            if (HandyObject.getPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
                // HandyObject.showAlert(DashBoardActivity.this,HandyObject.getPrams(this, AppConstants.JOBRUNNING_CLOSETIMEDATE));

                Calendar running_cal = Calendar.getInstance();
                running_cal.setTimeInMillis(Long.parseLong(HandyObject.getPrams(this, AppConstants.JOBRUNNING_CLOSETIMEDATE)));
                Calendar now = Calendar.getInstance();

                if (now.get(Calendar.DATE) == running_cal.get(Calendar.DATE)) {
                    //HandyObject.showAlert(DashBoardActivity.this,"same day");
                } else {
                    //  HandyObject.showAlert(DashBoardActivity.this,"differnet day");
                }

                HandyObject.putPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNINGCLOSE, "yes");
            } else {
                // HandyObject.showAlert(DashBoardActivity.this,"NotRunning");
                HandyObject.putPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNINGCLOSE, "no");
            }
        }
        // replaceFragmentWithoutBack(new DashBoardFragment());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStartDASHBBB", "onStartDASHBBB");
       /* DemoSyncJob.scheduleJob();
        SyncNeeddPart.scheduleJob();
        SyncSubmitLaborPerf.scheduleJob();
        SyncNeedEstimate.scheduleJob();
        SyncLCChange.scheduleJob();
        SyncJobStatus.scheduleJob();
        SyncUploadImages.scheduleJob();
        SyncAddPart.scheduleJob();*/
    }

    // Is there any job running inside app?
    boolean isJobRunning() {
        if (HandyObject.getPrams(DashBoardActivity.this, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
    }

    // runAllOfflineTask realted to app
    private void runAllOfflineTask() {
        DemoSyncJob.scheduleJob();
        SyncNeeddPart.scheduleJob();
        SyncSubmitLaborPerf.scheduleJob();
        SyncNeedEstimate.scheduleJob();
        SyncLCChange.scheduleJob();
        SyncJobStatus.scheduleJob();
        SyncUploadImages.scheduleJob();
        SyncAddPart.scheduleJob();
        SyncJobStart.scheduleJob();
    }

    //Check for real time internet connection
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            runAllOfflineTask();
            Log.e("CONNNECTED", "connected");
        } else {
            Log.e("DISSSSCONEECTEDDDDD", "DISCONEEeeeeeeDDDDD");
        }
    }

    private String manageMinutes(int min) {
        if (min == 0) {
            return "00";
        } else {
            return String.valueOf(min);
        }
    }

    //API and local storage for sick&vacation Labor code(JOBID:111111 internal job)
    private void TaskLCChange(String laborcode) {
        int count = 0;
        String teqid = HandyObject.getPrams(DashBoardActivity.this, AppConstants.LOGINTEQ_ID);
        String jobid = "111111";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, HandyObject.getNear15MinuteLB(Integer.parseInt(manageMinutes(calendar.get(Calendar.MINUTE)))));
        String startTime = HandyObject.ParseDateJobTime(calendar.getTime()) + " " + String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + calendar.get(Calendar.MINUTE);
        Gson gson = new Gson();
        database = ParseOpenHelper.getInstance(DashBoardActivity.this).getWritableDatabase();
        LCChangeSkeleton lcs_ke = new LCChangeSkeleton();
        lcs_ke.setTech_id(teqid);
        lcs_ke.setJob_id(jobid);
        lcs_ke.setLabour_code(laborcode);
        lcs_ke.setStart_time(startTime);
        lcs_ke.setEnd_time("0");
        lcs_ke.setHours("0");
        lcs_ke.setHours_adjusted("0");
        lcs_ke.setCreated_by(teqid);
        lcs_ke.setCount(String.valueOf(count));
        ArrayList<LCChangeSkeleton> arrayList = new ArrayList<>();
        arrayList.add(lcs_ke);
        String techlog = gson.toJson(arrayList);
        final String insertedTime = HandyObject.ParseDateTimeForNotes(new Date());
        insertIntoDBLC(teqid, jobid, laborcode, startTime, "0", "0", "0", insertedTime, String.valueOf(count));
        if (HandyObject.checkInternetConnection(DashBoardActivity.this)) {
            HandyObject.showProgressDialog(DashBoardActivity.this);
            HandyObject.getApiManagerMain().submitLCdata(techlog, HandyObject.getPrams(DashBoardActivity.this, AppConstants.LOGIN_SESSIONID))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.e("responseLC", jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.getString("status").toLowerCase().equals("success")) {

                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobj = jsonArray.getJSONObject(i);

                                        //Delete related row from database
                                        database.delete(ParseOpenHelper.TABLE_LCCHANGE, ParseOpenHelper.LCCHANGEJOBID + " =? AND " + ParseOpenHelper.LCCHANGECREATEDAT + " = ?",
                                                new String[]{jobj.getString("job_id"), insertedTime});
                                    }
                                    HandyObject.showAlert(DashBoardActivity.this, jsonObject.getString("message"));
                                } else {
                                    HandyObject.showAlert(DashBoardActivity.this, jsonObject.getString("message"));
                                    if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                        HandyObject.clearpref(DashBoardActivity.this);
                                        HandyObject.deleteAllDatabase(DashBoardActivity.this);
                                        App.appInstance.stopTimer();
                                        Intent intent_reg = new Intent(DashBoardActivity.this, LoginActivity.class);
                                        startActivity(intent_reg);
                                        finish();
                                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                                    }
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
        } else {
            HandyObject.showAlert(DashBoardActivity.this, getString(R.string.fetchdata_whenonline));
            HandyObject.stopProgressDialog();
        }
    }

    private void insertIntoDBLC(String teqid, String jobid, String lc, String starttime, String endtime, String hrsworked, String hrsAdjusted,
                                String createdAt, String count) {
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.LCCHANGETECHID, teqid);
        cv.put(ParseOpenHelper.LCCHANGEJOBID, jobid);
        cv.put(ParseOpenHelper.LCCHANGELC, lc);
        cv.put(ParseOpenHelper.LCCHANGESTARTTIME, starttime);
        cv.put(ParseOpenHelper.LCCHANGEENDTIME, endtime);
        cv.put(ParseOpenHelper.LCCHANGEHHOURS, hrsworked);
        cv.put(ParseOpenHelper.LCCHANGEHHOURSADJUSTED, hrsAdjusted);
        cv.put(ParseOpenHelper.LCCHANGECREATEDBY, teqid);
        cv.put(ParseOpenHelper.LCCHANGECREATEDAT, createdAt);
        cv.put(ParseOpenHelper.LCCHANGECOUNT, count);
        long idd = database.insert(ParseOpenHelper.TABLE_LCCHANGE, null, cv);
    }

}
