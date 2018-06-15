package teq.development.seatech;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.JobDetail.JobStatusDialog;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(HandyObject.getPrams(LoginActivity.this,AppConstants.IS_LOGIN).equalsIgnoreCase("login")) {
            movetoDashboard();
        }
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        binding.setLoginactivity(this);
        binding.etUsername.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(LoginActivity.this,R.drawable.et_username), null, null, null);
        binding.etPwd.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(LoginActivity.this,R.drawable.etpwd), null, null, null);
    }

    public void onClickLogin(){
            if (binding.etUsername.getText().toString().length() == 0) {
                binding.etUsername.setError(getString(R.string.fieldempty));
                binding.etUsername.requestFocus();
            } else if (binding.etPwd.getText().toString().length() == 0) {
                binding.etPwd.setError(getString(R.string.fieldempty));
                binding.etPwd.requestFocus();
            } else if (HandyObject.checkInternetConnection(this)) {
                loginTask(binding.etUsername.getText().toString(),binding.etPwd.getText().toString());
            } else {
                Toast.makeText(this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
    }

    private void loginTask(String username,String pwd){
        HandyObject.showProgressDialog(this);
        HandyObject.getApiManagerType().userLogin(username, pwd)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("response",jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("error"))
                                HandyObject.showAlert(LoginActivity.this,jsonObject.getString("message"));
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                HandyObject.showAlert(LoginActivity.this,jsonObject.getString("message"));
                                JSONObject dataobj = jsonObject.getJSONObject("data");
                                saveLoginData(dataobj);
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

    private void saveLoginData(JSONObject jobj) {
        try {

            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGIN_SESSIONID, jobj.getString("session_id"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_ID, jobj.getString("id"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_USERNAME, jobj.getString("username"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_EMAIL, jobj.getString("email"));

            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_GENDER, jobj.getString("gender"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_IMAGE, jobj.getString("image"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_DOB, jobj.getString("dob"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_PHONE, jobj.getString("phone"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_ROLE, jobj.getString("role"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_DESCRIPTION, jobj.getString("description"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_STATUS, jobj.getString("status"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_JOININGDATE, jobj.getString("joining_date"));
            if(binding.checkboxremb.isChecked()) {
                HandyObject.putPrams(LoginActivity.this, AppConstants.IS_LOGIN, "login");
            } else {
                HandyObject.putPrams(LoginActivity.this, AppConstants.IS_LOGIN, "notlogin");
            }
            movetoDashboard();
        } catch (Exception e){}
    }

    public void onClickFrgtPwd(){
        DialogFrgtPwd();
    }

    void DialogFrgtPwd() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialogfrgtpwd");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = FrgtPwdDialog.newInstance(8);
        newFragment.show(ft, "dialogfrgtpwd");
    }
    private void movetoDashboard(){
        Intent intent_reg = new Intent(LoginActivity.this, DashBoardActivity.class);
        startActivity(intent_reg);
        finish();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }
}
