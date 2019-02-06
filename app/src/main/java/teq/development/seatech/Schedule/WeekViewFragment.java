package teq.development.seatech.Schedule;

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
import java.util.ArrayList;

import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.Schedule.Adapter.WeekViewAdapter;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class WeekViewFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayListWeekView,arrayListMain;
    ArrayList<String> arrayListchecking;
    WeekViewAdapter adapter;
    SQLiteDatabase database;
    Cursor cursor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_weekview, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        arrayListMain = new ArrayList<>();
        arrayListchecking = new ArrayList<>();
        arrayListchecking.add("a");
        arrayListchecking.add("b");

        arrayListchecking.add("c");
        arrayListchecking.add("d");

        arrayListchecking.add("e");
        arrayListchecking.add("f");

        arrayListchecking.add("g");
        arrayListchecking.add("h");

        adapter = new WeekViewAdapter(getActivity(),arrayListMain);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        if (HandyObject.weekcount == 0) {
            new DatabaseFetch().execute();
            // RunTimelineTask();
        } else {
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
}
