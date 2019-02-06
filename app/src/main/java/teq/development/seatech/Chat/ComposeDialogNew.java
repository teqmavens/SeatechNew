package teq.development.seatech.Chat;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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
import teq.development.seatech.Chat.Adapter.AdapterOppEmpList;
import teq.development.seatech.Chat.Skeleton.AllEmployeeSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.PopupComposenewBinding;

public class ComposeDialogNew extends android.support.v4.app.DialogFragment {

    Dialog dialog;
    PopupComposenewBinding binding;
    ArrayList<AllEmployeeSkeleton> arrayListEmp;
    static String job_id, c_name,c_type, b_name, b_makeyear;

    public static ComposeDialogNew newInstance(int num, String jobid, String cname,String ctype, String bname, String bmakeyear) {
        ComposeDialogNew f = new ComposeDialogNew();
        c_name = cname;
        b_name = bname;
        b_makeyear = bmakeyear;
        job_id = jobid;
        c_type = ctype;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_composenew, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setPopupcomposenew(this);
        initViews();
        OpponentEmployeeList();
        return rootView;
    }

    private void initViews() {
        binding.etCusttype.setText(c_type);
        binding.etJobticketno.setText(job_id + "-" + c_name);
        binding.etBoatname.setText(b_name);
        binding.etBoatmakeyear.setText(b_makeyear);
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
                    UUID uniqueKey = UUID.randomUUID();
                    JSONObject jobj = new JSONObject();
                    JSONObject jobj_receiver = new JSONObject();
                    jobj_receiver.put("receiver_id", arrayListEmp.get(binding.jobspinner.getSelectedItemPosition()).getEmployeeId());
                    jobj_receiver.put("receiver_name", arrayListEmp.get(binding.jobspinner.getSelectedItemPosition()).getEmployeename());
                    JSONArray jarry_receiver = new JSONArray();
                    jarry_receiver.put(jobj_receiver);
                    jobj.put("job_id", binding.etJobticketno.getText().toString().split("-")[0]);
                    jobj.put("sender_id", Integer.parseInt(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID)));
                    jobj.put("sender_name", HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_USERNAME));
                    jobj.put("receiver", jarry_receiver);
                    jobj.put("urgent", urgent);
                    jobj.put("token", String.valueOf(uniqueKey));
                    jobj.put("message", binding.etDescription.getText().toString());
                    App.appInstance.getSocket().emit("send chat message", jobj);
                } catch (Exception e) {
                }
              /*  Intent intent = new Intent("ToChatLeftView");
                intent.putExtra("jobidtoleft", binding.etJobticketno.getText().toString().split("-")[0]);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);*/
                Intent resultIntent = new Intent(getActivity(), ChatActivity.class);
                resultIntent.putExtra("type", "chat");
                resultIntent.putExtra("jobid", binding.etJobticketno.getText().toString().split("-")[0]);
                ((Activity) getActivity()).startActivity(resultIntent);
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
        HandyObject.getApiManagerMain().AllEmployeeList(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQPARENT_ID), HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
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
                                    // AllJobsList();
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

}
