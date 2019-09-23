package teq.development.seatech.OfflineSync;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class SyncJobStart extends Job {

    public static final String TAG = "JobStart_tag";
    private SQLiteDatabase database;
    String jobid,isstart;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e("SyncJobStart", "SyncJobStart");
        database = ParseOpenHelper.getInstance(getContext()).getWritableDatabase();
        Cursor cursor = database.query(ParseOpenHelper.TABLE_STARTJOB, null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                     isstart = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ISJOBSTARTED));
                     jobid = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTARTED_JOBID));
                     cursor.moveToNext();
                }
                if(isstart.equalsIgnoreCase("yes")) {
                    SyncData(jobid);
                }
                cursor.close();
            } else {
                // HandyObject.showAlert(getContext(), "cursor not greater than zero");
            }
        } else {
            //    HandyObject.showAlert(getContext(), "cursor null");
        }
        return Result.SUCCESS;
    }

    private void SyncData(String jobid) {
        Log.e("STARTJOBID",jobid);
        HandyObject.getApiManagerMain().startJob(jobid, HandyObject.getPrams(getContext(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("respnsSTARTJOB", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                // HandyObject.showAlert(context,"STARTED");
                                //Delete related row from database
                                database.delete(ParseOpenHelper.TABLE_STARTJOB, null,
                                        null);
                            } else {
                                HandyObject.showAlert(getContext(), jsonObject.getString("message"));
                                if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                    HandyObject.clearpref(getContext());
                                    HandyObject.deleteAllDatabase(getContext());
                                    App.appInstance.stopTimer();
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
        new JobRequest.Builder(SyncJobStart.TAG)
                .setBackoffCriteria(5L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setExact(10L)
                .build()
                .schedule();
    }
}
