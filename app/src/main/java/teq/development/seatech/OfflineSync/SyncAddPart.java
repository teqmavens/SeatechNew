package teq.development.seatech.OfflineSync;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Dashboard.Skeleton.PartsSkeleton;
import teq.development.seatech.JobDetail.Skeleton.AddPartSkeleton;
import teq.development.seatech.JobDetail.Skeleton.UploadImageSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class SyncAddPart extends Job {

    public static final String TAG = "SyncAddPart_tag";
    private SQLiteDatabase database;
    ArrayList<AddPartSkeleton> arrayList;
    ArrayList<File> Allimagesfile;
    ArrayList<String> countArrayList;
    MediaType MEDIA_TYPE_FORM = MediaType.parse("image/png");
    Gson gson;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.e("SyncAddPart", "SyncAddPart");
        database = ParseOpenHelper.getInstance(getContext()).getWritableDatabase();
        arrayList = new ArrayList<>();
        Allimagesfile = new ArrayList<>();
        countArrayList = new ArrayList<>();
        gson = new Gson();
        Cursor cursor = database.query(ParseOpenHelper.TABLE_ADDPART, null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    Type type = new TypeToken<ArrayList<File>>() {
                    }.getType();
                    AddPartSkeleton ske = new AddPartSkeleton();
                    ske.setCount(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTCOUNT)));
                    ske.setTech_id(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTTECHID)));
                    ske.setJob_id(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTJOBID)));
                    ske.setUrgent(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTURGENT)));
                    ske.setPart_description(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTDESCRIPTION)));
                    ske.setHow_fast_needed(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTHOWFASTNEEDED)));
                    ske.setPrice_approval_required(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTPRICEAPPROVALREQUIRED)));
                    ske.setPart_for_repair(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTFORREPAIR)));
                    ske.setManufacturer_id(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTMANUFACTID)));
                    ske.setPart_no(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTNO)));
                    ske.setQuantity_needed(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTQUANTITYNEEDED)));
                    ske.setSerial_no(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTSERIALNO)));
                    ske.setFailure_description(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTFAILUREDESC)));
                    ske.setTech_support_name(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTTECHSUPPORTNAME)));
                    ske.setRma_or_case_from_mfg_support(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTSETRMAORCASE)));
                    ske.setMfg_deem_this_warranty(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTMFGDEEMTHISWARRANTY)));
                    ske.setAdvance_replacement(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTADVANCEREPLACEMENT)));
                    ske.setPart_sold_by_seatech(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTSOLDBYSEATECH)));
                    ske.setNeed_loner(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTNEEDLOANER)));
                    ske.setNotes(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTNOTES)));
                    String allimages = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTUPLOADEDIMAGES));
                    if (Allimagesfile == null || Allimagesfile.size() < 1) {
                    } else {
                        ArrayList<File> file = gson.fromJson(allimages, type);
                        for (int i = 0; i < file.size(); i++) {
                            Allimagesfile.add(file.get(i));
                            countArrayList.add(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.ADDPARTCOUNT)));
                        }
                    }
                    arrayList.add(ske);
                    cursor.moveToNext();
                }
                SyncData();
                cursor.close();
            } else {
                //HandyObject.showAlert(getContext(), "cursor not greater than zero");
            }
        } else {
            //HandyObject.showAlert(getContext(), "cursor null");
        }
        return Result.SUCCESS;
    }

    private void SyncData() {

        String part_request = gson.toJson(arrayList);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("part_request", part_request);

        if (Allimagesfile == null || Allimagesfile.size() < 1) {
        } else if (Allimagesfile.size() > 0) {
            ArrayList<File> imagesData = Allimagesfile;
            // co.showLoading();
            for (int i = 0; i < imagesData.size(); i++) {
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("img_" + countArrayList.get(i) + "[" + i + "]", "image" + i + ".png",
                                RequestBody.create(MEDIA_TYPE_FORM, imagesData.get(i)))
                        .build();
            }
        }

        MultipartBody requestBody = builder.build();
        // HandyObject.showProgressDialog(getContext());
        HandyObject.getApiManagerMain().NeedPartData(requestBody, HandyObject.getPrams(getContext(), AppConstants.LOGIN_SESSIONID)).enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("needpart", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                              //  HandyObject.showAlert(getContext(), jsonObject.getString("message"));
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobjInside = jsonArray.getJSONObject(i);
                                    String jobid = jobjInside.getString("job_id");

                                    JSONArray jArray_parts = jobjInside.getJSONArray("parts");
                                    ArrayList<PartsSkeleton> arraylistParts = new ArrayList<>();
                                    for (int k = 0; k < jArray_parts.length(); k++) {
                                        JSONObject jobj_parts = jArray_parts.getJSONObject(k);
                                        PartsSkeleton parts_ske = new PartsSkeleton();
                                        parts_ske.setManufname(jobj_parts.getString("mfg_name"));
                                        parts_ske.setPartdesc(jobj_parts.getString("part_description"));
                                        parts_ske.setEta(jobj_parts.getString("eta"));
                                        parts_ske.setLocation(jobj_parts.getString("part_location"));
                                        parts_ske.setHavepart(jobj_parts.getString("have_parts"));
                                        arraylistParts.add(parts_ske);
                                    }

                                    String PartsRecords = gson.toJson(arraylistParts);
                                    ContentValues cv = new ContentValues();
                                    ContentValues cvnew = new ContentValues();
                                    if (arraylistParts.size() > 0) {
                                        cv.put(ParseOpenHelper.JOBSTECHPARTSRECORDCURRDAY, PartsRecords);
                                        cvnew.put(ParseOpenHelper.JOBSTECHPARTSRECORD, PartsRecords);
                                    }

                                    //update all jobs current days table
                                    database.update(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, cv, ParseOpenHelper.TECHIDCURRDAY + " =? AND " + ParseOpenHelper.JOBIDCURRDAY + " = ?",
                                            new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQ_ID), jobid});
                                    //update all jobs table
                                    database.update(ParseOpenHelper.TABLENAME_ALLJOBS, cvnew, ParseOpenHelper.TECHID + " =? AND " + ParseOpenHelper.JOBID + " = ?",
                                            new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQ_ID), jobid});

                                    database.delete(ParseOpenHelper.TABLE_ADDPART, ParseOpenHelper.ADDPARTJOBID + "=?",
                                            new String[]{jobid});
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
                            //  commonObjects.showHideProgressBar(getActivity());
                            // HandyObject.stopProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        // commonObjects.showHideProgressBar(getActivity());
                        HandyObject.stopProgressDialog();
                    }
                }
        );

    }

    public static void scheduleJob() {
        new JobRequest.Builder(SyncAddPart.TAG)
                .setBackoffCriteria(5L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setExact(10L)
                .build()
                .schedule();
    }
}
