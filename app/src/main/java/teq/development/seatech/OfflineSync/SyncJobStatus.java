package teq.development.seatech.OfflineSync;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.JobDetail.Skeleton.JobStatusSkeleton;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class SyncJobStatus extends Job {
    public static final String TAG = "SyncJobStatus_tag";
    private SQLiteDatabase database;
    ArrayList<JobStatusSkeleton> arrayList;
    Gson gson;


    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e("SyncJobStatus", "SyncJobStatus");
        database = ParseOpenHelper.getInstance(getContext()).getWritableDatabase();
        arrayList = new ArrayList<>();
        gson = new Gson();
        Cursor cursor = database.query(ParseOpenHelper.TABLE_JOBSTATUS, null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    JobStatusSkeleton ske = new JobStatusSkeleton();
                    ske.setTech_id(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSTECHID)));
                    ske.setJob_id(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSJOBID)));
                    ske.setJob_completed(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSCOMPLETED)));
                    ske.setCaptain_aware(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSCAPTAINAWARE)));
                    ske.setNotes(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSNOTES)));
                    ske.setSupply_amount(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSSUPPLAYAMT)));
                    ske.setNon_billable_hours(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSNBILLABLEHR)));
                    ske.setNon_billable_hours_description(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSNBILLABLEHRDESC)));
                    ske.setReturn_immediately(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSRETURNIMMED)));
                    ske.setAlready_scheduled(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSALREADYSCHEDULED)));
                    ske.setReason(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSREASON)));
                    ske.setDescription(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSDESCRIPTION)));
                    ske.setStart_time(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSSTARTTIME)));
                    ske.setEnd_time(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSENDTTIME)));
                    ske.setHours(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSHOURS)));
                    ske.setHours_adjusted(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSHOURSADJUSTED)));

                    ske.setHours_adjusted_end(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSHOURSADJUSTEDEND)));

                    ske.setLabour_code(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSLABOURCODE)));
                    ske.setBoat_name(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSBOATNAME)));
                    ske.setHull_id(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSHULLID)));
                    ske.setCaptain_name(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSCAPTIONNAME)));
                    ske.setCount(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTATUSCOUNT)));
                    arrayList.add(ske);
                    cursor.moveToNext();
                }
                SyncData();
                cursor.close();
            } else {
               // HandyObject.showAlert(getContext(), "cursor not greater than zero");
            }
        } else {
          //  HandyObject.showAlert(getContext(), "cursor null");
        }
        return Result.SUCCESS;
    }

    private void SyncData() {
        Gson gson = new Gson();
        String alldata = gson.toJson(arrayList);
        HandyObject.getApiManagerMain().JobStatusData(alldata, HandyObject.getPrams(getContext(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseJobStatus", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobj = jsonArray.getJSONObject(i);
                                    database.delete(ParseOpenHelper.TABLE_JOBSTATUS, ParseOpenHelper.JOBSTATUSJOBID + " =?",
                                            new String[]{jobj.getString("job_id")});
                                }
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
        new JobRequest.Builder(SyncJobStatus.TAG)
                .setBackoffCriteria(5L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setExact(10L)
                .build()
                .schedule();
    }
}
