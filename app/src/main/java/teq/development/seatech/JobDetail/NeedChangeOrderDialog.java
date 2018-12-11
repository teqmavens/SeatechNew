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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.JobDetail.Skeleton.NeedEstimateSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.PopupNeedchangeorderBinding;

public class NeedChangeOrderDialog extends DialogFragment {

    Dialog dialog;
    public static String jobid;
    PopupNeedchangeorderBinding binding;
    SQLiteDatabase database;
    Gson gson;

    static NeedChangeOrderDialog newInstance(String id) {
        NeedChangeOrderDialog f = new NeedChangeOrderDialog();
        Bundle args = new Bundle();
        // args.putInt("num", num);
        jobid = id;
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_needchangeorder, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setPopupneedchangeorder(this);
        //initViews();
        gson = new Gson();
        database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        return rootView;
    }

    public void onClickSave() {
        if (binding.etDescription.getText().length() == 0) {
            HandyObject.showAlert(getActivity(), getString(R.string.fieldempty));
        } else {
            if (binding.checkboxNeedestimate.isChecked()) {
                NeedEstimateTask(binding.etDescription.getText().toString(), jobid, "1");
            } else {
                NeedEstimateTask(binding.etDescription.getText().toString(), jobid, "0");
            }
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

    private void NeedEstimateTask(final String message, final String jobid, final String urgent) {
        HandyObject.showProgressDialog(getActivity());

        NeedEstimateSkeleton needestimate_ske = new NeedEstimateSkeleton();
        needestimate_ske.setCreatedAt(HandyObject.ParseDateTimeForNotes(new Date()));
        final String insertedTime = HandyObject.ParseDateTimeForNotes(new Date());
        needestimate_ske.setMessage(message);
        needestimate_ske.setTech_id(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID));
        needestimate_ske.setJob_id(jobid);
        needestimate_ske.setUrgent(urgent);
        ArrayList<NeedEstimateSkeleton> needEstimate = new ArrayList<>();
        needEstimate.add(needestimate_ske);
        String jobrequest = gson.toJson(needEstimate);

      //  insertIntoDB(insertedTime, HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), jobid, message, urgent);
        if (HandyObject.checkInternetConnection(getActivity())) {
            HandyObject.getApiManagerMain().NeedEstimate(jobrequest, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                    /*HandyObject.getApiManagerMain().NeedEstimate(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), message, jobid, urgent, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))*/
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.e("responsneedchange", jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                    dialog.dismiss();
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                    JSONArray jarry_compose = new JSONArray();
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobj = jsonArray.getJSONObject(i);
                                        JSONObject jobj_compose = new JSONObject();
                                        jobj_compose.put("receiver_id", jobj.getString("receiver_id"));
                                        jobj_compose.put("receiver_name", jobj.getString("receiver_name"));
                                        jarry_compose.put(jobj_compose);

                                        //Delete related row from database
                                       /* database.delete(ParseOpenHelper.TABLENAME_NEEDESTIMATE, ParseOpenHelper.ESTIMATEJOBID + " =? AND " + ParseOpenHelper.ESTIMATECREATEDAT + " = ?",
                                                new String[]{jobj.getString("job_id"), insertedTime});*/
                                    }

                                    UUID uniqueKey = UUID.randomUUID();
                                    JSONObject jobjmain = new JSONObject();
                                    jobjmain.put("job_id", jobid);
                                    jobjmain.put("sender_id", Integer.parseInt(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID)));
                                    jobjmain.put("sender_name", HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_USERNAME));
                                    jobjmain.put("receiver", jarry_compose);
                                    jobjmain.put("urgent", urgent);
                                    jobjmain.put("token", String.valueOf(uniqueKey));
                                    jobjmain.put("message", message);
                                    App.appInstance.getSocket().emit("send chat message", jobjmain);

                                } else {
                                    dialog.dismiss();
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                    if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
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
        } else {
           // HandyObject.showAlert(getActivity(), getString(R.string.fetchdata_whenonline));
            HandyObject.showAlert(getActivity(), getString(R.string.check_internet_connection));
            HandyObject.stopProgressDialog();
            dialog.dismiss();
        }
    }

    public void onClickCross() {
        dialog.dismiss();
    }

    private void insertIntoDB(String time, String techid, String jobid, String description, String urgent) {
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.ESTIMATECREATEDAT, time);
        cv.put(ParseOpenHelper.ESTIMATETECHID, techid);
        cv.put(ParseOpenHelper.ESTIMATEJOBID, jobid);
        cv.put(ParseOpenHelper.ESTIMATEDESCRIPTION, description);
        cv.put(ParseOpenHelper.ESTIMATEURGENT, urgent);
        long idd = database.insert(ParseOpenHelper.TABLENAME_NEEDESTIMATE, null, cv);
    }
}
