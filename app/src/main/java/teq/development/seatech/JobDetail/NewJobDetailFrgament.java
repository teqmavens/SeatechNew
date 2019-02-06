package teq.development.seatech.JobDetail;

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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Dashboard.Adapter.AdapterDashbrdUrgentMsg;
import teq.development.seatech.Dashboard.Adapter.AdapterJobSpinner;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.Dashboard.Skeleton.UrgentMsgSkeleton;
import teq.development.seatech.JobDetail.Adapter.AdapterJobTime;
import teq.development.seatech.JobDetail.Skeleton.JobTimeSkeleton;
import teq.development.seatech.JobDetail.Skeleton.LCChangeSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.FrgmNewjobdetailBinding;

public class NewJobDetailFrgament extends Fragment {

    ArrayList<AllJobsSkeleton> arralistAllJobs;
    private DashBoardActivity activity;
    private Context context;
    private AdapterJobSpinner adapterjobspinner;
    FrgmNewjobdetailBinding binding;
    ArrayList<JobTimeSkeleton> arralistJobTime;
    private AdapterJobTime adapterjobTime;
    boolean fromjobselection;
    int timerindex_prev = 0;
    SQLiteDatabase database;
    boolean jobspinner;
    boolean updatereciver;
    String selectedJobId = "";
    ArrayList<UrgentMsgSkeleton> arraylistUrgent;
    Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = (DashBoardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_newjobdetail, container, false);
        container.removeAllViews();
        binding = DataBindingUtil.bind(rootView);
        binding.setNewjobdetail(this);
        initViews();
        return rootView;
    }

    private void initViews() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateUrgentReceiverNew, new IntentFilter("update_UrgentReceiverNew"));
        new databsefetch().execute();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(reciever, new IntentFilter("load_message"));

    }
    private BroadcastReceiver updateUrgentReceiverNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Urgentdatafetch().execute();
        }
    };

    private BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // loadChatMessages();
            binding.uploadimage.setText(intent.getStringExtra("secss"));
        }
    };

    public class JobItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (HandyObject.getPrams(context, AppConstants.ISJOB_NEWTYPE).equalsIgnoreCase("yes")) {
                runHandlerforLC();
                setAllSpinnerData(position);
            } else if (arralistAllJobs.get(position).getJobticketNo().equalsIgnoreCase("111111")) {
                runHandlerforLC();
                setAllSpinnerData(position);
                App.appInstance.stopTimer();
                App.appInstance.startTimer();
            } else {
                App.appInstance.stopTimer();
                Intent intent = new Intent("fromJobNewDetail");
                intent.putExtra("positionnew", String.valueOf(position));
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void setAllSpinnerData(int position) {
        ArrayAdapter<CharSequence> lcAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.newlaborcode_array,
                android.R.layout.simple_spinner_item);
        lcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerLc.setAdapter(lcAdapter);
        //  App.appInstance.startTimer();
        if (isJobRunning() == true) {
            binding.spinnerLc.setSelection(HandyObject.getIntPrams(context, AppConstants.JOBLABORCODENEW_INDEX));
        } else {
            fromjobselection = true;
            binding.spinnerLc.setSelection(0);
        }
        binding.spinnerLc.setOnItemSelectedListener(new LCItemSelectedListener());
        selectedJobId = "111111";
        updateTimeSpinner();
        new Urgentdatafetch().execute();
    }

    private class Urgentdatafetch extends AsyncTask<ArrayList<UrgentMsgSkeleton>, Void, ArrayList<UrgentMsgSkeleton>> {

        @Override
        protected ArrayList<UrgentMsgSkeleton> doInBackground(ArrayList<UrgentMsgSkeleton>... arrayLists) {
            Cursor cursor = database.query(ParseOpenHelper.TABLE_URGENTMSG, null, ParseOpenHelper.URGENT_JOBID + "=?", new String[]{selectedJobId}, null, null, null);
            cursor.moveToFirst();
            arraylistUrgent = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                UrgentMsgSkeleton ske = new UrgentMsgSkeleton();
                ske.setJobticketid(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_JOBID)));
                ske.setCustomername(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_CUSTNAME)));
                ske.setCustomertype(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_CUSTTYPE)));
                ske.setBoatyear(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_BOATMKYEAR)));
                ske.setBoatname(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_BOATNAME)));
                ske.setSender(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_SENDER)));
                ske.setReceiver(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_RECEIVER)));
                ske.setMessage(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_MESSAGE)));
                ske.setTime(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_CREATEDAT)));
                ske.setAcknowledge(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_ACKNOWLEDGE)));
                ske.setMessageid(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_MESSAGEID)));
                ske.setReceiverid(cursor.getString(cursor.getColumnIndex(ParseOpenHelper.URGENT_RECEIVERID)));
                arraylistUrgent.add(ske);
                cursor.moveToNext();
            }
            cursor.close();
            return arraylistUrgent;
        }

        @Override
        protected void onPostExecute(ArrayList<UrgentMsgSkeleton> urgentMsgSkeletons) {
            super.onPostExecute(urgentMsgSkeletons);
            if (urgentMsgSkeletons.size() == 0) {
                binding.llheaderur.setVisibility(View.GONE);
                binding.nourgentmsg.setVisibility(View.VISIBLE);
                binding.rcyviewUrgentmsg.setVisibility(View.GONE);
            } else {
                binding.llheaderur.setVisibility(View.VISIBLE);
                binding.nourgentmsg.setVisibility(View.GONE);
                binding.rcyviewUrgentmsg.setVisibility(View.VISIBLE);
                LinearLayoutManager lLManagerparts = new LinearLayoutManager(getActivity());
                lLManagerparts.setOrientation(LinearLayoutManager.VERTICAL);
                binding.rcyviewUrgentmsg.setLayoutManager(lLManagerparts);
                AdapterDashbrdUrgentMsg adapterurgentmsg = new AdapterDashbrdUrgentMsg(context, urgentMsgSkeletons, NewJobDetailFrgament.this);
                binding.rcyviewUrgentmsg.setAdapter(adapterurgentmsg);

            }
        }
    }

    private void updateTimeSpinner() {
        arralistJobTime = new ArrayList<>();
        getplusminus(getNear15Minute());
    }

    public static int getNear15Minute() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int mod = minutes % 15;
        int res = 0;
        if ((mod) >= 8) {
            res = minutes + (15 - mod);
        } else {
            res = minutes - mod;
        }
        return (res % 60);
    }

    public class LCItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (HandyObject.getPrams(context, AppConstants.ISJOB_NEWTYPE).equalsIgnoreCase("yes")) {
                Log.e("AlreadyrunningLC", "AlreadyrunningLC");
            } else {
                Log.e("NOTrunningLC", "NOTrunningLC");
                if (jobspinner == true) {
                    App.appInstance.stopTimer();
                    App.appInstance.startTimer();

                    if (fromjobselection == true) {
                    } else {
                        String runinngtime = binding.uploadimage.getText().toString();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.MINUTE, Integer.parseInt(arralistJobTime.get(1).getHrminutes().split(":")[1]));
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arralistJobTime.get(1).getHrminutes().split(":")[0]));
                        calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(runinngtime.split(":")[0]));

                        int running_mins = Integer.parseInt(runinngtime.split(":")[1]);

                        calendar.add(Calendar.MINUTE, Integer.parseInt(runinngtime.split(":")[1]));

                        calendar.set(Calendar.MINUTE, HandyObject.getNear15MinuteLB(Integer.parseInt(manageMinutes(calendar.get(Calendar.MINUTE)))));
                        if (HandyObject.getNear15MinuteLB(Integer.parseInt(manageMinutes(calendar.get(Calendar.MINUTE)))) == 0) {
                            String withoutzero = String.valueOf(running_mins).replaceFirst("^0+(?!$)", "");
                            if (Integer.parseInt(withoutzero) > 7) {
                                calendar.add(Calendar.HOUR_OF_DAY, 1);
                            }
                        }
                        //  String starttime = arralistJobTime.get(1).getParsedate() + " " + arralistJobTime.get(1).getHrminutes();

                        String starttime = arralistJobTime.get(binding.timespinner.getSelectedItemPosition()).getParsedate() + " " + arralistJobTime.get(binding.timespinner.getSelectedItemPosition()).getHrminutes();
                        arralistJobTime.clear();
                        calendar.add(Calendar.MINUTE, -15);
                        JobTimeSkeleton jobtimeske = new JobTimeSkeleton();
                        jobtimeske.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
                        jobtimeske.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
                        arralistJobTime.add(jobtimeske);

                        calendar.add(Calendar.MINUTE, 15);
                        JobTimeSkeleton jobtimeske2 = new JobTimeSkeleton();
                        jobtimeske2.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
                        jobtimeske2.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
                        arralistJobTime.add(jobtimeske2);

                        calendar.add(Calendar.MINUTE, 15);
                        JobTimeSkeleton jobtimeske3 = new JobTimeSkeleton();
                        jobtimeske3.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
                        jobtimeske3.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
                        arralistJobTime.add(jobtimeske3);
                        adapterjobTime = new AdapterJobTime(context, arralistJobTime);
                        binding.timespinner.setAdapter(adapterjobTime);
                        //   HandyObject.showAlert(context,String.valueOf(binding.timespinner.getSelectedItemPosition()));
                        if (isJobRunning() == true) {
                            binding.timespinner.setSelection(HandyObject.getIntPrams(context, AppConstants.JOBSTARTTIMENEW_INDEX));
                        } else {
                            binding.timespinner.setSelection(1);
                        }

                        String endtime = arralistJobTime.get(1).getParsedate() + " " + arralistJobTime.get(1).getHrminutes();
                        String hrsworked = String.valueOf(binding.uploadimage.getText()).split(":")[0] + ":" + String.valueOf(binding.uploadimage.getText().toString()).split(":")[1];


                        TaskLCChange(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID), arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(),
                                binding.spinnerLc.getSelectedItem().toString(), HandyObject.getIntPrams(getActivity(), AppConstants.LCSPINNERNEW_LASTPOSI),
                                starttime, endtime, hrsworked, String.valueOf(HoursAdjusted(timerindex_prev)), "normal");
                    }
                    /*HandyObject.showAlert(context, String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)) + "---" + HandyObject.ParseDateJobTime(calendar.getTime()));*/
                }
            }
            HandyObject.putPrams(context, AppConstants.ISJOB_NEWTYPE, "no");
            HandyObject.putIntPrams(getActivity(), AppConstants.LCSPINNERNEW_LASTPOSI, position);
            fromjobselection = false;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private void runHandlerforLC() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jobspinner = true;
            }
        }, 1000);
    }

    public int HoursAdjusted(int n) {
        int res = 0;
        if (n == 0) {
            res = -15;
        } else if (n == 2) {
            res = 15;
        }
        return res;
    }

    boolean isJobRunning() {
        if (HandyObject.getPrams(context, AppConstants.ISJOB_NEWTYPE).equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
    }

    public void onClickStop() {
        setDataForLCSumbmit();
    }

    public void OnClickMyLaborPerf() {
        arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo();
        if (binding.etLaborperform.getText().toString().length() == 0) {
            binding.etLaborperform.requestFocus();
            HandyObject.showAlert(context, getString(R.string.fieldempty));
        } else {
            SubmitMyLaborPerf_Task(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID),
                    arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(), binding.etLaborperform.getText().toString(), "Tech Labour Performed");
        }
    }

    private void SubmitMyLaborPerf_Task(String techid, final String jobid, String notes, String type) {

        HandyObject.showProgressDialog(getActivity());
        DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
        dashnotes_ske.setCreatedAt(HandyObject.ParseDateTimeForNotes(new Date()));
        final String insertedTime = HandyObject.ParseDateTimeForNotes(new Date());
        dashnotes_ske.setNoteWriter(techid);
        dashnotes_ske.setNotes(notes);
        dashnotes_ske.setTechid(techid);
        dashnotes_ske.setJobid(jobid);
        dashnotes_ske.setType(type);
        ArrayList<DashboardNotes_Skeleton> addtech = new ArrayList<>();
        addtech.add(dashnotes_ske);
        String OffTheRecord = gson.toJson(addtech);
        insertIntoDB(HandyObject.ParseDateTimeForNotes(new Date()), OffTheRecord, techid, jobid, notes, type);
        //  HandyObject.getApiManagerMain().submitTechLaborPerf(techid, jobid, notes, type, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
        if (HandyObject.checkInternetConnection(getActivity())) {
            HandyObject.getApiManagerMain().submitTechLaborPerf(OffTheRecord, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.e("responseMyLaborPerform", jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobjInside = jsonArray.getJSONObject(i);
                                        String jobid = jobjInside.getString("job_id");
                                       // arrayListLaborPerf.clear();
                                        JSONArray jArray_OffTheRecord = jobjInside.getJSONArray("TechLabourPerformed");
                                        ArrayList<DashboardNotes_Skeleton> arraylistOffTheRecord = new ArrayList<>();
                                        for (int k = 0; k < jArray_OffTheRecord.length(); k++) {
                                            JSONObject jobj_dashnotes = jArray_OffTheRecord.getJSONObject(k);
                                            DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
                                            dashnotes_ske.setCreatedAt(jobj_dashnotes.getString("created_at"));
                                            dashnotes_ske.setNoteWriter(jobj_dashnotes.getString("written_by"));
                                            dashnotes_ske.setNotes(jobj_dashnotes.getString("notes"));
                                            arraylistOffTheRecord.add(dashnotes_ske);
                                           // arrayListLaborPerf.add(dashnotes_ske);
                                        }
                                        String OffTheRecord = gson.toJson(arraylistOffTheRecord);
                                        ContentValues cv = new ContentValues();
                                        cv.put(ParseOpenHelper.JOBSTECHLABORPERFORMCURRDAY, OffTheRecord);
                                        database.update(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, cv, ParseOpenHelper.TECHIDCURRDAY + " =? AND " + ParseOpenHelper.JOBIDCURRDAY + " = ?",
                                                new String[]{HandyObject.getPrams(getContext(), AppConstants.LOGINTEQ_ID), jobid});
                                    }

                                    //Delete related row from database
                                    database.delete(ParseOpenHelper.TABLE_SUBMITMYLABOR_NEWOFFRECORD, ParseOpenHelper.SUBMITLABORJOBID + " =? AND " + ParseOpenHelper.SUBMITLABORTIME + " = ?",
                                            new String[]{jobid, insertedTime});
                                    binding.etLaborperform.setText("");
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
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
                                HandyObject.stopProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("responseError", t.getMessage());
                            HandyObject.stopProgressDialog();
                        }
                    });

        } else {
            HandyObject.showAlert(getActivity(), getString(R.string.fetchdata_whenonline));
            HandyObject.stopProgressDialog();
            binding.etLaborperform.setText("");
        }
    }

    private void insertIntoDB(String time, String offTheRecord, String techid, String jobid, String notes, String type) {
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.SUBMITLABORTECHID, techid);
        cv.put(ParseOpenHelper.SUBMITLABORJOBID, jobid);
        cv.put(ParseOpenHelper.SUBMITLABORTYPE, type);
        cv.put(ParseOpenHelper.SUBMITLABORNOTES, notes);
        cv.put(ParseOpenHelper.SUBMITLABORTIME, time);
        cv.put(ParseOpenHelper.SUBMITLABORREST, offTheRecord);
        long idd = database.insert(ParseOpenHelper.TABLE_SUBMITMYLABOR_NEWOFFRECORD, null, cv);
    }

    public void getplusminus(int n) {
        List<String> listtime = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        if (isJobRunning() == true) {
            Log.e("plusminusrunning", "running");
            calendar.set(Calendar.MINUTE, Integer.parseInt(HandyObject.getPrams(context, AppConstants.JOBRUNNINGNEW_CENTERTIME).split(":")[1]));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(HandyObject.getPrams(context, AppConstants.JOBRUNNINGNEW_CENTERTIME).split(":")[0]));
        } else {
            Log.e("notrunning", "notrunning");
            calendar.set(Calendar.MINUTE, n);
        }

        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, -15);
        listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
        JobTimeSkeleton jobtimeske = new JobTimeSkeleton();
        jobtimeske.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
        jobtimeske.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
        arralistJobTime.add(jobtimeske);

        calendar.add(Calendar.MINUTE, 15);
        listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
        JobTimeSkeleton jobtimeske2 = new JobTimeSkeleton();
        jobtimeske2.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
        jobtimeske2.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
        arralistJobTime.add(jobtimeske2);

        calendar.add(Calendar.MINUTE, 15);
        listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
        JobTimeSkeleton jobtimeske3 = new JobTimeSkeleton();
        jobtimeske3.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
        jobtimeske3.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
        arralistJobTime.add(jobtimeske3);
        adapterjobTime = new AdapterJobTime(context, arralistJobTime);
        // binding.timespinner.setAdapter(dataAdaptertime);
        binding.timespinner.setAdapter(adapterjobTime);

        binding.timespinner.setOnItemSelectedListener(new TimeItemSelectedListener());
        if (isJobRunning() == true) {
            binding.timespinner.setSelection(HandyObject.getIntPrams(context, AppConstants.JOBSTARTTIMENEW_INDEX));
        } else {
            binding.timespinner.setSelection(1);
        }
    }

    private String manageMinutes(int min) {
        if (min == 0) {
            return "00";
        } else {
            return String.valueOf(min);
        }
    }

    public class TimeItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
            // HandyObject.showAlert(context, String.valueOf(position));
            timerindex_prev = position;
            String runinngtime = binding.uploadimage.getText().toString();
            int running_mins = Integer.parseInt(runinngtime.split(":")[1]);
            if (position == 2) {
                if (running_mins < 15) {
                    HandyObject.showAlert(getActivity(), getString(R.string.cantselect_futtime));
                    binding.timespinner.setSelection(1);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private void TaskLCChange(String teqid, String jobid, final String seleclc, int lclast_posi, String starttime, String endtime, final String hrsworked, String hrsAdjusted, final String type) {

        LCChangeSkeleton lcs_ke = new LCChangeSkeleton();
        lcs_ke.setTech_id(teqid);
        lcs_ke.setJob_id(jobid);
        lcs_ke.setLabour_code(HandyObject.getLaborcodeNew(lclast_posi));
        lcs_ke.setStart_time(starttime);
        lcs_ke.setEnd_time(endtime);
        lcs_ke.setHours(hrsworked);
        lcs_ke.setHours_adjusted(hrsAdjusted);
        lcs_ke.setCreated_by(teqid);
        ArrayList<LCChangeSkeleton> arrayList = new ArrayList<>();
        arrayList.add(lcs_ke);
        String techlog = gson.toJson(arrayList);
        final String insertedTime = HandyObject.ParseDateTimeForNotes(new Date());
        insertIntoDBLC(teqid, jobid, HandyObject.getLaborcodeNew(lclast_posi), starttime, endtime, hrsworked, hrsAdjusted, insertedTime);
        if (HandyObject.checkInternetConnection(getActivity())) {
            HandyObject.showProgressDialog(getActivity());
            HandyObject.getApiManagerMain().submitLCdata(techlog, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.e("responseLC", jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                    HandyObject.showAlert(getActivity(), jsonObject.getString("message"));
                                    // String previous_runninghr = HandyObject.getPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME);
                                    // int newttlwrkHr = Integer.parseInt(previous_runninghr) + Integer.parseInt(String.valueOf(hrsworked.split(":")[1]).replaceFirst("^0+(?!$)", ""));
                                    //  HandyObject.putPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME, String.valueOf(newttlwrkHr));

                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobj = jsonArray.getJSONObject(i);

                                        //Delete related row from database
                                        database.delete(ParseOpenHelper.TABLE_LCCHANGE, ParseOpenHelper.LCCHANGEJOBID + " =? AND " + ParseOpenHelper.LCCHANGECREATEDAT + " = ?",
                                                new String[]{jobj.getString("job_id"), insertedTime});
                                    }

                                    if (type.equalsIgnoreCase("stop")) {
                                        updatereciver = true;
                                        HandyObject.putPrams(context, AppConstants.ISJOB_NEWTYPE, "no");
                                        Intent intent_reg = new Intent(getActivity(), DashBoardActivity.class);
                                        startActivity(intent_reg);
                                        getActivity().finish();
                                        getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                                    }
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
                                HandyObject.stopProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("responseError", t.getMessage());
                            HandyObject.stopProgressDialog();
                        }
                    });
        } else {
            if (type.equalsIgnoreCase("stop")) {
                HandyObject.showAlert(getActivity(), getString(R.string.fetchdata_whenonline));
                HandyObject.stopProgressDialog();
                updatereciver = true;
                HandyObject.putPrams(context, AppConstants.ISJOB_NEWTYPE, "no");
                Intent intent_reg = new Intent(getActivity(), DashBoardActivity.class);
                startActivity(intent_reg);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        }
    }

    private void insertIntoDBLC(String teqid, String jobid, String lc, String starttime, String endtime, String hrsworked, String hrsAdjusted, String createdAt) {
        ContentValues cv = new ContentValues();
        cv.put(ParseOpenHelper.LCCHANGETECHID, teqid);
        cv.put(ParseOpenHelper.LCCHANGEJOBID, jobid);
        cv.put(ParseOpenHelper.LCCHANGELC, lc);
        cv.put(ParseOpenHelper.LCCHANGESTARTTIME, starttime);
        cv.put(ParseOpenHelper.LCCHANGEENDTIME, endtime);
        cv.put(ParseOpenHelper.LCCHANGEHHOURS, hrsworked);
        cv.put(ParseOpenHelper.LCCHANGEHHOURSADJUSTED, hrsAdjusted);
        cv.put(ParseOpenHelper.LCCHANGECREATEDBY, teqid);
        cv.put(ParseOpenHelper.LCCHANGECREATEDAT, createdAt);
        long idd = database.insert(ParseOpenHelper.TABLE_LCCHANGE, null, cv);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(reciever);
            if (updatereciver == true) {
                updatereciver = false;
                HandyObject.putPrams(context, AppConstants.ISJOB_NEWTYPE, "no");
            } else {
                HandyObject.putPrams(context, AppConstants.ISJOB_NEWTYPE, "yes");
            }
            HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
          //  HandyObject.putPrams(context, AppConstants.ISJOB_NEWTYPE, "yes");
            HandyObject.putPrams(context, AppConstants.JOBRUNNINGNEW_ETLABORPERFORM, binding.etLaborperform.getText().toString());
            HandyObject.putPrams(context, AppConstants.JOBRUNNINGNEW_CENTERTIME, arralistJobTime.get(1).getHrminutes());
            HandyObject.putIntPrams(context, AppConstants.JOBRUNNINGNEW_INDEX, binding.jobspinner.getSelectedItemPosition());
            HandyObject.putIntPrams(context, AppConstants.JOBLABORCODENEW_INDEX, binding.spinnerLc.getSelectedItemPosition());
            HandyObject.putIntPrams(context, AppConstants.JOBSTARTTIMENEW_INDEX, binding.timespinner.getSelectedItemPosition());
            Log.e("onDestroyView", "onDestroyView");
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroyNewJob", "onDestroyNewJob");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onpauseNewJob", "onpauseNewJob");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("onStopNewJob", "onStopNewJob");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("onDetachNewJob", "onDetachNewJob");
    }


    private void setDataForLCSumbmit() {
        String runinngtime = binding.uploadimage.getText().toString();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, Integer.parseInt(arralistJobTime.get(1).getHrminutes().split(":")[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arralistJobTime.get(1).getHrminutes().split(":")[0]));
        calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(runinngtime.split(":")[0]));

        int running_mins = Integer.parseInt(runinngtime.split(":")[1]);

        calendar.add(Calendar.MINUTE, Integer.parseInt(runinngtime.split(":")[1]));

        calendar.set(Calendar.MINUTE, HandyObject.getNear15MinuteLB(Integer.parseInt(manageMinutes(calendar.get(Calendar.MINUTE)))));
        if (HandyObject.getNear15MinuteLB(Integer.parseInt(manageMinutes(calendar.get(Calendar.MINUTE)))) == 0) {
            String withoutzero = String.valueOf(running_mins).replaceFirst("^0+(?!$)", "");
            if (Integer.parseInt(withoutzero) > 7) {
                calendar.add(Calendar.HOUR_OF_DAY, 1);
            }
        }
        // String starttime = arralistJobTime.get(1).getParsedate() + " " + arralistJobTime.get(1).getHrminutes();

        String starttime = arralistJobTime.get(binding.timespinner.getSelectedItemPosition()).getParsedate() + " " + arralistJobTime.get(binding.timespinner.getSelectedItemPosition()).getHrminutes();

        ArrayList<JobTimeSkeleton> arralistJobTime_JobStatus = new ArrayList<>();
        calendar.add(Calendar.MINUTE, -15);
        JobTimeSkeleton jobtimeske = new JobTimeSkeleton();
        jobtimeske.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
        jobtimeske.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
        arralistJobTime_JobStatus.add(jobtimeske);

        calendar.add(Calendar.MINUTE, 15);
        JobTimeSkeleton jobtimeske2 = new JobTimeSkeleton();
        jobtimeske2.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
        jobtimeske2.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
        arralistJobTime_JobStatus.add(jobtimeske2);

        calendar.add(Calendar.MINUTE, 15);
        JobTimeSkeleton jobtimeske3 = new JobTimeSkeleton();
        jobtimeske3.setHrminutes(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
        jobtimeske3.setParsedate(HandyObject.ParseDateJobTime(calendar.getTime()));
        arralistJobTime_JobStatus.add(jobtimeske3);

        String endtime = arralistJobTime_JobStatus.get(1).getParsedate() + " " + arralistJobTime_JobStatus.get(1).getHrminutes();
        String hrsworked = String.valueOf(binding.uploadimage.getText()).split(":")[0] + ":" + String.valueOf(binding.uploadimage.getText().toString()).split(":")[1];

        String hrsAdjusted = String.valueOf(HoursAdjusted(binding.timespinner.getSelectedItemPosition()));
        //  String finalValue = starttime + "--" + endtime + "--" + hrsworked + "--" + hrsAdjusted + "--" + binding.spinnerLc.getSelectedItem().toString();

        TaskLCChange(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID), arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(),
                binding.spinnerLc.getSelectedItem().toString(), binding.spinnerLc.getSelectedItemPosition(),
                starttime, endtime, hrsworked, String.valueOf(HoursAdjusted(timerindex_prev)), "stop");
        // }

    }


    private class databsefetch extends AsyncTask<ArrayList<AllJobsSkeleton>, Void, ArrayList<AllJobsSkeleton>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         //   HandyObject.showProgressDialog(getActivity());
        }

        @Override
        protected ArrayList<AllJobsSkeleton> doInBackground(ArrayList<AllJobsSkeleton>... arrayLists) {
            database = ParseOpenHelper.getInstance(context).getWritableDatabase();
            gson = new Gson();
            Cursor cursor = database.query(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, null, ParseOpenHelper.TECHIDCURRDAY + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)}, null, null, null);
            cursor.moveToFirst();
            arralistAllJobs = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                Type type = new TypeToken<AllJobsSkeleton>() {
                }.getType();
                Type typedashnotes = new TypeToken<ArrayList<DashboardNotes_Skeleton>>() {
                }.getType();
                Type typedstring = new TypeToken<ArrayList<String>>() {
                }.getType();
                String getSke = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSSKELETONCURRDAY));
                String getSkedashnotes = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHDASHBOARDNOTESCURRDAY));
                String getLaborPerfList = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHLABORPERFORMCURRDAY));
                String getOffTheRecordList = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHOFFTHERECORDCURRDAY));
                String getUploadImages = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHUPLOADEDIMAGESCURRDAY));

                AllJobsSkeleton ske = gson.fromJson(getSke, type);
                ArrayList<DashboardNotes_Skeleton> arrayListDash = gson.fromJson(getSkedashnotes, typedashnotes);
                ArrayList<DashboardNotes_Skeleton> arrayListLaborPerform = gson.fromJson(getLaborPerfList, typedashnotes);
                ArrayList<DashboardNotes_Skeleton> arrayListOffTheRecordList = gson.fromJson(getOffTheRecordList, typedashnotes);
                ArrayList<String> arrayListUploadImages = gson.fromJson(getUploadImages, typedstring);

                ske.setArrayList(arrayListDash);
                ske.setArrayListLaborPerf(arrayListLaborPerform);
                ske.setArrayListOffTheRecord(arrayListOffTheRecordList);
            //    ske.setArrayListImages(arrayListUploadImages);
                arralistAllJobs.add(ske);
                //     arrayListDashNotes.addAll(arrayListDash);
                cursor.moveToNext();
            }
            cursor.close();
            return arralistAllJobs;
        }

        @Override
        protected void onPostExecute(ArrayList<AllJobsSkeleton> allJobsSkeletons) {
            super.onPostExecute(allJobsSkeletons);
            adapterjobspinner = new AdapterJobSpinner(context, allJobsSkeletons);
            binding.jobspinner.setAdapter(adapterjobspinner);
            //  if (getArguments() != null) {
            // int posi = Integer.parseInt(getArguments().getString("posi"));
            binding.jobspinner.setSelection(allJobsSkeletons.size() - 1);
            //  }
            if (isJobRunning() == true) {
                binding.etLaborperform.setText(HandyObject.getPrams(context, AppConstants.JOBRUNNINGNEW_ETLABORPERFORM));
                //  binding.jobspinner.setSelection(HandyObject.getIntPrams(context, AppConstants.JOBRUNNING_INDEX));
            }
            binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());
          //  HandyObject.stopProgressDialog();
        }
    }

}
