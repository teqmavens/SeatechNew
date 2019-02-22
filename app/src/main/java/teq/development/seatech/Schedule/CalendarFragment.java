package teq.development.seatech.Schedule;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class CalendarFragment extends Fragment {

    private static final String TAG = CalendarCustomView.class.getSimpleName();
    private ImageView previousButton, nextButton;
    private TextView currentDate;
    private AutoGridViewCalendar calendarGridView;
    private Button addEventButton;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private int month, year;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    ArrayList<ScheduleCalendarViewSkeleton.AllData> arrayList;
    private CalendarGridAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_calendar, container, false);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(reciever, new IntentFilter("calendarcall"));
        initializeUILayout(rootView);
        setUpCalendarAdapter();
      //  setPreviousButtonClickEvent();
      //  setNextButtonClickEvent();
        //setGridCellClickEvents();
        Log.d(TAG, "I need to call this method");
        return rootView;
    }

    private void initializeUILayout(View view) {
    //   LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        //    View view = inflater.inflate(R.layout.calendar_layout, this);

        context = getActivity();
        arrayList = new ArrayList<>();
     //   previousButton = (ImageView) view.findViewById(R.id.previous_month);
     //   nextButton = (ImageView) view.findViewById(R.id.next_month);
     //   currentDate = (TextView) view.findViewById(R.id.display_current_date);
        calendarGridView = (AutoGridViewCalendar) view.findViewById(R.id.calendar_grid);
    }

//    private void setPreviousButtonClickEvent() {
//        previousButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cal.add(Calendar.MONTH, -1);
//                //setUpCalendarAdapter();
//            }
//        });
//    }

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


//    private void setNextButtonClickEvent() {
//        nextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cal.add(Calendar.MONTH, 1);
//               // setUpCalendarAdapter();
//            }
//        });
//    }

    public void  setUpCalendarAdapter() {
        List<Date> dayValueInCells = new ArrayList<Date>();
        //  mQuery = new DatabaseQuery(context);
        //     List<EventObjects> mEvents = mQuery.getAllFutureEvents();
        Calendar mCal = (Calendar) cal.clone();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 2;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        while (dayValueInCells.size() < MAX_CALENDAR_COLUMN) {
            dayValueInCells.add(mCal.getTime());
            mCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        Log.e(TAG, "Number of date " + dayValueInCells.size());
        String sDate = formatter.format(cal.getTime());
       // currentDate.setText(sDate);
        ScheduleParent.binding.firstlastDatecal.setText(sDate);
        arrayList.clear();
        //  mAdapter = new CalendarGridAdapter(context, dayValueInCells, cal, mEvents);
        mAdapter = new CalendarGridAdapter(context, dayValueInCells, cal, arrayList);
        calendarGridView.setAdapter(mAdapter);
        Date dateF = dayValueInCells.get(0);
        Date dateL = dayValueInCells.get(dayValueInCells.size()-1);
      //  HandyObject.showAlert(getActivity(),HandyObject.getDateFromCalendar(dateF)+"------------"+HandyObject.getDateFromCalendar(dateL));

            GetScheduledWeekData(HandyObject.getDateFromCalendar(dateF),HandyObject.getDateFromCalendar(dateL));


    }

    // Get CalendarView Data to update events
    private void GetScheduledWeekData(String startdate,String enddate) {
        //HandyObject.showProgressDialog(this);

//        HandyObject.showAlert(getActivity(),HandyObject.parseDateToYMDSchedule(seldate.split("-")[0])+"_______"+HandyObject.parseDateToYMDSchedule(seldate.split("-")[1]));

        HandyObject.getApiManagerMain().GetScheduleCalendarViewData(startdate, enddate)
                .enqueue(new Callback<ScheduleCalendarViewSkeleton>() {
                    @Override
                    public void onResponse(Call<ScheduleCalendarViewSkeleton> call, Response<ScheduleCalendarViewSkeleton> response) {
                        try {
                            ScheduleCalendarViewSkeleton skeleton = response.body();
                            String status = skeleton.status;

                            if (status.equalsIgnoreCase("success")) {
                                arrayList.addAll(skeleton.data);
                          //      mAdapter.notifyDataSetChanged();

                                if(getArguments() != null && getArguments().containsKey("regionfilter")) {
                                    HandyObject.showAlert(getActivity(),"filter");
                                    Log.e("regionfilter",getArguments().getString("regionfilter"));
                                    Log.e("jobfilter",getArguments().getString("jobfilter"));
                                    for(int i=0;i<arrayList.size();i++) {
                                        for(int k=0; k<arrayList.get(i).scheduled.size();k++) {
                                            if (arrayList.get(i).scheduled.get(k).region_name.toLowerCase().contains(getArguments().getString("regionfilter").toLowerCase()) && arrayList.get(i).scheduled.get(k).jobid.toLowerCase().contains(getArguments().getString("jobfilter"))) {
                                                arrayList.get(i).scheduled.set(k,arrayList.get(i).scheduled.get(k));
                                            } else {
                                                arrayList.get(i).scheduled.set(k,new ScheduleCalendarViewSkeleton.Scheduled());
                                             //   arrayList.get(i).scheduled.remove(k);
                                            }
                                        }


                                        for(int m=arrayList.get(i).scheduled.size()-1; m >= 0;m--) {
                                            if(arrayList.get(i).scheduled.get(m).jobid != null && !arrayList.get(i).scheduled.get(m).jobid.isEmpty()) {

                                            } else {
                                                arrayList.get(i).scheduled.remove(m);
                                            }
                                        }
                                    }
                                } else {
                                    HandyObject.showAlert(getActivity(),"Nofilter");
                                }
                                Log.e("ddsds",String.valueOf(arrayList.size()));
                                mAdapter.notifyDataSetChanged();
                            } else {
                                //  HandyObject.showAlert(context, status);
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
}
