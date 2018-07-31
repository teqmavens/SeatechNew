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
import teq.development.seatech.databinding.PopupNeedestimateBinding;

public class NeedEstimateDialog extends DialogFragment {

    Dialog dialog;
    public static String jobid;
    PopupNeedestimateBinding binding;
    SQLiteDatabase database;

    static NeedEstimateDialog newInstance(String id) {
        NeedEstimateDialog f = new NeedEstimateDialog();
        Bundle args = new Bundle();
        /*  args.putInt("num", num);*/
        jobid = id;
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_needestimate, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setPopupneedestimate(this);
        //  initViews();
        database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        return rootView;
    }

    public void onClickSave() {
        if (binding.etDescription.getText().length() == 0) {
            HandyObject.showAlert(getActivity(), getString(R.string.fieldempty));
        } else {
            if (binding.checkboxNeedestimate.isChecked()) {
                insertToDB("1");
            } else {
                insertToDB("0");
            }
        }
    }

    private void insertToDB(String urgent) {
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.ESTIMATETECHID, HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID));
        cv.put(ParseOpenHelper.ESTIMATEJOBID, urgent);
        cv.put(ParseOpenHelper.ESTIMATEDESCRIPTION, binding.etDescription.getText().toString());
        cv.put(ParseOpenHelper.ESTIMATEURGENT, urgent);
        long id = database.insert(ParseOpenHelper.TABLENAME_NEEDESTIMATE, null, cv);

        NeedEstimateTask(binding.etDescription.getText().toString(), jobid, urgent);
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

    private void NeedEstimateTask(String message, String jobid, String urgent) {
        HandyObject.showProgressDialog(getActivity());
        HandyObject.getApiManagerMain().NeedEstimate(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), message, jobid, urgent, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responsneedestimate", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                dialog.dismiss();
                                HandyObject.showAlert(getActivity(), jsonObject.getString("message"));


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

    }

    public void onClickCross() {
        dialog.dismiss();
    }
}
