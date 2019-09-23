package teq.development.seatech.Schedule;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.CustomCalendar.AutoGridViewCalendar;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Schedule.Adapter.CalendarGridAdapter;
import teq.development.seatech.Schedule.Skeleton.ScheduleCalendarViewSkeleton;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class CalendarFragment extends Fragment {

    private static final String TAG = CalendarCustomView.class.getSimpleName();
    private AutoGridViewCalendar calendarGridView;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    private Cursor cursor;
    private SQLiteDatabase database;
    ArrayList<ScheduleCalendarViewSkeleton.AllData> arrayListMain,arrayListCalendar;
    private CalendarGridAdapter mAdapter;
    String visibledate;
    String currentdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_calendar, container, false);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(reciever, new IntentFilter("calendarcall"));
        initializeUILayout(rootView);

      //  setPreviousButtonClickEvent();
      //  setNextButtonClickEvent();
        //setGridCellClickEvents();
        Log.d(TAG, "I need to call this method");
        return rootView;
    }

    private void initializeUILayout(View view) {
        context = getActivity();
        arrayListMain = new ArrayList<>();
        database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        calendarGridView = (AutoGridViewCalendar) view.findViewById(R.id.calendar_grid);
        if(getArguments() != null) {
            String date = getArguments().getString("calDate");
            String seldate = HandyObject.parseDateToMM(date);
          //  cal.set(2019,3,0);
            cal.set(Integer.parseInt(seldate.split("-")[1]),Integer.parseInt(seldate.split("-")[0])-1,1);
        }
        setUpCalendarAdapter();
    }

    private BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           // binding.uploadimage.setText(intent.getStringExtra("secss"));
            String type = intent.getStringExtra("type");
            if(type.equalsIgnoreCase("today")) {
                 cal = Calendar.getInstance(Locale.ENGLISH);
              //  cal.set(Calendar.MONTH, 6);
                setUpCalendarAdapter();
            } else if(type.equalsIgnoreCase("next")){
                cal.add(Calendar.MONTH, 1);
                 setUpCalendarAdapter();
            } else if(type.equalsIgnoreCase("previous")){
                cal.add(Calendar.MONTH, -1);
                 setUpCalendarAdapter();
            }
        }
    };


    public void  setUpCalendarAdapter() {
        List<Date> dayValueInCells = new ArrayList<Date>();
        Calendar mCal = (Calendar) cal.clone();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 2;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        while (dayValueInCells.size() < MAX_CALENDAR_COLUMN) {
            dayValueInCells.add(mCal.getTime());
            mCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        Log.e(TAG, "Number of date " + dayValueInCells.size());
        visibledate = formatter.format(cal.getTime());
        currentdate = formatter.format(Calendar.getInstance(Locale.ENGLISH).getTime());
       // currentDate.setText(sDate);
        ScheduleParent.binding.firstlastDatecal.setText(visibledate);
        arrayListMain.clear();
        mAdapter = new CalendarGridAdapter(context, dayValueInCells, cal, arrayListMain);
        calendarGridView.setAdapter(mAdapter);
        Date dateF = dayValueInCells.get(0);
        Date dateL = dayValueInCells.get(dayValueInCells.size()-1);

        if (visibledate.split("\\s+")[0].equalsIgnoreCase(currentdate.split("\\s+")[0])) {
          //  if(ScheduleParent.IsSearchable == false){
                new DatabaseFetch().execute();
            //}
        }

        if(ScheduleParent.IsSearchable){
          //  HandyObject.showAlert(getActivity(),"filter");
            String regionfilter = "";
            if(ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).region_name.equalsIgnoreCase("-- All --")) {
                regionfilter = "";
            } else {
                regionfilter = ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).id;
            }
            String jobfilter = ScheduleParent.binding.etJobticketno.getText().toString().split("-")[0];
            String techids = android.text.TextUtils.join(",",ScheduleParent.techincianSel_ID);
            String custids = android.text.TextUtils.join(",",ScheduleParent.customeSel_ID);
            GetScheduledWeekData(HandyObject.getDateFromCalendar(dateF),HandyObject.getDateFromCalendar(dateL),custids,techids,regionfilter,jobfilter);
            HandyObject.showAlert(getActivity(),jobfilter);
        } else {
          //  HandyObject.showAlert(getActivity(),"Nofilter");
            GetScheduledWeekData(HandyObject.getDateFromCalendar(dateF),HandyObject.getDateFromCalendar(dateL),"","","","");
        }

    }

    // Get CalendarView Data to update events
    private void GetScheduledWeekData(String startdate,String enddate,String customerids,String techids,String regionid,String ticketid) {

        HandyObject.showProgressDialog(getActivity());
        HandyObject.getApiManagerMain().GetScheduleCalendarViewData(startdate, enddate,customerids,techids,regionid,ticketid)
                .enqueue(new Callback<ScheduleCalendarViewSkeleton>() {
                    @Override
                    public void onResponse(Call<ScheduleCalendarViewSkeleton> call, Response<ScheduleCalendarViewSkeleton> response) {
                        try {
                            ScheduleCalendarViewSkeleton skeleton = response.body();
                            String status = skeleton.status;

                            if (status.equalsIgnoreCase("success")) {
                                arrayListMain.clear();
                                arrayListMain.addAll(skeleton.data);
                          //      mAdapter.notifyDataSetChanged();
                                if (visibledate.split("\\s+")[0].equalsIgnoreCase(currentdate.split("\\s+")[0])) {
                                    insertDB_CalendarData(arrayListMain);
                                }

//                                if(getArguments() != null && getArguments().containsKey("regionfilter")) {
//                                    HandyObject.showAlert(getActivity(),"filter");
//                                    Log.e("regionfilter",getArguments().getString("regionfilter"));
//                                    Log.e("jobfilter",getArguments().getString("jobfilter"));
//                                    for(int i=0;i<arrayListMain.size();i++) {
//                                        for(int k=0; k<arrayListMain.get(i).scheduled.size();k++) {
//                                            if (arrayListMain.get(i).scheduled.get(k).region_name.toLowerCase().contains(getArguments().getString("regionfilter").toLowerCase()) && arrayListMain.get(i).scheduled.get(k).jobid.toLowerCase().contains(getArguments().getString("jobfilter"))) {
//                                                arrayListMain.get(i).scheduled.set(k,arrayListMain.get(i).scheduled.get(k));
//                                            } else {
//                                                arrayListMain.get(i).scheduled.set(k,new ScheduleCalendarViewSkeleton.Scheduled());
//                                             //   arrayList.get(i).scheduled.remove(k);
//                                            }
//                                        }
//
//                                        for(int m=arrayListMain.get(i).scheduled.size()-1; m >= 0;m--) {
//                                            if(arrayListMain.get(i).scheduled.get(m).jobid != null && !arrayListMain.get(i).scheduled.get(m).jobid.isEmpty()) {
//
//                                            } else {
//                                                arrayListMain.get(i).scheduled.remove(m);
//                                            }
//                                        }
//                                    }
//                                }
//                                else if(ScheduleParent.IsSearchable){
//                                    String regionfilter = ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition());
//                                    String jobfilter = ScheduleParent.binding.etJobticketno.getText().toString().split("-")[0];
//                                    for(int i=0;i<arrayListMain.size();i++) {
//                                        for(int k=0; k<arrayListMain.get(i).scheduled.size();k++) {
//                                            if(arrayListMain.get(i).scheduled.get(k).tech_name.toLowerCase().contains("seatech marine electronics")) {
//                                                arrayListMain.get(i).scheduled.set(k,arrayListMain.get(i).scheduled.get(k));
//                                            }
//
//                                            if (arrayListMain.get(i).scheduled.get(k).region_name.toLowerCase().contains(regionfilter.toLowerCase()) && arrayListMain.get(i).scheduled.get(k).jobid.toLowerCase().contains(jobfilter)) {
//                                                arrayListMain.get(i).scheduled.set(k,arrayListMain.get(i).scheduled.get(k));
//                                            }
////                                            if(arrayListMain.get(i).scheduled.get(k).tech_name.toLowerCase().contains("seatech marine electronics")){
////                                                arrayListMain.get(i).scheduled.set(k,arrayListMain.get(i).scheduled.get(k));
////                                            }
//                                            else {
//                                                arrayListMain.get(i).scheduled.set(k,new ScheduleCalendarViewSkeleton.Scheduled());
//                                                //   arrayList.get(i).scheduled.remove(k);
//                                            }
//                                        }
//
//                                        for(int m=arrayListMain.get(i).scheduled.size()-1; m >= 0;m--) {
//                                            if(arrayListMain.get(i).scheduled.get(m).jobid != null && !arrayListMain.get(i).scheduled.get(m).jobid.isEmpty()) {
//
//                                            } else {
//                                                arrayListMain.get(i).scheduled.remove(m);
//                                            }
//                                        }
//                                    }
//                                }

//                                else {
//                                    HandyObject.showAlert(getActivity(),"Nofilter");
//                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                if (status.equalsIgnoreCase("logout")) {
                                    HandyObject.clearpref(context);
                                    HandyObject.deleteAllDatabase(context);
                                    App.appInstance.stopTimer();
                                    Intent intent_reg = new Intent(context, LoginActivity.class);
                                    ((Activity) context).startActivity(intent_reg);
                                    ((Activity) context).finish();
                                    ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            HandyObject.stopProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ScheduleCalendarViewSkeleton> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        HandyObject.stopProgressDialog();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(reciever);
    }

    private void insertDB_CalendarData(ArrayList<ScheduleCalendarViewSkeleton.AllData> arrayList) {
        Gson gson = new Gson();
        database.delete(ParseOpenHelper.TABLE_SCHEDULECALENDARDATA, null, null);
        String ScheduledList = gson.toJson(arrayList);
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.SCHEDULECALENDARDATA, ScheduledList);
        long idd = database.insert(ParseOpenHelper.TABLE_SCHEDULECALENDARDATA, null, cv);
    }

    private class DatabaseFetch extends AsyncTask<ArrayList<ScheduleCalendarViewSkeleton.AllData>, Void, ArrayList<ScheduleCalendarViewSkeleton.AllData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // arrayListFilterParent.clear();
            arrayListCalendar = new ArrayList<>();
            //  database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        }

        @Override
        protected ArrayList<ScheduleCalendarViewSkeleton.AllData> doInBackground(ArrayList<ScheduleCalendarViewSkeleton.AllData>... arrayLists) {
            Gson gson = new Gson();
            cursor = database.query(ParseOpenHelper.TABLE_SCHEDULECALENDARDATA, null, null, null, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        Type typetech = new TypeToken<ArrayList<ScheduleCalendarViewSkeleton.AllData>>() {
                        }.getType();
                        String data = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULECALENDARDATA));
                        ArrayList<ScheduleCalendarViewSkeleton.AllData> arrayList = gson.fromJson(data, typetech);
                        arrayListCalendar.addAll(arrayList);
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {
                }
            } else {
            }
            return arrayListCalendar;
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleCalendarViewSkeleton.AllData> arrayList) {
            super.onPostExecute(arrayList);
            //HandyObject.showAlert(context, String.valueOf(cursor.getCount()));
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    arrayListMain.addAll(arrayList);
//                    if(getArguments() != null && getArguments().containsKey("regionfilter")) {
//                        for(int i=0;i<arrayListMain.size();i++) {
//                            for(int k=0; k<arrayListMain.get(i).scheduled.size();k++) {
//                                if (arrayListMain.get(i).scheduled.get(k).region_name.toLowerCase().contains(getArguments().getString("regionfilter").toLowerCase()) && arrayListMain.get(i).scheduled.get(k).jobid.toLowerCase().contains(getArguments().getString("jobfilter"))) {
//                                    arrayListMain.get(i).scheduled.set(k,arrayListMain.get(i).scheduled.get(k));
//                                } else {
//                                    arrayListMain.get(i).scheduled.set(k,new ScheduleCalendarViewSkeleton.Scheduled());
//                                }
//                            }
//                            for(int m=arrayListMain.get(i).scheduled.size()-1; m >= 0;m--) {
//                                if(arrayListMain.get(i).scheduled.get(m).jobid != null && !arrayListMain.get(i).scheduled.get(m).jobid.isEmpty()) {
//
//                                } else {
//                                    arrayListMain.get(i).scheduled.remove(m);
//                                }
//                            }
//                        }
//                    } else if(ScheduleParent.IsSearchable){
//                        String regionfilter = ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition());
//                        String jobfilter = ScheduleParent.binding.etJobticketno.getText().toString().split("-")[0];
//                        for(int i=0;i<arrayListMain.size();i++) {
//                            for(int k=0; k<arrayListMain.get(i).scheduled.size();k++) {
//                                if (arrayListMain.get(i).scheduled.get(k).region_name.toLowerCase().contains(regionfilter.toLowerCase()) && arrayListMain.get(i).scheduled.get(k).jobid.toLowerCase().contains(jobfilter)) {
//                                    arrayListMain.get(i).scheduled.set(k,arrayListMain.get(i).scheduled.get(k));
//                                } else {
//                                    arrayListMain.get(i).scheduled.set(k,new ScheduleCalendarViewSkeleton.Scheduled());
//                                    //   arrayList.get(i).scheduled.remove(k);
//                                }
//                            }
//
//                            for(int m=arrayListMain.get(i).scheduled.size()-1; m >= 0;m--) {
//                                if(arrayListMain.get(i).scheduled.get(m).jobid != null && !arrayListMain.get(i).scheduled.get(m).jobid.isEmpty()) {
//
//                                } else {
//                                    arrayListMain.get(i).scheduled.remove(m);
//                                }
//                            }
//                        }
//                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                }
            }
        }
    }
}
