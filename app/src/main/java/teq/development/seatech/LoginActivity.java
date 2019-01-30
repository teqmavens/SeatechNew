package teq.development.seatech;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.JobDetail.JobStatusDialog;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final int REQUEST_CAMERA = 0;
    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA};
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SQLiteDatabase sqLiteDatabase;
    String deviceToken = "";
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        check_RequestPermission();

        /*Check for Automatic login*/
        if (HandyObject.getPrams(LoginActivity.this, AppConstants.IS_LOGIN).equalsIgnoreCase("login")) {
            //  HandyObject.putPrams(LoginActivity.this, AppConstants.ISJOB_RUNNING, "no");
            movetoDashboard();
        } else {
            if (HandyObject.checkInternetConnection(this)) {
                GetManufacturerData();
            } else {
                HandyObject.showAlert(this, getString(R.string.check_internet_connection));
            }
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLoginactivity(this);
    }

    public void onClickLogin() {
        if (binding.etUsername.getText().toString().length() == 0) {
            binding.etUsername.setError(getString(R.string.fieldempty));
            binding.etUsername.requestFocus();
        } else if (binding.etPwd.getText().toString().length() == 0) {
            binding.etPwd.setError(getString(R.string.fieldempty));
            binding.etPwd.requestFocus();
        } else if (HandyObject.checkInternetConnection(this)) {
            deviceToken = HandyObject.getPrams(LoginActivity.this, AppConstants.DEVICE_TOKEN);
            if (deviceToken.length() == 0) {
                deviceToken = FirebaseInstanceId.getInstance().getToken();
                HandyObject.putPrams(getApplicationContext(), AppConstants.DEVICE_TOKEN, deviceToken);
                if (deviceToken.length() == 0) {
                    deviceToken = FirebaseInstanceId.getInstance().getToken();
                    HandyObject.putPrams(getApplicationContext(), AppConstants.DEVICE_TOKEN, deviceToken);
                    loginTask(binding.etUsername.getText().toString(), binding.etPwd.getText().toString(), deviceToken);
                } else {
                    loginTask(binding.etUsername.getText().toString(), binding.etPwd.getText().toString(), deviceToken);
                }
            } else {
                HandyObject.putPrams(getApplicationContext(), AppConstants.DEVICE_TOKEN, deviceToken);
                loginTask(binding.etUsername.getText().toString(), binding.etPwd.getText().toString(), deviceToken);
            }
        } else {
            HandyObject.showAlert(this, getString(R.string.check_internet_connection));
        }
        Log.e("DEVICETOKEN", deviceToken);
    }

    private void loginTask(String username, String pwd, String token) {
        HandyObject.showProgressDialog(this);
        //  Log.e("DEVICETOKEN",token);
        //  HandyObject.showAlert(LoginActivity.this,token);
        HandyObject.getApiManagerType().userLogin(username, pwd, token)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("response", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                JSONObject dataobj = jsonObject.getJSONObject("data");
                                /*Save login user detail to local database*/
                                saveLoginData(dataobj, jsonObject.getString("message"));
                            } else {
                                HandyObject.showAlert(LoginActivity.this, jsonObject.getString("message"));
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

    /*Save login user detail to local database*/
    private void saveLoginData(JSONObject jobj, String msg) {
        try {
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGIN_SESSIONID, jobj.getString("session_id"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_ID, jobj.getString("id"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQPARENT_ID, jobj.getString("parent_id"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_USERNAME, jobj.getString("username"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_FIRSTNAME, jobj.getString("firstname"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_MIDDLENAME, jobj.getString("middlename"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_LASTNAME, jobj.getString("lastname"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_EMAIL, jobj.getString("email"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_GENDER, jobj.getString("gender"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_IMAGE, jobj.getString("image"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_DOB, jobj.getString("dob"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_PHONE, jobj.getString("phone"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_ROLE, jobj.getString("role"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_DESCRIPTION, jobj.getString("description"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_STATUS, jobj.getString("status"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.LOGINTEQ_JOININGDATE, jobj.getString("joining_date"));
            HandyObject.putPrams(LoginActivity.this, AppConstants.JOBRUNNING_TOTALTIME, "0");
            HandyObject.putPrams(LoginActivity.this, AppConstants.ISJOB_RUNNING, "no");
            HandyObject.putbooleanPrams(LoginActivity.this, AppConstants.PICKJOBPERMISSION, jobj.getBoolean("permission"));
            //  HandyObject.putPrams(LoginActivity.this, AppConstants.IS_LOGIN, "login");
            HandyObject.putIntPrams(LoginActivity.this, AppConstants.LCJOBCOMPLETION_COUNT, 0);
            HandyObject.putIntPrams(LoginActivity.this, AppConstants.NEEDPART_COUNT, 0);
//            if (binding.checkboxremb.isChecked()) {
//                HandyObject.putPrams(LoginActivity.this, AppConstants.IS_LOGIN, "login");
//            } else {
//                HandyObject.putPrams(LoginActivity.this, AppConstants.IS_LOGIN, "notlogin");
//            }

            HandyObject.putPrams(LoginActivity.this, AppConstants.IS_LOGIN, "login");
            HandyObject.showAlert(LoginActivity.this, msg);
            movetoDashboard();
        } catch (Exception e) {
        }
    }

    public void onClickFrgtPwd() {
        //  DialogFrgtPwd();
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

    //Navigate to Dashboard screen
    private void movetoDashboard() {
        Intent intent_reg = new Intent(LoginActivity.this, DashBoardActivity.class);
        startActivity(intent_reg);
        finish();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }

    /*Check for All permission required for app*/
    void check_RequestPermission() {
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    android.Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Permission is needed")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                ActivityCompat.requestPermissions(LoginActivity.this,
                                        new String[]{android.Manifest.permission.CAMERA},
                                        REQUEST_CAMERA);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {

                ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS_CAMERA, REQUEST_CAMERA);
            }
        } else if (ContextCompat.checkSelfPermission(LoginActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Permission is needed")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                ActivityCompat.requestPermissions(LoginActivity.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        2);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS_STORAGE, 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    check_RequestPermission();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                            android.Manifest.permission.CAMERA)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Permission is needed to access Scanner")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        ActivityCompat.requestPermissions(LoginActivity.this,
                                                new String[]{android.Manifest.permission.CAMERA},
                                                REQUEST_CAMERA);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Manually turn on camera permission", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    check_RequestPermission();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Permission is needed")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        ActivityCompat.requestPermissions(LoginActivity.this,
                                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                2);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Manually turn on permission", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                return;
            }
        }
    }

    // Get Manufacturer data for Need Part Dialog(Inside Job Detail Page)
    private void GetManufacturerData() {
        HandyObject.showProgressDialog(this);
        HandyObject.getApiManagerMain().GetManufacturerData()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("response", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            ArrayList<ManufacturerSkeleton> manuArrayList = new ArrayList<>();
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                JSONArray jsonArray_manuf = jsonObject1.getJSONArray("manufacturers");
                                JSONArray jsonArray_lc = jsonObject1.getJSONArray("labor_codes");
                                sqLiteDatabase = ParseOpenHelper.getInstance(LoginActivity.this).getWritableDatabase();
                                gson = new Gson();
                                for (int i = 0; i < jsonArray_manuf.length(); i++) {
                                    JSONObject jobj = jsonArray_manuf.getJSONObject(i);
                                    ManufacturerSkeleton ske = new ManufacturerSkeleton();
                                    ske.setId(jobj.getString("id"));
                                    ske.setName(jobj.getString("name"));
                                    ske.setPhone(jobj.getString("phone"));
                                    ske.setComment(jobj.getString("warranty_comments"));
                                    ske.setRmaRequired(jobj.getString("rma_required"));
                                    ske.setNeedProduct(jobj.getString("need_product"));
                                    manuArrayList.add(ske);
                                }

                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < jsonArray_lc.length(); i++) {
                                    JSONObject jobj = jsonArray_lc.getJSONObject(i);
                                    sb.append(jobj.getString("code_name") + ",");
                                }

                                sb.setLength(sb.length() - 1);
                                HandyObject.putPrams(LoginActivity.this, AppConstants.JOBRUNNING_LC, sb.toString());
                                String manufactureData = gson.toJson(manuArrayList);
                                ContentValues cv = new ContentValues();
                                cv.put(ParseOpenHelper.ALLMANUFACTURER, manufactureData);
                                long manufId = sqLiteDatabase.insert(ParseOpenHelper.TABLENAME_MANUFACTURER, null, cv);
                            } else {
                                HandyObject.showAlert(LoginActivity.this, jsonObject.getString("message"));
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
