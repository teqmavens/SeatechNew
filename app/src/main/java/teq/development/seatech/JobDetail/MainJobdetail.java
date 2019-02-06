package teq.development.seatech.JobDetail;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;

public class MainJobdetail extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_mainjobdetail, container, false);


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(fromJobDetail,
                new IntentFilter("fromJobDetail"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(fromJobNewDetail,
                new IntentFilter("fromJobNewDetail"));
        if (HandyObject.getPrams(getActivity(), AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
            replaceChildFragment(new JobDetailFragment());
        } /*else if (HandyObject.getPrams(getActivity(), AppConstants.ISJOB_NEWTYPE).equalsIgnoreCase("yes")) {
            replaceChildFragment(new NewJobDetailFrgament());
        } */else {
            replaceChildFragment(new JobDetailFragment());
        }
        return rootView;
    }

    private void replaceChildFragment(Fragment mFragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.childcontainerjob, mFragment).addToBackStack(null)
                .commit();
    }

    private BroadcastReceiver fromJobDetail = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NewJobDetailFrgament frgm = new NewJobDetailFrgament();
            Bundle bundle = new Bundle();
            bundle.putString("posi", intent.getStringExtra("position"));
            frgm.setArguments(bundle);
            replaceChildFragment(frgm);
        }
    };

    private BroadcastReceiver fromJobNewDetail = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JobDetailFragment frgm = new JobDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("posinew", intent.getStringExtra("positionnew"));
            frgm.setArguments(bundle);
            Activity activity = getActivity();
            if (isAdded() && activity != null) {
                replaceChildFragment(frgm);
            } else {
               // HandyObject.showAlert(getActivity(), "Unable to switch");
            }

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(fromJobDetail);
    }
}
