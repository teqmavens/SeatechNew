package teq.development.seatech.Schedule;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
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
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.FrgmTimelineBinding;

public class TimeLineFragment extends Fragment {

    FrgmTimelineBinding binding;
    ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayListTimeline,arrayListMain;
    AdapterGridTest adapter;
    AutoGridView gridView;
    SQLiteDatabase database;
    Cursor cursor;
    Context context;
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
        adapter = new AdapterGridTest(getActivity(),arrayListMain);
        binding.gridView1.setAdapter(adapter);
//      LocalBroadcastManager.getInstance(getActivity()).registerReceiver(FromParent,
//                new IntentFilter("UpdateView"));
        if (HandyObject.weekcount == 0) {
            new DatabaseFetch().execute();
            // RunTimelineTask();
        } else {
            RunTimelineTask();
        }
    }

//    private BroadcastReceiver FromParent = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String weekdate = intent.getStringExtra("weekdate");
//            if (HandyObject.weekcount == 0) {
//                new DatabaseFetch().execute();
//            } else {
//                RunTimelineTask(weekdate);
//            }
//
//        }
//    };

    private void RunTimelineTask() {
        if(getArguments() != null) {
            String coming_date = getArguments().getString("weekdate");
            ArrayList<String> abc = getArguments().getStringArrayList("techlist");
            for(int i=0;i<abc.size()*8;i++) {
              arrayListMain.add(new ScheduleFilterSkeleton.SchedulesData());
            }
            adapter.notifyDataSetChanged();
         //   HandyObject.showAlert(context,coming_date);
            GetScheduledFilterData(HandyObject.parseDateToYMDSchedule(coming_date.split("-")[0]),HandyObject.parseDateToYMDSchedule(coming_date.split("-")[1]));
        }
    }

    // Get Manufacturer data for Need Part Dialog(Inside Job Detail Page)
    private void GetScheduledFilterData(String startdate,String enddate) {
       // HandyObject.showAlert(context,startdate+"----"+enddate);
        //HandyObject.showProgressDialog(this);
        HandyObject.getApiManagerMain().GetScheduleFilterData(startdate,enddate)
                .enqueue(new Callback<ScheduleFilterSkeleton>() {
                    @Override
                    public void onResponse(Call<ScheduleFilterSkeleton> call, Response<ScheduleFilterSkeleton> response) {
                        try {
                            ScheduleFilterSkeleton skeleton = response.body();
                            String status = skeleton.status;
                            //Log.e("response", status);
                          //  HandyObject.showAlert(context,status);
                            arrayListMain.clear();
                            ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayListSchedules = new ArrayList<>();
                            if (status.equalsIgnoreCase("success")) {
                                arrayListSchedules = skeleton.schedulesData;
                                arrayListMain.addAll(arrayListSchedules);
                             //   arrayListSchedules.add(new ScheduleFilterSkeleton.SchedulesData());
                               // adapter = new AdapterGridTest(getActivity(),arrayListSchedules);
                                adapter.notifyDataSetChanged();
                               // binding.gridView1.setAdapter(adapter);
                               // insertDB_ScheduleFilter(arrayListRegion,arrayListTechnician,arrayListSchedules);
                            } else {
                                //  HandyObject.showAlert(context, status);
                                if(status.equalsIgnoreCase("logout")){
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

    private class DatabaseFetch extends AsyncTask<ArrayList<ScheduleFilterSkeleton.SchedulesData>, Void, ArrayList<ScheduleFilterSkeleton.SchedulesData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // arrayListFilterParent.clear();
            arrayListTimeline = new ArrayList<>();
            database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        }

        @Override
        protected ArrayList<ScheduleFilterSkeleton.SchedulesData> doInBackground(ArrayList<ScheduleFilterSkeleton.SchedulesData>... arrayLists) {
            Gson gson = new Gson();
            cursor = database.query(ParseOpenHelper.TABLE_SCHEDULEDATA, null, null, null, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        Type typetech = new TypeToken<ArrayList<ScheduleFilterSkeleton.SchedulesData>>() {}.getType();
                        String data = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEDDATA));
                        ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayList = gson.fromJson(data, typetech);
                        arrayListTimeline.addAll(arrayList);
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {}
            } else {}
            return arrayListTimeline;
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayList) {
            super.onPostExecute(arrayList);
            //HandyObject.showAlert(context, String.valueOf(cursor.getCount()));
            if (cursor != null) {
                if (cursor.getCount() > 0) {
//                    adapter = new AdapterGridTest(getActivity(),arrayList);
//                    binding.gridView1.setAdapter(adapter);
                    arrayListMain.addAll(arrayList);
                    adapter.notifyDataSetChanged();
                } else {}
            }
        }
    }
}
