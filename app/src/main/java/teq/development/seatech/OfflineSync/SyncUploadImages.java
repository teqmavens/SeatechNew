package teq.development.seatech.OfflineSync;

import android.content.ContentValues;
import android.content.Context;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.JobDetail.Skeleton.NeedEstimateSkeleton;
import teq.development.seatech.JobDetail.Skeleton.UploadImageSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class SyncUploadImages extends Job {

    public static final String TAG = "SyncUploadImages_tag";
    private SQLiteDatabase database;
    ArrayList<UploadImageSkeleton> arrayList;
    ArrayList<File> Allimagesfile;
    ArrayList<String> jobidArrayList;
    MediaType MEDIA_TYPE_FORM = MediaType.parse("image/png");
    Gson gson;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e("SyncUploadImages", "SyncUploadImages");
        database = ParseOpenHelper.getInstance(getContext()).getWritableDatabase();
        arrayList = new ArrayList<>();
        Allimagesfile = new ArrayList<>();
        jobidArrayList = new ArrayList<>();
        gson = new Gson();
        Cursor cursor = database.query(ParseOpenHelper.TABLE_UPLOADIMAGES, null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    Type type = new TypeToken<ArrayList<File>>() {
                    }.getType();
                    UploadImageSkeleton ske = new UploadImageSkeleton();
                    ske.setCreated_by(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.UPLOADIMAGESCREATEDBY)));
                    ske.setJob_id(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.UPLOADIMAGESJOBID)));
                    ske.setDescription(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.UPLOADIMAGESJOBID)));
                    String allimages = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.UPLOADIMAGESALLIMAGE));
                    ArrayList<File> file = gson.fromJson(allimages, type);
                    for (int i = 0; i < file.size(); i++) {
                        Allimagesfile.add(file.get(i));
                        jobidArrayList.add(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.UPLOADIMAGESJOBID)));
                    }
                    arrayList.add(ske);
                    cursor.moveToNext();
                }
                SyncData();
                cursor.close();
            } else {
               // HandyObject.showAlert(getContext(), "cursor not greater than zero");
            }
        } else {
         //   HandyObject.showAlert(getContext(), "cursor null");
        }
        return Result.SUCCESS;
    }

    private void SyncData() {
        MultipartBody responseBody = createResponseBody();

        HandyObject.getApiManagerMain().addJobImages(responseBody, HandyObject.getPrams(getContext(), AppConstants.LOGIN_SESSIONID)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String rep = response.body().string();
                    Log.e("RespSyncuploadImageOFF ", rep);
                    JSONObject jsonObject = new JSONObject(rep);
                    Gson gson = new Gson();
                    if (jsonObject.getString("status").toLowerCase().equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        ArrayList<String> arrayList = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobjInside = jsonArray.getJSONObject(i);
                            String jobid = jobjInside.getString("job_id");

                            JSONArray jArray_upldImages = jobjInside.getJSONArray("uploads");
                            ArrayList<String> arraylistupldImages = new ArrayList<>();
                            for (int k = 0; k < jArray_upldImages.length(); k++) {
                                arraylistupldImages.add(jArray_upldImages.getString(k));
                            }
                            String images = gson.toJson(arraylistupldImages);
                            if (arraylistupldImages.size() > 0) {
                                ContentValues cv = new ContentValues();
                                ContentValues cvnew = new ContentValues();
                                cv.put(ParseOpenHelper.JOBSTECHUPLOADEDIMAGESCURRDAY, images);
                                cvnew.put(ParseOpenHelper.JOBSTECHUPLOADEDIMAGES, images);
                                //update all jobs current day table
                                database.update(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, cv, ParseOpenHelper.TECHIDCURRDAY + " =? AND " + ParseOpenHelper.JOBIDCURRDAY + " = ?",
                                        new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQ_ID), jobid});
                                //update all jobs table
                                database.update(ParseOpenHelper.TABLENAME_ALLJOBS, cvnew, ParseOpenHelper.TECHID + " =? AND " + ParseOpenHelper.JOBID + " = ?",
                                        new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQ_ID), jobid});

                                //Delete related row from database
                                database.delete(ParseOpenHelper.TABLE_UPLOADIMAGES, ParseOpenHelper.UPLOADIMAGESJOBID + " =?",
                                        new String[]{jobid});
                            }



                        }
                    } else if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                        HandyObject.clearpref(getContext());
                        HandyObject.deleteAllDatabase(getContext());
                        App.appInstance.stopTimer();
                    } else {
                        HandyObject.showAlert(getContext(), jsonObject.getString("message"));
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
                t.printStackTrace();
                // co.myToast(t.getMessage());
                HandyObject.stopProgressDialog();
            }
        });
    }

    private MultipartBody createResponseBody() {
        ArrayList<File> imagesData = Allimagesfile;

        String uploaded_data = gson.toJson(arrayList);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("uploaded_data", uploaded_data)
                .build();

        for (int i = 0; i < imagesData.size(); i++) {
            builder.setType(MultipartBody.FORM)
                    .addFormDataPart("img_" + jobidArrayList.get(i) + "[" + i + "]", "image" + i + ".png",
                            RequestBody.create(MEDIA_TYPE_FORM, imagesData.get(i)))
                    .build();

        }
        return builder.build();
    }

    public static void scheduleJob() {
        new JobRequest.Builder(SyncUploadImages.TAG)
                .setBackoffCriteria(5L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setExact(10L)
                .build()
                .schedule();
    }

}
