package teq.development.seatech.Chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import teq.development.seatech.R;

public class ChatMainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_chatmain, container, false);
        getActivity()
                .getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
       // setLeftView(new ChatLeftFragment());
      //  setRightView(new ChatRightFragment());
        return rootView;
    }

   /* private void setLeftView(Fragment fragment) {
        FragmentManager fmleft = getChildFragmentManager();
        fmleft.beginTransaction().replace(R.id.containerLeft, fragment).commit();
    }

    private void setRightView(Fragment fragment) {
        FragmentManager fmright = getChildFragmentManager();
        fmright.beginTransaction().replace(R.id.containerRight, fragment).commit();
    }*/
}