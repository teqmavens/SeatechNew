package teq.development.seatech.JobDetail;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.PopupNeedchangeorderBinding;

public class AddTechNotesDialog extends DialogFragment implements View.OnClickListener {

    Dialog dialog;
    private EditText et_laborperform;
    static String comingJobId;
    private SQLiteDatabase database;
    public static ArrayList<DashboardNotes_Skeleton> arraylistOffTheRecord;
    Gson gson;

    static AddTechNotesDialog newInstance(String jobid) {
        AddTechNotesDialog f = new AddTechNotesDialog();
        Bundle args = new Bundle();
        //  args.putInt("num", num);
        comingJobId = jobid;
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_addtechnotes, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        gson = new Gson();
        Button submit = (Button) rootView.findViewById(R.id.submit);
        ImageView cross = (ImageView) rootView.findViewById(R.id.cross);
        et_laborperform = (EditText) rootView.findViewById(R.id.et_laborperform);
        submit.setOnClickListener(this);
        cross.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = width - 420;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        ;
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
            case R.id.submit:
                SubmitMyLaborPerf_Task(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), comingJobId, et_laborperform.getText().toString(), "Off The Record");

                break;
            case R.id.cross:
                dialog.dismiss();
                break;
        }
    }

    private void SubmitMyLaborPerf_Task(String techid, final String jobid, String notes, String type) {

        HandyObject.showProgressDialog(getActivity());
     //   HandyObject.getApiManagerMain().submitTechLaborPerf(techid, jobid, notes, type, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
        DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
        dashnotes_ske.setCreatedAt(HandyObject.ParseDateTimeForNotes(new Date()));
       // HandyObject.parseDateToYMD()
        dashnotes_ske.setNoteWriter(techid);
        dashnotes_ske.setNotes(notes);
        dashnotes_ske.setTechid(techid);
        dashnotes_ske.setJobid(jobid);
        dashnotes_ske.setType(type);
        ArrayList<DashboardNotes_Skeleton> addtech = new ArrayList<>();
        addtech.add(dashnotes_ske);
        String OffTheRecord = gson.toJson(addtech);

        HandyObject.getApiManagerMain().submitTechLaborPerf(OffTheRecord, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseMyLaborPerform", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                JSONObject jobjdata = jsonObject.getJSONObject("data");
                                // arraylistOffTheRecord = new ArrayList<>();
                                DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
                                dashnotes_ske.setCreatedAt(jobjdata.getString("created_at"));
                                dashnotes_ske.setNoteWriter(jobjdata.getString("written_by"));
                                dashnotes_ske.setNotes(jobjdata.getString("notes"));

                                JobDetailFragment.addTechlistOffTheRecord.add(dashnotes_ske);

                                String OffTheRecord = gson.toJson(arraylistOffTheRecord);
                                database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
                                ContentValues cv = new ContentValues();
                                cv.put(ParseOpenHelper.JOBSTECHOFFTHERECORD, OffTheRecord);

                                database.update(ParseOpenHelper.TABLENAME_ALLJOBS, cv, ParseOpenHelper.TECHID + " =? AND " + ParseOpenHelper.JOBID + " = ?",
                                        new String[]{HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), jobid});

                                Intent intent = new Intent("pass_addtechlast");
                                intent.putExtra("lastofftherecord",jobjdata.getString("notes"));
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                                dialog.dismiss();
                                HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                            } else {
                                HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                if(jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                    HandyObject.clearpref(getActivity());
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


    }
}
