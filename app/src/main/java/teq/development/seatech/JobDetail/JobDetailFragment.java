package teq.development.seatech.JobDetail;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.util.List;

import teq.development.seatech.Dashboard.Adapter.AdapterDashbrdUrgentMsg;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.R;
import teq.development.seatech.databinding.FrgmJobdetailBinding;

public class JobDetailFragment extends Fragment {

    private DashBoardActivity activity;
    private Context context;
    private AdapterDashbrdUrgentMsg adapterurgentmsg;

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
        FrgmJobdetailBinding binding = DataBindingUtil.bind(rootView);
        binding.setFrgmjobdetail(this);
        initViews(binding);
        return rootView;
    }

    private void initViews(FrgmJobdetailBinding binding) {
        LinearLayoutManager lLManagerUrgentJobs = new LinearLayoutManager(getActivity());
        lLManagerUrgentJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcyviewUrgentmsg.setLayoutManager(lLManagerUrgentJobs);
        adapterurgentmsg = new AdapterDashbrdUrgentMsg(context);
        binding.rcyviewUrgentmsg.setAdapter(adapterurgentmsg);

        List<String> list = new ArrayList<String>();
        list.add("Job1");
        list.add("JOb2");
        list.add("Job3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.jobspinner.setAdapter(dataAdapter);
        binding.jobspinner.setOnItemSelectedListener(new JobItemSelectedListener());

        ArrayAdapter<CharSequence> lcAdapter =  ArrayAdapter.createFromResource(getActivity(),R.array.laborcode_array,
                android.R.layout.simple_spinner_item);
        lcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerLc.setAdapter(lcAdapter);

    }

    public class JobItemSelectedListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(activity, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public void onClickJobChange(){
        showDialog();
    }

    public void onClickNeedPart(){
        showDialogNeedPart();
    }

    public void onClickNeedEstimate(View v){
        dialogNeedEstimate(v);
    }

    public void onClickNeedChangeOrder(View v){
        dialogNeedChangeOrder();
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
}
