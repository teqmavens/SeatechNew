package teq.development.seatech.Chat;

import android.app.Activity;
import android.app.Dialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Chat.Adapter.AdapterAutoCompleteText;
import teq.development.seatech.Chat.Adapter.AdapterOppEmpList;
import teq.development.seatech.Chat.Skeleton.AllEmployeeSkeleton;
import teq.development.seatech.Chat.Skeleton.ChatJobListSkeleton;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.PopupComposeBinding;

public class ComposeDialog extends DialogFragment {

    Dialog dialog;
    ArrayList<String> jobidList, Jobidfilter;
    SQLiteDatabase database;
    AdapterAutoCompleteText adapterAutoCompleteText;
    PopupComposeBinding binding;
    ArrayList<AllEmployeeSkeleton> arrayListEmp;
    ArrayList<ChatJobListSkeleton> arrayList;

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
        initViews();
        OpponentEmployeeList();
        return rootView;
    }

    private void initViews() {
        binding.etJobticketno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("dsff", "adfs");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (jobidList == null) {
                    HandyObject.showAlert(getActivity(), getString(R.string.ticketlistempty));
                } else {
                    if (jobidList.contains(String.valueOf(s))) {
                        //  int posi = jobidList.indexOf(String.valueOf(s));
                        int posi = Jobidfilter.indexOf(String.valueOf(s).split("-")[0]);
                        // binding.custnameKey.setVisibility(View.VISIBLE);
                        //   binding.etCustname.setVisibility(View.VISIBLE);
                        binding.custtypeKey.setVisibility(View.VISIBLE);
                        binding.etCusttype.setVisibility(View.VISIBLE);
                        binding.boatnameKey.setVisibility(View.VISIBLE);
                        binding.etBoatname.setVisibility(View.VISIBLE);
                        binding.boatmakeyearKey.setVisibility(View.VISIBLE);
                        binding.etBoatmakeyear.setVisibility(View.VISIBLE);

                        binding.etCustname.setText(arrayList.get(posi).getCustomer_name());
                        binding.etCusttype.setText(arrayList.get(posi).getCustomer_type());
                        binding.etBoatname.setText(arrayList.get(posi).getBoat_name());
                        binding.etBoatmakeyear.setText(arrayList.get(posi).getBoat_make_year());
                    } else {
                        //   binding.etCustname.setText("");
                        //  binding.etCusttype.setText("");
                        binding.etBoatname.setText("");
                        binding.etBoatmakeyear.setText("");

                        //   binding.custnameKey.setVisibility(View.GONE);
                        //  binding.etCustname.setVisibility(View.GONE);
                        binding.custtypeKey.setVisibility(View.GONE);
                        binding.etCusttype.setVisibility(View.GONE);
                        binding.boatnameKey.setVisibility(View.GONE);
                        binding.etBoatname.setVisibility(View.GONE);
                        binding.boatmakeyearKey.setVisibility(View.GONE);
                        binding.etBoatmakeyear.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("dsff", "adfs");
            }
        });
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
        } else if (binding.custtypeKey.getVisibility() == View.GONE) {
            HandyObject.showAlert(getActivity(), getString(R.string.nojobmatching));
        } else {
            if (HandyObject.checkInternetConnection(getActivity())) {
                String urgent = "";
                if (binding.cburgentmsg.isChecked()) {
                    urgent = "1";
                } else {
                    urgent = "0";
                }
                try {
                   /* JSONObject jobj = new JSONObject();
                    jobj.put("job_id", binding.etJobticketno.getText().toString());
                    jobj.put("sender_id", Integer.parseInt(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID)));
                    jobj.put("receiver_id", arrayListEmp.get(binding.jobspinner.getSelectedItemPosition()).getEmployeeId());
                    jobj.put("urgent", urgent);
                    jobj.put("message", binding.etDescription.getText().toString());*/
                    UUID uniqueKey = UUID.randomUUID();
                    JSONObject jobj = new JSONObject();
                    JSONObject jobj_receiver = new JSONObject();
                    jobj_receiver.put("receiver_id", arrayListEmp.get(binding.jobspinner.getSelectedItemPosition()).getEmployeeId());
                    jobj_receiver.put("receiver_name", arrayListEmp.get(binding.jobspinner.getSelectedItemPosition()).getEmployeename());
                    JSONArray jarry_receiver = new JSONArray();
                    jarry_receiver.put(jobj_receiver);
                    jobj.put("job_id", binding.etJobticketno.getText().toString().split("-")[0]);
                    jobj.put("sender_id", Integer.parseInt(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID)));
                    jobj.put("sender_name", HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_USERNAME));
                    jobj.put("receiver", jarry_receiver);
                    jobj.put("urgent", urgent);
                    jobj.put("token", String.valueOf(uniqueKey));
                    jobj.put("message", binding.etDescription.getText().toString());
                    //  ChatActivity.mSocket.emit("send chat message", jobj);
                    App.appInstance.getSocket().emit("send chat message", jobj);
                } catch (Exception e) {
                }
                Intent intent = new Intent("ToChatLeftView");
                intent.putExtra("jobidtoleft", binding.etJobticketno.getText().toString().split("-")[0]);
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
                                if (((Activity) getActivity()) != null) {
                                    AdapterOppEmpList adapterOppEmpList = new AdapterOppEmpList(getActivity(), arrayListEmp);
                                    binding.jobspinner.setAdapter(adapterOppEmpList);
                                    AllJobsList();
                                }
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
        HandyObject.getApiManagerMain().AllJobsList(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseAllJobs", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            jobidList = new ArrayList<>();
                            arrayList = new ArrayList<>();
                            Jobidfilter = new ArrayList<>();
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobj = jsonArray.getJSONObject(i);
                                    jobidList.add(jobj.getString("job_id") + "-" + jobj.getString("customer_name"));
                                    Jobidfilter.add(jobj.getString("job_id"));
                                    ChatJobListSkeleton ske = new ChatJobListSkeleton();
                                    ske.setJobid(jobj.getString("job_id"));
                                    ske.setCustomer_name(jobj.getString("customer_name"));
                                    ske.setCustomer_type(jobj.getString("customer_type"));
                                    ske.setBoat_make_year(jobj.getString("boat_make_year"));
                                    ske.setBoat_name(jobj.getString("boat_name"));
                                    arrayList.add(ske);
                                }

                                adapterAutoCompleteText = new AdapterAutoCompleteText(getActivity(), R.layout.popup_compose, R.id.textspinner, jobidList);
                                binding.etJobticketno.setAdapter(adapterAutoCompleteText);
                            } else {
                                HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } /*catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            HandyObject.stopProgressDialog();
                        }*/
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
