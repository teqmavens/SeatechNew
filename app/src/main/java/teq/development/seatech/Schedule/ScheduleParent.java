package teq.development.seatech.Schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import teq.development.seatech.Chat.Adapter.AdapterAutoCompleteText;
import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;
import teq.development.seatech.PickUpJobs.Adapter.AdapterTechnician;
import teq.development.seatech.PickUpJobs.TechinicianSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.Schedule.Adapter.AdapterETCustomer;
import teq.development.seatech.Schedule.Adapter.AdapterRegionFilter;
import teq.development.seatech.Schedule.Skeleton.SkeletonScheduleParentFilter;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.ScheduleParentBinding;

public class ScheduleParent extends AppCompatActivity {

    public static ScheduleParentBinding binding;
    ArrayList<SkeletonScheduleParentFilter> arrayListFilterParent;
    public static ArrayList<String> techincianSel_ID, customeSel_ID;
    AdapterAutoCompleteText adapterAutoCompleteText;
    AdapterETCustomer adapterETCustomer;
    AdapterTechnician adapterETTechnician;
    ArrayList<String> techList, jobsList;
    public static ArrayList<ScheduleFilterSkeleton.RegionData> regionList;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    AdapterRegionFilter adapterRegion;
    SQLiteDatabase database;
    Calendar calendarweek, calendarDay, calendarCal;
    Cursor cursor;
    ArrayList<TechinicianSkeleton> arrayListTech, arrayListCustomer;
    String fromMonth_SelDate;
    public static boolean IsSearchable = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.schedule_parent);
        binding.setScheduleparent(this);
        LocalBroadcastManager.getInstance(ScheduleParent.this).registerReceiver(refreshParent,
                new IntentFilter("refresh"));
        initViews();
    }

    private BroadcastReceiver refreshParent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            initViews();
        }
    };

    private void initViews() {
        techincianSel_ID = new ArrayList<>();
        customeSel_ID = new ArrayList<>();
        techList = new ArrayList<>();
        jobsList = new ArrayList<>();
        arrayListTech = new ArrayList<>();
        arrayListCustomer = new ArrayList<>();
        regionList = new ArrayList<>();
        replaceFragmentWithoutBack(new TimeLineFragment());
        new DatabaseFetch().execute();
        binding.firstlastDate.setVisibility(View.VISIBLE);
        binding.firstlastDatecal.setVisibility(View.INVISIBLE);
        binding.firstlastDateDay.setVisibility(View.INVISIBLE);
        binding.firstlastDate.setText(HandyObject.getCurrentWeek_FirstDateSchedule(ScheduleParent.this));
        calendarweek = Calendar.getInstance(Locale.UK);
        calendarDay = Calendar.getInstance(Locale.UK);
        calendarCal = Calendar.getInstance(Locale.UK);
        fromMonth_SelDate = HandyObject.ParseDateJobTimeNew(calendarweek.getTime());
        String currentDate = HandyObject.ParseDateSchedule(calendarDay.getTime());
        binding.firstlastDateDay.setText(currentDate);
        binding.firstlastDatecal.setText(formatter.format(calendarCal.getTime()));
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

    //Replace fragment Without backstack
    public void replaceFragmentWithBack(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.tabswitch, mFragment).addToBackStack(null)
                .commit();
    }

    public void OnClickToday() {
        if (checkVisibleFragment().equalsIgnoreCase("timeline")) {
            binding.firstlastDate.setVisibility(View.VISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            binding.firstlastDateDay.setVisibility(View.INVISIBLE);

            String todaydate = HandyObject.getCurrentWeek_FirstDateSchedule(ScheduleParent.this);
            binding.firstlastDate.setText(todaydate);
            fromMonth_SelDate = HandyObject.ParseDateJobTimeNew(calendarweek.getTime());

            TimeLineFragment fgm = new TimeLineFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", todaydate);
            bundle.putStringArrayList("techlist", techList);
            if (IsSearchable) {
                bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
                bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
            }
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        } else if (checkVisibleFragment().equalsIgnoreCase("calendar")) {
            binding.firstlastDate.setVisibility(View.INVISIBLE);
            binding.firstlastDatecal.setVisibility(View.VISIBLE);
            binding.firstlastDateDay.setVisibility(View.INVISIBLE);

            Intent intent = new Intent("calendarcall");
            intent.putExtra("type", "today");

            LocalBroadcastManager.getInstance(ScheduleParent.this).sendBroadcast(intent);

        } else if (checkVisibleFragment().equalsIgnoreCase("weekview")) {
            binding.firstlastDate.setVisibility(View.VISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            binding.firstlastDateDay.setVisibility(View.INVISIBLE);
            String todaydate = HandyObject.getCurrentWeek_FirstDateSchedule(ScheduleParent.this);
            binding.firstlastDate.setText(todaydate);
            fromMonth_SelDate = HandyObject.ParseDateJobTimeNew(calendarweek.getTime());

            WeekViewFragment fgm = new WeekViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", todaydate);
//            if (IsSearchable) {
//                bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
//                bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
//            }
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        } else if (checkVisibleFragment().equalsIgnoreCase("dayview")) {
            binding.firstlastDate.setVisibility(View.INVISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            binding.firstlastDateDay.setVisibility(View.VISIBLE);
            calendarDay = Calendar.getInstance(Locale.UK);
            String currentDate = HandyObject.ParseDateSchedule(calendarDay.getTime());
            binding.firstlastDateDay.setText(currentDate);

            DayViewFragment fgm = new DayViewFragment();
            Bundle bundle = new Bundle();
            String sendDate = HandyObject.parseDateToYMDSchedule(currentDate);
            bundle.putString("daydate", sendDate);
//            if (IsSearchable) {
//                bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
//                bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
//            }
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        }
    }

    public void OnClickPrevious() {
        if (checkVisibleFragment().equalsIgnoreCase("timeline")) {
            binding.firstlastDate.setVisibility(View.VISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            binding.firstlastDateDay.setVisibility(View.INVISIBLE);
            String previousdate = HandyObject.getSelectedPreviousWeek_FirstLastSchedule(ScheduleParent.this, fromMonth_SelDate);
            binding.firstlastDate.setText(previousdate);

            TimeLineFragment fgm = new TimeLineFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", previousdate);
            bundle.putStringArrayList("techlist", techList);
            if (IsSearchable) {
                bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
                bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
            }
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        } else if (checkVisibleFragment().equalsIgnoreCase("calendar")) {
            binding.firstlastDate.setVisibility(View.INVISIBLE);
            binding.firstlastDatecal.setVisibility(View.VISIBLE);
            binding.firstlastDateDay.setVisibility(View.INVISIBLE);

            Intent intent = new Intent("calendarcall");
            intent.putExtra("type", "previous");
            //intent.putExtra("calDate",binding.firstlastDatecal.getText().toString());
            LocalBroadcastManager.getInstance(ScheduleParent.this).sendBroadcast(intent);
        } else if (checkVisibleFragment().equalsIgnoreCase("weekview")) {
            binding.firstlastDate.setVisibility(View.VISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            binding.firstlastDateDay.setVisibility(View.INVISIBLE);

            String previousdate = HandyObject.getSelectedPreviousWeek_FirstLastSchedule(ScheduleParent.this, fromMonth_SelDate);
            binding.firstlastDate.setText(previousdate);

            WeekViewFragment fgm = new WeekViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", previousdate);
//            if (IsSearchable) {
//                bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
//                bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
//            }
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        } else if (checkVisibleFragment().equalsIgnoreCase("dayview")) {
            binding.firstlastDate.setVisibility(View.INVISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            binding.firstlastDateDay.setVisibility(View.VISIBLE);

            calendarDay.add(Calendar.HOUR_OF_DAY, -24);
            String currentDate = HandyObject.ParseDateSchedule(calendarDay.getTime());
            binding.firstlastDateDay.setText(currentDate);

            DayViewFragment fgm = new DayViewFragment();
            Bundle bundle = new Bundle();
            String sendDate = HandyObject.parseDateToYMDSchedule(currentDate);
            bundle.putString("daydate", sendDate);
            if (IsSearchable) {
                bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
                bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
            }
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        }
    }

    public void OnClickNext() {
        if (checkVisibleFragment().equalsIgnoreCase("timeline")) {
            binding.firstlastDate.setVisibility(View.VISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            binding.firstlastDateDay.setVisibility(View.INVISIBLE);
            String nextdate = HandyObject.getSelectedNextWeek_FirstLastSchedule(ScheduleParent.this, fromMonth_SelDate);
            binding.firstlastDate.setText(nextdate);

            TimeLineFragment fgm = new TimeLineFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", nextdate);
            bundle.putStringArrayList("techlist", techList);
            if (IsSearchable) {
                bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
                bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
            }
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        } else if (checkVisibleFragment().equalsIgnoreCase("calendar")) {
            binding.firstlastDate.setVisibility(View.INVISIBLE);
            binding.firstlastDatecal.setVisibility(View.VISIBLE);
            binding.firstlastDateDay.setVisibility(View.INVISIBLE);

            Intent intent = new Intent("calendarcall");
            intent.putExtra("type", "next");
            LocalBroadcastManager.getInstance(ScheduleParent.this).sendBroadcast(intent);
        } else if (checkVisibleFragment().equalsIgnoreCase("weekview")) {
            binding.firstlastDate.setVisibility(View.VISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            binding.firstlastDateDay.setVisibility(View.INVISIBLE);

            String nextdate = HandyObject.getSelectedNextWeek_FirstLastSchedule(ScheduleParent.this, fromMonth_SelDate);
            binding.firstlastDate.setText(nextdate);

            WeekViewFragment fgm = new WeekViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weekdate", nextdate);
//            if (IsSearchable) {
//                bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
//                bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
//            }
            fgm.setArguments(bundle);
            replaceFragmentWithoutBack(fgm);
        } else if (checkVisibleFragment().equalsIgnoreCase("dayview")) {
            binding.firstlastDate.setVisibility(View.INVISIBLE);
            binding.firstlastDatecal.setVisibility(View.INVISIBLE);
            binding.firstlastDateDay.setVisibility(View.VISIBLE);

            calendarDay.add(Calendar.HOUR_OF_DAY, 24);
            String currentDate = HandyObject.ParseDateSchedule(calendarDay.getTime());
            binding.firstlastDateDay.setText(currentDate);

            DayViewFragment fgm = new DayViewFragment();
            Bundle bundle = new Bundle();
            String sendDate = HandyObject.parseDateToYMDSchedule(currentDate);
            bundle.putString("daydate", sendDate);
            if (IsSearchable) {
                bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
                bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
            }
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
        binding.daytext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.daytext.setTextColor(Color.parseColor("#000000"));

        binding.firstlastDate.setVisibility(View.VISIBLE);
        binding.firstlastDatecal.setVisibility(View.INVISIBLE);
        binding.firstlastDateDay.setVisibility(View.INVISIBLE);

        String selDate = binding.firstlastDate.getText().toString();
        TimeLineFragment fgm = new TimeLineFragment();
        Bundle bundle = new Bundle();
        bundle.putString("weekdate", selDate);
        bundle.putStringArrayList("techlist", techList);
        if (IsSearchable) {
            bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
            bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
        }
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
        binding.daytext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.daytext.setTextColor(Color.parseColor("#000000"));

        binding.firstlastDate.setVisibility(View.INVISIBLE);
        binding.firstlastDatecal.setVisibility(View.VISIBLE);
        binding.firstlastDateDay.setVisibility(View.INVISIBLE);

        CalendarFragment fgm = new CalendarFragment();
//        if (IsSearchable) {
//            Bundle bundle = new Bundle();
//            bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
//            bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
//            fgm.setArguments(bundle);
//        }
        Bundle bundle = new Bundle();
        bundle.putString("calDate", binding.firstlastDatecal.getText().toString());
        fgm.setArguments(bundle);
        replaceFragmentWithoutBack(fgm);
    }

    public void onClickSearch() {
        techincianSel_ID.clear();
        customeSel_ID.clear();
        for (int i = 0; i < arrayListTech.size(); i++) {
            if (arrayListTech.get(i).isStatus()) {
                techincianSel_ID.add(arrayListTech.get(i).getId());
            }
        }

        for (int k = 0; k < arrayListCustomer.size(); k++) {
            if (arrayListCustomer.get(k).isStatus()) {
                customeSel_ID.add(arrayListCustomer.get(k).getId());
            }
        }

        if (HandyObject.checkInternetConnection(ScheduleParent.this)) {
            if (techincianSel_ID.size() == 0 && customeSel_ID.size() == 0 && regionList.get(binding.regionspinner.getSelectedItemPosition()).id.equalsIgnoreCase("") && binding.etJobticketno.getText().toString().length() == 0) {
                HandyObject.showAlert(ScheduleParent.this, getString(R.string.fieldblankfilter));
            } else {
                binding.recyclerTechname.setVisibility(View.GONE);
                binding.recyclerCustname.setVisibility(View.GONE);
                if (techincianSel_ID.size() > 0) {
                    binding.etFilterTechnician.setText("Selected");
                } else {
                    binding.etFilterTechnician.setText("");
                }

                if (customeSel_ID.size() > 0) {
                    binding.etFiltercustomer.setText("Selected");
                } else {
                    binding.etFiltercustomer.setText("");
                }

                IsSearchable = true;
                if (checkVisibleFragment().equalsIgnoreCase("timeline")) {
                    TimeLineFragment fgm = new TimeLineFragment();
                    Bundle bundle = new Bundle();

                    String selDate = binding.firstlastDate.getText().toString();
                    bundle.putString("weekdate", selDate);
                    bundle.putStringArrayList("techlist", techList);
                    bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
                    bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
                    fgm.setArguments(bundle);
                    replaceFragmentWithoutBack(fgm);
                } else if (checkVisibleFragment().equalsIgnoreCase("calendar")) {

                    CalendarFragment fgm = new CalendarFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("calDate", binding.firstlastDatecal.getText().toString());
                    fgm.setArguments(bundle);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
//                    bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
//                    fgm.setArguments(bundle);
                    replaceFragmentWithoutBack(fgm);
                } else if (checkVisibleFragment().equalsIgnoreCase("weekview")) {
                    String selDate = binding.firstlastDate.getText().toString();
                    WeekViewFragment fgm = new WeekViewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("weekdate", selDate);
//                    bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
//                    bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
                    fgm.setArguments(bundle);
                    replaceFragmentWithoutBack(fgm);
                } else if (checkVisibleFragment().equalsIgnoreCase("dayview")) {
                    String currentDate = binding.firstlastDateDay.getText().toString();
                    binding.firstlastDateDay.setText(currentDate);
                    DayViewFragment fgm = new DayViewFragment();
                    Bundle bundle = new Bundle();
                    String sendDate = HandyObject.parseDateToYMDSchedule(currentDate);
                    bundle.putString("daydate", sendDate);
                    fgm.setArguments(bundle);
                    replaceFragmentWithoutBack(fgm);
                }
            }
        } else {
            HandyObject.showAlert(ScheduleParent.this, getString(R.string.withoutinter_nofilter));
        }

    }

    public void onClickClear() {
        if (HandyObject.checkInternetConnection(ScheduleParent.this)) {
            techincianSel_ID.clear();
            customeSel_ID.clear();
            binding.etFilterTechnician.setText("");
            binding.etFiltercustomer.setText("");
            binding.etJobticketno.setText("");
            adapterRegion = new AdapterRegionFilter(ScheduleParent.this, regionList);
            binding.regionspinner.setAdapter(adapterRegion);

            for (int i = 0; i < arrayListTech.size(); i++) {
                arrayListTech.get(i).setStatus(false);
            }

            for (int i = 0; i < arrayListCustomer.size(); i++) {
                arrayListCustomer.get(i).setStatus(false);
            }

            binding.recyclerTechname.setVisibility(View.GONE);
            binding.recyclerCustname.setVisibility(View.GONE);
            IsSearchable = false;
            if (checkVisibleFragment().equalsIgnoreCase("timeline")) {
                TimeLineFragment fgm = new TimeLineFragment();
                Bundle bundle = new Bundle();
                String selDate = binding.firstlastDate.getText().toString();
                bundle.putString("weekdate", selDate);
                bundle.putStringArrayList("techlist", techList);
                fgm.setArguments(bundle);
                replaceFragmentWithoutBack(fgm);
            } else if (checkVisibleFragment().equalsIgnoreCase("calendar")) {
                CalendarFragment fgm = new CalendarFragment();
                Bundle bundle = new Bundle();
                bundle.putString("calDate", binding.firstlastDatecal.getText().toString());
                fgm.setArguments(bundle);
                replaceFragmentWithoutBack(fgm);
            } else if (checkVisibleFragment().equalsIgnoreCase("weekview")) {
                String selDate = binding.firstlastDate.getText().toString();
                WeekViewFragment fgm = new WeekViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("weekdate", selDate);
                fgm.setArguments(bundle);
                replaceFragmentWithoutBack(fgm);
            } else if (checkVisibleFragment().equalsIgnoreCase("dayview")) {
                String currentDate = binding.firstlastDateDay.getText().toString();
                binding.firstlastDateDay.setText(currentDate);
                DayViewFragment fgm = new DayViewFragment();
                Bundle bundle = new Bundle();
                String sendDate = HandyObject.parseDateToYMDSchedule(currentDate);
                bundle.putString("daydate", sendDate);
                fgm.setArguments(bundle);
                replaceFragmentWithoutBack(fgm);
            }
        } else {
            HandyObject.showAlert(ScheduleParent.this, getString(R.string.check_internet_connection));
        }
    }

    public void OnClickWeekView() {
        binding.timelinetext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.timelinetext.setTextColor(Color.parseColor("#000000"));
        binding.calendartext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.calendartext.setTextColor(Color.parseColor("#000000"));
        binding.weektext.setBackground(getResources().getDrawable(R.drawable.timelinetab_bg));
        binding.weektext.setTextColor(Color.parseColor("#ffffff"));
        binding.daytext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.daytext.setTextColor(Color.parseColor("#000000"));
        //     fromMonth_SelDate = HandyObject.parseDateToMDYSchedule(binding.firstlastDate.getText().toString());
        binding.firstlastDate.setVisibility(View.VISIBLE);
        binding.firstlastDatecal.setVisibility(View.INVISIBLE);
        binding.firstlastDateDay.setVisibility(View.INVISIBLE);
        String selDate = binding.firstlastDate.getText().toString();
        WeekViewFragment fgm = new WeekViewFragment();
        Bundle bundle = new Bundle();
//        if (IsSearchable) {
//            bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
//            bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
//        }
        bundle.putString("weekdate", selDate);
        fgm.setArguments(bundle);
        replaceFragmentWithoutBack(fgm);
    }

    public void OnClickDayView() {
        binding.timelinetext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.timelinetext.setTextColor(Color.parseColor("#000000"));
        binding.calendartext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.calendartext.setTextColor(Color.parseColor("#000000"));
        binding.weektext.setBackground(getResources().getDrawable(R.drawable.dashboardlinebg));
        binding.weektext.setTextColor(Color.parseColor("#000000"));
        binding.daytext.setBackground(getResources().getDrawable(R.drawable.timelinetab_bg));
        binding.daytext.setTextColor(Color.parseColor("#ffffff"));
        binding.firstlastDate.setVisibility(View.INVISIBLE);
        binding.firstlastDatecal.setVisibility(View.INVISIBLE);
        binding.firstlastDateDay.setVisibility(View.VISIBLE);

        String currentDate = binding.firstlastDateDay.getText().toString();
        binding.firstlastDateDay.setText(currentDate);
        DayViewFragment fgm = new DayViewFragment();
        Bundle bundle = new Bundle();
        String sendDate = HandyObject.parseDateToYMDSchedule(currentDate);
        bundle.putString("daydate", sendDate);
//        if (IsSearchable) {
//            bundle.putString("regionfilter", regionList.get(binding.regionspinner.getSelectedItemPosition()).id);
//            bundle.putString("jobfilter", binding.etJobticketno.getText().toString().split("-")[0]);
//        }
        fgm.setArguments(bundle);
        replaceFragmentWithoutBack(fgm);
    }

    public void OnClicketTechnician() {
        for (int i = 0; i < arrayListTech.size(); i++) {
            for (int k = 0; k < techincianSel_ID.size(); k++) {
                if (techincianSel_ID.get(k).equalsIgnoreCase(arrayListTech.get(i).getId())) {
                    arrayListTech.get(i).setStatus(true);
                }
            }
        }
        binding.recyclerTechname.setVisibility(View.VISIBLE);
        LinearLayoutManager lLManagerTech = new LinearLayoutManager(ScheduleParent.this);
        lLManagerTech.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerTechname.setLayoutManager(lLManagerTech);
        adapterETTechnician = new AdapterTechnician(ScheduleParent.this, arrayListTech);
        binding.recyclerTechname.setAdapter(adapterETTechnician);
    }

    public void OnClicketCustomer() {
        binding.recyclerCustname.setVisibility(View.VISIBLE);
        LinearLayoutManager lLManagerTech = new LinearLayoutManager(ScheduleParent.this);
        lLManagerTech.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerCustname.setLayoutManager(lLManagerTech);
        adapterETCustomer = new AdapterETCustomer(ScheduleParent.this, arrayListCustomer);
        //adapterETTechnician = new AdapterTechnician(ScheduleParent.this, arrayListCustomer);
        binding.recyclerCustname.setAdapter(adapterETCustomer);
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
                        Type typeregion = new TypeToken<ArrayList<ScheduleFilterSkeleton.RegionData>>() {
                        }.getType();
                        Type typetech = new TypeToken<ArrayList<ScheduleFilterSkeleton.TechnicianData>>() {
                        }.getType();
                        Type typejobs = new TypeToken<ArrayList<ScheduleFilterSkeleton.JobsData>>() {
                        }.getType();
                        Type typeCustomer = new TypeToken<ArrayList<ScheduleFilterSkeleton.CustomerData>>() {
                        }.getType();

                        String region = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEFILTER_REGIONDATA));
                        String technician = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEFILTER_TECHDATA));
                        String jobs = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEFILTER_JOBSDATA));
                        String customer = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEFILTER_CUSTOMERDATA));
                        ArrayList<ScheduleFilterSkeleton.RegionData> arrayListregion = gson.fromJson(region, typeregion);
                        ArrayList<ScheduleFilterSkeleton.TechnicianData> arrayListtech = gson.fromJson(technician, typetech);
                        ArrayList<ScheduleFilterSkeleton.JobsData> arrayListJobs = gson.fromJson(jobs, typejobs);
                        ArrayList<ScheduleFilterSkeleton.CustomerData> arrayListCustomer = gson.fromJson(customer, typeCustomer);

                        skeleton.setArrayListRegion(arrayListregion);
                        skeleton.setArrayListTechnician(arrayListtech);
                        skeleton.setArrayListJobs(arrayListJobs);
                        skeleton.setArrayListCustomer(arrayListCustomer);
                        arrayListFilterParent.add(skeleton);
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {
                }
            } else {
            }
            return arrayListFilterParent;
        }

        @Override
        protected void onPostExecute(ArrayList<SkeletonScheduleParentFilter> arrayListFilterParent) {
            super.onPostExecute(arrayListFilterParent);
            //HandyObject.showAlert(context, String.valueOf(cursor.getCount()));
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    adapterRegion = new AdapterRegionFilter(ScheduleParent.this, arrayListFilterParent.get(0).getArrayListRegion());
                    binding.regionspinner.setAdapter(adapterRegion);
                    techList = new ArrayList<>();
                    jobsList = new ArrayList<>();
                    arrayListTech = new ArrayList<>();
                    arrayListCustomer = new ArrayList<>();
                    regionList = new ArrayList<>();
                    for (int i = 0; i < arrayListFilterParent.get(0).getArrayListTechnician().size(); i++) {
                        techList.add(arrayListFilterParent.get(0).getArrayListTechnician().get(i).name);
                        TechinicianSkeleton ske = new TechinicianSkeleton();
                        ske.setName(arrayListFilterParent.get(0).getArrayListTechnician().get(i).name);
                        ske.setId(arrayListFilterParent.get(0).getArrayListTechnician().get(i).id);
                        ske.setStatus(false);
                        arrayListTech.add(ske);
                    }

                    for (int i = 0; i < arrayListFilterParent.get(0).getArrayListCustomer().size(); i++) {
                        //techList.add(arrayListFilterParent.get(0).getArrayListTechnician().get(i).name);
                        TechinicianSkeleton ske = new TechinicianSkeleton();
                        ske.setName(arrayListFilterParent.get(0).getArrayListCustomer().get(i).name);
                        ske.setId(arrayListFilterParent.get(0).getArrayListCustomer().get(i).id);
                        ske.setStatus(false);
                        arrayListCustomer.add(ske);
                    }

                    regionList.addAll(arrayListFilterParent.get(0).getArrayListRegion());
//                    for (int j = 0; j < arrayListFilterParent.get(0).getArrayListRegion().size(); j++) {
//                        regionList.add(arrayListFilterParent.get(0).getArrayListRegion().get(j).region_name);
//                    }

                    for (int k = 0; k < arrayListFilterParent.get(0).getArrayListJobs().size(); k++) {
                        jobsList.add(arrayListFilterParent.get(0).getArrayListJobs().get(k).jobid + "-" + arrayListFilterParent.get(0).getArrayListJobs().get(k).customer_name);
                    }
                    adapterAutoCompleteText = new AdapterAutoCompleteText(ScheduleParent.this, R.layout.popup_compose, R.id.textspinner, jobsList);
                    binding.etJobticketno.setAdapter(adapterAutoCompleteText);
                } else {
                }
            }
        }
    }

    private String checkVisibleFragment() {
        String visible = "";
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.tabswitch);
        if (currentFragment instanceof TimeLineFragment) {
            //   HandyObject.showAlert(ScheduleParent.this,"timeline");
            visible = "timeline";
        } else if (currentFragment instanceof CalendarFragment) {
            //    HandyObject.showAlert(ScheduleParent.this,"Calendar");
            visible = "calendar";
        } else if (currentFragment instanceof WeekViewFragment) {
            //   HandyObject.showAlert(ScheduleParent.this,"WeekView");
            visible = "weekview";
        } else if (currentFragment instanceof DayViewFragment) {
            //   HandyObject.showAlert(ScheduleParent.this,"WeekView");
            visible = "dayview";
        }
        return visible;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IsSearchable = false;
        LocalBroadcastManager.getInstance(ScheduleParent.this).unregisterReceiver(refreshParent);
    }
}
