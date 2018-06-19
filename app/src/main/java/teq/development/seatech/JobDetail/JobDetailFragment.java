package teq.development.seatech.JobDetail;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import teq.development.seatech.Dashboard.Adapter.AdapterDashbrdUrgentMsg;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.JobDetail.Adapter.AdapterDashbrdUrgentMsgDetail;
import teq.development.seatech.R;
import teq.development.seatech.databinding.FrgmJobdetailBinding;

public class JobDetailFragment extends Fragment {

    private DashBoardActivity activity;
    private Context context;
    private AdapterDashbrdUrgentMsgDetail adapterurgentmsg;
    FrgmJobdetailBinding binding;
    private long startTime = 0L;

    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = (DashBoardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_jobdetail, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.setFrgmjobdetail(this);
        initViews(binding);
        return rootView;
    }

    private void initViews(FrgmJobdetailBinding binding) {
        LinearLayoutManager lLManagerUrgentJobs = new LinearLayoutManager(getActivity());
        lLManagerUrgentJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcyviewUrgentmsg.setLayoutManager(lLManagerUrgentJobs);
        adapterurgentmsg = new AdapterDashbrdUrgentMsgDetail(context);
        binding.rcyviewUrgentmsg.setNestedScrollingEnabled(false);
        binding.rcyviewUrgentmsg.setAdapter(adapterurgentmsg);
        //  binding.textnotes.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(context,R.drawable.plus), null);

        List<String> list = new ArrayList<String>();
        list.add("Job1");
        list.add("Job2");
        list.add("Job3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.jobspinner.setAdapter(dataAdapter);
        binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());

        ArrayAdapter<CharSequence> lcAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.laborcode_array,
                android.R.layout.simple_spinner_item);
        lcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerLc.setAdapter(lcAdapter);
        binding.spinnerLc.setOnItemSelectedListener(new LCItemSelectedListener());
        updateTimeSpinner();
    }

    private void updateTimeSpinner() {
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

        calendar.set(Calendar.MINUTE, n);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, -15);
        //  Toast.makeText(activity, calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
        listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));

        calendar.add(Calendar.MINUTE, 15);
        //  Toast.makeText(activity, calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
        listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));

        calendar.add(Calendar.MINUTE, 15);
        // Toast.makeText(activity, calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
        listtime.add(calendar.get(Calendar.HOUR_OF_DAY) + ":" + manageMinutes(calendar.get(Calendar.MINUTE)));
        ArrayAdapter<String> dataAdaptertime = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, listtime);
        dataAdaptertime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.timespinner.setAdapter(dataAdaptertime);


    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int hrs = mins / 60;
            //int milliseconds = (int) (updatedTime % 1000);
          /*  binding.uploadimage.setText("" + mins + ":"

                    + String.format("%02d", secs) + ":"

                    + String.format("%03d", updatedTime));*/
            binding.uploadimage.setText(hrs + ":" + mins + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };

    private String manageMinutes(int min) {
        if (min == 0) {
            return "00";
        } else {
            return String.valueOf(min);
        }
    }

    public class JobItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //  Toast.makeText(activity, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class LCItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(activity, "lcsleect", Toast.LENGTH_SHORT).show();
            customHandler.removeCallbacks(updateTimerThread);

            customHandler = new Handler();
            startTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updatedTime = 0L;

            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 1000);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public void onClickJobChange() {
        showDialog();
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
        dialogViewComment();
    }

    public void OnClickAddTech() {
        dialogAddTechNotes();
    }

    public void OnClickUpload() {
        dialogUploadImage();
    }

    void showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = JobStatusDialog.newInstance(8);
        newFragment.show(ft, "dialog");
    }

    void showDialogNeedPart() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialogneedpart");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = NeedPartDialog.newInstance(8);
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
        DialogFragment newFragment = NeedEstimateDialog.newInstance(8);
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
        DialogFragment newFragment = NeedChangeOrderDialog.newInstance(8);
        newFragment.show(ft, "dialogneedchgorder");
    }

    private void dialogViewComment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialogviewcomment");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = ViewCommentDialog.newInstance(8);
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
        DialogFragment newFragment = AddTechNotesDialog.newInstance(8);
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
        DialogFragment newFragment = UploadImageDialog.newInstance(8);
        newFragment.show(ft, "addtech");
    }


    public void OnClicksubmit() {
        binding.etLaborperform.setText("");
    }

}
