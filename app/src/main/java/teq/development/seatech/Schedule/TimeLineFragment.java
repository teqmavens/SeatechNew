package teq.development.seatech.Schedule;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.CustomCalendar.AdapterGridTest;
import teq.development.seatech.CustomCalendar.AutoGridView;
import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Schedule.Skeleton.ScheduleCalendarViewSkeleton;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.FrgmTimelineBinding;

public class TimeLineFragment extends Fragment {

    FrgmTimelineBinding binding;
    ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayListTimeline, arrayListMain;
    AdapterGridTest adapter;
    AutoGridView gridView;
    SQLiteDatabase database;
    Cursor cursor;
    Context context;
    boolean checkcursor;
    //Appointment type,App Confirm symbol,urgent symbol,

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_timeline, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmtimeline(this);
        initViews();
        return rootView;
    }

    private void initViews() {
        context = getActivity();
        arrayListMain = new ArrayList<>();
        database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        adapter = new AdapterGridTest(getActivity(), arrayListMain);
        binding.gridView1.setAdapter(adapter);
        String setdates = "";
        if (HandyObject.weekcount == 0) {
            new DatabaseFetch().execute();
            // RunTimelineTask();
            final String todaydate = HandyObject.getCurrentWeek_FirstDateSchedule(getActivity());
            setdates = todaydate;
            if(ScheduleParent.IsSearchable){
                //HandyObject.showAlert(getActivity(),"filter");
                String regionfilter = "";
                if(ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).region_name.equalsIgnoreCase("-- All --")) {
                    regionfilter = "";
                } else {
                    regionfilter = ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).id;
                }
                String jobfilter = ScheduleParent.binding.etJobticketno.getText().toString().split("-")[0];
                String techids = android.text.TextUtils.join(",",ScheduleParent.techincianSel_ID);
                String custids = android.text.TextUtils.join(",",ScheduleParent.customeSel_ID);
                GetScheduledFilterData(HandyObject.parseDateToYMDSchedule(todaydate.split("-")[0]), HandyObject.parseDateToYMDSchedule(todaydate.split("-")[1]),custids,techids,regionfilter,jobfilter);
               // HandyObject.showAlert(getActivity(),jobfilter);
            } else {
               // HandyObject.showAlert(getActivity(),"Nofilter");
                GetScheduledFilterData(HandyObject.parseDateToYMDSchedule(todaydate.split("-")[0]), HandyObject.parseDateToYMDSchedule(todaydate.split("-")[1]),"","","","");
            }
        } else {
            if (getArguments() != null) {
                setdates = getArguments().getString("weekdate");
            }
            RunTimelineTask();

        }
        setMonToSun(setdates);
    }

    //Set Top header day from Monday to Sunday
    private void setMonToSun(String bothdates) {

        String firstsplit_0 = bothdates.split("-")[0];
        String firstsplit_1 = bothdates.split("-")[1];

        String firstcomma = firstsplit_0.split(",")[0];
        String actualnumber = firstcomma.split("\\s+")[1];
        int actualint = Integer.parseInt(actualnumber);
        binding.mon.setText("MON" + String.valueOf(actualint));
        actualint++;
        binding.tue.setText("TUE" + String.valueOf(actualint));
        actualint++;
        binding.wed.setText("WED" + String.valueOf(actualint));
        actualint++;
        binding.thu.setText("THU" + String.valueOf(actualint));
        actualint++;
        binding.fri.setText("FRI" + String.valueOf(actualint));
        actualint++;
        binding.sat.setText("SAT" + String.valueOf(actualint));
        actualint++;
        binding.sun.setText("SUN" + String.valueOf(actualint));
    }

    private void RunTimelineTask() {
        if (getArguments() != null) {
            String coming_date = getArguments().getString("weekdate");
            ArrayList<String> abc = getArguments().getStringArrayList("techlist");
            for (int i = 0; i < abc.size() * 8; i++) {
                arrayListMain.add(new ScheduleFilterSkeleton.SchedulesData());
            }
            adapter.notifyDataSetChanged();
            if(ScheduleParent.IsSearchable){
               // HandyObject.showAlert(getActivity(),"filter");
                String regionfilter = "";
                if(ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).region_name.equalsIgnoreCase("-- All --")) {
                    regionfilter = "";
                } else {
                    regionfilter = ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).id;
                }
                String jobfilter = ScheduleParent.binding.etJobticketno.getText().toString().split("-")[0];
                String techids = android.text.TextUtils.join(",",ScheduleParent.techincianSel_ID);
                String custids = android.text.TextUtils.join(",",ScheduleParent.customeSel_ID);
                GetScheduledFilterData(HandyObject.parseDateToYMDSchedule(coming_date.split("-")[0]), HandyObject.parseDateToYMDSchedule(coming_date.split("-")[1]),custids,techids,regionfilter,jobfilter);
              //  HandyObject.showAlert(getActivity(),jobfilter);
            } else {
                //HandyObject.showAlert(getActivity(),"Nofilter");
                GetScheduledFilterData(HandyObject.parseDateToYMDSchedule(coming_date.split("-")[0]), HandyObject.parseDateToYMDSchedule(coming_date.split("-")[1]),"","","","");
            }
          //  GetScheduledFilterData(HandyObject.parseDateToYMDSchedule(coming_date.split("-")[0]), HandyObject.parseDateToYMDSchedule(coming_date.split("-")[1]));
        }
    }

    // Get TimeLine updated data from API
    private void GetScheduledFilterData(String startdate, String enddate,String customerids,String techids,String regionid,String ticketid) {
        HandyObject.showProgressDialog(context);
        HandyObject.getApiManagerMain().GetScheduleFilterData(startdate, enddate,customerids,techids,regionid,ticketid)
                .enqueue(new Callback<ScheduleFilterSkeleton>() {
                    @Override
                    public void onResponse(Call<ScheduleFilterSkeleton> call, Response<ScheduleFilterSkeleton> response) {
                        try {
                            ScheduleFilterSkeleton skeleton = response.body();
                            String status = skeleton.status;
                            Log.e("responseTODAY", status);
                            //  HandyObject.showAlert(context,status);
                            arrayListMain.clear();
                            ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayListSchedules = new ArrayList<>();
                            ArrayList<ScheduleFilterSkeleton.RegionData> arrayListRegion = new ArrayList<>();
                            ArrayList<ScheduleFilterSkeleton.TechnicianData> arrayListTechnician = new ArrayList<>();
                            ArrayList<ScheduleFilterSkeleton.JobsData> arrayListJobs = new ArrayList<>();
                            ArrayList<ScheduleFilterSkeleton.CustomerData> arrayListCustomer = new ArrayList<>();
                            if (status.equalsIgnoreCase("success")) {
                               //  arrayListSchedules = skeleton.schedulesData;
                               // arrayListMain.addAll(arrayListSchedules);

                                arrayListSchedules.addAll(skeleton.schedulesData);
                                arrayListMain.addAll(skeleton.schedulesData);

                                arrayListRegion = skeleton.regionData;
                                arrayListTechnician = skeleton.techniciansData;
                                arrayListJobs = skeleton.jobsData;
                                arrayListCustomer = skeleton.customerData;
                                if (HandyObject.weekcount == 0) {
                                    insertDB_ScheduleData(arrayListRegion, arrayListTechnician, arrayListSchedules, arrayListJobs, arrayListCustomer);
                                }
                                adapter.notifyDataSetChanged();
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
                    public void onFailure(Call<ScheduleFilterSkeleton> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        HandyObject.stopProgressDialog();
                    }
                });
    }

    public void insertDB_ScheduleData(ArrayList<ScheduleFilterSkeleton.RegionData> arrayListRegion, ArrayList<ScheduleFilterSkeleton.TechnicianData> arrayListTechnician, ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayListSchedules, ArrayList<ScheduleFilterSkeleton.JobsData> arrayListJobs, ArrayList<ScheduleFilterSkeleton.CustomerData> arrayListCustomers) {
        Gson gson = new Gson();
        if(checkcursor) {
            database.delete(ParseOpenHelper.TABLE_SCHEDULEFILTER, null, null);
            String RegionList = gson.toJson(arrayListRegion);
            String TechnicianList = gson.toJson(arrayListTechnician);
            String JobsList = gson.toJson(arrayListJobs);
            String CustomersList = gson.toJson(arrayListCustomers);
            ContentValues cv = new ContentValues();
            cv.put(ParseOpenHelper.SCHEDULEFILTER_REGIONDATA, RegionList);
            cv.put(ParseOpenHelper.SCHEDULEFILTER_TECHDATA, TechnicianList);
            cv.put(ParseOpenHelper.SCHEDULEFILTER_JOBSDATA, JobsList);
            cv.put(ParseOpenHelper.SCHEDULEFILTER_CUSTOMERDATA, CustomersList);
            long idd = database.insert(ParseOpenHelper.TABLE_SCHEDULEFILTER, null, cv);
            Intent intent = new Intent("refresh");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }

        database.delete(ParseOpenHelper.TABLE_SCHEDULEDATA, null, null);
        String ScheduledList = gson.toJson(arrayListSchedules);
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.SCHEDULEDDATA, ScheduledList);
        long idd = database.insert(ParseOpenHelper.TABLE_SCHEDULEDATA, null, cv);
    }

    private class DatabaseFetch extends AsyncTask<ArrayList<ScheduleFilterSkeleton.SchedulesData>, Void, ArrayList<ScheduleFilterSkeleton.SchedulesData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrayListTimeline = new ArrayList<>();
        }

        @Override
        protected ArrayList<ScheduleFilterSkeleton.SchedulesData> doInBackground(ArrayList<ScheduleFilterSkeleton.SchedulesData>... arrayLists) {
            Gson gson = new Gson();
            cursor = database.query(ParseOpenHelper.TABLE_SCHEDULEDATA, null, null, null, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    checkcursor = false;
                    while (!cursor.isAfterLast()) {
                        Type typetech = new TypeToken<ArrayList<ScheduleFilterSkeleton.SchedulesData>>() {
                        }.getType();
                        String data = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEDDATA));
                        ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayList = gson.fromJson(data, typetech);
                        arrayListTimeline.addAll(arrayList);
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {
                    checkcursor = true;
                }
            } else {

            }
            return arrayListTimeline;
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayList) {
            super.onPostExecute(arrayList);
            //HandyObject.showAlert(context, String.valueOf(cursor.getCount()));
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    arrayListMain.addAll(arrayList);
//                    if(getArguments() != null && getArguments().containsKey("regionfilter")) {
//                        for(int i=0;i<arrayListMain.size();i++) {
//                            for(int k=0; k<arrayListMain.get(i).eventData.size();k++) {
//                                if (arrayListMain.get(i).eventData.get(k).region_name.toLowerCase().contains(getArguments().getString("regionfilter").toLowerCase()) && arrayListMain.get(i).eventData.get(k).jobid.toLowerCase().contains(getArguments().getString("jobfilter"))) {
//                                    arrayListMain.get(i).eventData.set(k,arrayListMain.get(i).eventData.get(k));
//                                } else {
//                                    //  arrayListMain.get(i).eventData.clear();
//                                    arrayListMain.get(i).eventData.set(k,new ScheduleFilterSkeleton.EventData());
//                                }
//                            }
//                        }
//                    }
                    adapter.notifyDataSetChanged();
                } else {
                }
            }
        }
    }
}
