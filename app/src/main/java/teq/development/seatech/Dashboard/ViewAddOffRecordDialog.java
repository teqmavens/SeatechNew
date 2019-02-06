package teq.development.seatech.Dashboard;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.JobDetail.Adapter.AdapterDashboardNotes;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class ViewAddOffRecordDialog extends DialogFragment implements View.OnClickListener {

    Dialog dialog;
    SQLiteDatabase database;
    Gson gson;
    ArrayList<AllJobsSkeleton> arralistAllJobs;
    static int position;
    static String comingJobId;
    EditText et_addtechnotes;
    RecyclerView rcyview_dashbrdnotes;
    String insertedTime = "";
    TextView norecord;

    static ViewAddOffRecordDialog newInstance(int num, String jobid) {
        ViewAddOffRecordDialog f = new ViewAddOffRecordDialog();
        Bundle args = new Bundle();
        //  args.putInt("num", num);
        position = num;
        comingJobId = jobid;
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_viewtechnotes, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        ImageView cross = (ImageView) rootView.findViewById(R.id.cross);
        et_addtechnotes = (EditText) rootView.findViewById(R.id.et_addtechnotes);
        rcyview_dashbrdnotes = (RecyclerView) rootView.findViewById(R.id.rcyview_dashbrdnotes);
        norecord = (TextView) rootView.findViewById(R.id.norecord);
        Button submit = (Button) rootView.findViewById(R.id.submit);
        TextView toptext = (TextView) rootView.findViewById(R.id.toptext);
        TextView toptextview = (TextView) rootView.findViewById(R.id.toptextview);

        toptext.setText("Add a new off the record");
        toptextview.setText("View off the record Notes");
        cross.setOnClickListener(this);
        submit.setOnClickListener(this);

        database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        gson = new Gson();
        Cursor cursor = database.query(ParseOpenHelper.TABLENAME_ALLJOBS, null, ParseOpenHelper.TECHID + "=?", new String[]{HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID)}, null, null, null);
        cursor.moveToFirst();
        arralistAllJobs = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            Type type = new TypeToken<AllJobsSkeleton>() {
            }.getType();
            Type typedashnotes = new TypeToken<ArrayList<DashboardNotes_Skeleton>>() {
            }.getType();
            String getSke = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSSKELETON));
            String getSkedashnotes = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHOFFTHERECORD));

            AllJobsSkeleton ske = gson.fromJson(getSke, type);
            ArrayList<DashboardNotes_Skeleton> arrayListDash = gson.fromJson(getSkedashnotes, typedashnotes);
            ske.setArrayListOffTheRecord(arrayListDash);
            arralistAllJobs.add(ske);
            //     arrayListDashNotes.addAll(arrayListDash);
            cursor.moveToNext();
        }
        cursor.close();
        LinearLayoutManager lLManagerDashNotes = new LinearLayoutManager(getActivity());
        lLManagerDashNotes.setOrientation(LinearLayoutManager.VERTICAL);
        rcyview_dashbrdnotes.setLayoutManager(lLManagerDashNotes);
        if (arralistAllJobs.get(position).getArrayListOffTheRecord().size() == 0) {
            norecord.setVisibility(View.VISIBLE);
            rcyview_dashbrdnotes.setVisibility(View.GONE);
            norecord.setText(getString(R.string.nonotesavailable));
        } else {
            norecord.setVisibility(View.GONE);
            rcyview_dashbrdnotes.setVisibility(View.VISIBLE);
            AdapterDashboardNotes adapterdashnotes = new AdapterDashboardNotes(getActivity(), arralistAllJobs.get(position).getArrayListOffTheRecord());
            rcyview_dashbrdnotes.setNestedScrollingEnabled(false);
            rcyview_dashbrdnotes.setAdapter(adapterdashnotes);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = width - 220;
        // params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = height - 320;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public void onClickCross() {
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross:
                dialog.dismiss();
                break;
            case R.id.submit:
                SubmitAddDashboard(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), comingJobId, et_addtechnotes.getText().toString(), "Off The Record");
                break;
        }
    }

    private void SubmitAddDashboard(String techid, final String jobid, String notes, String type) {

        HandyObject.showProgressDialog(getActivity());
        //   HandyObject.getApiManagerMain().submitTechLaborPerf(techid, jobid, notes, type, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
        DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
        dashnotes_ske.setCreatedAt(HandyObject.ParseDateTimeForNotes(new Date()));
        insertedTime = HandyObject.ParseDateTimeForNotes(new Date());

        dashnotes_ske.setNotes(notes);
        dashnotes_ske.setTechid(techid);
        dashnotes_ske.setJobid(jobid);
        dashnotes_ske.setType(type);
        ArrayList<DashboardNotes_Skeleton> addtech = new ArrayList<>();
        addtech.add(dashnotes_ske);
        String OffTheRecord = gson.toJson(addtech);
        //String OffTheRecordske = gson.toJson(dashnotes_ske);
        //  HandyObject.showAlert(getActivity(),OffTheRecord);
        insertIntoDB(HandyObject.ParseDateTimeForNotes(new Date()), OffTheRecord, techid, jobid, notes, type);
        if (HandyObject.checkInternetConnection(getActivity())) {
            HandyObject.getApiManagerMain().submitTechLaborPerf(OffTheRecord, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.e("responseAddDashboard", jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.getString("status").toLowerCase().equals("success")) {
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
                                            // JobDetailFragment.addTechlistOffTheRecord.add(dashnotes_ske);
                                            // VMJobDetail.addTechlistOffTheRecord.add(dashnotes_ske);
                                        }

                                        String OffTheRecord = gson.toJson(arraylistOffTheRecord);
                                        ContentValues cv = new ContentValues();
                                        ContentValues cv_current = new ContentValues();
                                        cv.put(ParseOpenHelper.JOBSTECHOFFTHERECORD, OffTheRecord);
                                        cv_current.put(ParseOpenHelper.JOBSTECHOFFTHERECORDCURRDAY, OffTheRecord);
                                        database.update(ParseOpenHelper.TABLENAME_ALLJOBS, cv, ParseOpenHelper.TECHID + " =? AND " + ParseOpenHelper.JOBID + " = ?",
                                                new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQ_ID), jobid});

                                        database.update(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, cv_current, ParseOpenHelper.TECHIDCURRDAY + " =? AND " + ParseOpenHelper.JOBIDCURRDAY + " = ?",
                                                new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQ_ID), jobid});

                                        norecord.setVisibility(View.GONE);
                                        rcyview_dashbrdnotes.setVisibility(View.VISIBLE);
                                        AdapterDashboardNotes adapterdashnotes = new AdapterDashboardNotes(getActivity(), arraylistOffTheRecord);
                                        // rcyview_dashbrdnotes.setNestedScrollingEnabled(false);
                                        rcyview_dashbrdnotes.setAdapter(adapterdashnotes);
                                        et_addtechnotes.setText("");

                                    }


                                    //Delete related row from database
                                    database.delete(ParseOpenHelper.TABLE_SUBMITMYLABOR_NEWOFFRECORD, ParseOpenHelper.SUBMITLABORJOBID + " =? AND " + ParseOpenHelper.SUBMITLABORTIME + " = ?",
                                            new String[]{jobid, insertedTime});

                                    // dialog.dismiss();
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                } else {
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                    if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                        HandyObject.clearpref(getActivity());
                                        HandyObject.deleteAllDatabase(getActivity());
                                        App.appInstance.stopTimer();
                                        Intent intent_reg = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent_reg);
                                        getActivity().finish();
                                        getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
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
        } else {
            HandyObject.showAlert(getActivity(), getString(R.string.fetchdata_whenonline));
            HandyObject.stopProgressDialog();
            dialog.dismiss();
        }
    }

    private void insertIntoDB(String time, String offTheRecord, String techid, String jobid, String notes, String type) {
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.SUBMITLABORTECHID, techid);
        cv.put(ParseOpenHelper.SUBMITLABORJOBID, jobid);
        cv.put(ParseOpenHelper.SUBMITLABORTYPE, type);
        cv.put(ParseOpenHelper.SUBMITLABORNOTES, notes);
        cv.put(ParseOpenHelper.SUBMITLABORTIME, time);
        cv.put(ParseOpenHelper.SUBMITLABORREST, offTheRecord);
        long idd = database.insert(ParseOpenHelper.TABLE_SUBMITMYLABOR_NEWOFFRECORD, null, cv);
    }
}

