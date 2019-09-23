package teq.development.seatech.Schedule;

import android.app.Activity;
import android.content.ContentValues;
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
    ArrayList<ScheduleWeekViewSkeleton.AllData> arrayListWeekView, arrayListMain;
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
        adapter = new WeekViewAdapter(getActivity(), arrayListMain);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        if (HandyObject.weekcount == 0) {
            new DatabaseFetch().execute();
            String todaydate = HandyObject.getCurrentWeek_FirstDateSchedule(getActivity());

            if(ScheduleParent.IsSearchable) {
           //     HandyObject.showAlert(getActivity(), "filter");
                String regionfilter = "";
                if (ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).region_name.equalsIgnoreCase("-- All --")) {
                    regionfilter = "";
                } else {
                    regionfilter = ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).id;
                }
                String jobfilter = ScheduleParent.binding.etJobticketno.getText().toString().split("-")[0];
                String techids = android.text.TextUtils.join(",", ScheduleParent.techincianSel_ID);
                String custids = android.text.TextUtils.join(",", ScheduleParent.customeSel_ID);
                GetScheduledWeekData(todaydate,custids,techids,regionfilter,jobfilter);
            } else {
                GetScheduledWeekData(todaydate,"","","","");
            }

        } else {
            if (getArguments() != null) {
                String coming_date = getArguments().getString("weekdate");
                if(ScheduleParent.IsSearchable) {
                    HandyObject.showAlert(getActivity(), "filter");
                    String regionfilter = "";
                    if (ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).region_name.equalsIgnoreCase("-- All --")) {
                        regionfilter = "";
                    } else {
                        regionfilter = ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).id;
                    }
                    String jobfilter = ScheduleParent.binding.etJobticketno.getText().toString().split("-")[0];
                    String techids = android.text.TextUtils.join(",", ScheduleParent.techincianSel_ID);
                    String custids = android.text.TextUtils.join(",", ScheduleParent.customeSel_ID);
                    GetScheduledWeekData(coming_date,custids,techids,regionfilter,jobfilter);
                } else {
                    GetScheduledWeekData(coming_date,"","","","");
                }
            }

            // RunTimelineTask();
        }
    }

    private class DatabaseFetch extends AsyncTask<ArrayList<ScheduleWeekViewSkeleton.AllData>, Void, ArrayList<ScheduleWeekViewSkeleton.AllData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // arrayListFilterParent.clear();
            arrayListWeekView = new ArrayList<>();
            database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        }

        @Override
        protected ArrayList<ScheduleWeekViewSkeleton.AllData> doInBackground(ArrayList<ScheduleWeekViewSkeleton.AllData>... arrayLists) {
            Gson gson = new Gson();
            cursor = database.query(ParseOpenHelper.TABLE_SCHEDULEWEEKDATA, null, null, null, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        Type typetech = new TypeToken<ArrayList<ScheduleWeekViewSkeleton.AllData>>() {
                        }.getType();
                        String data = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEWEEKDATA));
                        ArrayList<ScheduleWeekViewSkeleton.AllData> arrayList = gson.fromJson(data, typetech);
                        arrayListWeekView.addAll(arrayList);
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {
                }
            } else {
            }
            return arrayListWeekView;
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleWeekViewSkeleton.AllData> arrayList) {
            super.onPostExecute(arrayList);
            //HandyObject.showAlert(context, String.valueOf(cursor.getCount()));
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    arrayListMain.addAll(arrayList);
//                    if (getArguments() != null && getArguments().containsKey("regionfilter")) {
//                        for (int i = 0; i < arrayListMain.size(); i++) {
//                            for (int k = 0; k < arrayListMain.get(i).scheduled.size(); k++) {
//                                if (arrayListMain.get(i).scheduled.get(k).region_name.toLowerCase().contains(getArguments().getString("regionfilter").toLowerCase()) && arrayListMain.get(i).scheduled.get(k).jobid.toLowerCase().contains(getArguments().getString("jobfilter"))) {
//                                    arrayListMain.get(i).scheduled.set(k, arrayListMain.get(i).scheduled.get(k));
//                                } else {
//                                    arrayListMain.get(i).scheduled.set(k, new ScheduleWeekViewSkeleton.Scheduled());
//                                    //   arrayList.get(i).scheduled.remove(k);
//                                }
//                            }
//
//
//                            for (int m = arrayListMain.get(i).scheduled.size() - 1; m >= 0; m--) {
//                                if (arrayListMain.get(i).scheduled.get(m).jobid != null && !arrayListMain.get(i).scheduled.get(m).jobid.isEmpty()) {
//
//                                } else {
//                                    arrayListMain.get(i).scheduled.remove(m);
//                                }
//                            }
//                        }
//
//                        for (int ab = arrayListMain.size() - 1; ab >= 0; ab--) {
//                            if (arrayListMain.get(ab).scheduled.size() == 0) {
//                                arrayListMain.remove(ab);
//                            }
//                        }
//                    }
                    adapter.notifyDataSetChanged();
                } else {
                }
            }
        }
    }

    // Get WeekView Data to update events
    private void GetScheduledWeekData(String seldate,String customerids,String techids,String regionid,String ticketid) {
        HandyObject.showProgressDialog(getActivity());
        HandyObject.getApiManagerMain().GetScheduleWeekViewData(HandyObject.parseDateToYMDSchedule(seldate.split("-")[0]), HandyObject.parseDateToYMDSchedule(seldate.split("-")[1]),customerids,techids,regionid,ticketid)
                .enqueue(new Callback<ScheduleWeekViewSkeleton>() {
                    @Override
                    public void onResponse(Call<ScheduleWeekViewSkeleton> call, Response<ScheduleWeekViewSkeleton> response) {
                        try {
                            ScheduleWeekViewSkeleton skeleton = response.body();
                            String status = skeleton.status;

                            if (status.equalsIgnoreCase("success")) {
                                //  ArrayList<ScheduleWeekViewSkeleton.AllData> arrayList = new ArrayList<>();
                                arrayListMain.clear();
                              //  arrayListMain = skeleton.data;
                                arrayListMain.addAll(skeleton.data);

                                if (HandyObject.weekcount == 0) {
                                    insertDB_WeekData(arrayListMain);
                                }


                                if (arrayListMain.size() == 0) {
                                    noevent.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.INVISIBLE);
                                } else {
//                                    if (getArguments() != null && getArguments().containsKey("regionfilter")) {
//                                        for (int i = 0; i < arrayListMain.size(); i++) {
//                                            for (int k = 0; k < arrayListMain.get(i).scheduled.size(); k++) {
//                                                if (arrayListMain.get(i).scheduled.get(k).region_name.toLowerCase().contains(getArguments().getString("regionfilter").toLowerCase()) && arrayListMain.get(i).scheduled.get(k).jobid.toLowerCase().contains(getArguments().getString("jobfilter"))) {
//                                                    arrayListMain.get(i).scheduled.set(k, arrayListMain.get(i).scheduled.get(k));
//                                                } else {
//                                                    arrayListMain.get(i).scheduled.set(k, new ScheduleWeekViewSkeleton.Scheduled());
//                                                    //   arrayList.get(i).scheduled.remove(k);
//                                                }
//                                            }
//
//
//                                            for (int m = arrayListMain.get(i).scheduled.size() - 1; m >= 0; m--) {
//                                                if (arrayListMain.get(i).scheduled.get(m).jobid != null && !arrayListMain.get(i).scheduled.get(m).jobid.isEmpty()) {
//
//                                                } else {
//                                                    arrayListMain.get(i).scheduled.remove(m);
//                                                }
//                                            }
//                                        }
//
//                                        for (int ab = arrayListMain.size() - 1; ab >= 0; ab--) {
//                                            if (arrayListMain.get(ab).scheduled.size() == 0) {
//                                                arrayListMain.remove(ab);
//                                            }
//                                        }
//                                    } else {
//                                        HandyObject.showAlert(getActivity(), "Nofilter");
//                                    }
//
//                                    if (arrayListMain.size() == 0) {
//                                        noevent.setVisibility(View.VISIBLE);
//                                        recyclerView.setVisibility(View.INVISIBLE);
//                                    } else {
//                                        noevent.setVisibility(View.INVISIBLE);
//                                        recyclerView.setVisibility(View.VISIBLE);
////                                        adapter = new WeekViewAdapter(getActivity(), arrayListMain);
////                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
////                                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
////                                        recyclerView.setLayoutManager(linearLayoutManager);
////                                        recyclerView.setAdapter(adapter);
//                                        adapter.notifyDataSetChanged();
//                                    }
                                    adapter.notifyDataSetChanged();
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

    private void insertDB_WeekData(ArrayList<ScheduleWeekViewSkeleton.AllData> arrayList) {
        Gson gson = new Gson();
        database.delete(ParseOpenHelper.TABLE_SCHEDULEWEEKDATA, null, null);
        String ScheduledList = gson.toJson(arrayList);
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.SCHEDULEWEEKDATA, ScheduledList);
        long idd = database.insert(ParseOpenHelper.TABLE_SCHEDULEWEEKDATA, null, cv);
    }
}
