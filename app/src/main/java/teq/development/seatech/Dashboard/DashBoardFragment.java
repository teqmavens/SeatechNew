package teq.development.seatech.Dashboard;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import teq.development.seatech.Dashboard.Adapter.AdapterDashbrdUrgentMsg;
import teq.development.seatech.Dashboard.Adapter.AdapterJosbForYou;
import teq.development.seatech.Dashboard.Adapter.AdapterPickUpJobs;
import teq.development.seatech.JobDetail.JobDetailFragment;
import teq.development.seatech.R;
import teq.development.seatech.databinding.FrgmDashboardBinding;

public class DashBoardFragment extends Fragment {

    private DashBoardActivity activity;
    private Context context;
    private AdapterJosbForYou adapterjobs;
    private AdapterPickUpJobs adapterpickup;
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
        View rootView = inflater.inflate(R.layout.frgm_dashboard, container, false);
        FrgmDashboardBinding binding = DataBindingUtil.bind(rootView);
        binding.setFrgmdashboard(this);
        initViews(binding);
        return rootView;
    }

    private void initViews(FrgmDashboardBinding binding) {
        LinearLayoutManager lLManagerJobs = new LinearLayoutManager(getActivity());
        lLManagerJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcyviewJobs.setLayoutManager(lLManagerJobs);
        adapterjobs = new AdapterJosbForYou(context);
        binding.rcyviewJobs.setAdapter(adapterjobs);

        LinearLayoutManager lLManagerPickupJobs = new LinearLayoutManager(getActivity());
        lLManagerPickupJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcyviewPickupjobs.setLayoutManager(lLManagerPickupJobs);
        adapterpickup = new AdapterPickUpJobs(context);
        binding.rcyviewPickupjobs.setAdapter(adapterpickup);

        LinearLayoutManager lLManagerUrgentJobs = new LinearLayoutManager(getActivity());
        lLManagerUrgentJobs.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcyviewUrgentmsg.setLayoutManager(lLManagerUrgentJobs);
        adapterurgentmsg = new AdapterDashbrdUrgentMsg(context);
        binding.rcyviewUrgentmsg.setAdapter(adapterurgentmsg);
       // DateTime((dt.Ticks / d.Ticks) * d.Ticks);

        // binding.rec.setLayoutManager(linearLayoutManager);
    }

    public void OnClickStartTime(){
       // Toast.makeText(activity, String.valueOf(getNear15Minute()), Toast.LENGTH_SHORT).show();
        activity.replaceFragment(new JobDetailFragment());
    }

    public static int getNear15Minute(){
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int mod = minutes%15;
        int res = 0 ;
        if((mod) >=8){
            res = minutes+(15 - mod);
        }else{
            res = minutes-mod;
        }
        return (res%60);
    }

    public Date getQuarter() {
        Calendar calendar = Calendar.getInstance();
        int mins = calendar.get(Calendar.MINUTE);

      /*  if (mins < 15) {
            mins = 0;
        } else if (mins < 30) {
            mins = 15;
        } else if (mins < 45) {
            mins = 30;
        } else {
            mins = 45;
        }*/

        if (mins >=0 && mins < 8) {
            mins = 0;
        } else if (mins >= 8 && mins < 23) {
            mins = 15;
        } else if (mins >= 23 &&mins < 38) {
            mins = 30;
        } else if (mins >= 38 &&mins < 53) {
            mins = 45;
        } else {
            mins = 0;
        }

        calendar.set(Calendar.MINUTE, mins);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
