package teq.development.seatech.JobDetail;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

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
import teq.development.seatech.App;
import teq.development.seatech.Chat.ChatActivity;
import teq.development.seatech.Chat.ComposeDialogNew;
import teq.development.seatech.Dashboard.Adapter.AdapterDashbrdUrgentMsg;
import teq.development.seatech.Dashboard.Adapter.AdapterJobSpinner;
import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.Dashboard.Skeleton.PartsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.TimeSpentSkeleton;
import teq.development.seatech.Dashboard.Skeleton.UploadImageNewSkeleton;
import teq.development.seatech.Dashboard.Skeleton.UrgentMsgSkeleton;
import teq.development.seatech.JobDetail.Adapter.AdapterDashboardNotes;
import teq.development.seatech.JobDetail.Adapter.AdapterParts;
import teq.development.seatech.JobDetail.Adapter.AdapterTimeSpent;
import teq.development.seatech.JobDetail.Adapter.AdapterUploadedImages;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.FrgmJobdetailstaticBinding;


public class JobDetailStaticFragment extends Fragment {

    public FrgmJobdetailstaticBinding binding;
    private AdapterDashboardNotes adapterdashnotes;
    ArrayList<AllJobsSkeleton> arralistAllJobs;
    private AdapterUploadedImages adapterUploadedImages;
    private ArrayList<AllJobsSkeleton> jobsArrayList;
    private AdapterTimeSpent adapterTImeSpent;
    private AdapterParts adapterParts;
    SQLiteDatabase database;
    Gson gson;
    Cursor cursor;
    Context context;
    String selc_ticketno;
    String pdfUrl = "";
    String selectedJobId = "";
    ArrayList<UrgentMsgSkeleton> arraylistUrgent;
    ArrayList<DashboardNotes_Skeleton> arrayListLaborPerf, arrayListOffTheRecord;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_jobdetailstatic, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmjobdetailstatic(this);
        arralistAllJobs = new ArrayList<>();
        arrayListLaborPerf = new ArrayList<>();
        arrayListOffTheRecord = new ArrayList<>();
        new databsefetch().execute();
        return rootView;
    }

    public void OnClickViewComment() {
        dialogViewComment("viewcommment", arrayListLaborPerf);
        //dialogViewComment("viewcommment", arrayNewCheck);
    }

    public void OnClickOffTheRecord() {
        dialogViewComment("offrecord", arrayListOffTheRecord);
    }

    private void dialogViewComment(String type, ArrayList<DashboardNotes_Skeleton> arraylist) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialogviewcomment");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = ViewCommentDialog.newInstance(arraylist, type);
        newFragment.show(ft, "dialogviewcomment");
    }

    private class databsefetch extends AsyncTask<ArrayList<AllJobsSkeleton>, Void, ArrayList<AllJobsSkeleton>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HandyObject.showProgressDialog(getActivity());
            selc_ticketno = getArguments().getString("position_ticketId");
        //    HandyObject.showAlert(getActivity(),selc_ticketno);
        }

        @Override
        protected ArrayList<AllJobsSkeleton> doInBackground(ArrayList<AllJobsSkeleton>... arrayLists) {
            database = ParseOpenHelper.getInstance(context).getWritableDatabase();
            gson = new Gson();
            cursor = database.query(ParseOpenHelper.TABLENAME_ALLJOBS, null, ParseOpenHelper.TECHID + " =? AND " + ParseOpenHelper.JOBID + " = ?",
                    new String[]{HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID), selc_ticketno}, null, null, null);
            cursor.moveToFirst();
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    /*arralistAllJobs = new ArrayList<>();
                    arrayListLaborPerf = new ArrayList<>();
                    arrayListOffTheRecord = new ArrayList<>();*/
                    while (!cursor.isAfterLast()) {
                        Type type = new TypeToken<AllJobsSkeleton>() {
                        }.getType();
                        Type typedashnotes = new TypeToken<ArrayList<DashboardNotes_Skeleton>>() {
                        }.getType();
                        Type typeparts = new TypeToken<ArrayList<PartsSkeleton>>() {
                        }.getType();
                        Type typeimages = new TypeToken<ArrayList<UploadImageNewSkeleton>>() {
                        }.getType();
                        Type typeLC = new TypeToken<ArrayList<TimeSpentSkeleton>>() {
                        }.getType();
                        Type typedstring = new TypeToken<ArrayList<String>>() {
                        }.getType();

                        String getSke = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSSKELETON));
                        String getSkedashnotes = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHDASHBOARDNOTES));
                        String getLaborPerfList = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHLABORPERFORM));
                        String getOffTheRecordList = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHOFFTHERECORD));
                        String getUploadImages = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHUPLOADEDIMAGES));
                      //  String getUploadImages = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHUPLOADEDIMAGES));
                        String getPartsRecord = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHPARTSRECORD));
                        String getLcRecords = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHLCRECORD));

                        AllJobsSkeleton ske = gson.fromJson(getSke, type);
                        ArrayList<DashboardNotes_Skeleton> arrayListDash = gson.fromJson(getSkedashnotes, typedashnotes);
                        ArrayList<DashboardNotes_Skeleton> arrayListLaborPerform = gson.fromJson(getLaborPerfList, typedashnotes);
                        ArrayList<DashboardNotes_Skeleton> arrayListOffTheRecordList = gson.fromJson(getOffTheRecordList, typedashnotes);
                        ArrayList<PartsSkeleton> arrayListParts = gson.fromJson(getPartsRecord, typeparts);
                        ArrayList<UploadImageNewSkeleton> arrayListUploadImages = gson.fromJson(getUploadImages, typeimages);
                        ArrayList<TimeSpentSkeleton> arrayListLC = gson.fromJson(getLcRecords, typeLC);
                       // ArrayList<String> arrayListUploadImages = gson.fromJson(getUploadImages, typedstring);

                        ske.setArrayList(arrayListDash);
                        ske.setArrayListLaborPerf(arrayListLaborPerform);
                        ske.setArrayListOffTheRecord(arrayListOffTheRecordList);
                        ske.setArrayListImages(arrayListUploadImages);
                        ske.setArrayListParts(arrayListParts);
                        ske.setArrayListLC(arrayListLC);
                        arralistAllJobs.add(ske);
                        cursor.moveToNext();
                    }
                    cursor.close();
                } else {
                    if (HandyObject.checkInternetConnection(getActivity())) {
                        getRelatedTask();
                    } else {
                        HandyObject.stopProgressDialog();
                        HandyObject.showAlert(getActivity(), getString(R.string.nodataRegthisjob));
                    }
                }
            } else {
                HandyObject.showAlert(getActivity(), "cursornull");
            }
            return arralistAllJobs;
        }

        @Override
        protected void onPostExecute(ArrayList<AllJobsSkeleton> allJobsSkeletons) {
            super.onPostExecute(allJobsSkeletons);
            if (getArguments() != null) {
                if (cursor.getCount() > 0) {
                    int position = getArguments().getInt("position");
                    setAllSpinnerData(0);
                    HandyObject.stopProgressDialog();
                }

            }
        }
    }

    public void OnClickViewPdf() {
        if (pdfUrl.equalsIgnoreCase("-")) {
            HandyObject.showAlert(getActivity(), getString(R.string.nosalesorder));
        } else if (pdfUrl != null && !pdfUrl.isEmpty()) {
            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
            startActivity(browserIntent);*/
            Intent intent = new Intent();
            intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
            Intent chooserIntent = Intent.createChooser(intent, "Open Report");
            startActivity(chooserIntent);
           /* String url= "https://docs.google.com/gview?embedded=true&url="+pdfUrl;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);*/
        } else {
            HandyObject.showAlert(getActivity(), getString(R.string.nosalesorder));
        }
    }

    private void setAllSpinnerData(int position) {
        selectedJobId = arralistAllJobs.get(position).getJobticketNo();
        pdfUrl = arralistAllJobs.get(position).getSalesorder();
        binding.boatmakeyearValue.setText(arralistAllJobs.get(position).getBoatmakeYear());
        binding.boatmodelValue.setText(arralistAllJobs.get(position).getBoatmodelLength());
        binding.boatnameValue.setText(arralistAllJobs.get(position).getBoatName());
        binding.hullidValue.setText(arralistAllJobs.get(position).getHullid());
        binding.captnameValue.setText(arralistAllJobs.get(position).getCaptainname());
        binding.promisedateValue.setText(arralistAllJobs.get(position).getPromisedate());
        binding.repValue.setText(arralistAllJobs.get(position).getRep());
        binding.jobselectionValue.setText(arralistAllJobs.get(position).getJobselection());
        binding.jobtypeValue.setText(arralistAllJobs.get(position).getJobType());
        binding.ifbidValue.setText(arralistAllJobs.get(position).getIfbid());
        binding.qcpersonValue.setText(arralistAllJobs.get(position).getQcPerson());
        binding.qcpersonValue.setText(arralistAllJobs.get(position).getQcPerson());
        binding.supervisorValue.setText(arralistAllJobs.get(position).getTechSupervisor());
        binding.jobdesValue.setText(arralistAllJobs.get(position).getJobdescription());
        binding.boatlocationValue.setText(arralistAllJobs.get(position).getBoatLocation());

        if (pdfUrl.equalsIgnoreCase("-")) {
            binding.salesorderValuetext.setVisibility(View.VISIBLE);
            binding.salesorderValue.setVisibility(View.GONE);
        } else if (pdfUrl != null && !pdfUrl.isEmpty()) {
            binding.salesorderValuetext.setVisibility(View.GONE);
            binding.salesorderValue.setVisibility(View.VISIBLE);
        }
       /* LinearLayoutManager lLManagerDashNotes = new LinearLayoutManager(getActivity());
        lLManagerDashNotes.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcyviewDashbrdnotes.setLayoutManager(lLManagerDashNotes);
        adapterdashnotes = new AdapterDashboardNotes(context, arralistAllJobs.get(position).getArrayList());
        binding.rcyviewDashbrdnotes.setNestedScrollingEnabled(false);
        binding.rcyviewDashbrdnotes.setAdapter(adapterdashnotes);*/

        arrayListLaborPerf = arralistAllJobs.get(position).getArrayListLaborPerf();
        arrayListOffTheRecord = arralistAllJobs.get(position).getArrayListOffTheRecord();
        // binding.lastoffRecrdnotes.setText(arrayListOffTheRecord.get(arrayListOffTheRecord.size() - 1).getNotes());
        binding.etValuesupplies.setText(arralistAllJobs.get(position).getSupplyamount() + "$");

        //Set LC Time Spent related data
        binding.remainhrsValue.setText(arralistAllJobs.get(position).getRemainhr());
        if (arralistAllJobs.get(position).getArrayListLC().size() == 0) {
            binding.noTSmsg.setVisibility(View.VISIBLE);
            binding.llheaderTS.setVisibility(View.GONE);
            binding.rcyviewts.setVisibility(View.GONE);
        } else {
            binding.noTSmsg.setVisibility(View.GONE);
            binding.llheaderTS.setVisibility(View.VISIBLE);
            binding.rcyviewts.setVisibility(View.VISIBLE);
            LinearLayoutManager lLManagerLC = new LinearLayoutManager(context);
            lLManagerLC.setOrientation(LinearLayoutManager.VERTICAL);
            // binding.r.setLayoutManager(lLManagerDashNotes);
            binding.rcyviewts.setLayoutManager(lLManagerLC);
            adapterTImeSpent = new AdapterTimeSpent(arralistAllJobs.get(position).getArrayListLC(),context);
            binding.rcyviewts.setAdapter(adapterTImeSpent);
        }

        if (arralistAllJobs.get(position).getArrayList().size() == 0) {
            binding.nodashbrdnotes.setVisibility(View.VISIBLE);
            binding.rcyviewDashbrdnotes.setVisibility(View.GONE);
        } else {
            binding.nodashbrdnotes.setVisibility(View.GONE);
            binding.rcyviewDashbrdnotes.setVisibility(View.VISIBLE);
            LinearLayoutManager lLManagerDashNotes = new LinearLayoutManager(getActivity());
            lLManagerDashNotes.setOrientation(LinearLayoutManager.VERTICAL);
            binding.rcyviewDashbrdnotes.setLayoutManager(lLManagerDashNotes);
            adapterdashnotes = new AdapterDashboardNotes(context, arralistAllJobs.get(position).getArrayList());
            binding.rcyviewDashbrdnotes.setNestedScrollingEnabled(false);
            binding.rcyviewDashbrdnotes.setAdapter(adapterdashnotes);
        }

        if (arrayListOffTheRecord.size() == 0) {
            binding.lastoffRecrdnotes.setText(getString(R.string.norecordFound));
        } else {
           // binding.lastoffRecrdnotes.setText(arrayListOffTheRecord.get(arrayListOffTheRecord.size() - 1).getNotes());
            binding.lastoffRecrdnotes.setText(arrayListOffTheRecord.get(0).getNotes()+" ("+arrayListOffTheRecord.get(0).getNoteWriter()+")");
        }

        if (arralistAllJobs.get(position).getArrayListImages().size() == 0) {
            binding.nouploadedimage.setVisibility(View.VISIBLE);
            binding.rcylrviewUpldedImages.setVisibility(View.GONE);
        } else {
            binding.nouploadedimage.setVisibility(View.GONE);
            binding.rcylrviewUpldedImages.setVisibility(View.VISIBLE);
            LinearLayoutManager lLManagerImages = new LinearLayoutManager(getActivity());
            lLManagerImages.setOrientation(LinearLayoutManager.HORIZONTAL);
            binding.rcylrviewUpldedImages.setLayoutManager(lLManagerImages);
            adapterUploadedImages = new AdapterUploadedImages(context, arralistAllJobs.get(position).getArrayListImages(), getFragmentManager());
            //binding.rc.setNestedScrollingEnabled(false);
            binding.rcylrviewUpldedImages.setAdapter(adapterUploadedImages);
        }

        if (arralistAllJobs.get(position).getArrayListParts().size() == 0) {
            binding.noparts.setVisibility(View.VISIBLE);
            binding.lltopparts.setVisibility(View.GONE);
            binding.recyclerparts.setVisibility(View.GONE);
        } else {
            binding.noparts.setVisibility(View.GONE);
            binding.lltopparts.setVisibility(View.VISIBLE);
            binding.recyclerparts.setVisibility(View.VISIBLE);
            LinearLayoutManager lLManagerparts = new LinearLayoutManager(getActivity());
            lLManagerparts.setOrientation(LinearLayoutManager.VERTICAL);
            // binding.r.setLayoutManager(lLManagerDashNotes);
            binding.recyclerparts.setLayoutManager(lLManagerparts);
            adapterParts = new AdapterParts(context, arralistAllJobs.get(position).getArrayListParts());
            binding.recyclerparts.setAdapter(adapterParts);
        }

        new Urgentdatafetch().execute();
        binding.rcyviewDashbrdnotes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    //For related chat page
    public void OnClickChat() {
        // If chat is not initiated yet then chat dialog will open either direct chat window related to selected job
        if (arralistAllJobs.get(0).getJobmsgCount().equalsIgnoreCase("0")) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("composemsgnew");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            // Create and show the dialog.
            DialogFragment newFragment = ComposeDialogNew.newInstance(4, arralistAllJobs.get(0).getJobticketNo(), arralistAllJobs.get(0).getCustomerName(), arralistAllJobs.get(0).getCustomerType(),
                    arralistAllJobs.get(0).getBoatName(), arralistAllJobs.get(0).getBoatmakeYear());
            newFragment.show(ft, "composemsgnew");
        } else {
            Intent resultIntent = new Intent(context, ChatActivity.class);
            resultIntent.putExtra("type", "chat");
            resultIntent.putExtra("jobid", selectedJobId);
            ((Activity) context).startActivity(resultIntent);
        }
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
              /*  if (updateUrgent == true) {
                    UrgentMsgSkeleton ske = urgentMsgSkeletons.get(urgentMsgSkeletons.size() - 1);
                    urgentMsgSkeletons.remove(urgentMsgSkeletons.size() - 1);
                    HandyObject.showAlert(getActivity(), ske.getMessage());
                    urgentMsgSkeletons.add(0, ske);
                }*/
                AdapterDashbrdUrgentMsg adapterurgentmsg = new AdapterDashbrdUrgentMsg(context, urgentMsgSkeletons, JobDetailStaticFragment.this);
                binding.rcyviewUrgentmsg.setAdapter(adapterurgentmsg);

            }
        }
    }

    public void OnClickBacktoList() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void getRelatedTask() {
        jobsArrayList = new ArrayList<>();
        HandyObject.getApiManagerMain().getTicketData(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID),selc_ticketno, HandyObject.getPrams(getActivity(), AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.e("responseDetail", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                jobsArrayList.clear();
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                Gson gson = new Gson();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobjInside = jsonArray.getJSONObject(i);
                                    AllJobsSkeleton skeleton = new AllJobsSkeleton();
                                    skeleton.setCustomerName(jobjInside.getString("customer_name"));
                                    skeleton.setJobticketNo(jobjInside.getString("job_id"));
                                    skeleton.setJobType(jobjInside.getString("job_type"));
                                    skeleton.setBoatLocation(jobjInside.getString("boat_address"));
                                    skeleton.setSupplyamount(jobjInside.getString("supply_amount"));
                                    skeleton.setJobdescription(jobjInside.getString("job_description"));
                                    skeleton.setJobmsgCount(jobjInside.getString("job_message_count"));
                                    skeleton.setTime(jobjInside.getString("estimate_hours"));
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
                                    skeleton.setRep(jobjInside.getString("sup_rep_name"));
                                    skeleton.setJobselection(jobjInside.getString("job_selection"));
                                    skeleton.setIfbid(jobjInside.getString("bid_hours"));
                                    skeleton.setQcPerson(jobjInside.getString("qc"));
                                    skeleton.setJoblatitude(jobjInside.getString("job_lattitude"));
                                    skeleton.setJoblongitude(jobjInside.getString("job_longitude"));
                                    skeleton.setRemainhr(jobjInside.getString("remain_hours"));

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

//                                    JSONArray jArray_upldImages = jobjInside.getJSONArray("uploads");
//                                    ArrayList<String> arraylistupldImages = new ArrayList<>();
//                                    for (int k = 0; k < jArray_upldImages.length(); k++) {
//                                        arraylistupldImages.add(jArray_upldImages.getString(k));
//                                    }

                                    JSONArray jArray_upldImages = jobjInside.getJSONArray("uploads");
                                    ArrayList<UploadImageNewSkeleton> arraylistupldImages = new ArrayList<>();
                                    for (int m = 0; m < jArray_upldImages.length(); m++) {
                                        JSONObject jobj_images = jArray_upldImages.getJSONObject(m);
                                        UploadImageNewSkeleton uploadimg_ske = new UploadImageNewSkeleton();
                                        uploadimg_ske.setUrl(jobj_images.getString("img"));
                                        uploadimg_ske.setDescription(jobj_images.getString("desc"));
                                        arraylistupldImages.add(uploadimg_ske);
                                    }

                                    JSONArray jArray_LC = jobjInside.getJSONArray("laborCodes");
                                    ArrayList<TimeSpentSkeleton> arraylistLC = new ArrayList<>();
                                    for (int m = 0; m < jArray_LC.length(); m++) {
                                        JSONObject jobjlc = jArray_LC.getJSONObject(m);
                                        TimeSpentSkeleton ske = new TimeSpentSkeleton();
                                        ske.setLcname(jobjlc.getString("lb_code_name"));
                                        ske.setStarttime(jobjlc.getString("lb_start_time"));
                                        ske.setEndtime(jobjlc.getString("lb_end_time"));
                                        arraylistLC.add(ske);
                                    }

                                    // skeleton.setArrayList(arraylistDashNotes);
                                    jobsArrayList.add(skeleton);
                                    String jobsSke_databse = gson.toJson(skeleton);
                                    String dashnotes = gson.toJson(arraylistDashNotes);
                                    String laborPerformed = gson.toJson(arraylistLabrperformed);
                                    String OffTheRecord = gson.toJson(arraylistOffTheRecord);
                                    String UploadedImages = gson.toJson(arraylistupldImages);
                                    String PartsRecords = gson.toJson(arraylistParts);
                                    String LCRecords = gson.toJson(arraylistLC);

                                    ContentValues cv = new ContentValues();
                                    cv.put(ParseOpenHelper.TECHID, HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID));
                                    cv.put(ParseOpenHelper.JOBID, jobjInside.getString("job_id"));
                                    cv.put(ParseOpenHelper.JOBSSKELETON, jobsSke_databse);
                                    cv.put(ParseOpenHelper.JOBSTECHDASHBOARDNOTES, dashnotes);
                                    cv.put(ParseOpenHelper.JOBSTECHLABORPERFORM, laborPerformed);
                                    cv.put(ParseOpenHelper.JOBSTECHOFFTHERECORD, OffTheRecord);
                                    cv.put(ParseOpenHelper.JOBSTECHUPLOADEDIMAGES, UploadedImages);
                                    cv.put(ParseOpenHelper.JOBSTECHPARTSRECORD, PartsRecords);
                                    cv.put(ParseOpenHelper.JOBSTECHLCRECORD, LCRecords);
                                    long idd = database.insert(ParseOpenHelper.TABLENAME_ALLJOBS, null, cv);
                                    Log.e("table", String.valueOf(idd));


                                    JSONArray jArray_urgent = jobjInside.getJSONArray("job_messages");
                                    for (int k = 0; k < jArray_urgent.length(); k++) {
                                        JSONObject jobj_urgent = jArray_urgent.getJSONObject(k);
                                        ContentValues cvurgent = new ContentValues();
                                        cvurgent.put(ParseOpenHelper.URGENT_TECHID, HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID));
                                        cvurgent.put(ParseOpenHelper.URGENT_JOBID, jobj_urgent.getString("job_id"));
                                        cvurgent.put(ParseOpenHelper.URGENT_CUSTNAME, jobjInside.getString("customer_name"));
                                        cvurgent.put(ParseOpenHelper.URGENT_CUSTTYPE, jobjInside.getString("customer_type_name"));
                                        cvurgent.put(ParseOpenHelper.URGENT_BOATMKYEAR, jobjInside.getString("boat_make_year"));
                                        cvurgent.put(ParseOpenHelper.URGENT_BOATNAME, jobjInside.getString("boat_name"));
                                        cvurgent.put(ParseOpenHelper.URGENT_SENDER, jobj_urgent.getString("Sender"));
                                        cvurgent.put(ParseOpenHelper.URGENT_RECEIVER, jobj_urgent.getString("Receiver"));
                                        cvurgent.put(ParseOpenHelper.URGENT_MESSAGE, jobj_urgent.getString("message"));
                                        cvurgent.put(ParseOpenHelper.URGENT_CREATEDAT, jobj_urgent.getString("created_at"));
                                        cvurgent.put(ParseOpenHelper.URGENT_ACKNOWLEDGE, jobj_urgent.getString("acknowledge"));
                                        cvurgent.put(ParseOpenHelper.URGENT_MESSAGEID, jobj_urgent.getString("id"));
                                        cvurgent.put(ParseOpenHelper.URGENT_RECEIVERID, jobj_urgent.getString("receiver_id"));

                                        database.delete(ParseOpenHelper.TABLE_URGENTMSG, ParseOpenHelper.URGENT_JOBID + " =? AND " + ParseOpenHelper.URGENT_MESSAGEID + " = ?",
                                                new String[]{jobj_urgent.getString("job_id"),  jobj_urgent.getString("id")});

                                        long iddurgent = database.insert(ParseOpenHelper.TABLE_URGENTMSG, null, cvurgent);
                                    }

                                }
                                HandyObject.stopProgressDialog();
                                new databsefetch().execute();
                            } else {
                                HandyObject.stopProgressDialog();
                                jobsArrayList.clear();
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
    }
}
