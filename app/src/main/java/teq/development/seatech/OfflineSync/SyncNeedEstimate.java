package teq.development.seatech.OfflineSync;

import android.content.ContentValues;
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
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.JobDetail.Skeleton.NeedEstimateSkeleton;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class SyncNeedEstimate extends Job {

    public static final String TAG = "NeedEstimate_tag";
    private SQLiteDatabase database;
    ArrayList<NeedEstimateSkeleton> arrayList;
    Gson gson;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e("SyncNeedEstimate", "SyncNeedEstimate");
        database = ParseOpenHelper.getInstance(getContext()).getWritableDatabase();
        arrayList = new ArrayList<>();
        gson = new Gson();
        Cursor cursor = database.query(ParseOpenHelper.TABLENAME_NEEDESTIMATE, null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    NeedEstimateSkeleton needestimate_ske = new NeedEstimateSkeleton();
                    needestimate_ske.setCreatedAt(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ESTIMATECREATEDAT)));
                    needestimate_ske.setMessage(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ESTIMATEDESCRIPTION)));
                    needestimate_ske.setTech_id(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ESTIMATETECHID)));
                    needestimate_ske.setJob_id(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ESTIMATEJOBID)));
                    needestimate_ske.setUrgent(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ESTIMATEURGENT)));
                    arrayList.add(needestimate_ske);
                    cursor.moveToNext();
                }
                SyncData();
                cursor.close();
            } else {
               // HandyObject.showAlert(getContext(), "cursor not greater than zero");
            }
        } else {
        //    HandyObject.showAlert(getContext(), "cursor null");
        }
        return Result.SUCCESS;
    }

    private void SyncData() {
        String jobrequest = gson.toJson(arrayList);
        HandyObject.getApiManagerMain().NeedEstimate(jobrequest, HandyObject.getPrams(getContext(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("respNeedEstimate", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++) {
                                    JSONObject jobj = jsonArray.getJSONObject(i);
                                    database.delete(ParseOpenHelper.TABLENAME_NEEDESTIMATE, ParseOpenHelper.ESTIMATEJOBID + " =?",
                                            new String[]{jobj.getString("job_id")});
                                }
                            } else {
                                HandyObject.showAlert(getContext(), jsonObject.getString("message"));
                                if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                    HandyObject.clearpref(getContext());
                                    HandyObject.deleteAllDatabase(getContext());
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
        new JobRequest.Builder(SyncNeedEstimate.TAG)
                .setBackoffCriteria(5L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setExact(10L)
                .build()
                .schedule();
    }
}
