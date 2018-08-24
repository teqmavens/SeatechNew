package teq.development.seatech.Chat;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.Chat.Adapter.AdapterAutoCompleteText;
import teq.development.seatech.Chat.Adapter.AdapterOppEmpList;
import teq.development.seatech.Chat.Skeleton.AllEmployeeSkeleton;
import teq.development.seatech.Dashboard.Adapter.AdapterJobSpinner;
import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.JobDetail.JobDetailFragment;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.PopupComposeBinding;

public class ComposeDialog extends DialogFragment {

    Dialog dialog;
    ArrayList<String> jobidList;
    SQLiteDatabase database;
    AdapterAutoCompleteText adapterAutoCompleteText;
    PopupComposeBinding binding;
    ArrayList<AllEmployeeSkeleton> arrayListEmp;

    public static ComposeDialog newInstance(int num) {
        ComposeDialog f = new ComposeDialog();
        // position = num;
        // arraylistImages = arraylist;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_compose, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setPopupcompose(this);
        OpponentEmployeeList();

        //new databsefetch().execute();
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
        params.width = width - 220;
        // params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = height - 420;
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

    public void OnClickSubmit() {

        if (binding.etJobticketno.getText().toString().length() == 0) {
            binding.etJobticketno.requestFocus();
            HandyObject.showAlert(getActivity(), getString(R.string.jobticketnoreq));
        } else if (binding.etDescription.getText().toString().length() == 0) {
            binding.etDescription.requestFocus();
            HandyObject.showAlert(getActivity(), getString(R.string.fieldempty));
        } else {
            if (HandyObject.checkInternetConnection(getActivity())) {

                try {
                    JSONObject jobj = new JSONObject();
                    jobj.put("job_id", binding.etJobticketno.getText().toString());
                    jobj.put("sender_id", Integer.parseInt(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID)));
                    jobj.put("receiver_id", arrayListEmp.get(binding.jobspinner.getSelectedItemPosition()).getEmployeeId());
                    jobj.put("urgent", 0);
                    jobj.put("message", binding.etDescription.getText().toString());
                    ChatActivity.mSocket.emit("send chat message", jobj);
                } catch (Exception e) {
                }

                Intent intent = new Intent("ToChatLeftView");
                intent.putExtra("jobidtoleft", binding.etJobticketno.getText().toString());
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                dialog.dismiss();
            } else {
                HandyObject.showAlert(getActivity(), getString(R.string.check_internet_connection));
            }

        }
    }

    public void OnClickCross() {
        dialog.dismiss();
    }


    /*private class databsefetch extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HandyObject.showProgressDialog(getActivity());
            jobidList = new ArrayList<>();
        }

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... arrayLists) {
            database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();

            Cursor cursor = database.query(ParseOpenHelper.TABLENAME_ALLJOBS, null, null, null, null, null, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBID));
                jobidList.add(id);
                //     arrayListDashNotes.addAll(arrayListDash);
                cursor.moveToNext();
            }
            cursor.close();
            return jobidList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayList) {
            super.onPostExecute(arrayList);
            // adapterjobspinner = new AdapterJobSpinner(context, allJobsSkeletons);
            //  binding.jobspinner.setAdapter(adapterjobspinner);
            adapterAutoCompleteText = new AdapterAutoCompleteText(getActivity(), R.layout.popup_compose, R.id.textspinner, arrayList);
            binding.etJobticketno.setAdapter(adapterAutoCompleteText);
            //https://akshaymukadam.wordpress.com/2015/02/01/how-to-create-autocompletetextview-using-custom-filter-implementation/
            HandyObject.stopProgressDialog();
        }
    }*/

    private void OpponentEmployeeList() {
        //  HandyObject.showProgressDialog(getActivity());
        HandyObject.getApiManagerMain().AllEmployeeList(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseChatLeft", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            arrayListEmp = new ArrayList<>();
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobj = jsonArray.getJSONObject(i);
                                    AllEmployeeSkeleton ske = new AllEmployeeSkeleton();
                                    ske.setEmployeeId(jobj.getString("id"));
                                    ske.setEmployeename(jobj.getString("name"));
                                    arrayListEmp.add(ske);
                                }
                                AdapterOppEmpList adapterOppEmpList = new AdapterOppEmpList(getActivity(), arrayListEmp);
                                binding.jobspinner.setAdapter(adapterOppEmpList);
                                AllJobsList();
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

    private void AllJobsList() {
        //  HandyObject.showProgressDialog(getActivity());
        HandyObject.getApiManagerMain().AllJobsList(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseAllJobs", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            jobidList = new ArrayList<>();
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobj = jsonArray.getJSONObject(i);
                                    jobidList.add(jobj.getString("job_id"));
                                }
                                adapterAutoCompleteText = new AdapterAutoCompleteText(getActivity(), R.layout.popup_compose, R.id.textspinner, jobidList);
                                binding.etJobticketno.setAdapter(adapterAutoCompleteText);
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
    //https://www.learn2crack.com/2016/11/android-rxjava-2-and-retrofit.html
}
