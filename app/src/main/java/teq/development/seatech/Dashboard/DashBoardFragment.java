package teq.development.seatech.Dashboard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Chat.ChatActivity;
import teq.development.seatech.Dashboard.Adapter.AdapterDashbrdUrgentMsg;
import teq.development.seatech.Dashboard.Adapter.AdapterJosbForYou;
import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.Dashboard.Skeleton.PartsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.UrgentMsgSkeleton;
import teq.development.seatech.JobDetail.JobDetailFragment;
import teq.development.seatech.JobDetail.JobDetailStaticFragment;
import teq.development.seatech.JobDetail.MainJobdetail;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.FrgmDashboardBinding;

public class DashBoardFragment extends Fragment {

    FrgmDashboardBinding binding;
    private DashBoardActivity activity;
    private Context context;
    private AdapterJosbForYou adapterjobs;
    private AdapterDashbrdUrgentMsg adapterurgentmsg;
    private ArrayList<AllJobsSkeleton> jobsArrayList;
    GoogleMap googleMap;
    SQLiteDatabase database;
    Calendar myCalendar;
    Marker marker;
    boolean localdata;
    Cursor cursor;
    DatePickerDialog.OnDateSetListener date;
    ArrayList<UrgentMsgSkeleton> arraylistUrgent;
    ArrayList<Marker> arraylistmarker = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = (DashBoardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_dashboard, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmdashboard(this);
        binding.map.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initViews(binding);
        return rootView;
    }


    @Override
    public void onResume() {
        binding.map.onResume();
        super.onResume();
        Log.e("DashFragresume", "DashFragresume");

    }

    @Override
    public void onStart() {
        super.onStart();
        binding.map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gmap) {
                googleMap = gmap;
                new DatabaseFetch().execute();
                getAllDataTask(HandyObject.parseDateToYMD(HandyObject.getCurrentDate()));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.map.onDestroy();
        Log.e("DashFRagment Destroy", "DashFRagment Destroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.map.onLowMemory();
    }

    private void initViews(FrgmDashboardBinding binding) {
       /* DashBoardActivity.mSocket.off("urgent message", onNewUrgentMessage);
        DashBoardActivity.mSocket.on("urgent message", onNewUrgentMessage);
        DashBoardActivity.mSocket.connect();*/
        App.appInstance.getSocket().off("urgent message", onNewUrgentMessage);
        App.appInstance.getSocket().on("urgent message", onNewUrgentMessage);
        App.appInstance.getSocket().connect();

        database = ParseOpenHelper.getInstance(context).getWritableDatabase();
        jobsArrayList = new ArrayList<>();
        arraylistUrgent = new ArrayList<>();
        adapterjobs = new AdapterJosbForYou(context, jobsArrayList, DashBoardFragment.this);
        LinearLayoutManager lLManagerJobs = new LinearLayoutManager(getActivity());
        lLManagerJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcyviewJobs.setLayoutManager(lLManagerJobs);
        adapterjobs = new AdapterJosbForYou(context, jobsArrayList, DashBoardFragment.this);
        binding.rcyviewJobs.setAdapter(adapterjobs);

        LinearLayoutManager lLManagerUrgentJobs = new LinearLayoutManager(getActivity());
        lLManagerUrgentJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcyviewUrgentmsg.setLayoutManager(lLManagerUrgentJobs);
        adapterurgentmsg = new AdapterDashbrdUrgentMsg(context, arraylistUrgent, DashBoardFragment.this);
        binding.rcyviewUrgentmsg.setAdapter(adapterurgentmsg);

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (view.isShown()) {
                    updateLabel();
                }
            }
        };

        binding.currentdate.setText(HandyObject.getCurrentDate());
        binding.previousdate.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, R.drawable.leftarrow), null, null, null);
        binding.nextdate.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(context, R.drawable.rightarrow), null);
    }


    private void updateLabel() {
        binding.currentdate.setText(HandyObject.getDateFromPicker(myCalendar.getTime()));
        getAllDataTask(HandyObject.parseDateToYMD(binding.currentdate.getText().toString()));
    }

    public void OnClickStartTime() {
        if (isJobRunning() == true || HandyObject.getPrams(context, AppConstants.ISJOB_NEWTYPE).equalsIgnoreCase("yes")) {
            HandyObject.showAlert(context, getString(R.string.cannotstart_newjob));
        } else {
            if (HandyObject.getCurrentDate().split("\\s+")[1].equalsIgnoreCase(binding.currentdate.getText().toString().split("\\s+")[1])) {
                if (jobsArrayList.size() == 0) {
                    HandyObject.showAlert(context, getString(R.string.nojobs_tostart));
                } else {
                    activity.replaceFragment(new MainJobdetail());
                    /*if (isJobRunning() == true) {
                        activity.replaceFragment(new MainJobdetail());
                    } else if (HandyObject.getPrams(context, AppConstants.ISJOB_NEWTYPE).equalsIgnoreCase("yes")) {
                        activity.replaceFragment(new MainJobdetail());
                    }*/
                }
            } else {
                HandyObject.showAlert(context, getString(R.string.onlystatrt_currentdayjob));
            }
        }
    }

    public void OnClickNextDate() {

        if (HandyObject.checkInternetConnection(context)) {
            binding.currentdate.setText(HandyObject.getNextDate(myCalendar));
            //  HandyObject.showAlert(context, HandyObject.parseDateToYMD(binding.currentdate.getText().toString()));
            getAllDataTask(HandyObject.parseDateToYMD(binding.currentdate.getText().toString()));
        } else {
            HandyObject.showAlert(getActivity(), getString(R.string.withoutinter_noprenextjob));
        }
    }

    public void OnClickPreviousDate() {
        if (HandyObject.checkInternetConnection(context)) {
            binding.currentdate.setText(HandyObject.getPreviousDate(myCalendar));
            // HandyObject.showAlert(context, HandyObject.parseDateToYMD(binding.currentdate.getText().toString()));
            getAllDataTask(HandyObject.parseDateToYMD(binding.currentdate.getText().toString()));
        } else {
            HandyObject.showAlert(getActivity(), getString(R.string.withoutinter_noprenextjob));
        }
    }

    public void OnClickCalendar() {
        if (HandyObject.checkInternetConnection(context)) {
            new DatePickerDialog(getActivity(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else {
            HandyObject.showAlert(getActivity(), getString(R.string.withoutinter_nopartidatedata));
        }
    }

    public void onClickTicketNo(int position) {
        JobDetailStaticFragment jb = new JobDetailStaticFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("position_ticketId", jobsArrayList.get(position).getJobticketNo());
        jb.setArguments(bundle);
        activity.replaceFragment(jb);
    }

    public void onClickNotes(int position) {
        dialogTechViewNotes(position);
    }

    private void dialogTechViewNotes(int position) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialogviewnotes");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = ViewTechNotesDialog.newInstance(position);
        newFragment.show(ft, "dialogviewnotes");
    }

    private void getAllDataTask(final String date) {
        HandyObject.getApiManagerTypeJobs().getDashboradData(HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), date, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseDash", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            database.delete(ParseOpenHelper.TABLE_URGENTMSG, ParseOpenHelper.URGENT_TECHID + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)});
                            database.delete(ParseOpenHelper.TABLENAME_ALLJOBS, ParseOpenHelper.TECHID + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)});
                            database.delete(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, ParseOpenHelper.TECHIDCURRDAY + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)});
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                jobsArrayList.clear();
                                arraylistUrgent.clear();
                                arraylistmarker.clear();
                                googleMap.clear();
                                adapterjobs.notifyDataSetChanged();
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                Gson gson = new Gson();

                                //For Urgent Messages
                                JSONArray jArray_urgent = jsonObject.getJSONArray("urgent");
                                if (jArray_urgent.length() == 0) {
                                    //arraylistUrgent.clear();
                                    binding.llheaderur.setVisibility(View.GONE);
                                    binding.rcyviewUrgentmsg.setVisibility(View.GONE);
                                    binding.nourgentmsg.setVisibility(View.VISIBLE);
                                    HandyObject.stopAlarm(getActivity());
                                } else {
                                    binding.llheaderur.setVisibility(View.VISIBLE);
                                    binding.rcyviewUrgentmsg.setVisibility(View.VISIBLE);
                                    binding.nourgentmsg.setVisibility(View.GONE);
                                    HandyObject.stopStartAlarm(getActivity());
                                }
                                // ArrayList<UrgentMsgSkeleton> arraylisturgentDB = new ArrayList<>();
                                for (int k = 0; k < jArray_urgent.length(); k++) {
                                    JSONObject jobj_urgent = jArray_urgent.getJSONObject(k);
                                    UrgentMsgSkeleton ske = new UrgentMsgSkeleton();
                                    ske.setJobticketid(jobj_urgent.getString("job_id"));
                                    ske.setCustomername(jobj_urgent.getString("customer_name"));
                                    ske.setCustomertype(jobj_urgent.getString("customer_type"));
                                    ske.setBoatyear(jobj_urgent.getString("boat_make_year"));
                                    ske.setBoatname(jobj_urgent.getString("boat_name"));
                                    ske.setSender(jobj_urgent.getString("Sender"));
                                    ske.setReceiver(jobj_urgent.getString("Receiver"));
                                    ske.setMessage(jobj_urgent.getString("message"));
                                    ske.setTime(jobj_urgent.getString("created_at"));
                                    ske.setAcknowledge(jobj_urgent.getString("acknowledge"));
                                    ske.setMessageid(jobj_urgent.getString("id"));
                                    ske.setReceiverid(jobj_urgent.getString("receiver_id"));
                                    arraylistUrgent.add(ske);
                                    adapterurgentmsg.notifyDataSetChanged();
                                    ContentValues cv = new ContentValues();
                                    cv.put(ParseOpenHelper.URGENT_TECHID, HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID));
                                    cv.put(ParseOpenHelper.URGENT_JOBID, jobj_urgent.getString("job_id"));
                                    cv.put(ParseOpenHelper.URGENT_CUSTNAME, jobj_urgent.getString("customer_name"));
                                    cv.put(ParseOpenHelper.URGENT_CUSTTYPE, jobj_urgent.getString("customer_type"));
                                    cv.put(ParseOpenHelper.URGENT_BOATMKYEAR, jobj_urgent.getString("boat_make_year"));
                                    cv.put(ParseOpenHelper.URGENT_BOATNAME, jobj_urgent.getString("boat_name"));
                                    cv.put(ParseOpenHelper.URGENT_SENDER, jobj_urgent.getString("Sender"));
                                    cv.put(ParseOpenHelper.URGENT_RECEIVER, jobj_urgent.getString("Receiver"));
                                    cv.put(ParseOpenHelper.URGENT_MESSAGE, jobj_urgent.getString("message"));
                                    cv.put(ParseOpenHelper.URGENT_CREATEDAT, jobj_urgent.getString("created_at"));
                                    cv.put(ParseOpenHelper.URGENT_ACKNOWLEDGE, jobj_urgent.getString("acknowledge"));
                                    cv.put(ParseOpenHelper.URGENT_MESSAGEID, jobj_urgent.getString("id"));
                                    cv.put(ParseOpenHelper.URGENT_RECEIVERID, jobj_urgent.getString("receiver_id"));
                                    long idd = database.insert(ParseOpenHelper.TABLE_URGENTMSG, null, cv);
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobjInside = jsonArray.getJSONObject(i);
                                    AllJobsSkeleton skeleton = new AllJobsSkeleton();
                                    skeleton.setCustomerName(jobjInside.getString("customer_name"));
                                    skeleton.setJobticketNo(jobjInside.getString("job_id"));
                                    skeleton.setJobType(jobjInside.getString("job_type"));
                                    skeleton.setBoatLocation(jobjInside.getString("boat_address"));
                                    skeleton.setSupplyamount(jobjInside.getString("supply_amount"));
                                    skeleton.setJobdescription(jobjInside.getString("job_description"));
                                    skeleton.setTime(jobjInside.getString("job_start_time"));
                                    skeleton.setSalesorder(jobjInside.getString("sales_order"));
                                    skeleton.setTechSupervisor(jobjInside.getString("supervisor"));
                                    skeleton.setOtherMembers(jobjInside.getString("other_member"));
                                    skeleton.setPartLocation(jobjInside.getString("location"));
                                    skeleton.setBoatmakeYear(jobjInside.getString("boat_make_year"));
                                    skeleton.setBoatmodelLength(jobjInside.getString("boat_model_length"));
                                    skeleton.setBoatName(jobjInside.getString("boat_name"));
                                    skeleton.setHullid(jobjInside.getString("hull_id"));
                                    skeleton.setCaptainname(jobjInside.getString("captain_name"));
                                    skeleton.setPromisedate(jobjInside.getString("promise_ending_date"));
                                    skeleton.setRep(jobjInside.getString("rep"));
                                    skeleton.setJobselection(jobjInside.getString("job_selection"));
                                    skeleton.setIfbid(jobjInside.getString("bid_hours"));
                                    skeleton.setQcPerson(jobjInside.getString("qc"));
                                    skeleton.setJoblatitude(jobjInside.getString("job_lattitude"));
                                    skeleton.setJoblongitude(jobjInside.getString("job_longitude"));
                                    skeleton.setHavepart(jobjInside.getString("have_parts"));
                                    skeleton.setNeedpart(jobjInside.getString("need_parts"));

                                    JSONArray jArray_DashNotes = jobjInside.getJSONArray("TechDashboard");
                                    ArrayList<DashboardNotes_Skeleton> arraylistDashNotes = new ArrayList<>();
                                    for (int k = 0; k < jArray_DashNotes.length(); k++) {
                                        JSONObject jobj_dashnotes = jArray_DashNotes.getJSONObject(k);
                                        DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
                                        dashnotes_ske.setCreatedAt(jobj_dashnotes.getString("created_at"));
                                        dashnotes_ske.setNoteWriter(jobj_dashnotes.getString("note_writer"));
                                        dashnotes_ske.setNotes(jobj_dashnotes.getString("notes"));
                                        arraylistDashNotes.add(dashnotes_ske);
                                    }

                                    JSONArray jArray_Labrperformed = jobjInside.getJSONArray("TechLabourPerformed");
                                    ArrayList<DashboardNotes_Skeleton> arraylistLabrperformed = new ArrayList<>();
                                    for (int k = 0; k < jArray_Labrperformed.length(); k++) {
                                        JSONObject jobj_dashnotes = jArray_Labrperformed.getJSONObject(k);
                                        DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
                                        dashnotes_ske.setCreatedAt(jobj_dashnotes.getString("created_at"));
                                        dashnotes_ske.setNoteWriter(jobj_dashnotes.getString("note_writer"));
                                        dashnotes_ske.setNotes(jobj_dashnotes.getString("notes"));
                                        arraylistLabrperformed.add(dashnotes_ske);
                                    }

                                    JSONArray jArray_OffTheRecord = jobjInside.getJSONArray("OffTheRecord");
                                    ArrayList<DashboardNotes_Skeleton> arraylistOffTheRecord = new ArrayList<>();
                                    for (int k = 0; k < jArray_OffTheRecord.length(); k++) {
                                        JSONObject jobj_dashnotes = jArray_OffTheRecord.getJSONObject(k);
                                        DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
                                        dashnotes_ske.setCreatedAt(jobj_dashnotes.getString("created_at"));
                                        dashnotes_ske.setNoteWriter(jobj_dashnotes.getString("note_writer"));
                                        dashnotes_ske.setNotes(jobj_dashnotes.getString("notes"));
                                        arraylistOffTheRecord.add(dashnotes_ske);
                                    }

                                    JSONArray jArray_parts = jobjInside.getJSONArray("parts");
                                    ArrayList<PartsSkeleton> arraylistParts = new ArrayList<>();
                                    for (int k = 0; k < jArray_parts.length(); k++) {
                                        JSONObject jobj_parts = jArray_parts.getJSONObject(k);
                                        PartsSkeleton parts_ske = new PartsSkeleton();
                                        parts_ske.setManufname(jobj_parts.getString("mfg_name"));
                                        parts_ske.setPartdesc(jobj_parts.getString("part_description"));
                                        parts_ske.setEta(jobj_parts.getString("eta"));
                                        parts_ske.setLocation(jobj_parts.getString("part_location"));
                                        parts_ske.setHavepart(jobj_parts.getString("have_parts"));
                                        arraylistParts.add(parts_ske);
                                    }

                                    JSONArray jArray_upldImages = jobjInside.getJSONArray("uploads");
                                    ArrayList<String> arraylistupldImages = new ArrayList<>();
                                    for (int k = 0; k < jArray_upldImages.length(); k++) {
                                        arraylistupldImages.add(jArray_upldImages.getString(k));
                                    }

                                    jobsArrayList.add(skeleton);
                                    String jobsSke_databse = gson.toJson(skeleton);
                                    String dashnotes = gson.toJson(arraylistDashNotes);
                                    String laborPerformed = gson.toJson(arraylistLabrperformed);
                                    String OffTheRecord = gson.toJson(arraylistOffTheRecord);
                                    String UploadedImages = gson.toJson(arraylistupldImages);
                                    String PartsRecords = gson.toJson(arraylistParts);
                                    //   String UrgentRecords = gson.toJson(arraylisturgentDB);

                                    ContentValues cv = new ContentValues();
                                    cv.put(ParseOpenHelper.TECHID, HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID));
                                    cv.put(ParseOpenHelper.JOBID, jobjInside.getString("job_id"));
                                    cv.put(ParseOpenHelper.JOBSSKELETON, jobsSke_databse);
                                    cv.put(ParseOpenHelper.JOBSTECHDASHBOARDNOTES, dashnotes);
                                    cv.put(ParseOpenHelper.JOBSTECHLABORPERFORM, laborPerformed);
                                    cv.put(ParseOpenHelper.JOBSTECHOFFTHERECORD, OffTheRecord);
                                    cv.put(ParseOpenHelper.JOBSTECHUPLOADEDIMAGES, UploadedImages);
                                    cv.put(ParseOpenHelper.JOBSTECHPARTSRECORD, PartsRecords);
                                    //cv.put(ParseOpenHelper.JOBSTECHURGENTMSG, UrgentRecords);
                                    long idd = database.insert(ParseOpenHelper.TABLENAME_ALLJOBS, null, cv);
                                    Log.e("table", String.valueOf(idd));
                                    if (jobjInside.getString("job_id").equalsIgnoreCase("111111")) {
                                    } else {
                                        setJobMarkers(jobsArrayList.get(i).getJoblatitude(), jobsArrayList.get(i).getJoblongitude());
                                    }


                                    if (date.split("-")[2].equalsIgnoreCase(binding.currentdate.getText().toString().split("\\s+")[1])) {
                                        ContentValues cv_current = new ContentValues();
                                        cv_current.put(ParseOpenHelper.TECHIDCURRDAY, HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID));
                                        cv_current.put(ParseOpenHelper.JOBIDCURRDAY, jobjInside.getString("job_id"));
                                        cv_current.put(ParseOpenHelper.JOBSSKELETONCURRDAY, jobsSke_databse);
                                        cv_current.put(ParseOpenHelper.JOBSTECHDASHBOARDNOTESCURRDAY, dashnotes);
                                        cv_current.put(ParseOpenHelper.JOBSTECHLABORPERFORMCURRDAY, laborPerformed);
                                        cv_current.put(ParseOpenHelper.JOBSTECHOFFTHERECORDCURRDAY, OffTheRecord);
                                        cv_current.put(ParseOpenHelper.JOBSTECHUPLOADEDIMAGESCURRDAY, UploadedImages);
                                        cv_current.put(ParseOpenHelper.JOBSTECHPARTSRECORDCURRDAY, PartsRecords);
                                        // cv_current.put(ParseOpenHelper.JOBSTECHURGENTMSGCURRDAY, UrgentRecords);
                                        long iddc = database.insert(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, null, cv_current);
                                        Log.e("tableCurrentDay", String.valueOf(iddc));
                                    }
                                }

                                if (localdata == false) {
                                    LatLng latlong1 = new LatLng(Double.parseDouble(jsonArray.getJSONObject(0).getString("job_lattitude")), Double.parseDouble(jsonArray.getJSONObject(0).getString("job_longitude")));
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latlong1).zoom(2).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }
                                localdata = false;
                                adapterjobs.notifyDataSetChanged();
                            } else {
                                jobsArrayList.clear();
                                arraylistmarker.clear();
                                arraylistUrgent.clear();
                                googleMap.clear();
                                adapterjobs.notifyDataSetChanged();
                                adapterurgentmsg.notifyDataSetChanged();
                                binding.llheaderur.setVisibility(View.GONE);
                                binding.rcyviewUrgentmsg.setVisibility(View.GONE);
                                binding.nourgentmsg.setVisibility(View.VISIBLE);
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
                            //  HandyObject.stopProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("responseError", t.getMessage());
                        HandyObject.stopProgressDialog();
                    }
                });
    }

    private void setJobMarkers(String latitude, String longitude) {
        LatLng latlong = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        marker = googleMap.addMarker(new MarkerOptions().position(latlong).title("Marker Title").snippet("Marker Description").
                icon(BitmapDescriptorFactory.fromResource(R.drawable.mappinpng)));
        arraylistmarker.add(marker);
    }

    boolean isJobRunning() {
        if (HandyObject.getPrams(context, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("DashFragment onPause", "DashFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("DashFragment onStop", "DashFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("DashFnt onDeoyView", "DashFragment onDestroyView");
    }

    public class DatabaseFetch extends AsyncTask<ArrayList<AllJobsSkeleton>, Void, ArrayList<AllJobsSkeleton>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HandyObject.showProgressDialog(getActivity());
        }

        @Override
        protected ArrayList<AllJobsSkeleton> doInBackground(ArrayList<AllJobsSkeleton>... arrayLists) {
            jobsArrayList.clear();
            arraylistUrgent.clear();
            Gson gson = new Gson();
            cursor = database.query(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, null, ParseOpenHelper.TECHIDCURRDAY + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)}, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        Type type = new TypeToken<AllJobsSkeleton>() {
                        }.getType();
                        Type typeurgent = new TypeToken<ArrayList<UrgentMsgSkeleton>>() {
                        }.getType();
                        String getSke = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSSKELETONCURRDAY));
                        // String getUrgentMsg = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHURGENTMSGCURRDAY));

                        AllJobsSkeleton ske = gson.fromJson(getSke, type);
                        // ArrayList<UrgentMsgSkeleton> arrayListUrgentMsg = gson.fromJson(getUrgentMsg, typeurgent);
                        //  arraylistUrgent.addAll(arrayListUrgentMsg);
                                       /* if (arraylistUrgent.size() == 0) {
                                            //arraylistUrgent.clear();
                                            binding.llheaderur.setVisibility(View.GONE);
                                            binding.rcyviewUrgentmsg.setVisibility(View.GONE);
                                            binding.nourgentmsg.setVisibility(View.VISIBLE);
                                        } else {
                                            binding.llheaderur.setVisibility(View.VISIBLE);
                                            binding.rcyviewUrgentmsg.setVisibility(View.VISIBLE);
                                            binding.nourgentmsg.setVisibility(View.GONE);
                                        }*/
                        jobsArrayList.add(ske);
                        //setJobMarkers(jobsArrayList.get(cursor.getPosition()).getJoblatitude(), jobsArrayList.get(cursor.getPosition()).getJoblongitude());
                        cursor.moveToNext();
                    }
                    cursor.close();
                    localdata = true;
                    // binding.rcyviewJobs.setAdapter(adapterjobs);
                } else {
                    //binding.rcyviewJobs.setAdapter(adapterjobs);
                    //  HandyObject.showAlert(context, "cursor not greater than zero");
                }

            } else {
                // HandyObject.showAlert(context, "cursor null");
            }
            return jobsArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<AllJobsSkeleton> allJobsSkeletons) {
            super.onPostExecute(allJobsSkeletons);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    LatLng latlong1 = new LatLng(Double.parseDouble(allJobsSkeletons.get(0).getJoblatitude()), Double.parseDouble(allJobsSkeletons.get(0).getJoblongitude()));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latlong1).zoom(2).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    binding.rcyviewJobs.setAdapter(adapterjobs);
                    // binding.rcyviewUrgentmsg.setAdapter(adapterurgentmsg);
                    new Urgentdatafetch().execute();
                    for (int i = 0; i < jobsArrayList.size(); i++) {
                        if (jobsArrayList.get(i).getJobticketNo().equalsIgnoreCase("111111")) {
                        } else {
                            setJobMarkers(jobsArrayList.get(i).getJoblatitude(), jobsArrayList.get(i).getJoblongitude());
                        }
                    }
                } else {
                    binding.rcyviewJobs.setAdapter(adapterjobs);
                }
            }
            HandyObject.stopProgressDialog();

        }
    }

    private Emitter.Listener onNewUrgentMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("Msg Ugent", "urgntmessage");
                    try {
                        JSONArray data = (JSONArray) args[0];
                        if (arraylistUrgent.size() == 0) {
                            binding.llheaderur.setVisibility(View.VISIBLE);
                            binding.rcyviewUrgentmsg.setVisibility(View.VISIBLE);
                            binding.nourgentmsg.setVisibility(View.GONE);
                            HandyObject.stopStartAlarm(getActivity());
                        }
                        for (int k = 0; k < data.length(); k++) {
                            JSONObject jobj_urgent = data.getJSONObject(k);
                            UrgentMsgSkeleton ske = new UrgentMsgSkeleton();
                            ske.setJobticketid(jobj_urgent.getString("job_id"));
                            ske.setCustomername(jobj_urgent.getString("customer_name"));
                            ske.setCustomertype(jobj_urgent.getString("customer_type"));
                            ske.setBoatyear(jobj_urgent.getString("boat_make_year"));
                            ske.setBoatname(jobj_urgent.getString("boat_name"));
                            ske.setSender(jobj_urgent.getString("Sender"));
                            ske.setReceiver(jobj_urgent.getString("Receiver"));
                            ske.setMessage(jobj_urgent.getString("message"));
                            ske.setTime(jobj_urgent.getString("created_at"));
                            ske.setAcknowledge(jobj_urgent.getString("acknowledge"));
                            ske.setMessageid(jobj_urgent.getString("id"));
                            ske.setReceiverid(jobj_urgent.getString("receiver_id"));
                            arraylistUrgent.add(0, ske);
                            adapterurgentmsg.notifyDataSetChanged();

                            database.delete(ParseOpenHelper.TABLE_URGENTMSG, ParseOpenHelper.URGENT_TECHID + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)});
                            for (int i = 0; i < arraylistUrgent.size(); i++) {
                                ContentValues cv = new ContentValues();
                                cv.put(ParseOpenHelper.URGENT_TECHID, HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID));
                                cv.put(ParseOpenHelper.URGENT_JOBID, arraylistUrgent.get(i).getJobticketid());
                                cv.put(ParseOpenHelper.URGENT_CUSTNAME, arraylistUrgent.get(i).getCustomername());
                                cv.put(ParseOpenHelper.URGENT_CUSTTYPE, arraylistUrgent.get(i).getCustomertype());
                                cv.put(ParseOpenHelper.URGENT_BOATMKYEAR, arraylistUrgent.get(i).getBoatyear());
                                cv.put(ParseOpenHelper.URGENT_BOATNAME, arraylistUrgent.get(i).getBoatname());
                                cv.put(ParseOpenHelper.URGENT_SENDER, arraylistUrgent.get(i).getSender());
                                cv.put(ParseOpenHelper.URGENT_RECEIVER, arraylistUrgent.get(i).getReceiver());
                                cv.put(ParseOpenHelper.URGENT_MESSAGE, arraylistUrgent.get(i).getMessage());
                                cv.put(ParseOpenHelper.URGENT_CREATEDAT, arraylistUrgent.get(i).getTime());
                                cv.put(ParseOpenHelper.URGENT_MESSAGEID, arraylistUrgent.get(i).getMessageid());
                                cv.put(ParseOpenHelper.URGENT_ACKNOWLEDGE, arraylistUrgent.get(i).getAcknowledge());
                                cv.put(ParseOpenHelper.URGENT_RECEIVERID, arraylistUrgent.get(i).getReceiverid());
                                long idd = database.insert(ParseOpenHelper.TABLE_URGENTMSG, null, cv);
                            }

                            if (jobj_urgent.getString("job_id").equalsIgnoreCase("111111")) {
                                Intent intent = new Intent("update_UrgentReceiverNew");
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            } else {
                                Intent intent = new Intent("update_UrgentReceiver");
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            }

                            sendDeliverStatus(jobj_urgent.getString("id"), jobj_urgent.getString("sender_id"));

                        }
                    } catch (Exception e) {
                    }
                }
            }, 10);

        }
    };

    private void sendDeliverStatus(String msgid, String senderid) {
        try {
            Log.e("DELEIVER FROM DASHBRD", "dvdfdsfds");
            JSONObject jobj_delv = new JSONObject();
            jobj_delv.put("id", Integer.parseInt(msgid));
            jobj_delv.put("sender_id", senderid);
            JSONArray jarry_delv = new JSONArray();
            jarry_delv.put(jobj_delv);
            // DashBoardActivity.mSocket.emit("delivered", jarry_delv);
            App.appInstance.getSocket().emit("delivered", jarry_delv);
        } catch (Exception e) {
        }
    }


    private class Urgentdatafetch extends AsyncTask<ArrayList<UrgentMsgSkeleton>, Void, ArrayList<UrgentMsgSkeleton>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arraylistUrgent.clear();
        }

        @Override
        protected ArrayList<UrgentMsgSkeleton> doInBackground(ArrayList<UrgentMsgSkeleton>... arrayLists) {
            Cursor cursor = database.query(ParseOpenHelper.TABLE_URGENTMSG, null, ParseOpenHelper.URGENT_TECHID + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)}, null, null, null);
            cursor.moveToFirst();
            //  Log.e("CURSOR_____COUNT",String.valueOf(cursor.getCount()));
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
                binding.rcyviewUrgentmsg.setVisibility(View.GONE);
                binding.nourgentmsg.setVisibility(View.VISIBLE);
                HandyObject.stopAlarm(getActivity());
            } else {
                HandyObject.stopStartAlarm(getActivity());
                binding.llheaderur.setVisibility(View.VISIBLE);
                binding.rcyviewUrgentmsg.setVisibility(View.VISIBLE);
                binding.nourgentmsg.setVisibility(View.GONE);
                // arraylistUrgent.addAll(urgentMsgSkeletons);
                adapterurgentmsg.notifyDataSetChanged();
            }
        }
    }
}