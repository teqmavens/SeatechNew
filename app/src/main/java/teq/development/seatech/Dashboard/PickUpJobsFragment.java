package teq.development.seatech.Dashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import teq.development.seatech.Dashboard.Adapter.AdapterPickUpJobs;
import teq.development.seatech.JobDetail.JobDetailFragment;
import teq.development.seatech.R;

public class PickUpJobsFragment extends Fragment {

    DashBoardActivity activity;
    private AdapterPickUpJobs adapterpickup;
    RecyclerView recyclerView;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DashBoardActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_pickupjobs, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView){
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rcyview_pickupjobs);
        LinearLayoutManager lLManagerPickupJobs = new LinearLayoutManager(getActivity());
        lLManagerPickupJobs.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lLManagerPickupJobs);
        adapterpickup = new AdapterPickUpJobs(getActivity(),PickUpJobsFragment.this);
        recyclerView.setAdapter(adapterpickup);
    }

    public void onClickTicketNo(){
       activity.replaceFragment(new JobDetailPickupFragment());
    }
}
