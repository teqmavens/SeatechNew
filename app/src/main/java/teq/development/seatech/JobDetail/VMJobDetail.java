package teq.development.seatech.JobDetail;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.TextView;

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
import java.util.Observable;
import java.util.Observer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import teq.development.seatech.App;
import teq.development.seatech.Chat.ChatActivity;
import teq.development.seatech.Chat.ComposeDialog;
import teq.development.seatech.Chat.ComposeDialogNew;
import teq.development.seatech.Dashboard.Adapter.AdapterDashbrdUrgentMsg;
import teq.development.seatech.Dashboard.Adapter.AdapterJobSpinner;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.Dashboard.DashBoardFragment;
import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.Dashboard.Skeleton.PartsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;
import teq.development.seatech.Dashboard.Skeleton.TimeSpentSkeleton;
import teq.development.seatech.Dashboard.Skeleton.UploadImageNewSkeleton;
import teq.development.seatech.Dashboard.Skeleton.UrgentMsgSkeleton;
import teq.development.seatech.JobDetail.Adapter.AdapterDashboardNotes;
import teq.development.seatech.JobDetail.Adapter.AdapterJobTime;
import teq.development.seatech.JobDetail.Adapter.AdapterParts;
import teq.development.seatech.JobDetail.Adapter.AdapterTimeSpent;
import teq.development.seatech.JobDetail.Adapter.AdapterUploadedImages;
import teq.development.seatech.JobDetail.Skeleton.JobTimeSkeleton;
import teq.development.seatech.JobDetail.Skeleton.LCChangeSkeleton;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;
import teq.development.seatech.databinding.FrgmJobdetailBinding;


public class VMJobDetail extends Observable implements SwipeRefreshLayout.OnRefreshListener {

    Context context;
    FrgmJobdetailBinding binding;
    private AdapterDashboardNotes adapterdashnotes;
    private AdapterParts adapterParts;
    private AdapterJobSpinner adapterjobspinner;
    private AdapterJobTime adapterjobTime;
    private AdapterUploadedImages adapterUploadedImages;
    private AdapterTimeSpent adapterTImeSpent;
    public static ArrayList<DashboardNotes_Skeleton> addTechlistOffTheRecord;
    ArrayList<AllJobsSkeleton> arralistAllJobs;
    ArrayList<JobTimeSkeleton> arralistJobTime;
    ArrayList<UrgentMsgSkeleton> arraylistUrgent;
    ArrayList<String> arrayListsJobIds;
    SQLiteDatabase database;
    boolean jobspinner, updatereciver;
    int timerindex_prev = 0;
    Gson gson;
    String pdfUrl = "";
    boolean fromjobselection;
    ArrayList<DashboardNotes_Skeleton> arrayListLaborPerf, arrayListOffTheRecord;
    ArrayList<UploadImageNewSkeleton> arrayListUpdateImage;
    FragmentManager fragmentManager;
    String selectedJobId = "";
    boolean blankfield;
    Fragment fragment;
    String[] valuesLC;
    DashBoardActivity activity;

    public VMJobDetail(Context context, FrgmJobdetailBinding binding, FragmentManager fragmentManager, Fragment fragment, DashBoardActivity activity) {
        this.context = context;
        this.binding = binding;
        this.fragmentManager = fragmentManager;
        this.fragment = fragment;
        this.activity = activity;
        binding.setFrgmjobdetailvm(this);
        initViews();
    }


    private void initViews() {
        arrayListUpdateImage = new ArrayList<>();
        valuesLC = HandyObject.getPrams(context, AppConstants.JOBRUNNING_LC).split(",");
        binding.swipeview.setOnRefreshListener(this);
        new databsefetch().execute();
        LocalBroadcastManager.getInstance(context).registerReceiver(reciever, new IntentFilter("load_message"));

        LocalBroadcastManager.getInstance(context).registerReceiver(updateUrgentReceiver, new IntentFilter("update_UrgentReceiver"));

        LocalBroadcastManager.getInstance(context).registerReceiver(JobUpdatereciever,
                new IntentFilter("updateJob"));

        LocalBroadcastManager.getInstance(context).registerReceiver(LastOffTechreciever,
                new IntentFilter("pass_addtechlast"));

        LocalBroadcastManager.getInstance(context).registerReceiver(UpdateDashboard,
                new IntentFilter("updateDashboardNotes"));

        LocalBroadcastManager.getInstance(context).registerReceiver(UpdateImageReciever,
                new IntentFilter("updateImage"));

        LocalBroadcastManager.getInstance(context).registerReceiver(NeddPartReciever,
                new IntentFilter("needpartreciever"));
    }


    private BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            binding.uploadimage.setText(intent.getStringExtra("secss"));
        }
    };

    private BroadcastReceiver updateUrgentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            new Urgentdatafetch().execute();
        }
    };

    public void OnClickViewPdf() {
        if (pdfUrl.equalsIgnoreCase("-")) {
            HandyObject.showAlert(context, context.getString(R.string.nosalesorder));
        } else if (pdfUrl != null && !pdfUrl.isEmpty()) {
            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
            startActivity(browserIntent);*/
            Intent intent = new Intent();
            intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
            Intent chooserIntent = Intent.createChooser(intent, "Open Report");
            ((Activity) context).startActivity(chooserIntent);
           /* String url= "https://docs.google.com/gview?embedded=true&url="+pdfUrl;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);*/
        } else {
            HandyObject.showAlert(context, context.getString(R.string.nosalesorder));
        }
    }

    private BroadcastReceiver JobUpdatereciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context conte, Intent intent) {
            Log.e("update", "jobbbb");

//            if (Integer.parseInt(intent.getStringExtra("nextjobid")) >= arralistAllJobs.size() - 1) {
//                updatereciver = true;
//                HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
//                App.appInstance.stopTimer();
//                Intent intent_reg = new Intent(context, DashBoardActivity.class);
//                ((Activity) context).startActivity(intent_reg);
//                ((Activity) context).finish();
//                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
//            } else {
//                adapterjobspinner = new AdapterJobSpinner(context, arralistAllJobs);
//                binding.jobspinner.setAdapter(adapterjobspinner);
//                binding.jobspinner.setSelection(Integer.parseInt(intent.getStringExtra("nextjobid")));
//                binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());
//            }
            //   HandyObject.showAlert(context,intent.getStringExtra("nextjobid"));
            if (Integer.parseInt(HandyObject.getPrams(context, AppConstants.JOBRUNNING_TOTALTIME)) == 66) {
                updatereciver = true;
                HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
                HandyObject.putPrams(context, AppConstants.JOBRUNNING_TOTALTIME, "0");
                App.appInstance.stopTimer();
                activity.replaceFragmentWithoutBack(new DashBoardFragment());
//                Intent intent_reg = new Intent(context, DashBoardActivity.class);
//                ((Activity) context).startActivity(intent_reg);
//                ((Activity) context).finish();
//                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            } else {
                adapterjobspinner = new AdapterJobSpinner(context, arralistAllJobs);
                binding.jobspinner.setAdapter(adapterjobspinner);
                //  binding.jobspinner.setSelection(Integer.parseInt(intent.getStringExtra("nextjobid")));
                binding.jobspinner.setSelection(arrayListsJobIds.indexOf(intent.getStringExtra("nextjobid")));
                binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());
            }
//            if (arrayListsJobIds.indexOf(intent.getStringExtra("nextjobid")) >= arralistAllJobs.size() - 1) {
//
//            } else {
//                adapterjobspinner = new AdapterJobSpinner(context, arralistAllJobs);
//                binding.jobspinner.setAdapter(adapterjobspinner);
//              //  binding.jobspinner.setSelection(Integer.parseInt(intent.getStringExtra("nextjobid")));
//                binding.jobspinner.setSelection(arrayListsJobIds.indexOf(intent.getStringExtra("nextjobid")));
//                binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());
//            }


        }
    };

    private BroadcastReceiver LastOffTechreciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            binding.lastoffRecrdnotes.setText(intent.getStringExtra("lastofftherecord") + " (" + intent.getStringExtra("lastofftherecord_writtenby") + ")");
        }
    };

    private BroadcastReceiver UpdateDashboard = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getArrayList().size() == 0) {
                binding.nodashbrdnotes.setVisibility(View.GONE);
                binding.rcyviewDashbrdnotes.setVisibility(View.VISIBLE);
                LinearLayoutManager lLManagerDashNotes = new LinearLayoutManager(context);
                lLManagerDashNotes.setOrientation(LinearLayoutManager.VERTICAL);
                binding.rcyviewDashbrdnotes.setLayoutManager(lLManagerDashNotes);
                arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).setArrayList(intent.<DashboardNotes_Skeleton>getParcelableArrayListExtra("updatedDashboardLIST"));
                adapterdashnotes = new AdapterDashboardNotes(context, arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getArrayList());
                binding.rcyviewDashbrdnotes.setAdapter(adapterdashnotes);
            } else {
                arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).setArrayList(intent.<DashboardNotes_Skeleton>getParcelableArrayListExtra("updatedDashboardLIST"));
                adapterdashnotes = new AdapterDashboardNotes(context, arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getArrayList());
                binding.rcyviewDashbrdnotes.setAdapter(adapterdashnotes);
            }

            /*arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).setArrayList(intent.<DashboardNotes_Skeleton>getParcelableArrayListExtra("updatedDashboardLIST"));
            adapterdashnotes = new AdapterDashboardNotes(context, arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getArrayList());
            binding.rcyviewDashbrdnotes.setAdapter(adapterdashnotes);*/
            //binding.lastoffRecrdnotes.setText(intent.getStringExtra("lastofftherecord") + " (" + intent.getStringExtra("lastofftherecord_writtenby") + ")");
        }
    };

    private BroadcastReceiver UpdateImageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context conte, Intent intent) {
            int posi = arrayListsJobIds.indexOf(intent.getStringExtra("UpdatedImages_Jobid"));
            if (arrayListUpdateImage.size() == 0) {
                binding.nouploadedimage.setVisibility(View.GONE);
                binding.rcylrviewUpldedImages.setVisibility(View.VISIBLE);
                LinearLayoutManager lLManagerImages = new LinearLayoutManager(context);
                lLManagerImages.setOrientation(LinearLayoutManager.HORIZONTAL);
                binding.rcylrviewUpldedImages.setLayoutManager(lLManagerImages);
                arrayListUpdateImage.addAll(intent.<UploadImageNewSkeleton>getParcelableArrayListExtra("updateImageArray"));
                adapterUploadedImages = new AdapterUploadedImages(context, arrayListUpdateImage, fragmentManager);
                binding.rcylrviewUpldedImages.setAdapter(adapterUploadedImages);
            } else {
                arrayListUpdateImage.clear();
                arrayListUpdateImage.addAll(intent.<UploadImageNewSkeleton>getParcelableArrayListExtra("updateImageArray"));
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
        List<String> listtime = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        if (isJobRunning() == true) {
            Log.e("plusminusrunning", "running");
            calendar.set(Calendar.MINUTE, Integer.parseInt(HandyObject.getPrams(context, AppConstants.JOBRUNNING_CENTERTIME).split(":")[1]));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(HandyObject.getPrams(context, AppConstants.JOBRUNNING_CENTERTIME).split(":")[0]));
        } else {
            Log.e("notrunning", "notrunning");
            if (n == 0) {
                String withoutzero = String.valueOf(calendar.get(Calendar.MINUTE)).replaceFirst("^0+(?!$)", "");
                if (Integer.parseInt(withoutzero) > 7) {
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                }
            }
            calendar.set(Calendar.MINUTE, n);
        }
        calendar.set(Calendar.SECOND, 0);


//        if (HandyObject.getNear15MinuteLB(Integer.parseInt(manageMinutes(calendar.get(Calendar.MINUTE)))) == 0) {
//            String withoutzero = String.valueOf(running_mins).replaceFirst("^0+(?!$)", "");calendar.get(Calendar.MINUTE)
//            if (Integer.parseInt(withoutzero) > 7 && Integer.parseInt(runinngtime.split(":")[0]) == 0) {
//                calendar.add(Calendar.HOUR_OF_DAY, 1);
//            }
//        }


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

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //HandyObject.showAlert(context, "refreshing");
                if (HandyObject.checkInternetConnection(context)) {
                    getRelatedTask(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(),binding.jobspinner.getSelectedItemPosition());
                } else {
                    binding.swipeview.setRefreshing(false);
                    HandyObject.showAlert(context, context.getString(R.string.check_internet_connection));
                }

            }
        }, 1000);
    }

    public class JobItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
            String kjj = HandyObject.getPrams(context, AppConstants.JOBRUNNING_TOTALTIME);
            int newttlwrkHrNew = Integer.parseInt(kjj) + Integer.parseInt(String.valueOf(binding.uploadimage.getText().toString().split(":")[1]).replaceFirst("^0+(?!$)", ""));
            if (arralistAllJobs.get(position).getJobticketNo().equalsIgnoreCase("111111")) {
                runHandlerforLC();
                String kj = HandyObject.getPrams(context, AppConstants.JOBRUNNING_TOTALTIME);
                int newttlwrkHr = Integer.parseInt(kj) + Integer.parseInt(String.valueOf(binding.uploadimage.getText().toString().split(":")[1]).replaceFirst("^0+(?!$)", ""));
                if (newttlwrkHr >= 10) {
                    if (checkedit() == true) {
                        if (blankfield == false) {
                            adapterjobspinner = new AdapterJobSpinner(context, arralistAllJobs);
                            binding.jobspinner.setAdapter(adapterjobspinner);
                            binding.jobspinner.setSelection(HandyObject.getIntPrams(context, AppConstants.JObSPINNER_LASTPOSI));
                            binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());
                            if (binding.boatnameValue.getText().toString().length() == 0) {
                                binding.boatnameValue.requestFocus();
                                HandyObject.showAlert(context, context.getString(R.string.boatnameempty));
                            } else {
                                binding.hullidValue.requestFocus();
                                HandyObject.showAlert(context, context.getString(R.string.hullidempty));
                            }

                            blankfield = true;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    blankfield = false;
                                }
                            }, 1000);
                        }
                    } else if (isJobRunning() == true) {
                        binding.stopbtnInternal.setVisibility(View.VISIBLE);
                        binding.llbelow.setVisibility(View.INVISIBLE);
                        binding.uploadimagenew.setVisibility(View.GONE);
                        setAllSpinnerData111111();
                    } else {
                        // HandyObject.showAlert(context,setDataForEdit());
                        // HandyObject.showAlert(context,"First");
                        showDialog(arralistAllJobs.get(HandyObject.getIntPrams(context, AppConstants.JObSPINNER_LASTPOSI)).getJobticketNo(), setDataForLCSumbmit(), setDataForEdit());
                    }
                } else {
                    if (isJobRunning() == true) {
                        binding.stopbtnInternal.setVisibility(View.VISIBLE);
                        binding.llbelow.setVisibility(View.INVISIBLE);
                        binding.uploadimagenew.setVisibility(View.GONE);
                        setAllSpinnerData111111();
                    } else {
                        binding.stopbtnInternal.setVisibility(View.VISIBLE);
                        binding.llbelow.setVisibility(View.INVISIBLE);
                        binding.uploadimagenew.setVisibility(View.GONE);
                        App.appInstance.stopTimer();
                        App.appInstance.startTimer();
                        setAllSpinnerData111111();
                    }

                }

//                binding.stopbtnInternal.setVisibility(View.VISIBLE);
//                binding.llbelow.setVisibility(View.INVISIBLE);
//                binding.uploadimagenew.setVisibility(View.GONE);
//                setAllSpinnerData111111();

            } else if (HandyObject.getPrams(context, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
                binding.stopbtnInternal.setVisibility(View.GONE);
                binding.llbelow.setVisibility(View.VISIBLE);
                binding.uploadimagenew.setVisibility(View.VISIBLE);
                Log.e("AlreadyrunningJOB", "AlreadyrunningJOB");
                runHandlerforLC();
                setAllSpinnerData(position);
            } else if (arralistAllJobs.get(HandyObject.getIntPrams(context, AppConstants.JObSPINNER_LASTPOSI)).getJobticketNo().equalsIgnoreCase("111111")) {
                if (newttlwrkHrNew >= 10) {
                    if (binding.stopbtnInternal.getVisibility() == View.VISIBLE) {
                        onClickStop();
                    } else {
                        if (JobStatusDialog.crossclick == true) {
                            JobStatusDialog.crossclick = false;
                        }
                    }
                } else {
                    binding.stopbtnInternal.setVisibility(View.GONE);
                    binding.llbelow.setVisibility(View.VISIBLE);
                    binding.uploadimagenew.setVisibility(View.VISIBLE);
                    App.appInstance.stopTimer();
                    App.appInstance.startTimer();
                    increaseCount();
                    setAllSpinnerData(position);
                    HandyObject.putPrams(context, AppConstants.JOBRUNNING_TOTALTIME, "0");
                }
            } else {
                binding.stopbtnInternal.setVisibility(View.GONE);
                binding.llbelow.setVisibility(View.VISIBLE);
                binding.uploadimagenew.setVisibility(View.VISIBLE);
                Log.e("NOTrunningJOB", "NOTrunningJOB");
                runHandlerforLC();
                String kj = HandyObject.getPrams(context, AppConstants.JOBRUNNING_TOTALTIME);
                int newttlwrkHr = Integer.parseInt(kj) + Integer.parseInt(String.valueOf(binding.uploadimage.getText().toString().split(":")[1]).replaceFirst("^0+(?!$)", ""));
                if (HandyObject.getPrams(context, AppConstants.JOBRUNNING_TOTALTIME).equalsIgnoreCase("0")) {
                    Log.e("FIRSTTT", "FIRSTTT");
                    if (newttlwrkHr >= 10) {
                        if (JobStatusDialog.crossclick == true) {
                            JobStatusDialog.crossclick = false;
                        } else {
                            if (checkedit() == true) {
                                if (blankfield == false) {
                                    adapterjobspinner = new AdapterJobSpinner(context, arralistAllJobs);
                                    binding.jobspinner.setAdapter(adapterjobspinner);
                                    binding.jobspinner.setSelection(HandyObject.getIntPrams(context, AppConstants.JObSPINNER_LASTPOSI));
                                    binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());
                                    if (binding.boatnameValue.getText().toString().length() == 0) {
                                        binding.boatnameValue.requestFocus();
                                        HandyObject.showAlert(context, context.getString(R.string.boatnameempty));
                                    } else {
                                        binding.hullidValue.requestFocus();
                                        HandyObject.showAlert(context, context.getString(R.string.hullidempty));
                                    }
                                    blankfield = true;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            blankfield = false;
                                        }
                                    }, 1000);
                                }
                            } else {
                                //  showDialog(HandyObject.getIntPrams(context, AppConstants.JObSPINNER_LASTPOSI), setDataForLCSumbmit(), setDataForEdit());
                                // HandyObject.showAlert(context,"Second");
                                showDialog(arralistAllJobs.get(HandyObject.getIntPrams(context, AppConstants.JObSPINNER_LASTPOSI)).getJobticketNo(), setDataForLCSumbmit(), setDataForEdit());
                            }
                        }
                    } else {
                        if (JobStatusDialog.crossclick == true) {
                            JobStatusDialog.crossclick = false;
                        } else {
                            App.appInstance.stopTimer();
                            App.appInstance.startTimer();
                            increaseCount();
                            setAllSpinnerData(position);
                        }
                    }
                } else if (Integer.parseInt(HandyObject.getPrams(context, AppConstants.JOBRUNNING_TOTALTIME)) == 66) {
                    App.appInstance.stopTimer();
                    App.appInstance.startTimer();
                    increaseCount();
                    setAllSpinnerData(position);
                    HandyObject.putPrams(context, AppConstants.JOBRUNNING_TOTALTIME, "0");
                } else if (newttlwrkHr >= 10) {
                    Log.e("SECOND", "SECOND");
                    if (JobStatusDialog.crossclick == true) {
                        JobStatusDialog.crossclick = false;
                    } else {
                        if (checkedit() == true) {
                            if (blankfield == false) {
                                adapterjobspinner = new AdapterJobSpinner(context, arralistAllJobs);
                                binding.jobspinner.setAdapter(adapterjobspinner);
                                binding.jobspinner.setSelection(HandyObject.getIntPrams(context, AppConstants.JObSPINNER_LASTPOSI));
                                binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());
                                if (binding.boatnameValue.getText().toString().length() == 0) {
                                    binding.boatnameValue.requestFocus();
                                    HandyObject.showAlert(context, context.getString(R.string.boatnameempty));
                                } else {
                                    binding.hullidValue.requestFocus();
                                    HandyObject.showAlert(context, context.getString(R.string.hullidempty));
                                }
                                blankfield = true;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        blankfield = false;
                                    }
                                }, 1000);
                            }
                        } else {
                            //   HandyObject.showAlert(context,"Third");
                            showDialog(arralistAllJobs.get(HandyObject.getIntPrams(context, AppConstants.JObSPINNER_LASTPOSI)).getJobticketNo(), setDataForLCSumbmit(), setDataForEdit());
                        }
                    }
                } else {
                    App.appInstance.stopTimer();
                    App.appInstance.startTimer();
                    increaseCount();
                    setAllSpinnerData(position);
                    HandyObject.putPrams(context, AppConstants.JOBRUNNING_TOTALTIME, "0");
                }
            }
            HandyObject.putIntPrams(context, AppConstants.JObSPINNER_LASTPOSI, position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private void increaseCount() {
        int count_lcjobcomp = HandyObject.getIntPrams(context, AppConstants.LCJOBCOMPLETION_COUNT);
        count_lcjobcomp++;
        HandyObject.putIntPrams(context, AppConstants.LCJOBCOMPLETION_COUNT, count_lcjobcomp);
        //HandyObject.showAlert(context, String.valueOf(count_lcjobcomp));
    }

    private void setAllSpinnerData111111() {
        increaseCount();
//        ArrayAdapter<CharSequence> lcAdapter = ArrayAdapter.createFromResource(context, R.array.laborcode_array,
//                android.R.layout.simple_spinner_item);
        //  String[] valuesLC = HandyObject.getPrams(context,AppConstants.JOBRUNNING_LC).split(",");
        ArrayAdapter<String> lcAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, android.R.id.text1, valuesLC);
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
        //Set LC Time Spent related data
        binding.remainhrsValue.setText(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getRemainhr());
        if (arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getArrayListLC().size() == 0) {
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
            adapterTImeSpent = new AdapterTimeSpent(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getArrayListLC(),context);
            binding.rcyviewts.setAdapter(adapterTImeSpent);
        }
        selectedJobId = "111111";
        new Urgentdatafetch().execute();
    }

    private void setAllSpinnerData(int position) {
        try {
            selectedJobId = arralistAllJobs.get(position).getJobticketNo();
            pdfUrl = arralistAllJobs.get(position).getSalesorder();
            binding.boatmakeyearValue.setText(arralistAllJobs.get(position).getBoatmakeYear());
            binding.boatmodelValue.setText(arralistAllJobs.get(position).getBoatmodelLength());
            if (pdfUrl.equalsIgnoreCase("-")) {
                binding.salesorderValuetext.setVisibility(View.VISIBLE);
                binding.salesorderValue.setVisibility(View.GONE);
            } else if (pdfUrl != null && !pdfUrl.isEmpty()) {
                binding.salesorderValuetext.setVisibility(View.GONE);
                binding.salesorderValue.setVisibility(View.VISIBLE);
            }

            if (arralistAllJobs.get(position).getBoatName().equalsIgnoreCase("-")) {
                if (isJobRunning() == true) {
                    if (HandyObject.getPrams(context, AppConstants.JOBRUNNING_BOATNAME).equalsIgnoreCase("")) {
                        binding.boatnameValue.setBackgroundColor(Color.parseColor("#A9A9A9"));
                        binding.boatnameValue.setEnabled(true);
                        binding.boatnameValue.setText("");
                    } else {
                        binding.boatnameValue.setText(HandyObject.getPrams(context, AppConstants.JOBRUNNING_BOATNAME));
                        binding.boatnameValue.setBackgroundColor(Color.TRANSPARENT);
                        binding.boatnameValue.setEnabled(false);
                    }

                } else {
                    binding.boatnameValue.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    binding.boatnameValue.setEnabled(true);
                    binding.boatnameValue.setText("");
                }
            } else {
                binding.boatnameValue.setBackgroundColor(Color.TRANSPARENT);
                binding.boatnameValue.setEnabled(false);
                binding.boatnameValue.setText(arralistAllJobs.get(position).getBoatName());
            }
            if (arralistAllJobs.get(position).getHullid().equalsIgnoreCase("-")) {
                if (isJobRunning() == true) {
                    if (HandyObject.getPrams(context, AppConstants.JOBRUNNING_HULLID).equalsIgnoreCase("")) {
                        binding.hullidValue.setBackgroundColor(Color.parseColor("#A9A9A9"));
                        binding.hullidValue.setText("");
                        binding.hullidValue.setEnabled(true);
                    } else {
                        binding.hullidValue.setEnabled(false);
                        binding.hullidValue.setText(HandyObject.getPrams(context, AppConstants.JOBRUNNING_HULLID));
                        binding.hullidValue.setBackgroundColor(Color.TRANSPARENT);
                    }

                } else {
                    binding.hullidValue.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    binding.hullidValue.setText("");
                    binding.hullidValue.setEnabled(true);
                }
            } else {
                binding.hullidValue.setBackgroundColor(Color.TRANSPARENT);
                binding.hullidValue.setEnabled(false);
                binding.hullidValue.setText(arralistAllJobs.get(position).getHullid());
            }
            if (arralistAllJobs.get(position).getCaptainname().equalsIgnoreCase("-")) {
                if (isJobRunning() == true) {
                    if (HandyObject.getPrams(context, AppConstants.JOBRUNNING_CAPTNAME).equalsIgnoreCase("")) {
                        binding.captnameValue.setBackgroundColor(Color.parseColor("#A9A9A9"));
                        binding.captnameValue.setText("");
                        binding.captnameValue.setEnabled(true);
                    } else {
                        binding.captnameValue.setEnabled(false);
                        binding.captnameValue.setText(HandyObject.getPrams(context, AppConstants.JOBRUNNING_CAPTNAME));
                        binding.captnameValue.setBackgroundColor(Color.TRANSPARENT);
                    }

                } else {
                    binding.captnameValue.setBackgroundColor(Color.parseColor("#A9A9A9"));
                    binding.captnameValue.setText("");
                    binding.captnameValue.setEnabled(true);
                }

            } else {
                binding.captnameValue.setBackgroundColor(Color.TRANSPARENT);
                binding.captnameValue.setEnabled(false);
                binding.captnameValue.setText(arralistAllJobs.get(position).getCaptainname());
            }
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

//            ArrayAdapter<CharSequence> lcAdapter = ArrayAdapter.createFromResource(context, R.array.laborcode_array,
//                    android.R.layout.simple_spinner_item);
            ArrayAdapter<String> lcAdapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1, android.R.id.text1, valuesLC);
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

            //Set Dashboard Notes related data
            binding.etValuesupplies.setText("$ " + arralistAllJobs.get(position).getSupplyamount());
            if (arralistAllJobs.get(position).getArrayList().size() == 0) {
                binding.nodashbrdnotes.setVisibility(View.VISIBLE);
                binding.rcyviewDashbrdnotes.setVisibility(View.GONE);
            } else {
                binding.nodashbrdnotes.setVisibility(View.GONE);
                binding.rcyviewDashbrdnotes.setVisibility(View.VISIBLE);
                LinearLayoutManager lLManagerDashNotes = new LinearLayoutManager(context);
                lLManagerDashNotes.setOrientation(LinearLayoutManager.VERTICAL);
                binding.rcyviewDashbrdnotes.setLayoutManager(lLManagerDashNotes);
                adapterdashnotes = new AdapterDashboardNotes(context, arralistAllJobs.get(position).getArrayList());
                binding.rcyviewDashbrdnotes.setAdapter(adapterdashnotes);
            }

            //Set Last off the record notes
            if (arrayListOffTheRecord.size() == 0) {
                binding.lastoffRecrdnotes.setText(context.getString(R.string.norecordFound));
            } else {
                binding.lastoffRecrdnotes.setText(arrayListOffTheRecord.get(0).getNotes() + " (" + arrayListOffTheRecord.get(0).getNoteWriter() + ")");
            }

            //Set Uploaded image Related data
            if (arralistAllJobs.get(position).getArrayListImages().size() == 0) {
                binding.nouploadedimage.setVisibility(View.VISIBLE);
                binding.rcylrviewUpldedImages.setVisibility(View.GONE);
            } else {
                binding.nouploadedimage.setVisibility(View.GONE);
                binding.rcylrviewUpldedImages.setVisibility(View.VISIBLE);
                LinearLayoutManager lLManagerImages = new LinearLayoutManager(context);
                lLManagerImages.setOrientation(LinearLayoutManager.HORIZONTAL);
                binding.rcylrviewUpldedImages.setLayoutManager(lLManagerImages);
                arrayListUpdateImage = arralistAllJobs.get(position).getArrayListImages();
                adapterUploadedImages = new AdapterUploadedImages(context, arrayListUpdateImage, fragmentManager);
                //binding.rc.setNestedScrollingEnabled(false);
                binding.rcylrviewUpldedImages.setAdapter(adapterUploadedImages);
            }

            //Set Parts record data
            if (arralistAllJobs.get(position).getArrayListParts().size() == 0) {
                binding.noparts.setVisibility(View.VISIBLE);
                binding.lltopparts.setVisibility(View.GONE);
                binding.recyclerparts.setVisibility(View.GONE);
            } else {
                binding.noparts.setVisibility(View.GONE);
                binding.lltopparts.setVisibility(View.VISIBLE);
                binding.recyclerparts.setVisibility(View.VISIBLE);
                LinearLayoutManager lLManagerparts = new LinearLayoutManager(context);
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

            binding.recyclerparts.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            binding.rcyviewUrgentmsg.setOnTouchListener(new View.OnTouchListener() {
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

    //Will trigger on the creation of fragment & when select any labor code from labor code spinner
    public class LCItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (HandyObject.getPrams(context, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
                  if (HandyObject.getPrams(context, AppConstants.ISJOB_RUNNINGCLOSE).equalsIgnoreCase("yes")) {
                       App.appInstance.stopTimer();
                       App.appInstance.startTimer();
                       HandyObject.putPrams(context, AppConstants.ISJOB_RUNNINGCLOSE, "no");
                  }

                Log.e("AlreadyrunningLC", "AlreadyrunningLC");
            } else {
                  HandyObject.putPrams(context, AppConstants.ISJOB_RUNNINGCLOSE, "no");
                Log.e("NOTrunningLC", "NOTrunningLC");
             //   if (jobspinner == true) {
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
                            if (Integer.parseInt(withoutzero) > 7 && Integer.parseInt(runinngtime.split(":")[0]) == 0) {
                                calendar.add(Calendar.HOUR_OF_DAY, 1);
                            }
                        }
                        String starttime = arralistJobTime.get(binding.timespinner.getSelectedItemPosition()).getParsedate() + " " + arralistJobTime.get(binding.timespinner.getSelectedItemPosition()).getHrminutes();

                        Log.e("STARTTIMEE", starttime);
                        arralistJobTime.clear();

//                        if(HandyObject.getPrams(context, AppConstants.LOGINTEQ_WIGGLEROOM).equalsIgnoreCase("15")) {
//
//                        } else {
//
//                        }

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
                        String endtime="";
                        if(timerindex_prev == 2) {
                             endtime = arralistJobTime.get(2).getParsedate() + " " + arralistJobTime.get(2).getHrminutes();
                        } else {
                             endtime = arralistJobTime.get(1).getParsedate() + " " + arralistJobTime.get(1).getHrminutes();
                        }

                        Log.e("ENDDDTIMEE", endtime);
                        String hrsworked = String.valueOf(binding.uploadimage.getText()).split(":")[0] + ":" + String.valueOf(binding.uploadimage.getText().toString()).split(":")[1];
                        TaskLCChange(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID), arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(),
                                binding.spinnerLc.getSelectedItem().toString(), HandyObject.getIntPrams(context, AppConstants.LCSPINNER_LASTPOSI),
                                starttime, endtime, hrsworked, String.valueOf(HoursAdjusted(timerindex_prev)), "normal");
                    }
              //  }
            }
            HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
            HandyObject.putIntPrams(context, AppConstants.LCSPINNER_LASTPOSI, position);
            fromjobselection = false;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    //Will trigger on the creation of fragment & when select any time from time spinner
    public class TimeItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
            // HandyObject.showAlert(context, String.valueOf(position));
            timerindex_prev = position;
            String runinngtime = binding.uploadimage.getText().toString();
            int running_mins = Integer.parseInt(runinngtime.split(":")[1]);
//            if (position == 2) {
//                if (running_mins < 15) {
//                    HandyObject.showAlert(context, context.getString(R.string.cantselect_futtime));
//                    binding.timespinner.setSelection(1);
//                }
//            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    //Adjust Hours to -15min & +15min
    public int HoursAdjusted(int n) {
        int res = 0;
        if (n == 0) {
            res = -15;
        } else if (n == 2) {
            res = 15;
        }
        return res;
    }

    // Implement Labor code change API for submit all related data to server
    private void TaskLCChange(String teqid, final String jobid, final String seleclc, int lclast_posi, String starttime, String endtime, final String hrsworked, String hrsAdjusted, final String type) {
        //  HandyObject.showAlert(context, endtime);
        int count = HandyObject.getIntPrams(context, AppConstants.LCJOBCOMPLETION_COUNT);
        LCChangeSkeleton lcs_ke = new LCChangeSkeleton();
        lcs_ke.setTech_id(teqid);
        lcs_ke.setJob_id(jobid);
        //   lcs_ke.setLabour_code(HandyObject.getLaborcode(lclast_posi));
        lcs_ke.setLabour_code(valuesLC[lclast_posi]);
        lcs_ke.setStart_time(starttime);
        lcs_ke.setEnd_time(endtime);
        lcs_ke.setHours(hrsworked);
        lcs_ke.setHours_adjusted(hrsAdjusted);
        lcs_ke.setCreated_by(teqid);
        lcs_ke.setCount(String.valueOf(count));
        ArrayList<LCChangeSkeleton> arrayList = new ArrayList<>();
        arrayList.add(lcs_ke);
        String techlog = gson.toJson(arrayList);
        final String insertedTime = HandyObject.ParseDateTimeForNotes(new Date());
        insertIntoDBLC(teqid, jobid, valuesLC[lclast_posi], starttime, endtime, hrsworked, hrsAdjusted, insertedTime, String.valueOf(count));
        if (HandyObject.checkInternetConnection(context)) {
            HandyObject.showProgressDialog(context);
            HandyObject.getApiManagerMain().submitLCdata(techlog, HandyObject.getPrams(context, AppConstants.LOGIN_SESSIONID))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String jsonResponse = response.body().string();
                                Log.e("responseLC", jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                    String previous_runninghr = HandyObject.getPrams(context, AppConstants.JOBRUNNING_TOTALTIME);
                                    int newttlwrkHr = Integer.parseInt(previous_runninghr) + Integer.parseInt(String.valueOf(hrsworked.split(":")[1]).replaceFirst("^0+(?!$)", ""));
                                    HandyObject.putPrams(context, AppConstants.JOBRUNNING_TOTALTIME, String.valueOf(newttlwrkHr));

                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobj = jsonArray.getJSONObject(i);
                                        JSONArray jArray_code = jobj.getJSONArray("labor_codes");
                                        binding.remainhrsValue.setText(jobj.getString("remain_hours"));
                                        ArrayList<TimeSpentSkeleton> arrayList1 = new ArrayList<>();
                                        for(int k = 0;k<jArray_code.length();k++) {
                                            JSONObject jobjlc = jArray_code.getJSONObject(k);
                                            TimeSpentSkeleton ske = new TimeSpentSkeleton();
                                            ske.setLcname(jobjlc.getString("lb_code_name"));
                                            ske.setStarttime(jobjlc.getString("lb_start_time"));
                                            ske.setEndtime(jobjlc.getString("lb_end_time"));
                                            arrayList1.add(ske);
                                        }


                                        if(arrayList1.size()>0) {
                                            binding.noTSmsg.setVisibility(View.GONE);
                                            binding.llheaderTS.setVisibility(View.VISIBLE);
                                            binding.rcyviewts.setVisibility(View.VISIBLE);
                                            LinearLayoutManager lLManagertimespent = new LinearLayoutManager(context);
                                            lLManagertimespent.setOrientation(LinearLayoutManager.VERTICAL);
                                            binding.rcyviewts.setLayoutManager(lLManagertimespent);
                                            adapterTImeSpent = new AdapterTimeSpent(arrayList1,context);
                                            binding.rcyviewts.setAdapter(adapterTImeSpent);


                                            String LCRecords = gson.toJson(arrayList1);
                                            ContentValues cv = new ContentValues();
                                            cv.put(ParseOpenHelper.JOBSTECHLCRECORDCURRDAY, LCRecords);
                                            database.update(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, cv, ParseOpenHelper.TECHIDCURRDAY + " =? AND " + ParseOpenHelper.JOBIDCURRDAY + " = ?",
                                                    new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID),jobid});
                                        }


                                        //Delete related row from database
                                        database.delete(ParseOpenHelper.TABLE_LCCHANGE, ParseOpenHelper.LCCHANGEJOBID + " =? AND " + ParseOpenHelper.LCCHANGECREATEDAT + " = ?",
                                                new String[]{jobj.getString("job_id"), insertedTime});
                                    }
                                    if (type.equalsIgnoreCase("stop")) {
                                        updatereciver = true;
                                        HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
                                        HandyObject.putPrams(context, AppConstants.JOBRUNNING_TOTALTIME, "0");
                                        App.appInstance.stopTimer();
                                        activity.replaceFragmentWithoutBack(new DashBoardFragment());
//                                        Intent intent_reg = new Intent(context, DashBoardActivity.class);
//                                        ((Activity) context).startActivity(intent_reg);
//                                        ((Activity) context).finish();
//                                        ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);


//                                        updatereciver = true;
//                                        HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
//                                        HandyObject.putPrams(context, AppConstants.JOBRUNNING_TOTALTIME, "0");
//                                        Intent intent_reg = new Intent(context, DashBoardActivity.class);
//                                        ((Activity) context).startActivity(intent_reg);
//                                        ((Activity) context).finish();
//                                        ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                                    } else {
                                        HandyObject.showAlert(context, jsonObject.getString("message"));
                                    }
                                } else {
                                    HandyObject.showAlert(context, jsonObject.getString("message"));
                                    if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                        HandyObject.clearpref(context);
                                        HandyObject.deleteAllDatabase(context);
                                        App.appInstance.stopTimer();
                                        Intent intent_reg = new Intent(context, LoginActivity.class);
                                        context.startActivity(intent_reg);
                                        ((Activity) context).finish();
                                        ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
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
            HandyObject.showAlert(context, context.getString(R.string.fetchdata_whenonline));
            HandyObject.stopProgressDialog();
        }

    }

    //On click start/stop button
    public void onClickJobChange() {
        if (binding.spinnerLc.getSelectedItemPosition() == 0) {
            binding.spinnerLc.setSelection(1);
        } else {
            if (checkedit() == true) {
                if (binding.boatnameValue.getText().toString().length() == 0) {
                    binding.boatnameValue.requestFocus();
                    HandyObject.showAlert(context, context.getString(R.string.boatnameempty));
                } else {
                    binding.hullidValue.requestFocus();
                    HandyObject.showAlert(context, context.getString(R.string.hullidempty));
                }
            } else {
                // HandyObject.showAlert(context,setDataForEdit());
                showDialog(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(), setDataForLCSumbmit(), setDataForEdit());
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
        //  displyPopup(v);
    }

    public void onClickAddDashboardNotes(View v) {
       // HandyObject.showAlert(context,"Still Need to add api");
        dialogAddTechNotes("Dashboard");
    }

    public void OnClickViewComment() {
        dialogViewComment("viewcommment", arrayListLaborPerf);
    }

    //On click View previous off the record
    public void OnClickOffTheRecord() {
        if (addTechlistOffTheRecord.size() == 0) {
            // dialogViewComment("offrecord", arrayListOffTheRecord);
        } else {
            arrayListOffTheRecord.clear();
            arrayListOffTheRecord.addAll(addTechlistOffTheRecord);
            addTechlistOffTheRecord.clear();
        }
        dialogViewComment("offrecord", arrayListOffTheRecord);
    }

    //On click Add tech notes button
    public void OnClickAddTech() {
        dialogAddTechNotes("Off The Record");
    }

    //On click upload button to upload image to server related to selected job
    public void OnClickUpload() {
        dialogUploadImage();
    }

  /*  public void OnClickMyLaborPerf() {
        // binding.etLaborperform.setText("");
        arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo();
        if (binding.etLaborperform.getText().toString().length() == 0) {
            binding.etLaborperform.requestFocus();
            HandyObject.showAlert(context, context.getString(R.string.fieldempty));
        } else {
            SubmitMyLaborPerf_Task(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID),
                    arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(), binding.etLaborperform.getText().toString(), "Tech Labour Performed");
        }
    }*/

    // Show Job Completion Dialog related to selected job
    void showDialog(String posi_ticket, String allValues, String editvalues) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = JobStatusDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(), posi_ticket, allValues, editvalues);
        newFragment.setCancelable(false);
        newFragment.show(ft, "dialog");
        App.appInstance.pauseTimer();
    }

    //Create dialog for need any kind of part related to selected job
    void showDialogNeedPart() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialogneedpart");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = NeedPartDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo());
        newFragment.show(ft, "dialogneedpart");
    }

    //Create dialog for need estimate related to selected job
    private void dialogNeedEstimate(View anchorview) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialogneedestimate");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = NeedEstimateDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo());
        newFragment.show(ft, "dialogneedestimate");
    }

    //Create dialog for need or change order related to selected job
    private void dialogNeedChangeOrder() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialogneedchgorder");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = NeedChangeOrderDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo());
        newFragment.show(ft, "dialogneedchgorder");
    }

    //Create dialog for View Comments related to selected job
    private void dialogViewComment(String type, ArrayList<DashboardNotes_Skeleton> arraylist) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialogviewcomment");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = ViewCommentDialog.newInstance(arraylist, type);
        newFragment.show(ft, "dialogviewcomment");
    }

    //Create dialog for Add tech notes related to selected job
    private void dialogAddTechNotes(String type) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("addtech");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = AddTechNotesDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(),type);
        newFragment.show(ft, "addtech");
    }

    //Create dialog for upload multiple or single image related to selected job
    private void dialogUploadImage() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("addtech");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = UploadImageDialog.newInstance(arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(),"Detail");
        newFragment.show(ft, "addtech");
    }


    public void callOnStopView() {
        //  HandyObject.showAlert(context,"ONDestroyView");
        try {
            HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "yes");
//            if (updatereciver == true) {
//                updatereciver = false;
//                HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
//            } else {
//                HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "yes");
//            }
            //  HandyObject.putPrams(context, AppConstants.ISJOB_NEWTYPE, "no");
            //   HandyObject.putPrams(context, AppConstants.JOBRUNNING_ETLABORPERFORM, binding.etLaborperform.getText().toString());
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_ETLABORPERFORM, "");
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_CENTERTIME, arralistJobTime.get(1).getHrminutes());
            HandyObject.putIntPrams(context, AppConstants.JOBRUNNING_INDEX, binding.jobspinner.getSelectedItemPosition());
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_ID, arrayListsJobIds.get(binding.jobspinner.getSelectedItemPosition()));
            HandyObject.putIntPrams(context, AppConstants.JOBLABORCODE_INDEX, binding.spinnerLc.getSelectedItemPosition());
            HandyObject.putIntPrams(context, AppConstants.JOBSTARTTIME_INDEX, binding.timespinner.getSelectedItemPosition());

            HandyObject.putPrams(context, AppConstants.JOBRUNNING_BOATNAME, binding.boatnameValue.getText().toString());
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_HULLID, binding.hullidValue.getText().toString());
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_CAPTNAME, binding.captnameValue.getText().toString());
            // binding.uploadimage.getText().toString()
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_CLOSETIME, binding.uploadimage.getText().toString());
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_CLOSETIMEDATE, String.valueOf(new Date().getTime()));
        } catch (Exception e) {
        }
    }

    public void callOnDestroyView() {
          //  HandyObject.showAlert(context,"ONDestroyView");
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(reciever);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(JobUpdatereciever);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(LastOffTechreciever);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(UpdateDashboard);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(UpdateImageReciever);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(NeddPartReciever);
            if (updatereciver == true) {
                updatereciver = false;
                HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "no");
            } else {
                HandyObject.putPrams(context, AppConstants.ISJOB_RUNNING, "yes");
            }
            //  HandyObject.putPrams(context, AppConstants.ISJOB_NEWTYPE, "no");
            //   HandyObject.putPrams(context, AppConstants.JOBRUNNING_ETLABORPERFORM, binding.etLaborperform.getText().toString());
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_ETLABORPERFORM, "");
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_CENTERTIME, arralistJobTime.get(1).getHrminutes());
            HandyObject.putIntPrams(context, AppConstants.JOBRUNNING_INDEX, binding.jobspinner.getSelectedItemPosition());
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_ID, arrayListsJobIds.get(binding.jobspinner.getSelectedItemPosition()));
            HandyObject.putIntPrams(context, AppConstants.JOBLABORCODE_INDEX, binding.spinnerLc.getSelectedItemPosition());
            HandyObject.putIntPrams(context, AppConstants.JOBSTARTTIME_INDEX, binding.timespinner.getSelectedItemPosition());

            HandyObject.putPrams(context, AppConstants.JOBRUNNING_BOATNAME, binding.boatnameValue.getText().toString());
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_HULLID, binding.hullidValue.getText().toString());
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_CAPTNAME, binding.captnameValue.getText().toString());
            HandyObject.putPrams(context, AppConstants.JOBRUNNING_CLOSETIME, binding.uploadimage.getText().toString());

            HandyObject.putPrams(context, AppConstants.JOBRUNNING_CLOSETIMEDATE, String.valueOf(new Date().getTime()));
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

    //check either boatname,hullid,capt.name is blank
    private boolean checkedit() {
        boolean abc = false;
        if (binding.boatnameValue.getText().toString().length() == 0) {
            abc = true;
        } else if (binding.hullidValue.getText().toString().length() == 0) {
            abc = true;
        }
//        else if (binding.captnameValue.getText().toString().length() == 0) {
//            abc = true;
//        }
        else {
            abc = false;
        }
        return abc;
    }

    //Set data of boatname,hullid,capt.name to send it in job completion API
    private String setDataForEdit() {
        String value = "";
        if (binding.captnameValue.getText().toString() != null && !binding.captnameValue.getText().toString().isEmpty()) {
            value = binding.boatnameValue.getText().toString() + "-" + binding.hullidValue.getText().toString() + "-" + binding.captnameValue.getText().toString();
        } else {
            value = binding.boatnameValue.getText().toString() + "-" + binding.hullidValue.getText().toString() + "-" + "captainblank";
        }
        return value;
    }

  //  Urban Affordable Scheme
  //  Canfin homes.

    //Set data for submit the labor code
    private String setDataForLCSumbmit() {
        if (arralistJobTime != null && !arralistJobTime.isEmpty()) {
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
                if (Integer.parseInt(withoutzero) > 7 && Integer.parseInt(runinngtime.split(":")[0]) == 0) {
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                }
            }

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
            String endtime="";
            if(timerindex_prev == 2) {
                 endtime = arralistJobTime_JobStatus.get(2).getParsedate() + " " + arralistJobTime_JobStatus.get(2).getHrminutes();
            } else {
                 endtime = arralistJobTime_JobStatus.get(1).getParsedate() + " " + arralistJobTime_JobStatus.get(1).getHrminutes();
            }
           // String endtime = arralistJobTime_JobStatus.get(1).getParsedate() + " " + arralistJobTime_JobStatus.get(1).getHrminutes();
            String hrsworked = String.valueOf(binding.uploadimage.getText()).split(":")[0] + ":" + String.valueOf(binding.uploadimage.getText().toString()).split(":")[1];
            String hrsAdjusted = String.valueOf(HoursAdjusted(binding.timespinner.getSelectedItemPosition()));
            String finalValue = starttime + "--" + endtime + "--" + hrsworked + "--" + hrsAdjusted + "--" + binding.spinnerLc.getSelectedItem().toString();
            return finalValue;
        }
        return "";
    }

    //Fetch Urgent messages related to selected job from local database
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
                LinearLayoutManager lLManagerparts = new LinearLayoutManager(context);
                lLManagerparts.setOrientation(LinearLayoutManager.VERTICAL);
                binding.rcyviewUrgentmsg.setLayoutManager(lLManagerparts);
                AdapterDashbrdUrgentMsg adapterurgentmsg = new AdapterDashbrdUrgentMsg(context, urgentMsgSkeletons, fragment);
                binding.rcyviewUrgentmsg.setAdapter(adapterurgentmsg);
            }
        }
    }

    //Fetch All Jobs data from local database using AsyncTask for background purpose
    private class databsefetch extends AsyncTask<ArrayList<AllJobsSkeleton>, Void, ArrayList<AllJobsSkeleton>> {

        @Override
        protected ArrayList<AllJobsSkeleton> doInBackground(ArrayList<AllJobsSkeleton>... arrayLists) {
            database = ParseOpenHelper.getInstance(context).getWritableDatabase();
            gson = new Gson();
            Cursor cursor = database.query(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, null, ParseOpenHelper.TECHIDCURRDAY + "=?", new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID)}, null, null, null);
            cursor.moveToFirst();
            arralistAllJobs = new ArrayList<>();
            arrayListsJobIds = new ArrayList<>();
            arrayListLaborPerf = new ArrayList<>();
            arrayListOffTheRecord = new ArrayList<>();
            addTechlistOffTheRecord = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                Type type = new TypeToken<AllJobsSkeleton>() {
                }.getType();
                Type typedashnotes = new TypeToken<ArrayList<DashboardNotes_Skeleton>>() {
                }.getType();
                Type typeimages = new TypeToken<ArrayList<UploadImageNewSkeleton>>() {
                }.getType();
                Type typeparts = new TypeToken<ArrayList<PartsSkeleton>>() {
                }.getType();
                Type typeLC = new TypeToken<ArrayList<TimeSpentSkeleton>>() {
                }.getType();
                Type typedstring = new TypeToken<ArrayList<String>>() {
                }.getType();
                String getSke = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSSKELETONCURRDAY));
                String getSkedashnotes = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHDASHBOARDNOTESCURRDAY));
                String getLaborPerfList = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHLABORPERFORMCURRDAY));
                String getOffTheRecordList = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHOFFTHERECORDCURRDAY));
                String getUploadImages = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHUPLOADEDIMAGESCURRDAY));
                String getPartsRecord = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHPARTSRECORDCURRDAY));
                String getLcRecords = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHLCRECORDCURRDAY));

                String jobid = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBIDCURRDAY));
                AllJobsSkeleton ske = gson.fromJson(getSke, type);
                ArrayList<DashboardNotes_Skeleton> arrayListDash = gson.fromJson(getSkedashnotes, typedashnotes);
                ArrayList<DashboardNotes_Skeleton> arrayListLaborPerform = gson.fromJson(getLaborPerfList, typedashnotes);
                ArrayList<DashboardNotes_Skeleton> arrayListOffTheRecordList = gson.fromJson(getOffTheRecordList, typedashnotes);
                ArrayList<PartsSkeleton> arrayListParts = gson.fromJson(getPartsRecord, typeparts);

                ArrayList<TimeSpentSkeleton> arrayListLC = gson.fromJson(getLcRecords, typeLC);


                ArrayList<UploadImageNewSkeleton> arrayListUploadImages = gson.fromJson(getUploadImages, typeimages);
                //  ArrayList<String> arrayListUploadImages = gson.fromJson(getUploadImages, typedstring);

                ske.setArrayList(arrayListDash);
                ske.setArrayListLaborPerf(arrayListLaborPerform);
                ske.setArrayListOffTheRecord(arrayListOffTheRecordList);
                ske.setArrayListImages(arrayListUploadImages);
                ske.setArrayListParts(arrayListParts);
                ske.setArrayListLC(arrayListLC);
                arralistAllJobs.add(ske);
                arrayListsJobIds.add(jobid);
                cursor.moveToNext();
            }
            cursor.close();
            return arralistAllJobs;
        }

        @Override
        protected void onPostExecute(ArrayList<AllJobsSkeleton> allJobsSkeletons) {
            super.onPostExecute(allJobsSkeletons);
            adapterjobspinner = new AdapterJobSpinner(context, allJobsSkeletons);adapterjobspinner = new AdapterJobSpinner(context, allJobsSkeletons);
            binding.jobspinner.setAdapter(adapterjobspinner);
            if (isJobRunning() == true) {
                int posi = arrayListsJobIds.indexOf(HandyObject.getPrams(context, AppConstants.JOBRUNNING_ID));
                binding.jobspinner.setSelection(posi);
            } else if (fragment.getArguments() != null) {
                // binding.jobspinner.setSelection(Integer.parseInt(fragment.getArguments().getString("posinew")));
                binding.jobspinner.setSelection(fragment.getArguments().getInt("clicked_position"));
            }
            binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());
        }
    }

    //Insert labor code related data to local database
    private void insertIntoDBLC(String teqid, String jobid, String lc, String starttime, String endtime, String hrsworked, String hrsAdjusted,
                                String createdAt, String count) {
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
        cv.put(ParseOpenHelper.LCCHANGECOUNT, count);
        long idd = database.insert(ParseOpenHelper.TABLE_LCCHANGE, null, cv);
    }

    //For related chat page
    public void OnClickChat() {
        // If chat is not initiated yet then chat dialog will open either direct chat window related to selected job
        if (arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobmsgCount().equalsIgnoreCase("0")) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment prev = fragmentManager.findFragmentByTag("composemsgnew");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            // Create and show the dialog.
            DialogFragment newFragment = ComposeDialogNew.newInstance(4, arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(), arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getCustomerName(), arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getCustomerType(),
                    arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getBoatName(), arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getBoatmakeYear());
            newFragment.show(ft, "composemsgnew");
        } else {
            Intent resultIntent = new Intent(context, ChatActivity.class);
            resultIntent.putExtra("type", "chat");
            resultIntent.putExtra("jobid", selectedJobId);
            ((Activity) context).startActivity(resultIntent);
        }
    }

    //If Seatech specific job is selected then this clicked button will show , this button will stop the seatech specific job and switch to dashboard screen
    public void onClickStop() {
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
            if (Integer.parseInt(withoutzero) > 7 && Integer.parseInt(runinngtime.split(":")[0]) == 0) {
                calendar.add(Calendar.HOUR_OF_DAY, 1);
            }
        }

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
        String endtime = "";
        if(timerindex_prev == 2) {
            endtime = arralistJobTime_JobStatus.get(2).getParsedate() + " " + arralistJobTime_JobStatus.get(2).getHrminutes();
        } else {
            endtime = arralistJobTime_JobStatus.get(1).getParsedate() + " " + arralistJobTime_JobStatus.get(1).getHrminutes();
        }

        // String endtime = arralistJobTime_JobStatus.get(1).getParsedate() + " " + arralistJobTime_JobStatus.get(1).getHrminutes();
        String hrsworked = String.valueOf(binding.uploadimage.getText()).split(":")[0] + ":" + String.valueOf(binding.uploadimage.getText().toString()).split(":")[1];
        if (binding.stopbtnInternal.getVisibility() == View.VISIBLE) {
            TaskLCChange(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID), "111111",
                    binding.spinnerLc.getSelectedItem().toString(), binding.spinnerLc.getSelectedItemPosition(),
                    starttime, endtime, hrsworked, String.valueOf(HoursAdjusted(timerindex_prev)), "stop");
        } else {
            TaskLCChange(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID), arralistAllJobs.get(binding.jobspinner.getSelectedItemPosition()).getJobticketNo(),
                    binding.spinnerLc.getSelectedItem().toString(), binding.spinnerLc.getSelectedItemPosition(),
                    starttime, endtime, hrsworked, String.valueOf(HoursAdjusted(timerindex_prev)), "stop");
        }
    }

    private void getRelatedTask(final String jobid,final int index) {
        //  jobsArrayList = new ArrayList<>();
        HandyObject.getApiManagerMain().getTicketData(HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID),jobid, HandyObject.getPrams(context, AppConstants.LOGIN_SESSIONID))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            binding.swipeview.setRefreshing(false);
                            String jsonResponse = response.body().string();
                            Log.e("responseDetail", jsonResponse);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getString("status").toLowerCase().equals("success")) {
                                //jobsArrayList.clear();

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                Gson gson = new Gson();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobjInside = jsonArray.getJSONObject(i);

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

                                    arralistAllJobs.get(index).setArrayList(arraylistDashNotes);
                                    arralistAllJobs.get(index).setArrayListLaborPerf(arraylistLabrperformed);
                                    arralistAllJobs.get(index).setArrayListOffTheRecord(arraylistOffTheRecord);
                                    arralistAllJobs.get(index).setArrayListImages(arraylistupldImages);
                                    arralistAllJobs.get(index).setArrayListParts(arraylistParts);
                                    arralistAllJobs.get(index).setArrayListLC(arraylistLC);
                                    arrayListLaborPerf.clear();
                                    arrayListLaborPerf.addAll(arraylistLabrperformed);

                                    arrayListOffTheRecord.clear();
                                    arrayListOffTheRecord.addAll(arraylistOffTheRecord);
                                    if (arrayListOffTheRecord.size() == 0) {
                                        binding.lastoffRecrdnotes.setText(context.getString(R.string.norecordFound));
                                    } else {
                                        binding.lastoffRecrdnotes.setText(arrayListOffTheRecord.get(0).getNotes() + " (" + arrayListOffTheRecord.get(0).getNoteWriter() + ")");
                                    }

                                    if (arralistAllJobs.get(index).getArrayList().size() == 0) {
                                        binding.nodashbrdnotes.setVisibility(View.VISIBLE);
                                        binding.rcyviewDashbrdnotes.setVisibility(View.GONE);
                                    }
                                    if (arralistAllJobs.get(index).getArrayList().size() > 0) {
                                        binding.nodashbrdnotes.setVisibility(View.GONE);
                                        binding.rcyviewDashbrdnotes.setVisibility(View.VISIBLE);
                                        LinearLayoutManager lLManagerDashNotes = new LinearLayoutManager(context);
                                        lLManagerDashNotes.setOrientation(LinearLayoutManager.VERTICAL);
                                        binding.rcyviewDashbrdnotes.setLayoutManager(lLManagerDashNotes);
                                        adapterdashnotes = new AdapterDashboardNotes(context, arralistAllJobs.get(index).getArrayList());
                                        binding.rcyviewDashbrdnotes.setAdapter(adapterdashnotes);
                                    }

                                    if (arralistAllJobs.get(index).getArrayListParts().size() == 0) {
                                        binding.noparts.setVisibility(View.VISIBLE);
                                        binding.lltopparts.setVisibility(View.GONE);
                                        binding.recyclerparts.setVisibility(View.GONE);
                                    }
                                    if (arralistAllJobs.get(index).getArrayListParts().size() > 0) {
                                        binding.noparts.setVisibility(View.GONE);
                                        binding.lltopparts.setVisibility(View.VISIBLE);
                                        binding.recyclerparts.setVisibility(View.VISIBLE);
                                        LinearLayoutManager lLManagerparts = new LinearLayoutManager(context);
                                        lLManagerparts.setOrientation(LinearLayoutManager.VERTICAL);
                                        binding.recyclerparts.setLayoutManager(lLManagerparts);
                                        adapterParts = new AdapterParts(context, arralistAllJobs.get(index).getArrayListParts());
                                        binding.recyclerparts.setAdapter(adapterParts);
                                    }

                                    if (arralistAllJobs.get(index).getArrayListImages().size() == 0) {
                                        binding.nouploadedimage.setVisibility(View.VISIBLE);
                                        binding.rcylrviewUpldedImages.setVisibility(View.GONE);
                                    }
                                    if (arralistAllJobs.get(index).getArrayListImages().size() > 0) {
                                        binding.nouploadedimage.setVisibility(View.GONE);
                                        binding.rcylrviewUpldedImages.setVisibility(View.VISIBLE);
                                        LinearLayoutManager lLManagerImages = new LinearLayoutManager(context);
                                        lLManagerImages.setOrientation(LinearLayoutManager.HORIZONTAL);
                                        binding.rcylrviewUpldedImages.setLayoutManager(lLManagerImages);
                                        adapterUploadedImages = new AdapterUploadedImages(context, arralistAllJobs.get(index).getArrayListImages(), fragmentManager);
                                        binding.rcylrviewUpldedImages.setAdapter(adapterUploadedImages);
                                    }

                                    if (arralistAllJobs.get(index).getArrayListLC().size() == 0) {
                                        binding.noTSmsg.setVisibility(View.VISIBLE);
                                        binding.llheaderTS.setVisibility(View.GONE);
                                        binding.rcyviewts.setVisibility(View.GONE);
                                    }
                                    if(arralistAllJobs.get(index).getArrayListLC().size() > 0) {
                                        binding.noTSmsg.setVisibility(View.GONE);
                                        binding.llheaderTS.setVisibility(View.VISIBLE);
                                        binding.rcyviewts.setVisibility(View.VISIBLE);
                                        LinearLayoutManager lLManagerLC = new LinearLayoutManager(context);
                                        lLManagerLC.setOrientation(LinearLayoutManager.VERTICAL);
                                        binding.rcyviewts.setLayoutManager(lLManagerLC);
                                        adapterTImeSpent = new AdapterTimeSpent(arralistAllJobs.get(index).getArrayListLC(),context);
                                        binding.rcyviewts.setAdapter(adapterTImeSpent);
                                    }

                                    String dashnotes = gson.toJson(arraylistDashNotes);
                                    String laborPerformed = gson.toJson(arraylistLabrperformed);
                                    String OffTheRecord = gson.toJson(arraylistOffTheRecord);
                                    String UploadedImages = gson.toJson(arraylistupldImages);
                                    String PartsRecords = gson.toJson(arraylistParts);
                                    String LCRecords = gson.toJson(arraylistLC);
                                    ContentValues cv = new ContentValues();

                                    cv.put(ParseOpenHelper.JOBSTECHPARTSRECORDCURRDAY, dashnotes);
                                    cv.put(ParseOpenHelper.JOBSTECHLABORPERFORMCURRDAY, laborPerformed);
                                    cv.put(ParseOpenHelper.JOBSTECHOFFTHERECORDCURRDAY, OffTheRecord);
                                    cv.put(ParseOpenHelper.JOBSTECHUPLOADEDIMAGESCURRDAY, UploadedImages);
                                    cv.put(ParseOpenHelper.JOBSTECHPARTSRECORDCURRDAY, PartsRecords);
                                    cv.put(ParseOpenHelper.JOBSTECHLCRECORDCURRDAY, LCRecords);
                                    database.update(ParseOpenHelper.TABLENAME_ALLJOBSCURRENTDAY, cv, ParseOpenHelper.TECHIDCURRDAY + " =? AND " + ParseOpenHelper.JOBIDCURRDAY + " = ?",
                                            new String[]{HandyObject.getPrams(context, AppConstants.LOGINTEQ_ID), jobid});
                                    //   Log.e("table", String.valueOf(idd));


                                }
                                HandyObject.stopProgressDialog();
                              //  new databsefetch().execute();
                            } else {
                                HandyObject.stopProgressDialog();
                                // jobsArrayList.clear();
                                HandyObject.showAlert(context, jsonObject.getString("message"));
                                if (jsonObject.getString("message").equalsIgnoreCase("Session Expired")) {
                                    HandyObject.clearpref(context);
                                    HandyObject.deleteAllDatabase(context);
                                    App.appInstance.stopTimer();
                                    Intent intent_reg = new Intent(context, LoginActivity.class);
                                    context.startActivity(intent_reg);
                                    ((Activity) context).finish();
                                    ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
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
                        binding.swipeview.setRefreshing(false);
                    }
                });
    }
}
