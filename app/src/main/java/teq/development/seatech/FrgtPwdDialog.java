package teq.development.seatech;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.DialogFrgtpwdBinding;

public class FrgtPwdDialog extends DialogFragment {

    Dialog dialog;
    DialogFrgtpwdBinding binding;

    static FrgtPwdDialog newInstance(int num) {
        FrgtPwdDialog f = new FrgtPwdDialog();
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mNum = getArguments().getInt("num");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_frgtpwd, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setDialogfrgtpwd(this);
        return rootView;
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
        return dialog;
    }

    public void onClickSubmit() {
        if (binding.etEmail.getText().toString().length() == 0) {
            binding.etEmail.setError(getString(R.string.fieldempty));
            binding.etEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()) {
            binding.etEmail.setError("Enter valid email");
            binding.etEmail.requestFocus();
        } else if (HandyObject.checkInternetConnection(getActivity())) {
            FrgtPwdTask(binding.etEmail.getText().toString());
        } else {
            Toast.makeText(getActivity(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void FrgtPwdTask(String email) {
        HandyObject.showProgressDialog(getActivity());
        HandyObject.getApiManagerType().frgtPwd(email, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("response", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                dialog.dismiss();
                            } else {
                                HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
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
