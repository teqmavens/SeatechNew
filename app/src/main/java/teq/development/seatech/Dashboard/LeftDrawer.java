package teq.development.seatech.Dashboard;

import android.content.Context;
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
import teq.development.seatech.R;
import teq.development.seatech.databinding.MenuDrawerBinding;

public class LeftDrawer extends Fragment{

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
        View rootview = inflater.inflate(R.layout.menu_drawer,container,false);
        MenuDrawerBinding binding = DataBindingUtil.bind(rootview);
        binding.setMenudrawer(this);
        return rootview;
    }

    public void OnClickDashboard(){
        activity.replaceFragment(new DashBoardFragment());
        activity.mDrawerLayout.closeDrawers();
    }

    public void OnClickDetailPage(){
        activity.replaceFragment(new JobDetailFragment());
        activity.mDrawerLayout.closeDrawers();
    }

    public void OnClickTimesheet(){
        activity.mDrawerLayout.closeDrawers();
    }

    public void OnClickPickupJobs(){
        activity.replaceFragment(new PickUpJobsFragment());
        activity.mDrawerLayout.closeDrawers();
    }
}
