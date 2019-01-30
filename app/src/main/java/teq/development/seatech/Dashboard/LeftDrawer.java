package teq.development.seatech.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import teq.development.seatech.JobDetail.JobDetailFragment;
import teq.development.seatech.JobDetail.MainJobdetail;
import teq.development.seatech.JobDetail.NewJobDetailFrgament;
import teq.development.seatech.PickUpJobs.PickUpJobsFragment;
import teq.development.seatech.R;
import teq.development.seatech.Schedule.ScheduleParent;
import teq.development.seatech.Timesheet.TimeSheetFragment;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.MenuDrawerBinding;

public class LeftDrawer extends Fragment {

    private DashBoardActivity activity;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = (DashBoardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.menu_drawer, container, false);
        MenuDrawerBinding binding = DataBindingUtil.bind(rootview);
        binding.setMenudrawer(this);
        return rootview;
    }

    public void OnClickDashboard() {
        activity.replaceFragment(new DashBoardFragment());
        activity.mDrawerLayout.closeDrawers();
    }

    public void OnClickDetailPage() {
       /* if (isJobRunning() == true) {
            if (HandyObject.getPrams(context, AppConstants.ISJOB_NEWTYPE).equalsIgnoreCase("yes")) {
                activity.replaceFragment(new NewJobDetailFrgament());
            } else {
                activity.replaceFragment(new JobDetailFragment());
            }
        } else {
            if (HandyObject.getPrams(context, AppConstants.ISJOB_NEWTYPE).equalsIgnoreCase("yes")) {
                activity.replaceFragment(new NewJobDetailFrgament());
            }
        }*/
        if (isJobRunning() == true) {
            activity.replaceFragment(new MainJobdetail());
        } else if (HandyObject.getPrams(context, AppConstants.ISJOB_NEWTYPE).equalsIgnoreCase("yes")) {
            activity.replaceFragment(new MainJobdetail());
        }

        // new JobDetailFragment();
        activity.mDrawerLayout.closeDrawers();
    }

    public void OnClickTimesheet() {
        activity.replaceFragment(new TimeSheetFragment());
        activity.mDrawerLayout.closeDrawers();
    }

    public void OnClickPickupJobs() {
        activity.replaceFragment(new PickUpJobsFragment());
        activity.mDrawerLayout.closeDrawers();
    }

    public void OnClickSchedule() {
        activity.mDrawerLayout.closeDrawers();
        startActivity(new Intent(context, ScheduleParent.class));
    }

    boolean isJobRunning() {
        if (HandyObject.getPrams(context, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
    }
}
