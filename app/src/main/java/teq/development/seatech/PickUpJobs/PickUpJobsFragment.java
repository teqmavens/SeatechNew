package teq.development.seatech.PickUpJobs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.Dashboard.DashBoardFragment;
import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.Dashboard.Skeleton.PickUpJobsSkeleton;
import teq.development.seatech.JobDetail.JobDetailStaticFragment;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.PickUpJobs.Adapter.AdapterPickUpJobs;
import teq.development.seatech.R;
import teq.development.seatech.Timesheet.TSWeekChildFragment;
import teq.development.seatech.Utils.AlarmReceiver;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.FrgmPickupjobsBinding;

public class PickUpJobsFragment extends Fragment {

    DashBoardActivity activity;
    private AdapterPickUpJobs adapterpickup;
    ArrayList<PickUpJobsSkeleton> arrayList;
    public static FrgmPickupjobsBinding binding;
    SQLiteDatabase database;
    Context context;
    Cursor cursor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DashBoardActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_pickupjobs, container, false);
        binding = DataBindingUtil.bind(rootView);
        initViews();
        return rootView;
    }

    private void initViews() {
        LinearLayoutManager lLManagerPickupJobs = new LinearLayoutManager(getActivity());
        lLManagerPickupJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcyviewPickupjobs.setLayoutManager(lLManagerPickupJobs);
        binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(getActivity(), R.drawable.searchleftchat), null);
        arrayList = new ArrayList<>();
       /* adapterpickup = new AdapterPickUpJobs(getActivity(), arrayList, PickUpJobsFragment.this);
        binding.rcyviewPickupjobs.setAdapter(adapterpickup);*/

        database = ParseOpenHelper.getInstance(context).getWritableDatabase();
        new DatabaseFetch().execute();
        PickUpJobsTask();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(schedulereciever,
                new IntentFilter("schedulereciever"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(nodatareciever,
                new IntentFilter("nodatareceiver"));

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterpickup.getFilter().filter(s.toString());
                } catch (Exception e) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

//    public void onClickTicketNo(int position) {
  public void onClickTicketNo(String jobid) {
        JobDetailStaticFragment jb = new JobDetailStaticFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", 0);
        bundle.putString("position_ticketId", jobid);
        jb.setArguments(bundle);
        activity.replaceFragment(jb);
    }

    private void PickUpJobsTask() {
        //  HandyObject.showProgressDialog(getActivity());
        if (HandyObject.checkInternetConnection(getActivity())) {
        HandyObject.getApiManagerMain().PickUpJobs(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseLC", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            arrayList.clear();
                            database.delete(ParseOpenHelper.TABLE_PICKUPJOBS, ParseOpenHelper.PICKUPTECHID + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)});
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {

                                //database.delete(ParseOpenHelper.TABLE_PICKUPJOBS, null, null);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                HandyObject.putbooleanPrams(context,AppConstants.PICKJOBPERMISSION,jsonObject.getBoolean("permission"));
                                if (jsonArray.length() == 0) {
                                    binding.rcyviewPickupjobs.setVisibility(View.INVISIBLE);
                                    binding.llnodata.setVisibility(View.VISIBLE);
                                } else {
                                    binding.rcyviewPickupjobs.setVisibility(View.VISIBLE);
                                    binding.llnodata.setVisibility(View.INVISIBLE);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobjInside = jsonArray.getJSONObject(i);
                                        PickUpJobsSkeleton ske = new PickUpJobsSkeleton();
                                        ske.setCustomerName(jobjInside.getString("customer_name"));
                                        ske.setJobTicketNo(jobjInside.getString("job_id"));
                                        ske.setCustomerType(jobjInside.getString("customer_type_name"));
                                        ske.setRegionName(jobjInside.getString("region_name"));
                                        ske.setEstimate_hours(jobjInside.getString("estimate_hours"));
                                        arrayList.add(ske);
                                        adapterpickup.notifyDataSetChanged();
                                        //     binding.rcyviewPickupjobs.setAdapter(adapterpickup);
                                        insertIntoDB(jobjInside.getString("customer_name"), jobjInside.getString("job_id"), jobjInside.getString("customer_type_name"), jobjInside.getString("region_name"), jobjInside.getString("estimate_hours"));
                                    }
                                }
                            } else {
                                binding.rcyviewPickupjobs.setVisibility(View.INVISIBLE);
                                binding.llnodata.setVisibility(View.VISIBLE);
                                //  HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
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

   // public void onClickSchedule(int position) {
    public void onClickSchedule(String esthr, String jobid) {
        if(HandyObject.getbooleanPrams(getActivity(),AppConstants.PICKJOBPERMISSION) == true) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("addtech");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            // Create and show the dialog.
           // DialogFragment newFragment = ScheduleTechDialog.newInstance(arrayList.get(position).getEstimate_hours(), arrayList.get(position).getJobTicketNo());
            DialogFragment newFragment = ScheduleTechDialog.newInstance(esthr, jobid);
            newFragment.show(ft, "scheduletech");
        } else {
            HandyObject.showAlert(getActivity(),getString(R.string.pickupjob_notpermitted));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(schedulereciever);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(nodatareciever);
    }

    private BroadcastReceiver schedulereciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PickUpJobsTask();
        }
    };

    private BroadcastReceiver nodatareciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("checkData").equalsIgnoreCase("blank")) {
                binding.rcyviewPickupjobs.setVisibility(View.INVISIBLE);
                binding.llnodata.setVisibility(View.VISIBLE);
            } else {
                PickUpJobsFragment.binding.rcyviewPickupjobs.setVisibility(View.VISIBLE);
                PickUpJobsFragment.binding.llnodata.setVisibility(View.INVISIBLE);
            }
        }
    };


    private void insertIntoDB(String customer_name, String job_id, String customer_type_name, String region_name, String estimate_hours) {
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.PICKUPTECHID, HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID));
        cv.put(ParseOpenHelper.PICKUPCUSTOMERNAME, customer_name);
        cv.put(ParseOpenHelper.PICKUPJOBID, job_id);
        cv.put(ParseOpenHelper.PICKUPCUSTOMERTYPENAME, customer_type_name);
        cv.put(ParseOpenHelper.PICKUPREGIONNAME, region_name);
        cv.put(ParseOpenHelper.PICKUPESTIMATEDHR, estimate_hours);
        long idd = database.insert(ParseOpenHelper.TABLE_PICKUPJOBS, null, cv);
    }

    public class DatabaseFetch extends AsyncTask<ArrayList<PickUpJobsSkeleton>, Void, ArrayList<PickUpJobsSkeleton>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HandyObject.showProgressDialog(getActivity());
        }

        @Override
        protected ArrayList<PickUpJobsSkeleton> doInBackground(ArrayList<PickUpJobsSkeleton>... arrayLists) {
            arrayList.clear();
            Gson gson = new Gson();
            cursor = database.query(ParseOpenHelper.TABLE_PICKUPJOBS, null, null, null, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        PickUpJobsSkeleton ske = new PickUpJobsSkeleton();
                        ske.setCustomerName(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.PICKUPCUSTOMERNAME)));
                        ske.setJobTicketNo(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.PICKUPJOBID)));
                        ske.setCustomerType(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.PICKUPCUSTOMERTYPENAME)));
                        ske.setRegionName(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.PICKUPREGIONNAME)));
                        ske.setEstimate_hours(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.PICKUPESTIMATEDHR)));
                        arrayList.add(ske);
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {
                }
            } else {
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<PickUpJobsSkeleton> allJobsSkeletons) {
            super.onPostExecute(allJobsSkeletons);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    adapterpickup = new AdapterPickUpJobs(getActivity(), arrayList, PickUpJobsFragment.this);
                    binding.rcyviewPickupjobs.setAdapter(adapterpickup);
                } else {
                    adapterpickup = new AdapterPickUpJobs(getActivity(), arrayList, PickUpJobsFragment.this);
                    binding.rcyviewPickupjobs.setAdapter(adapterpickup);
                }
            }
            HandyObject.stopProgressDialog();

        }
    }
}
