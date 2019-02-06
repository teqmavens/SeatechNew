package teq.development.seatech.Schedule;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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

    ScheduleParentBinding binding;
    ArrayList<SkeletonScheduleParentFilter> arrayListFilterParent;
    ArrayList<String> techList;
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
        binding.firstlastDate.setText(HandyObject.getCurrentWeek_FirstDateSchedule(ScheduleParent.this));
        calendarweek = Calendar.getInstance(Locale.UK);
        fromMonth_SelDate = HandyObject.ParseDateJobTimeNew(calendarweek.getTime());

    }

    //Replace fragment Without backstack
    public void replaceFragmentWithoutBack(Fragment mFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.tabswitch, mFragment)
                .commit();
    }

    public void OnClickToday() {
        String todaydate = HandyObject.getCurrentWeek_FirstDateSchedule(ScheduleParent.this);
        binding.firstlastDate.setText(todaydate);
        fromMonth_SelDate = HandyObject.ParseDateJobTimeNew(calendarweek.getTime());

        TimeLineFragment fgm = new TimeLineFragment();
        Bundle bundle = new Bundle();
        bundle.putString("weekdate", todaydate);
        bundle.putStringArrayList("techlist",techList);
        fgm.setArguments(bundle);
        replaceFragmentWithoutBack(fgm);
    }

    public void OnClickPrevious() {
        String previousdate = HandyObject.getSelectedPreviousWeek_FirstLastSchedule(ScheduleParent.this, fromMonth_SelDate);
        binding.firstlastDate.setText(previousdate);

//        Intent intent = new Intent("UpdateView");
//        intent.putExtra("weekdate", previousdate);
//        LocalBroadcastManager.getInstance(ScheduleParent.this).sendBroadcast(intent);

        TimeLineFragment fgm = new TimeLineFragment();
        Bundle bundle = new Bundle();
        bundle.putString("weekdate", previousdate);
        bundle.putStringArrayList("techlist",techList);
        fgm.setArguments(bundle);
        replaceFragmentWithoutBack(fgm);
    }

    public void OnClickNext() {
        String nextdate = HandyObject.getSelectedNextWeek_FirstLastSchedule(ScheduleParent.this, fromMonth_SelDate);
        binding.firstlastDate.setText(nextdate);

        TimeLineFragment fgm = new TimeLineFragment();
        Bundle bundle = new Bundle();
        bundle.putString("weekdate", nextdate);
        bundle.putStringArrayList("techlist",techList);
        fgm.setArguments(bundle);
        replaceFragmentWithoutBack(fgm);

//        Intent intent = new Intent("UpdateView");
//        intent.putExtra("weekdate", nextdate);
//        LocalBroadcastManager.getInstance(ScheduleParent.this).sendBroadcast(intent);
    }

    public void OnClickTimeLine() {
        replaceFragmentWithoutBack(new TimeLineFragment());
    }

    public void OnClickCalendar() {
        replaceFragmentWithoutBack(new CalendarFragment());
    }

    public void OnClickWeekView() {
        replaceFragmentWithoutBack(new WeekViewFragment());
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
                        String region = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEFILTER_REGIONDATA));
                        String technician = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEFILTER_TECHDATA));
                        ArrayList<ScheduleFilterSkeleton.RegionData> arrayListregion = gson.fromJson(region, typeregion);
                        ArrayList<ScheduleFilterSkeleton.TechnicianData> arrayListtech = gson.fromJson(technician, typetech);

                        skeleton.setArrayListRegion(arrayListregion);
                        skeleton.setArrayListTechnician(arrayListtech);
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
                    for(int i=0;i<arrayListFilterParent.get(0).getArrayListTechnician().size();i++) {
                        techList.add(arrayListFilterParent.get(0).getArrayListTechnician().get(i).name);
                    }
                } else {}
            }
        }
    }
}
