package teq.development.seatech.Dashboard;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import teq.development.seatech.JobDetail.Adapter.AdapterDashbrdUrgentMsgDetail;
import teq.development.seatech.JobDetail.ViewCommentDialog;
import teq.development.seatech.R;

public class JobDetailPickupFragment extends Fragment {

    private AdapterDashbrdUrgentMsgDetail adapterurgentmsg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.jobdetail_pickup, container, false);
       // FrgmJobdetailBinding binding = DataBindingUtil.bind(rootView);
      //  binding.setFrgmjobdetail(this);
      //  initViews(binding);
        //JobDetailPickupFragment binding = DataBindingUtil.bind(rootView);
      //  binding.setJ
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        TextView previouslabor_comment = (TextView) rootView.findViewById(R.id.previouslabor_comment);
        RecyclerView rcyview_urgentmsg = (RecyclerView) rootView.findViewById(R.id.rcyview_urgentmsg);
        LinearLayoutManager lLManagerUrgentJobs = new LinearLayoutManager(getActivity());
        lLManagerUrgentJobs.setOrientation(LinearLayoutManager.VERTICAL);
        rcyview_urgentmsg.setLayoutManager(lLManagerUrgentJobs);
        adapterurgentmsg = new AdapterDashbrdUrgentMsgDetail(getActivity());
        rcyview_urgentmsg.setAdapter(adapterurgentmsg);
        previouslabor_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  dialogViewComment();
            }
        });
    }

    /*public void OnClickViewComment() {
        dialogViewComment();
    }*/

   /* private void dialogViewComment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialogviewcomment");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = ViewCommentDialog.newInstance(8);
        newFragment.show(ft, "dialogviewcomment");
    }*/
}
