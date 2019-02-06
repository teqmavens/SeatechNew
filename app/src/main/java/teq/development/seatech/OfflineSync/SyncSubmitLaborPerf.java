package teq.development.seatech.OfflineSync;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.JobDetail.JobDetailFragment;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class SyncSubmitLaborPerf extends Job {

    public static final String TAG = "submitlaborperf_tag";
    private SQLiteDatabase database;
    Gson gson;
    ArrayList<DashboardNotes_Skeleton> addtech;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e("backgroSubmitlaborperf", "backgroSubmitlaborperf");
        database = ParseOpenHelper.getInstance(getContext()).getWritableDatabase();
        addtech = new ArrayList<>();
        gson = new Gson();
        Cursor cursor = database.query(ParseOpenHelper.TABLE_SUBMITMYLABOR_NEWOFFRECORD, null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    DashboardNotes_Skeleton ske = new DashboardNotes_Skeleton();
                    ske.setNotes(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SUBMITLABORNOTES)));
                    ske.setTechid(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SUBMITLABORTECHID)));
                    ske.setJobid(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SUBMITLABORJOBID)));
                    ske.setType(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SUBMITLABORTYPE)));
                    ske.setCreatedAt(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.SUBMITLABORTIME)));
                    addtech.add(ske);
                    cursor.moveToNext();
                }
                SyncData();
                cursor.close();
            } else {
                //binding.rcyviewJobs.setAdapter(adapterjobs);
                //    HandyObject.showAlert(getContext(), "cursor not greater than zero");
            }
        } else {
            //    HandyObject.showAlert(getContext(), "cursor null");
        }
        return Result.SUCCESS;
    }

    private void SyncData() {
        String OffTheRecord = gson.toJson(addtech);
        HandyObject.getApiManagerMain().submitTechLaborPerf(OffTheRecord, HandyObject.getPrams(getContext(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("respSyncLaborPerform", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                // JSONObject jobjdata = jsonObject.getJSONObject("data");
                                // arraylistOffTheRecord = new ArrayList<>();
                                JSONArray jsonArray = jsonObject.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobjInside = jsonArray.getJSONObject(i);
                                    String jobid = jobjInside.getString("job_id");

                                    JSONArray jArray_OffTheRecord = jobjInside.getJSONArray("OffTheRecord");
                                    ArrayList<DashboardNotes_Skeleton> arraylistOffTheRecord = new ArrayList<>();
                                    for (int k = 0; k < jArray_OffTheRecord.length(); k++) {
                                        JSONObject jobj_dashnotes = jArray_OffTheRecord.getJSONObject(k);
                                        DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
                                        dashnotes_ske.setCreatedAt(jobj_dashnotes.getString("created_at"));
                                        dashnotes_ske.setNoteWriter(jobj_dashnotes.getString("written_by"));
                                        dashnotes_ske.setNotes(jobj_dashnotes.getString("notes"));
                                        arraylistOffTheRecord.add(dashnotes_ske);
                                    }

                                    JSONArray jArray_Labrperformed = jobjInside.getJSONArray("TechLabourPerformed");
                                    ArrayList<DashboardNotes_Skeleton> arraylistLabrperformed = new ArrayList<>();
                                    for (int k = 0; k < jArray_Labrperformed.length(); k++) {
                                        JSONObject jobj_dashnotes = jArray_Labrperformed.getJSONObject(k);
                                        DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
                                        dashnotes_ske.setCreatedAt(jobj_dashnotes.getString("created_at"));
                                        dashnotes_ske.setNoteWriter(jobj_dashnotes.getString("written_by"));
                                        dashnotes_ske.setNotes(jobj_dashnotes.getString("notes"));
                                        arraylistLabrperformed.add(dashnotes_ske);
                                    }

                                    JSONArray jArray_Dashboardnotes = jobjInside.getJSONArray("Dashboard");
                                    ArrayList<DashboardNotes_Skeleton> arraylistDashboard = new ArrayList<>();
                                    for (int k = 0; k < jArray_Dashboardnotes.length(); k++) {
                                        JSONObject jobj_dashnotes = jArray_Dashboardnotes.getJSONObject(k);
                                        DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
                                        dashnotes_ske.setCreatedAt(jobj_dashnotes.getString("created_at"));
                                        dashnotes_ske.setNoteWriter(jobj_dashnotes.getString("written_by"));
                                        dashnotes_ske.setNotes(jobj_dashnotes.getString("notes"));
                                        arraylistDashboard.add(dashnotes_ske);
                                    }

                                    String OffTheRecord = gson.toJson(arraylistOffTheRecord);
                                    String laborPerformed = gson.toJson(arraylistLabrperformed);
                                    String dashboardNotes = gson.toJson(arraylistDashboard);

                                    ContentValues cv = new ContentValues();
                                    ContentValues cvnew = new ContentValues();
                                    if (arraylistOffTheRecord.size() > 0) {
                                        cv.put(ParseOpenHelper.JOBSTECHOFFTHERECORDCURRDAY, OffTheRecord);
                                        cvnew.put(ParseOpenHelper.JOBSTECHOFFTHERECORD, OffTheRecord);
                                    }
                                    if (arraylistLabrperformed.size() > 0) {
                                        cv.put(ParseOpenHelper.JOBSTECHLABORPERFORMCURRDAY, laborPerformed);
                                        cvnew.put(ParseOpenHelper.JOBSTECHLABORPERFORM, laborPerformed);
                                    }
                                    if (arraylistDashboard.size() > 0) {
                                        cv.put(ParseOpenHelper.JOBSTECHDASHBOARDNOTESCURRDAY, dashboardNotes);
                                        cvnew.put(ParseOpenHelper.JOBSTECHDASHBOARDNOTES, dashboardNotes);
                                    }

                                    //update all jobs current days table
                                    database.update(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, cv, ParseOpenHelper.TECHIDCURRDAY + " =? AND " + ParseOpenHelper.JOBIDCURRDAY + " = ?",
                                            new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQ_ID), jobid});
                                    //update all jobs table
                                    database.update(ParseOpenHelper.TABLENAME_ALLJOBS, cvnew, ParseOpenHelper.TECHID + " =? AND " + ParseOpenHelper.JOBID + " = ?",
                                            new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQ_ID), jobid});
                                    database.delete(ParseOpenHelper.TABLE_SUBMITMYLABOR_NEWOFFRECORD, ParseOpenHelper.SUBMITLABORJOBID + " =?",
                                            new String[]{jobid});
                                }

                            } else {
                                HandyObject.showAlert(getContext(), jsonObject.getString("message"));
                                if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                    HandyObject.deleteAllDatabase(getContext());
                                    HandyObject.clearpref(getContext());
                                    App.appInstance.stopTimer();
                                    /*Intent intent_reg = new Intent(getContext(), LoginActivity.class);
                                    startActivity(intent_reg);
                                    getActivity().finish();
                                    getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);*/
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            HandyObject.stopProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        HandyObject.stopProgressDialog();
                    }
                });
    }

    public static void scheduleJob() {
        new JobRequest.Builder(SyncSubmitLaborPerf.TAG)
                .setBackoffCriteria(5L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setExact(10L)
                .build()
                .schedule();
    }


}