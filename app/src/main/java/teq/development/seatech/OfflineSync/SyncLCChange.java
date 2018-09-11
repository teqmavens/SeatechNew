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
import teq.development.seatech.JobDetail.Skeleton.LCChangeSkeleton;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class SyncLCChange extends Job {

    public static final String TAG = "SyncLCChange_tag";
    private SQLiteDatabase database;
    Gson gson;
    ArrayList<LCChangeSkeleton> arrayList;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e("SyncLCChange", "SyncLCChange");
        database = ParseOpenHelper.getInstance(getContext()).getWritableDatabase();
        arrayList = new ArrayList<>();
        gson = new Gson();
        Cursor cursor = database.query(ParseOpenHelper.TABLE_LCCHANGE, null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    LCChangeSkeleton ske = new LCChangeSkeleton();
                    ske.setTech_id(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.LCCHANGETECHID)));
                    ske.setJob_id(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.LCCHANGEJOBID)));
                    ske.setLabour_code(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.LCCHANGELC)));
                    ske.setStart_time(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.LCCHANGESTARTTIME)));
                    ske.setEnd_time(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.LCCHANGEENDTIME)));
                    ske.setHours(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.LCCHANGEHHOURS)));
                    ske.setHours_adjusted(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.LCCHANGEHHOURSADJUSTED)));
                    ske.setCreated_by(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.LCCHANGECREATEDBY)));
                    arrayList.add(ske);
                    cursor.moveToNext();
                }
                SyncData();
                cursor.close();
            } else {
                HandyObject.showAlert(getContext(), "cursor not greater than zero");
            }
        } else {
            HandyObject.showAlert(getContext(), "cursor null");
        }
        return Result.SUCCESS;
    }

    private void SyncData() {
        String techlog = gson.toJson(arrayList);
        HandyObject.getApiManagerMain().submitLCdata(techlog, HandyObject.getPrams(getContext(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("RespLCChange", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobj = jsonArray.getJSONObject(i);
                                    //Delete related row from database
                                    database.delete(ParseOpenHelper.TABLE_LCCHANGE, ParseOpenHelper.LCCHANGEJOBID + " =?",
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
        new JobRequest.Builder(SyncLCChange.TAG)
                .setBackoffCriteria(5L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setExact(10L)
                .build()
                .schedule();
    }
}
