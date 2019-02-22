package teq.development.seatech.Schedule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Schedule.Adapter.WeekViewAdapter;
import teq.development.seatech.Schedule.Skeleton.ScheduleWeekViewSkeleton;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class WeekViewFragment extends Fragment {

    RecyclerView recyclerView;
    TextView noevent;
    ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayListWeekView,arrayListMain;
    ArrayList<String> arrayListchecking;
    WeekViewAdapter adapter;
    SQLiteDatabase database;
    Cursor cursor;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_weekview, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        context = getActivity();
        arrayListMain = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        noevent = (TextView) rootView.findViewById(R.id.noevent);
        if (HandyObject.weekcount == 0) {
          //  new DatabaseFetch().execute();
            // RunTimelineTask();
            String todaydate = HandyObject.getCurrentWeek_FirstDateSchedule(getActivity());
            GetScheduledWeekData(todaydate);
        } else {
            if (getArguments() != null) {
                String coming_date = getArguments().getString("weekdate");
                GetScheduledWeekData(coming_date);
            }

           // RunTimelineTask();
        }
    }

    private class DatabaseFetch extends AsyncTask<ArrayList<ScheduleFilterSkeleton.SchedulesData>, Void, ArrayList<ScheduleFilterSkeleton.SchedulesData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // arrayListFilterParent.clear();
            arrayListWeekView = new ArrayList<>();
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
                        arrayListWeekView.addAll(arrayList);
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {}
            } else {}
            return arrayListWeekView;
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
                    Log.e("S",String.valueOf(arrayListMain.size()));
//                    for (int i=0;i<arrayListMain.size();i++) {
//                        Log.e("Length",String.valueOf(i));
//                        if(arrayList.get(i).date.equalsIgnoreCase("2019-01-24")) {
//                            arrayListMain.remove(i);
//                        }
//                    }
                    for (int i=0;i<arrayListMain.size();i++) {
                        Log.e("Length",String.valueOf(i));
                        if(arrayList.get(i).date.equalsIgnoreCase("2019-01-24")) {
                            arrayListchecking.remove(i);
                        }
                    }
                   // Log.e("SSSSSSS",String.valueOf(arrayListMain.size()));
                    Log.e("SSSSSSS",String.valueOf(arrayListMain.size()));
                    adapter.notifyDataSetChanged();
                } else {}
            }
        }
    }

    // Get WeekView Data to update events
    private void GetScheduledWeekData(String seldate) {
        //HandyObject.showProgressDialog(this);
//
//        HandyObject.showAlert(getActivity(),HandyObject.parseDateToYMDSchedule(seldate.split("-")[0])+"_______"+HandyObject.parseDateToYMDSchedule(seldate.split("-")[1]));

        HandyObject.getApiManagerMain().GetScheduleWeekViewData(HandyObject.parseDateToYMDSchedule(seldate.split("-")[0]),HandyObject.parseDateToYMDSchedule(seldate.split("-")[1]))
                .enqueue(new Callback<ScheduleWeekViewSkeleton>() {
                    @Override
                    public void onResponse(Call<ScheduleWeekViewSkeleton> call, Response<ScheduleWeekViewSkeleton> response) {
                        try {
                            ScheduleWeekViewSkeleton skeleton = response.body();
                            String status = skeleton.status;

                            if (status.equalsIgnoreCase("success")) {
                                ArrayList<ScheduleWeekViewSkeleton.AllData> arrayList = new ArrayList<>();
                                arrayList = skeleton.data;
                                if(arrayList.size() == 0) {
                                    noevent.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.INVISIBLE);
                                } else {
                                    if(getArguments() != null && getArguments().containsKey("regionfilter")) {
                                        for (int i = 0; i < arrayList.size(); i++) {
                                            for (int k = 0; k < arrayList.get(i).scheduled.size(); k++) {
                                                if (arrayList.get(i).scheduled.get(k).region_name.toLowerCase().contains(getArguments().getString("regionfilter").toLowerCase()) && arrayList.get(i).scheduled.get(k).jobid.toLowerCase().contains(getArguments().getString("jobfilter"))) {
                                                    arrayList.get(i).scheduled.set(k, arrayList.get(i).scheduled.get(k));
                                                } else {
                                                    arrayList.get(i).scheduled.set(k, new ScheduleWeekViewSkeleton.Scheduled());
                                                    //   arrayList.get(i).scheduled.remove(k);
                                                }
                                            }


                                            for (int m = arrayList.get(i).scheduled.size() - 1; m >= 0; m--) {
                                                if (arrayList.get(i).scheduled.get(m).jobid != null && !arrayList.get(i).scheduled.get(m).jobid.isEmpty()) {

                                                } else {
                                                    arrayList.get(i).scheduled.remove(m);
                                                }
                                            }
                                        }

                                        for(int ab = arrayList.size()-1 ;ab>=0;ab--) {
                                            if(arrayList.get(ab).scheduled.size() == 0) {
                                                arrayList.remove(ab);
                                            }
                                        }
                                    } else {
                                        HandyObject.showAlert(getActivity(),"Nofilter");
                                    }

                                    if(arrayList.size() == 0) {
                                        noevent.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.INVISIBLE);
                                    } else {
                                        noevent.setVisibility(View.INVISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        adapter = new WeekViewAdapter(getActivity(), arrayList);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        recyclerView.setAdapter(adapter);
                                    }


                                }

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
                    public void onFailure(Call<ScheduleWeekViewSkeleton> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        HandyObject.stopProgressDialog();
                    }
                });
    }
}
