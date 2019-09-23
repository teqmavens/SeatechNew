package teq.development.seatech.Timesheet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import teq.development.seatech.App;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.JobDetail.JobDetailStaticFragment;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Timesheet.Adapter.AdapterDayJobStatus;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.FrgmDaytimesheetdetailBinding;

public class DayTimesheetDetailFragment extends Fragment {

    FrgmDaytimesheetdetailBinding binding;
    ArrayList<DayJobStatus_Skeleton> arrayList;
    AdapterDayJobStatus adapter;
    DashBoardActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DashBoardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_daytimesheetdetail, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmdaytimesheetdetail(this);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        LinearLayoutManager lLManagerImages = new LinearLayoutManager(getActivity());
        lLManagerImages.setOrientation(LinearLayoutManager.VERTICAL);
        binding.statusRecyclerView.setLayoutManager(lLManagerImages);

        if(getArguments() != null) {
            TimeSheetFragment.binding.displaydate.setText(getArguments().getString(AppConstants.SELECTED_JOBDAYSTATUS));
           // HandyObject.showAlert(getActivity(),getArguments().getString(AppConstants.SELECTED_JOBDAYSTATUS));
            DayJobStatusApi(HandyObject.parseDateToYMDNew(getArguments().getString(AppConstants.SELECTED_JOBDAYSTATUS)));
        }
    }

    private void DayJobStatusApi(String seldate) {
        //  HandyObject.showProgressDialog(getActivity());
        if (HandyObject.checkInternetConnection(getActivity())) {
            HandyObject.getApiManagerMain().DayJobStatusDetail(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), seldate, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.e("responseDayStatus", jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                arrayList = new ArrayList<>();
                                Gson gson = new Gson();
                                if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                    //   HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                    JSONObject jobjdata = jsonObject.getJSONObject("data");
                                    JSONArray jsonArray = jobjdata.getJSONArray("day_data");
                                    if (jsonArray.length() == 0) {
                                        //   HandyObject.showAlert(getActivity(),"null");
                                        binding.dataCardView.setVisibility(View.INVISIBLE);
                                        binding.nodataCardView.setVisibility(View.VISIBLE);

                                        binding.lltop.setVisibility(View.INVISIBLE);
                                        binding.totaltimeKey.setVisibility(View.INVISIBLE);
                                        binding.totaltimeValue.setVisibility(View.INVISIBLE);
                                        binding.totalWeekHrKey.setVisibility(View.INVISIBLE);
                                        binding.totalWeekHrValue.setVisibility(View.INVISIBLE);
                                        binding.view1.setVisibility(View.INVISIBLE);
                                    } else {
                                        //  HandyObject.showAlert(getActivity(),"notnull");
                                        binding.dataCardView.setVisibility(View.VISIBLE);
                                        binding.nodataCardView.setVisibility(View.INVISIBLE);


                                        binding.lltop.setVisibility(View.VISIBLE);
                                        binding.totaltimeKey.setVisibility(View.VISIBLE);
                                        binding.totaltimeValue.setVisibility(View.VISIBLE);
                                        binding.totalWeekHrKey.setVisibility(View.VISIBLE);
                                        binding.totalWeekHrValue.setVisibility(View.VISIBLE);
                                        binding.view1.setVisibility(View.VISIBLE);

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jobjInside = jsonArray.getJSONObject(i);
                                            DayJobStatus_Skeleton ske = new DayJobStatus_Skeleton();
                                            ske.setJob_id(jobjInside.getString("job_id"));
                                            ske.setCustomer_name(jobjInside.getString("customer_name"));
                                            ske.setLabour_code(jobjInside.getString("labour_code"));
                                            ske.setStarttime(jobjInside.getString("start_time"));
                                            ske.setEndtime(jobjInside.getString("end_time"));
                                            ske.setHours(jobjInside.getString("hours"));
                                            arrayList.add(ske);
                                        }
                                        adapter = new AdapterDayJobStatus(getActivity(), arrayList, DayTimesheetDetailFragment.this);
                                        binding.statusRecyclerView.setAdapter(adapter);
                                        binding.totaltimeValue.setText(jobjdata.getString("total"));
                                        binding.totalWeekHrValue.setText(jobjdata.getString("weekly_hours"));
                                    }
                                } else {
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                    if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                        HandyObject.clearpref(getActivity());
                                        HandyObject.deleteAllDatabase(getActivity());
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
        } else {
            HandyObject.showAlert(getActivity(), getString(R.string.check_internet_connection));
        }
    }

    public void onClickTicketNo(int position) {
        JobDetailStaticFragment jb = new JobDetailStaticFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("position_ticketId", arrayList.get(position).getJob_id());
        jb.setArguments(bundle);
        activity.replaceFragment(jb);
    }
}
