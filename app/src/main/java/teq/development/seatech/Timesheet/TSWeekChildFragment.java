package teq.development.seatech.Timesheet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Timesheet.Adapter.AdapterTSMonth;
import teq.development.seatech.Timesheet.Adapter.AdapterTSWeek;
import teq.development.seatech.Timesheet.Skeleton.TSMonthSkeleton;
import teq.development.seatech.Timesheet.Skeleton.TSWeekSkeleton;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.FrgmTsweekchildBinding;

public class TSWeekChildFragment extends Fragment {

    FrgmTsweekchildBinding binding;
    ArrayList<TSWeekSkeleton> arrayList;
    AdapterTSWeek adapterTSWeek;
    DashBoardActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DashBoardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_tsweekchild, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmtsweekchild(this);
        initViews();
        return rootView;
    }

    private void initViews() {
        LinearLayoutManager lLManagerImages = new LinearLayoutManager(getActivity());
        lLManagerImages.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerview.setLayoutManager(lLManagerImages);
        if (getArguments() != null) {
            TimeSheetFragment.binding.displaydate.setText("(" + getArguments().getString("weekstartdate").split(",")[0] + ") - " + "(" + getArguments().getString("weekstartdate").split(",")[1] + ")");
            TSMonthApi(getArguments().getString("weekstartdate").split(",")[0], getArguments().getString("weekstartdate").split(",")[1]);
        }
    }

    private void TSMonthApi(String weekstartdate, String weekenddate) {
        // HandyObject.showProgressDialog(getActivity());
        HandyObject.getApiManagerMain().TSWeekData(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), weekstartdate, weekenddate, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseLC", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            arrayList = new ArrayList<>();
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                //   HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                JSONObject jobjdata = jsonObject.getJSONObject("data");
                                JSONArray jsonArray = jobjdata.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobjinside = jsonArray.getJSONObject(i);
                                    TSWeekSkeleton ske = new TSWeekSkeleton();
                                    ske.setWeek(jobjinside.getString("day"));
                                    ske.setDate(jobjinside.getString("c_date"));
                                    ske.setBillable(jobjinside.getString("billable_time"));
                                    ske.setNonbillable(jobjinside.getString("non_billable_time"));
                                    ske.setTotal(jobjinside.getString("total_time"));
                                    arrayList.add(ske);
                                }
                                adapterTSWeek = new AdapterTSWeek(getActivity(), arrayList, TSWeekChildFragment.this);
                                binding.recyclerview.setAdapter(adapterTSWeek);
                                JSONObject ttl = jobjdata.getJSONObject("total");
                                binding.billableWeekttl.setText(ttl.getString("t_billable"));
                                binding.nonbillableWeekttl.setText(ttl.getString("t_non_billable"));
                                binding.weekttlTtl.setText(ttl.getString("t_total"));
                            } else {
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
                            //   HandyObject.stopProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        //  HandyObject.stopProgressDialog();
                    }
                });
    }

    public void OnClickDate(String date) {
        DayJobStatusDetailFragment frgm = new DayJobStatusDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.SELECTED_JOBDAYSTATUS, HandyObject.parseDateToMDY(date));
        frgm.setArguments(bundle);
        activity.replaceFragment(frgm);
    }

    public void OnClickBack() {
        Intent intent = new Intent("clickback_fromchild");
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        getFragmentManager().popBackStack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestyWeek", "onDestyWeek");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("onDestyViewWeek", "onDestyViewWeek");
    }
}
