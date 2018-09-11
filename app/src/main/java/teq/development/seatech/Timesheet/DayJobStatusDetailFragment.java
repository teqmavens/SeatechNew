package teq.development.seatech.Timesheet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.JobDetail.JobDetailStaticFragment;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Timesheet.Adapter.AdapterDayJobStatus;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.FrgmDayjobstatusdetailBinding;

public class DayJobStatusDetailFragment extends Fragment implements View.OnClickListener {

    AdapterDayJobStatus adapter;
    ArrayList<DayJobStatus_Skeleton> arrayList;
    FrgmDayjobstatusdetailBinding binding;
    //   private ArrayList<AllJobsSkeleton> jobsArrayList;
    SQLiteDatabase database;
    int count = 0;
    DashBoardActivity activity;
    Calendar calendar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DashBoardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_dayjobstatusdetail, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmdayjobstatus(this);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        binding.previousdate.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.leftarrow), null, null, null);
        binding.nextdate.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(getActivity(), R.drawable.rightarrow), null);
        binding.previousdate.setOnClickListener(this);
        binding.nextdate.setOnClickListener(this);
        LinearLayoutManager lLManagerImages = new LinearLayoutManager(getActivity());
        lLManagerImages.setOrientation(LinearLayoutManager.VERTICAL);
        binding.statusRecyclerView.setLayoutManager(lLManagerImages);
        if (getArguments() != null) {
            binding.currentdate.setText(HandyObject.parseDateToDayStatusText(getArguments().getString(AppConstants.SELECTED_JOBDAYSTATUS)));
            calendar = Calendar.getInstance();
            int year = Integer.parseInt(getArguments().getString(AppConstants.SELECTED_JOBDAYSTATUS).split("\\s+")[2]);
            int month = Integer.parseInt(getArguments().getString(AppConstants.SELECTED_JOBDAYSTATUS).split("\\s+")[0]);
            int date = Integer.parseInt(getArguments().getString(AppConstants.SELECTED_JOBDAYSTATUS).split("\\s+")[1]);
            calendar.set(year, month - 1, date);
            setCountForDateChange(HandyObject.getDayFromDate(getArguments().getString(AppConstants.SELECTED_JOBDAYSTATUS)));

            //jobsArrayList = new ArrayList<>();
            database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
            DayJobStatusApi(HandyObject.parseDateToYMD(binding.currentdate.getText().toString()));
        }
    }

    private void DayJobStatusApi(String seldate) {
        //  HandyObject.showProgressDialog(getActivity());
        HandyObject.getApiManagerMain().DayJobStatusDetail(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), seldate, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseLC", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            arrayList = new ArrayList<>();
                            Gson gson = new Gson();
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                //   HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                JSONObject jobjdata = jsonObject.getJSONObject("data");
                                JSONArray jsonArray = jobjdata.getJSONArray("day_data");
                                if (jsonArray.length() == 0) {
                                    binding.nodataCardView.setVisibility(View.VISIBLE);
                                    binding.dataCardView.setVisibility(View.INVISIBLE);
                                } else {
                                    binding.nodataCardView.setVisibility(View.INVISIBLE);
                                    binding.dataCardView.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobjInside = jsonArray.getJSONObject(i);
                                        DayJobStatus_Skeleton ske = new DayJobStatus_Skeleton();
                                        ske.setJob_id(jobjInside.getString("job_id"));
                                        ske.setLabour_code(jobjInside.getString("labour_code"));
                                        ske.setHours(jobjInside.getString("hours"));
                                        arrayList.add(ske);
                                    }
                                    adapter = new AdapterDayJobStatus(getActivity(), arrayList, DayJobStatusDetailFragment.this);
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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previousdate:
                if (count == 1) {
                    HandyObject.showAlert(getActivity(), getString(R.string.selectedweekdate));
                } else if (count > 1) {
                    count--;
                    calendar.add(calendar.HOUR_OF_DAY, -24);
                    binding.currentdate.setText(HandyObject.getDateFromPicker(calendar.getTime()));
                    DayJobStatusApi(HandyObject.parseDateToYMD(binding.currentdate.getText().toString()));
                }
                break;
            case R.id.nextdate:
                long selectedMilli = calendar.getTimeInMillis();
                Date datePickerDate = new Date(selectedMilli);
                /*if (datePickerDate.after(new Date())) {
                    HandyObject.showAlert(getActivity(), "Can't Select Future date");
                } else {
                    binding.etfrom.setText(HandyObject.getDateFromPicker(myCalendarFrom.getTime()));
                }*/
                if (datePickerDate.after(new Date())) {
                    HandyObject.showAlert(getActivity(), "Can't Select Future date");
                } else if (count == 7) {
                    HandyObject.showAlert(getActivity(), getString(R.string.selectedweekdate));
                } else if (count < 7) {
                    calendar.add(calendar.HOUR_OF_DAY, -24);
                    count++;
                    calendar.add(calendar.HOUR_OF_DAY, 24);
                    binding.currentdate.setText(HandyObject.getDateFromPicker(calendar.getTime()));
                    // DayJobStatusApi(HandyObject.parseDateToYMD(binding.currentdate.getText().toString()));
                    calendar.add(calendar.HOUR_OF_DAY, 24);
                }
                break;
        }
    }

    private int setCountForDateChange(String day) {
        if (day.equalsIgnoreCase("Monday")) {
            count = 1;
        } else if (day.equalsIgnoreCase("Tuesday")) {
            count = 2;
        } else if (day.equalsIgnoreCase("Wednesday")) {
            count = 3;
        } else if (day.equalsIgnoreCase("Thursday")) {
            count = 4;
        } else if (day.equalsIgnoreCase("Friday")) {
            count = 5;
        } else if (day.equalsIgnoreCase("Saturday")) {
            count = 6;
        } else if (day.equalsIgnoreCase("Sunday")) {
            count = 7;
        }


       /* if (day.equalsIgnoreCase("Monday")) {
            count = 1;
        } else if (day.equalsIgnoreCase("Tuesday")) {
            count = 2;
        } else if (day.equalsIgnoreCase("Wednesday")) {
            count = 3;
        } else if (day.equalsIgnoreCase("Thursday")) {
            count = 4;
        } else if (day.equalsIgnoreCase("Friday")) {
            count = 5;
        }*/

        return count;
    }

    public void OnClickBack() {
        getActivity().getSupportFragmentManager().popBackStack();
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
