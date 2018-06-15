package teq.development.seatech.Profile;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.FrgmChangepwdBinding;

public class ChangePwdFragment extends Fragment {

    FrgmChangepwdBinding binding;
    DashBoardActivity activity;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DashBoardActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_changepwd, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setChangepassword(this);
        return rootView;
    }

    public void onClickSave() {
        if (binding.etCurrentpwd.getText().toString().length() == 0) {
            binding.etCurrentpwd.setError(getString(R.string.fieldempty));
        } else if (binding.etNewpwd.getText().toString().length() == 0) {
            binding.etNewpwd.setError(getString(R.string.fieldempty));
        } else if (binding.etReenterpwd.getText().toString().length() == 0) {
            binding.etReenterpwd.setError(getString(R.string.fieldempty));
        } else if (binding.etNewpwd.getText().toString().compareTo(binding.etReenterpwd.getText().toString()) != 0) {
            binding.etNewpwd.setError("");
            binding.etReenterpwd.setError("");
            HandyObject.showAlert(context, getString(R.string.pwdnotmatch));
        } else if (HandyObject.checkInternetConnection(context)) {
            pwdChangeTask(binding.etCurrentpwd.getText().toString(), binding.etNewpwd.getText().toString(), binding.etReenterpwd.getText().toString());
        } else {
            HandyObject.showAlert(context, getString(R.string.check_internet_connection));
        }
    }

    private void pwdChangeTask(String currentpwd, String newpwd, String reenterpwd) {
        HandyObject.showProgressDialog(context);
        HandyObject.getApiManagerType().changePwd(currentpwd, newpwd, reenterpwd, HandyObject.getPrams(context, AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("response", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                HandyObject.showAlert(context, jsonObject.getString("message"));
                                getFragmentManager().popBackStack();
                       /*      android.content.Intent intent_reg = new android.content.Intent(getActivity(), teq.development.seatech.LoginActivity.class);
      intent_reg.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
      getA
startActivity(intent_reg);*/


                            } else {
                                HandyObject.showAlert(context, jsonObject.getString("message"));
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

    public void OnClickCancel() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
