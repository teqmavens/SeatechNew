package teq.development.seatech.JobDetail;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import teq.development.seatech.JobDetail.Adapter.AdapterDashboardNotes;
import teq.development.seatech.JobDetail.Adapter.AdapterDashbrdUrgentMsgDetail;
import teq.development.seatech.JobDetail.Adapter.AdapterJobTime;
import teq.development.seatech.JobDetail.Adapter.AdapterUploadedImages;
import teq.development.seatech.JobDetail.Skeleton.JobTimeSkeleton;
import teq.development.seatech.JobDetail.Skeleton.LCChangeSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.Utils.PdfActivity;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.FrgmJobdetailBinding;

public class JobDetailFragment extends Fragment {

    private DashBoardActivity activity;
    private Context context;
    private AdapterDashbrdUrgentMsgDetail adapterurgentmsg;
    private AdapterDashboardNotes adapterdashnotes;
    private AdapterJobSpinner adapterjobspinner;
    private AdapterJobTime adapterjobTime;
    private AdapterUploadedImages adapterUploadedImages;
    public FrgmJobdetailBinding binding;
    public static ArrayList<DashboardNotes_Skeleton> addTechlistOffTheRecord;
    ArrayList<AllJobsSkeleton> arralistAllJobs;
    ArrayList<JobTimeSkeleton> arralistJobTime;
    SQLiteDatabase database;
    boolean jobspinner, updatereciver;
    int timerindex_prev = 0;
    Gson gson;
    String pdfUrl = "";
    boolean fromjobselection;
    ArrayList<DashboardNotes_Skeleton> arrayListLaborPerf, arrayListOffTheRecord;
    ArrayList<String> arrayListUpdateImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        //activity = (DashBoardActivity) getActivity();
        Log.e("onCreate", "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_jobdetail, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmjobdetail(this);
        initViews();
        Log.e("onCreateVIEW", "onCreateVIEW");
        return rootView;
    }

    private void initViews() {
        arrayListUpdateImage = new ArrayList<>();
        new databsefetch().execute();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(reciever, new IntentFilter("load_message"));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(JobUpdatereciever,
                new IntentFilter("updateJob"));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(LastOffTechreciever,
                new IntentFilter("pass_addtechlast"));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(UpdateImageReciever,
                new IntentFilter("updateImage"));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(NeddPartReciever,
                new IntentFilter("needpartreciever"));
    }


    private BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // loadChatMessages();
            binding.uploadimage.setText(intent.getStringExtra("secss"));
        }
    };

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

    private BroadcastReceiver JobUpdatereciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("update", "jobbbb");
            if (Integer.parseInt(intent.getStringExtra("nextjobid")) >= arralistAllJobs.size() - 1) {
                updatereciver = true;
                HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
                Intent intent_reg = new Intent(getActivity(), DashBoardActivity.class);
                startActivity(intent_reg);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            } else {
                adapterjobspinner = new AdapterJobSpinner(context, arralistAllJobs);
                binding.jobspinner.setAdapter(adapterjobspinner);
                binding.jobspinner.setSelection(Integer.parseInt(intent.getStringExtra("nextjobid")));
                binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());
            }

        }
    };

    private BroadcastReceiver LastOffTechreciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            binding.lastoffRecrdnotes.setText(intent.getStringExtra("lastofftherecord"));
        }
    };

    //
    private BroadcastReceiver UpdateImageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (arrayListUpdateImage.size() == 0) {
                binding.nouploadedimage.setVisibility(View.GONE);
                binding.rcylrviewUpldedImages.setVisibility(View.VISIBLE);
                LinearLayoutManager lLManagerImages = new LinearLayoutManager(getActivity());
                lLManagerImages.setOrientation(LinearLayoutManager.HORIZONTAL);
                binding.rcylrviewUpldedImages.setLayoutManager(lLManagerImages);
                arrayListUpdateImage.addAll(intent.getStringArrayListExtra("updateImageArray"));
                adapterUploadedImages = new AdapterUploadedImages(context, arrayListUpdateImage, getFragmentManager());
                binding.rcylrviewUpldedImages.setAdapter(adapterUploadedImages);
            } else {
                arrayListUpdateImage.clear();
                arrayListUpdateImage.addAll(intent.getStringArrayListExtra("updateImageArray"));
                adapterUploadedImages.notifyDataSetChanged();
            }
        }
    };
    private BroadcastReceiver NeddPartReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showDialogNeedPart();
        }
    };


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

    public void getplusminus(int n) {
        List<String> listtime = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        if (isJobRunning() == true) {
            Log.e("plusminusrunning", "running");
            calendar.set(Calendar.MINUTE, Integer.parseInt(HandyObject.getPrams(context, AppConstants.JOBRUNNING_CENTERTIME).split(":")[1]));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(HandyObject.getPrams(context, AppConstants.JOBRUNNING_CENTERTIME).split(":")[0]));
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
            binding.timespinner.setSelection(HandyObject.getIntPrams(context, AppConstants.JOBSTARTTIME_INDEX));
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

    public class JobItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
            Log.e("job", "job");
            if (arralistAllJobs.get(position).getJobticketNo().equalsIgnoreCase("111111")) {
                // HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
                // App.appInstance.stopTimer();
                //   NewJobDetailFrgament newjobdetail = new NewJobDetailFrgament();

                Intent intent = new Intent("fromJobDetail");
                intent.putExtra("position", String.valueOf(position));
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                //  activity.removeFragment(JobDetailFragment.this);
            } else if (HandyObject.getPrams(context, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
                Log.e("AlreadyrunningJOB", "AlreadyrunningJOB");
                runHandlerforLC();
                String kj = HandyObject.getPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME);
                //  HandyObject.showAlert(getActivity(), kj);
                setAllSpinnerData(position);
            } else {
                Log.e("NOTrunningJOB", "NOTrunningJOB");
                runHandlerforLC();
                String kj = HandyObject.getPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME);
                int newttlwrkHr = Integer.parseInt(kj) + Integer.parseInt(String.valueOf(binding.uploadimage.getText().toString().split(":")[1]).replaceFirst("^0+(?!$)", ""));

                if (HandyObject.getPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME).equalsIgnoreCase("0")) {
                    if (newttlwrkHr >= 10) {
                        if (JobStatusDialog.crossclick == true) {
                            JobStatusDialog.crossclick = false;
                        } else {
                            if (checkedit() == true) {
                                HandyObject.showAlert(getActivity(), getString(R.string.jobdetailempty));
                            } else {
                                showDialog(HandyObject.getIntPrams(getActivity(), AppConstants.JObSPINNER_LASTPOSI), setDataForLCSumbmit(), setDataForEdit());
                            }
                        }
                    } else {
                        if (JobStatusDialog.crossclick == true) {
                            JobStatusDialog.crossclick = false;
                        } else {
                            App.appInstance.stopTimer();
                            App.appInstance.startTimer();
                            setAllSpinnerData(position);
                        }
                    }
                } else if (Integer.parseInt(HandyObject.getPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME)) == 66) {
                    App.appInstance.stopTimer();
                    App.appInstance.startTimer();
                    setAllSpinnerData(position);
                    HandyObject.putPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME, "0");
                } else if (newttlwrkHr >= 10) {
                    if (JobStatusDialog.crossclick == true) {
                        JobStatusDialog.crossclick = false;
                    } else {
                        if (checkedit() == true) {
                            HandyObject.showAlert(getActivity(), getString(R.string.jobdetailempty));
                        } else {
                            showDialog(HandyObject.getIntPrams(getActivity(), AppConstants.JObSPINNER_LASTPOSI), setDataForLCSumbmit(), setDataForEdit());
                        }
                    }
                } else {
                    App.appInstance.stopTimer();
                    App.appInstance.startTimer();
                    setAllSpinnerData(position);
                    HandyObject.putPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME, "0");
                }
            }
            HandyObject.putIntPrams(getActivity(), AppConstants.JObSPINNER_LASTPOSI, position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private void setAllSpinnerData(int position) {
        try {
            pdfUrl = arralistAllJobs.get(position).getSalesorder();
            binding.boatmakeyearValue.setText(arralistAllJobs.get(position).getBoatmakeYear());
            binding.boatmodelValue.setText(arralistAllJobs.get(position).getBoatmodelLength());
            if (arralistAllJobs.get(position).getBoatName().equalsIgnoreCase("-")) {
                binding.boatnameValue.setBackgroundColor(Color.parseColor("#A9A9A9"));
                binding.boatnameValue.setEnabled(true);
                binding.boatnameValue.setText("");
            } else {
                binding.boatnameValue.setBackgroundColor(Color.TRANSPARENT);
                binding.boatnameValue.setEnabled(false);
                binding.boatnameValue.setText(arralistAllJobs.get(position).getBoatName());
            }
            if (arralistAllJobs.get(position).getHullid().equalsIgnoreCase("-")) {
                binding.hullidValue.setBackgroundColor(Color.parseColor("#A9A9A9"));
                binding.hullidValue.setText("");
                binding.hullidValue.setEnabled(true);
            } else {
                binding.hullidValue.setBackgroundColor(Color.TRANSPARENT);
                binding.hullidValue.setEnabled(false);
                binding.hullidValue.setText(arralistAllJobs.get(position).getHullid());
            }
            if (arralistAllJobs.get(position).getCaptainname().equalsIgnoreCase("-")) {
                binding.captnameValue.setBackgroundColor(Color.parseColor("#A9A9A9"));
                binding.captnameValue.setText("");
                binding.captnameValue.setEnabled(true);
            } else {
                binding.captnameValue.setBackgroundColor(Color.TRANSPARENT);
                binding.captnameValue.setEnabled(false);
                binding.captnameValue.setText(arralistAllJobs.get(position).getCaptainname());
            }

            // binding.hullidValue.setText(arralistAllJobs.get(position).getHullid());
            //  binding.captnameValue.setText(arralistAllJobs.get(position).getCaptainname());
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


            arrayListLaborPerf = arralistAllJobs.get(position).getArrayListLaborPerf();
            arrayListOffTheRecord = arralistAllJobs.get(position).getArrayListOffTheRecord();

            ArrayAdapter<CharSequence> lcAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.laborcode_array,
                    android.R.layout.simple_spinner_item);
            lcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerLc.setAdapter(lcAdapter);
            if (isJobRunning() == true) {
                binding.spinnerLc.setSelection(HandyObject.getIntPrams(context, AppConstants.JOBLABORCODE_INDEX));
            } else {
                fromjobselection = true;
                binding.spinnerLc.setSelection(0);
            }
            binding.spinnerLc.setOnItemSelectedListener(new LCItemSelectedListener());
            updateTimeSpinner();

            binding.etValuesupplies.setText("$ " + arralistAllJobs.get(position).getSupplyamount());

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
                //binding.rcyviewDashbrdnotes.setNestedScrollingEnabled(false);
                binding.rcyviewDashbrdnotes.setAdapter(adapterdashnotes);
                binding.lastoffRecrdnotes.setText(arrayListOffTheRecord.get(arrayListOffTheRecord.size() - 1).getNotes());
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
                arrayListUpdateImage = arralistAllJobs.get(position).getArrayListImages();
                adapterUploadedImages = new AdapterUploadedImages(context, arrayListUpdateImage, getFragmentManager());
                //binding.rc.setNestedScrollingEnabled(false);
                binding.rcylrviewUpldedImages.setAdapter(adapterUploadedImages);
            }

            binding.rcyviewDashbrdnotes.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
        } catch (Exception e) {
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

    public class LCItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (HandyObject.getPrams(context, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
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

                        /*if (binding.timespinner.getSelectedItemPosition() == 0) {
                            calendar.add(Calendar.MINUTE, 15);
                            calendar.getTime();
                        } else if (binding.timespinner.getSelectedItemPosition() == 2) {
                            calendar.add(Calendar.MINUTE, -15);
                            calendar.getTime();
                        }*/

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
                            binding.timespinner.setSelection(HandyObject.getIntPrams(context, AppConstants.JOBSTARTTIME_INDEX));
                        } else {
                            binding.timespinner.setSelection(1);
                        }

                        String endtime = arralistJobTime.get(1).getParsedate() + " " + arralistJobTime.get(1).getHrminutes();
                        String hrsworked = String.valueOf(binding.uploadimage.getText()).split(":")[0] + ":" + String.valueOf(binding.uploadimage.getText().toString()).split(":")[1];


                        TaskLCChange(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID), arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(),
                                binding.spinnerLc.getSelectedItem().toString(), HandyObject.getIntPrams(getActivity(), AppConstants.LCSPINNER_LASTPOSI),
                                starttime, endtime, hrsworked, String.valueOf(HoursAdjusted(timerindex_prev)));
                    }
                    /*HandyObject.showAlert(context, String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)) + "---" + HandyObject.ParseDateJobTime(calendar.getTime()));*/
                }
            }
            HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
            HandyObject.putIntPrams(getActivity(), AppConstants.LCSPINNER_LASTPOSI, position);
            fromjobselection = false;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
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

    public int HoursAdjusted(int n) {
        int res = 0;
        if (n == 0) {
            res = -15;
        } else if (n == 2) {
            res = 15;
        }
        return res;
    }

    private void TaskLCChange(String teqid, String jobid, final String seleclc, int lclast_posi, String starttime, String endtime, final String hrsworked, String hrsAdjusted) {

        LCChangeSkeleton lcs_ke = new LCChangeSkeleton();
        lcs_ke.setTech_id(teqid);
        lcs_ke.setJob_id(jobid);
        lcs_ke.setLabour_code(HandyObject.getLaborcode(lclast_posi));
        lcs_ke.setStart_time(starttime);
        lcs_ke.setEnd_time(endtime);
        lcs_ke.setHours(hrsworked);
        lcs_ke.setHours_adjusted(hrsAdjusted);
        lcs_ke.setCreated_by(teqid);
        ArrayList<LCChangeSkeleton> arrayList = new ArrayList<>();
        arrayList.add(lcs_ke);
        String techlog = gson.toJson(arrayList);
        final String insertedTime = HandyObject.ParseDateTimeForNotes(new Date());
        insertIntoDBLC(teqid, jobid, HandyObject.getLaborcode(lclast_posi), starttime, endtime, hrsworked, hrsAdjusted, insertedTime);
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
                                    String previous_runninghr = HandyObject.getPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME);
                                    int newttlwrkHr = Integer.parseInt(previous_runninghr) + Integer.parseInt(String.valueOf(hrsworked.split(":")[1]).replaceFirst("^0+(?!$)", ""));
                                    HandyObject.putPrams(getActivity(), AppConstants.JOBRUNNING_TOTALTIME, String.valueOf(newttlwrkHr));

                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobj = jsonArray.getJSONObject(i);

                                        //Delete related row from database
                                        database.delete(ParseOpenHelper.TABLE_LCCHANGE, ParseOpenHelper.LCCHANGEJOBID + " =? AND " + ParseOpenHelper.LCCHANGECREATEDAT + " = ?",
                                                new String[]{jobj.getString("job_id"), insertedTime});
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
            HandyObject.showAlert(getActivity(), getString(R.string.fetchdata_whenonline));
            HandyObject.stopProgressDialog();
        }

    }

    public void onClickJobChange() {
        if (binding.spinnerLc.getSelectedItemPosition() == 0) {
            binding.spinnerLc.setSelection(1);
        } else {
            if (checkedit() == true) {
                HandyObject.showAlert(getActivity(), getString(R.string.jobdetailempty));
            } else {
                showDialog(binding.jobspinner.getSelectedItemPosition(), setDataForLCSumbmit(), setDataForEdit());
            }
        }
    }

    public void onClickNeedPart() {
        showDialogNeedPart();
    }

    public void onClickNeedEstimate(View v) {
        dialogNeedEstimate(v);
    }

    public void onClickNeedChangeOrder(View v) {
        dialogNeedChangeOrder();
    }

    public void OnClickViewComment() {
        dialogViewComment("viewcommment", arrayListLaborPerf);
    }

    public void OnClickOffTheRecord() {
        if (addTechlistOffTheRecord.size() == 0) {
            // dialogViewComment("offrecord", arrayListOffTheRecord);
        } else {
            //  arrayListOffTheRecord.add(addTechlistOffTheRecord.get(0));
            arrayListOffTheRecord.clear();
            arrayListOffTheRecord.addAll(addTechlistOffTheRecord);
            addTechlistOffTheRecord.clear();
            // dialogViewComment("offrecord", arrayListOffTheRecord);
        }
        dialogViewComment("offrecord", arrayListOffTheRecord);
    }

    public void OnClickAddTech() {
        dialogAddTechNotes();
    }

    public void OnClickUpload() {
        dialogUploadImage();
       /* Intent intent = new Intent(getActivity(), PdfActivity.class);
        startActivity(intent);*/
    }

    public void OnClickMyLaborPerf() {
        // binding.etLaborperform.setText("");
        arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo();
        if (binding.etLaborperform.getText().toString().length() == 0) {
            binding.etLaborperform.requestFocus();
            HandyObject.showAlert(context, getString(R.string.fieldempty));
        } else {
            SubmitMyLaborPerf_Task(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID),
                    arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(), binding.etLaborperform.getText().toString(), "Tech Labour Performed");
        }
    }

    void showDialog(int posi, String allValues, String editvalues) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = JobStatusDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(), posi, allValues, editvalues);
        newFragment.setCancelable(false);
        newFragment.show(ft, "dialog");
        App.appInstance.pauseTimer();
    }

    void showDialogNeedPart() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialogneedpart");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = NeedPartDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo());
        newFragment.show(ft, "dialogneedpart");
    }

    private void dialogNeedEstimate(View anchorview) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialogneedestimate");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = NeedEstimateDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo());
        newFragment.show(ft, "dialogneedestimate");
    }

    private void dialogNeedChangeOrder() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialogneedchgorder");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = NeedChangeOrderDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo());
        newFragment.show(ft, "dialogneedchgorder");
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

    private void dialogAddTechNotes() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("addtech");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = AddTechNotesDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo());
        newFragment.show(ft, "addtech");
    }

    private void dialogUploadImage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("addtech");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = UploadImageDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo());
        newFragment.show(ft, "addtech");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroyJob", "onDestroyJob");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(reciever);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(JobUpdatereciever);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(LastOffTechreciever);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(UpdateImageReciever);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(NeddPartReciever);
            if (updatereciver == true) {
                updatereciver = false;
                HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
            } else {
                HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "yes");
            }
            HandyObject.putPrams(context, AppConstants.ISJOB_NEWTYPE, "no");
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_ETLABORPERFORM, binding.etLaborperform.getText().toString());
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_CENTERTIME, arralistJobTime.get(1).getHrminutes());
            HandyObject.putIntPrams(context, AppConstants.JOBRUNNING_INDEX, binding.jobspinner.getSelectedItemPosition());
            HandyObject.putIntPrams(context, AppConstants.JOBLABORCODE_INDEX, binding.spinnerLc.getSelectedItemPosition());
            HandyObject.putIntPrams(context, AppConstants.JOBSTARTTIME_INDEX, binding.timespinner.getSelectedItemPosition());
            Log.e("onDestroyViewJob", "onDestroyViewJob");
        } catch (Exception e) {
        }

    }

    boolean isJobRunning() {
        if (HandyObject.getPrams(context, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
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
                                        arrayListLaborPerf.clear();
                                        JSONArray jArray_OffTheRecord = jobjInside.getJSONArray("TechLabourPerformed");
                                        ArrayList<DashboardNotes_Skeleton> arraylistOffTheRecord = new ArrayList<>();
                                        for (int k = 0; k < jArray_OffTheRecord.length(); k++) {
                                            JSONObject jobj_dashnotes = jArray_OffTheRecord.getJSONObject(k);
                                            DashboardNotes_Skeleton dashnotes_ske = new DashboardNotes_Skeleton();
                                            dashnotes_ske.setCreatedAt(jobj_dashnotes.getString("created_at"));
                                            dashnotes_ske.setNoteWriter(jobj_dashnotes.getString("written_by"));
                                            dashnotes_ske.setNotes(jobj_dashnotes.getString("notes"));
                                            arraylistOffTheRecord.add(dashnotes_ske);
                                            arrayListLaborPerf.add(dashnotes_ske);
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

    private boolean checkedit() {
        boolean abc = false;

        if (binding.boatnameValue.getText().toString().length() == 0) {
            abc = true;
        } else if (binding.hullidValue.getText().toString().length() == 0) {
            abc = true;
        } else if (binding.captnameValue.getText().toString().length() == 0) {
            abc = true;
        } else {
            abc = false;
        }
        return abc;
    }

    private String setDataForEdit() {
        String value = binding.boatnameValue.getText().toString() + "-" + binding.hullidValue.getText().toString() + "-" + binding.captnameValue.getText().toString();
        return value;
    }

    private String setDataForLCSumbmit() {
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
        String finalValue = starttime + "--" + endtime + "--" + hrsworked + "--" + hrsAdjusted + "--" + binding.spinnerLc.getSelectedItem().toString();
        return finalValue;
    }

    private class databsefetch extends AsyncTask<ArrayList<AllJobsSkeleton>, Void, ArrayList<AllJobsSkeleton>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  HandyObject.showProgressDialog(getActivity());
        }

        @Override
        protected ArrayList<AllJobsSkeleton> doInBackground(ArrayList<AllJobsSkeleton>... arrayLists) {
            database = ParseOpenHelper.getInstance(context).getWritableDatabase();
            gson = new Gson();
            Cursor cursor = database.query(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, null, ParseOpenHelper.TECHIDCURRDAY + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)}, null, null, null);
            cursor.moveToFirst();
            arralistAllJobs = new ArrayList<>();
            arrayListLaborPerf = new ArrayList<>();
            arrayListOffTheRecord = new ArrayList<>();
            addTechlistOffTheRecord = new ArrayList<>();
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
                ske.setArrayListImages(arrayListUploadImages);
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
            if (isJobRunning() == true) {
                binding.etLaborperform.setText(HandyObject.getPrams(context, AppConstants.JOBRUNNING_ETLABORPERFORM));
                binding.jobspinner.setSelection(HandyObject.getIntPrams(context, AppConstants.JOBRUNNING_INDEX));
            } else if (getArguments() != null) {
                binding.jobspinner.setSelection(Integer.parseInt(getArguments().getString("posinew")));
            }
            binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());
           // HandyObject.stopProgressDialog();
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

}
