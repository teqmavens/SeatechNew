package teq.development.seatech.Profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.R;
import teq.development.seatech.databinding.FrgmMyprofileBinding;

public class MyProfileFragment extends Fragment {

    DashBoardActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DashBoardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_myprofile,container,false);
        FrgmMyprofileBinding binding = DataBindingUtil.bind(rootView);
        binding.setMyprofile(this);
        return rootView;
    }

    public void OnClickEdit(){
        activity.replaceFragment(new EditProfileFragment());
    }
}
