package teq.development.seatech.Schedule;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import teq.development.seatech.Chat.Adapter.AdapterAutoCompleteText;
import teq.development.seatech.Dashboard.DashBoardFragment;
import teq.development.seatech.Dashboard.Skeleton.NotificationAdapter;
import teq.development.seatech.Dashboard.Skeleton.NotificationSkeleton;
import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;
import teq.development.seatech.Dashboard.VMNotifications;
import teq.development.seatech.R;
import teq.development.seatech.Schedule.Adapter.AdapterRegionFilter;
import teq.development.seatech.Schedule.Skeleton.SkeletonScheduleParentFilter;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.ScheduleParentBinding;

public class ScheduleParent extends AppCompatActivity {

    public static ScheduleParentBinding binding;
    ArrayList<SkeletonScheduleParentFilter> arrayListFilterParent;
    AdapterAutoCompleteText adapterAutoCompleteText;
    ArrayList<String> techList,jobsList,regionList;
    AdapterRegionFilter adapterRegion;
    SQLiteDatabase database;
    Calendar calendarweek;
    Cursor cursor;
    String fromMonth_SelDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.schedule_parent);
        binding.setScheduleparent(this);
        initViews();
    }

    private void initViews() {
        replaceFragmentWithoutBack(new TimeLineFragment());
        new DatabaseFetch().execute();
        binding.firstlastDate.setVisibility(View.VISIBLE);
        binding.firstlastDatecal.setVisibility(View.INVISIBLE);
        binding.firstlastDate.setText(HandyObject.getCurrentWeek_FirstDateSchedule(ScheduleParent.this));
        calendarweek = Calendar.getInstance(Locale.UK);
        fromMonth_SelDate = HandyObject.ParseDateJobTimeNew(calendarweek.getTime());

    }

    //Will Trigger when user go back from Schedule
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_lefttoright, R.anim.activity_righttoleft);
    }

    public void OnClickBack() {
        onBackPressed();
    }

    //Replace fragment Without backstack
    public void replaceFragmentWithoutBack(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.tabswitch, mFragment)
                .commit();
    }

    public void OnClickToday() {

          if(checkVisibleFragment().equalsIgnoreCase("timeline")) {
            binding.firstlastDate.setVisibility(View.VISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            String todaydate = HandyObject.getCurrentWeek_FirstDateSchedule(ScheduleParent.this);
            binding.firstlastDate.setText(todaydate);
            fromMonth_SelDate = HandyObject.ParseDateJobTimeNew(calendarweek.getTime());

            TimeLineFragment fgm = new TimeLineFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", todaydate);
            bundle.putStringArrayList("techlist",techList);
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
          } else if(checkVisibleFragment().equalsIgnoreCase("calendar")) {
              binding.firstlastDate.setVisibility(View.INVISIBLE);
              binding.firstlastDatecal.setVisibility(View.VISIBLE);

              Intent intent = new Intent("calendarcall");
              intent.putExtra("type","today");
              LocalBroadcastManager.getInstance(ScheduleParent.this).sendBroadcast(intent);

          } else if(checkVisibleFragment().equalsIgnoreCase("weekview")) {
              binding.firstlastDate.setVisibility(View.VISIBLE);
              binding.firstlastDatecal.setVisibility(View.INVISIBLE);
              String todaydate = HandyObject.getCurrentWeek_FirstDateSchedule(ScheduleParent.this);
              binding.firstlastDate.setText(todaydate);
              fromMonth_SelDate = HandyObject.ParseDateJobTimeNew(calendarweek.getTime());

              WeekViewFragment fgm = new WeekViewFragment();
              Bundle bundle = new Bundle();
              bundle.putString("weekdate", todaydate);
              fgm.setArguments(bundle);
              replaceFragmentWithoutBack(fgm);
          }
    }

    public void OnClickPrevious() {
        if(checkVisibleFragment().equalsIgnoreCase("timeline")) {
            binding.firstlastDate.setVisibility(View.VISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            String previousdate = HandyObject.getSelectedPreviousWeek_FirstLastSchedule(ScheduleParent.this, fromMonth_SelDate);
            binding.firstlastDate.setText(previousdate);

            TimeLineFragment fgm = new TimeLineFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", previousdate);
            bundle.putStringArrayList("techlist",techList);
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        } else if(checkVisibleFragment().equalsIgnoreCase("calendar")) {
            binding.firstlastDate.setVisibility(View.INVISIBLE);
            binding.firstlastDatecal.setVisibility(View.VISIBLE);

            Intent intent = new Intent("calendarcall");
            intent.putExtra("type","previous");
            LocalBroadcastManager.getInstance(ScheduleParent.this).sendBroadcast(intent);
        } else if(checkVisibleFragment().equalsIgnoreCase("weekview")) {
            binding.firstlastDate.setVisibility(View.VISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            String previousdate = HandyObject.getSelectedPreviousWeek_FirstLastSchedule(ScheduleParent.this, fromMonth_SelDate);
            binding.firstlastDate.setText(previousdate);

            WeekViewFragment fgm = new WeekViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", previousdate);
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        }
    }

    public void OnClickNext() {
        if(checkVisibleFragment().equalsIgnoreCase("timeline")) {
            binding.firstlastDate.setVisibility(View.VISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            String nextdate = HandyObject.getSelectedNextWeek_FirstLastSchedule(ScheduleParent.this, fromMonth_SelDate);
            binding.firstlastDate.setText(nextdate);

            TimeLineFragment fgm = new TimeLineFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", nextdate);
            bundle.putStringArrayList("techlist",techList);
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        } else if(checkVisibleFragment().equalsIgnoreCase("calendar")) {
            binding.firstlastDate.setVisibility(View.INVISIBLE);
            binding.firstlastDatecal.setVisibility(View.VISIBLE);

            Intent intent = new Intent("calendarcall");
            intent.putExtra("type","next");
            LocalBroadcastManager.getInstance(ScheduleParent.this).sendBroadcast(intent);
        } else if(checkVisibleFragment().equalsIgnoreCase("weekview")) {
            binding.firstlastDate.setVisibility(View.VISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            String nextdate = HandyObject.getSelectedNextWeek_FirstLastSchedule(ScheduleParent.this, fromMonth_SelDate);
            binding.firstlastDate.setText(nextdate);

            WeekViewFragment fgm = new WeekViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", nextdate);
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        }



//        Intent intent = new Intent("UpdateView");
//        intent.putExtra("weekdate", nextdate);
//        LocalBroadcastManager.getInstance(ScheduleParent.this).sendBroadcast(intent);
    }

    public void OnClickTimeLine() {

        binding.timelinetext.setBackground(getResources().getDrawable(R.drawable.timelinetab_bg));
        binding.timelinetext.setTextColor(Color.parseColor("#ffffff"));
        binding.calendartext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.calendartext.setTextColor(Color.parseColor("#000000"));
        binding.weektext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.weektext.setTextColor(Color.parseColor("#000000"));

        binding.firstlastDate.setVisibility(View.VISIBLE);
        binding.firstlastDatecal.setVisibility(View.INVISIBLE);
        String selDate = binding.firstlastDate.getText().toString();
        TimeLineFragment fgm = new TimeLineFragment();
        Bundle bundle = new Bundle();
        bundle.putString("weekdate", selDate);
        bundle.putStringArrayList("techlist",techList);
        fgm.setArguments(bundle);
        replaceFragmentWithoutBack(fgm);

     //   binding.firstlastDate.setText(HandyObject.getCurrentWeek_FirstDateSchedule(ScheduleParent.this));
      //  calendarweek = Calendar.getInstance(Locale.UK);
      //  fromMonth_SelDate = HandyObject.ParseDateJobTimeNew(calendarweek.getTime());
    }

    public void OnClickCalendar() {
        //Link for custom calendar
        // https://inducesmile.com/android/how-to-create-android-custom-calendar-view-with-events/
        binding.timelinetext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.timelinetext.setTextColor(Color.parseColor("#000000"));
        binding.calendartext.setBackground(getResources().getDrawable(R.drawable.timelinetab_bg));
        binding.calendartext.setTextColor(Color.parseColor("#ffffff"));
        binding.weektext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.weektext.setTextColor(Color.parseColor("#000000"));
        binding.firstlastDate.setVisibility(View.INVISIBLE);
        binding.firstlastDatecal.setVisibility(View.VISIBLE);
        replaceFragmentWithoutBack(new CalendarFragment());
    }

    public void onClickSearch() {
        if(checkVisibleFragment().equalsIgnoreCase("timeline")) {
            TimeLineFragment fgm = new TimeLineFragment();
            Bundle bundle = new Bundle();

            String selDate = binding.firstlastDate.getText().toString();
            bundle.putString("weekdate", selDate);
            bundle.putStringArrayList("techlist",techList);
            bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()));
            bundle.putString("jobfilter",binding.etJobticketno.getText().toString().split("-")[0]);
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        } else if(checkVisibleFragment().equalsIgnoreCase("calendar")) {

            CalendarFragment fgm = new CalendarFragment();
            Bundle bundle = new Bundle();
            bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()));
            bundle.putString("jobfilter",binding.etJobticketno.getText().toString().split("-")[0]);
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);



//
//            Intent intent = new Intent("calendarcall");
//            intent.putExtra("type","next");
//            LocalBroadcastManager.getInstance(ScheduleParent.this).sendBroadcast(intent);
        } else if(checkVisibleFragment().equalsIgnoreCase("weekview")) {
            String selDate = binding.firstlastDate.getText().toString();
            WeekViewFragment fgm = new WeekViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", selDate);
            bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()));
            bundle.putString("jobfilter",binding.etJobticketno.getText().toString().split("-")[0]);
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        }
    }

    public void onClickClear() {
        Log.e("clearrr","clearrr");
    }

    public void OnClickWeekView() {
        binding.timelinetext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.timelinetext.setTextColor(Color.parseColor("#000000"));
        binding.calendartext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.calendartext.setTextColor(Color.parseColor("#000000"));
        binding.weektext.setBackground(getResources().getDrawable(R.drawable.timelinetab_bg));
        binding.weektext.setTextColor(Color.parseColor("#ffffff"));
   //     fromMonth_SelDate = HandyObject.parseDateToMDYSchedule(binding.firstlastDate.getText().toString());
        binding.firstlastDate.setVisibility(View.VISIBLE);
        binding.firstlastDatecal.setVisibility(View.INVISIBLE);
        String selDate = binding.firstlastDate.getText().toString();
        WeekViewFragment fgm = new WeekViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("weekdate", selDate);
        fgm.setArguments(bundle);
        replaceFragmentWithoutBack(fgm);
    }

    private class DatabaseFetch extends AsyncTask<ArrayList<SkeletonScheduleParentFilter>, Void, ArrayList<SkeletonScheduleParentFilter>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // arrayListFilterParent.clear();
            arrayListFilterParent = new ArrayList<>();
            database = ParseOpenHelper.getInstance(ScheduleParent.this).getWritableDatabase();
        }

        @Override
        protected ArrayList<SkeletonScheduleParentFilter> doInBackground(ArrayList<SkeletonScheduleParentFilter>... arrayLists) {
            Gson gson = new Gson();
            cursor = database.query(ParseOpenHelper.TABLE_SCHEDULEFILTER, null, null, null, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        SkeletonScheduleParentFilter skeleton = new SkeletonScheduleParentFilter();
                        Type typeregion = new TypeToken<ArrayList<ScheduleFilterSkeleton.RegionData>>() {}.getType();
                        Type typetech = new TypeToken<ArrayList<ScheduleFilterSkeleton.TechnicianData>>() {}.getType();
                        Type typejobs = new TypeToken<ArrayList<ScheduleFilterSkeleton.JobsData>>() {}.getType();

                        String region = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEFILTER_REGIONDATA));
                        String technician = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEFILTER_TECHDATA));
                        String jobs = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEFILTER_JOBSDATA));
                        ArrayList<ScheduleFilterSkeleton.RegionData> arrayListregion = gson.fromJson(region, typeregion);
                        ArrayList<ScheduleFilterSkeleton.TechnicianData> arrayListtech = gson.fromJson(technician, typetech);
                        ArrayList<ScheduleFilterSkeleton.JobsData> arrayListJobs = gson.fromJson(jobs, typejobs);

                        skeleton.setArrayListRegion(arrayListregion);
                        skeleton.setArrayListTechnician(arrayListtech);
                        skeleton.setArrayListJobs(arrayListJobs);
                        arrayListFilterParent.add(skeleton);
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {}
            } else {}
            return arrayListFilterParent;
        }

        @Override
        protected void onPostExecute(ArrayList<SkeletonScheduleParentFilter> arrayListFilterParent) {
            super.onPostExecute(arrayListFilterParent);
            //HandyObject.showAlert(context, String.valueOf(cursor.getCount()));
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    adapterRegion = new AdapterRegionFilter(ScheduleParent.this,arrayListFilterParent.get(0).getArrayListRegion());
                    binding.regionspinner.setAdapter(adapterRegion);
                    techList = new ArrayList<>();
                    jobsList = new ArrayList<>();
                    regionList = new ArrayList<>();
                    for(int i=0;i<arrayListFilterParent.get(0).getArrayListTechnician().size();i++) {
                        techList.add(arrayListFilterParent.get(0).getArrayListTechnician().get(i).name);
                    }

                    for(int j=0;j<arrayListFilterParent.get(0).getArrayListRegion().size();j++) {
                        regionList.add(arrayListFilterParent.get(0).getArrayListRegion().get(j).region_name);
                    }

                    for(int k=0;k<arrayListFilterParent.get(0).getArrayListJobs().size();k++) {
                        jobsList.add(arrayListFilterParent.get(0).getArrayListJobs().get(k).jobid+ "-" +arrayListFilterParent.get(0).getArrayListJobs().get(k).customer_name);
                    }
                    adapterAutoCompleteText = new AdapterAutoCompleteText(ScheduleParent.this, R.layout.popup_compose, R.id.textspinner, jobsList);
                    binding.etJobticketno.setAdapter(adapterAutoCompleteText);
                } else {}
            }
        }
    }

    private String checkVisibleFragment() {
        String visible = "";
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.tabswitch);
        if(currentFragment instanceof TimeLineFragment) {
         //   HandyObject.showAlert(ScheduleParent.this,"timeline");
            visible = "timeline";
        } else if(currentFragment instanceof CalendarFragment) {
        //    HandyObject.showAlert(ScheduleParent.this,"Calendar");
            visible = "calendar";
        } else if(currentFragment instanceof WeekViewFragment) {
         //   HandyObject.showAlert(ScheduleParent.this,"WeekView");
            visible = "weekview";
        }
        return visible;
    }
}
