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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Schedule.Adapter.AdapterDayView;
import teq.development.seatech.Schedule.Skeleton.ScheduleDayViewSkeleton;
import teq.development.seatech.Schedule.Skeleton.ScheduleWeekViewSkeleton;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class DayViewFragment extends Fragment {

    AdapterDayView adapterDayView;
    RecyclerView dayrecyclerview;
    Context context;
    SQLiteDatabase database;
    Cursor cursor;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    ArrayList<ScheduleDayViewSkeleton.AllData> arrayListMain,arrayListDayView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_dayview, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        context = getActivity();
        arrayListMain = new ArrayList<>();
        adapterDayView = new AdapterDayView(getActivity(), arrayListMain);
        dayrecyclerview = (RecyclerView) rootView.findViewById(R.id.dayrecyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dayrecyclerview.setLayoutManager(linearLayoutManager);
        dayrecyclerview.setAdapter(adapterDayView);

        if (getArguments() != null) {
            try {
                String coming_date = getArguments().getString("daydate");
                if(df.parse(coming_date).compareTo(df.parse(df.format(new Date()))) == 0) {
                    new DatabaseFetch().execute();
                }
                if (ScheduleParent.IsSearchable) {
              //      HandyObject.showAlert(getActivity(), "filter");
                    String regionfilter = "";
                    if (ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).region_name.equalsIgnoreCase("-- All --")) {
                        regionfilter = "";
                    } else {
                        regionfilter = ScheduleParent.regionList.get(ScheduleParent.binding.regionspinner.getSelectedItemPosition()).id;
                    }
                    String jobfilter = ScheduleParent.binding.etJobticketno.getText().toString().split("-")[0];
                    String techids = android.text.TextUtils.join(",", ScheduleParent.techincianSel_ID);
                    String custids = android.text.TextUtils.join(",", ScheduleParent.customeSel_ID);
                    GetScheduledDayData(coming_date, custids, techids, regionfilter, jobfilter);
                    HandyObject.showAlert(getActivity(), jobfilter);
                } else {
                 //   HandyObject.showAlert(getActivity(), "Nofilter");
                    GetScheduledDayData(coming_date, "", "", "", "");
                }
            } catch (Exception e){}

            //   GetScheduledDayData(coming_date);
        }
    }

    // Get WeekView Data to update events
    private void GetScheduledDayData(final String date, String customerids, String techids, String regionid, String ticketid) {
        //"2019-02-13"
        HandyObject.showProgressDialog(context);
        HandyObject.getApiManagerMain().GetScheduleDayViewData(date, customerids, techids, regionid, ticketid)
                .enqueue(new Callback<ScheduleDayViewSkeleton>() {
                    @Override
                    public void onResponse(Call<ScheduleDayViewSkeleton> call, Response<ScheduleDayViewSkeleton> response) {
                        try {
                            ScheduleDayViewSkeleton skeleton = response.body();
                            String status = skeleton.status;

                            if (status.equalsIgnoreCase("success")) {
                                arrayListMain.clear();
                                arrayListMain.addAll(skeleton.data);
                                if(df.parse(date).compareTo(df.parse(df.format(new Date()))) == 0) {
                                    insertDB_DayData(arrayListMain);
                                }
                                adapterDayView.notifyDataSetChanged();
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
                    public void onFailure(Call<ScheduleDayViewSkeleton> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        HandyObject.stopProgressDialog();
                    }
                });
    }

    private void insertDB_DayData(ArrayList<ScheduleDayViewSkeleton.AllData> arrayList) {
        Gson gson = new Gson();
        database.delete(ParseOpenHelper.TABLE_SCHEDULEDAYDATA, null, null);
        String ScheduledList = gson.toJson(arrayList);
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.SCHEDULEDAYDATA, ScheduledList);
        long idd = database.insert(ParseOpenHelper.TABLE_SCHEDULEDAYDATA, null, cv);
    }

    private class DatabaseFetch extends AsyncTask<ArrayList<ScheduleDayViewSkeleton.AllData>, Void, ArrayList<ScheduleDayViewSkeleton.AllData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // arrayListFilterParent.clear();
            arrayListDayView = new ArrayList<>();
            database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        }

        @Override
        protected ArrayList<ScheduleDayViewSkeleton.AllData> doInBackground(ArrayList<ScheduleDayViewSkeleton.AllData>... arrayLists) {
            Gson gson = new Gson();
            cursor = database.query(ParseOpenHelper.TABLE_SCHEDULEDAYDATA, null, null, null, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        Type typetech = new TypeToken<ArrayList<ScheduleDayViewSkeleton.AllData>>() {
                        }.getType();
                        String data = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SCHEDULEDAYDATA));
                        ArrayList<ScheduleDayViewSkeleton.AllData> arrayList = gson.fromJson(data, typetech);
                        arrayListDayView.addAll(arrayList);
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {
                }
            } else {
            }
            return arrayListDayView;
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleDayViewSkeleton.AllData> arrayList) {
            super.onPostExecute(arrayList);
            //HandyObject.showAlert(context, String.valueOf(cursor.getCount()));
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    arrayListMain.addAll(arrayList);
                    adapterDayView.notifyDataSetChanged();
                } else {
                }
            }
        }
    }
}
