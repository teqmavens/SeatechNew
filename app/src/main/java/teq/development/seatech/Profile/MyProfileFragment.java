package teq.development.seatech.Profile;

import android.content.Context;
import android.content.Intent;
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
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.FrgmMyprofileBinding;

public class MyProfileFragment extends Fragment {

    DashBoardActivity activity;
    FrgmMyprofileBinding binding;
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
        View rootView = inflater.inflate(R.layout.frgm_myprofile,container,false);
        binding = DataBindingUtil.bind(rootView);
        binding.setMyprofile(this);
        if (HandyObject.checkInternetConnection(context)) {
            setLocaldata();
            getProfileTask();
        } else {
            setLocaldata();
            Toast.makeText(context, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    public void OnClickEdit(){
        activity.replaceFragment(new EditProfileFragment());
    }

    private void getProfileTask(){
        HandyObject.showProgressDialog(context);
        HandyObject.getApiManagerType().getProfile(HandyObject.getPrams(context,AppConstants.LOGINTEQ_ID),HandyObject.getPrams(context,AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("response",jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")){
                                JSONObject dataobj = jsonObject.getJSONObject("data");
                                saveProfileData(dataobj);
                            } else {
                                setLocaldata();
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
                        setLocaldata();
                    }
                });
    }

    private void setLocaldata(){
        binding.username.setText(HandyObject.getPrams(context,AppConstants.LOGINTEQ_USERNAME));
        binding.gender.setText(HandyObject.getPrams(context,AppConstants.LOGINTEQ_GENDER));
        binding.dob.setText(HandyObject.getPrams(context,AppConstants.LOGINTEQ_DOB).split("T")[0]);
        binding.usertype.setText(HandyObject.getPrams(context,AppConstants.LOGINTEQ_ROLE));
        binding.emailvalue.setText(HandyObject.getPrams(context,AppConstants.LOGINTEQ_EMAIL));
        binding.phonevalue.setText(HandyObject.getPrams(context,AppConstants.LOGINTEQ_PHONE));
    }

    private void saveProfileData(JSONObject jobj) {
        try {
            HandyObject.putPrams(context, AppConstants.LOGIN_SESSIONID, jobj.getString("session_id"));
            HandyObject.putPrams(context, AppConstants.LOGINTEQ_USERNAME, jobj.getString("username"));
            HandyObject.putPrams(context, AppConstants.LOGINTEQ_EMAIL, jobj.getString("email"));
            HandyObject.putPrams(context, AppConstants.LOGINTEQ_GENDER, jobj.getString("gender"));
            HandyObject.putPrams(context, AppConstants.LOGINTEQ_IMAGE, jobj.getString("image"));
            HandyObject.putPrams(context, AppConstants.LOGINTEQ_DOB, jobj.getString("dob"));
            HandyObject.putPrams(context, AppConstants.LOGINTEQ_PHONE, jobj.getString("phone"));
            HandyObject.putPrams(context, AppConstants.LOGINTEQ_ROLE, jobj.getString("role_id"));
            setLocaldata();
        } catch (Exception e){}

    }
}
