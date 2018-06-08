package teq.development.seatech.JobDetail;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.PopupWindow;

import teq.development.seatech.R;
import teq.development.seatech.databinding.DialogJobstatusBinding;

public class JobStatusDialog extends DialogFragment{

    static JobStatusDialog newInstance(int num) {
        JobStatusDialog f = new JobStatusDialog();
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       int mNum = getArguments().getInt("num");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.dialog_jobstatus,container,false);
        DialogJobstatusBinding binding = DataBindingUtil.bind(rootView);
        initViews(binding);
       return rootView;
    }

    private void initViews(final DialogJobstatusBinding binding) {
        binding.checkboxyes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    displayPopupwindow(binding.checkboxyes);
                }
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = width-400;
        params.height = height-400;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private void displayPopupwindow(View anchorview) {
        PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater().inflate(R.layout.popupwd_jcyes, null);
        popup.setContentView(layout);
        /*DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int density = dm.densityDpi;
        if (density == DisplayMetrics.DENSITY_HIGH) {
            popup.setHeight(70);
            popup.setWidth(200);
        } else if (density == DisplayMetrics.DENSITY_MEDIUM) {

            popup.setHeight(90);
            popup.setWidth(250);
            //  Toast.makeText(Menuu.this, "DENSITY_MEDIUM... Density is " + String.valueOf(density), Toast.LENGTH_LONG).show();
        } else if (density == DisplayMetrics.DENSITY_LOW) {
            popup.setHeight(60);
            popup.setWidth(140);
            //   Toast.makeText(Menuu.this, "DENSITY_LOW... Density is " + String.valueOf(density), Toast.LENGTH_LONG).show();
        } else if (density == DisplayMetrics.DENSITY_XHIGH) {
            popup.setHeight(100);
            popup.setWidth(270);
            //  Toast.makeText(Menuu.this, "DENSITY_XHIGH... Density is " + String.valueOf(density), Toast.LENGTH_LONG).show();
        } else if (density == DisplayMetrics.DENSITY_XXHIGH) {
            popup.setHeight(130);
            popup.setWidth(320);
            // Toast.makeText(Menuu.this, "DENSITY_XXHIGH... Density is " + String.valueOf(density), Toast.LENGTH_LONG).show();
        } else if (density == DisplayMetrics.DENSITY_XXXHIGH) {
            popup.setHeight(140);
            popup.setWidth(370);
            //  Toast.makeText(Menuu.this, "DENSITY_XXHIGH... Density is " + String.valueOf(density), Toast.LENGTH_LONG).show();
        } else {
        }*/
       // RelativeLayout rl_company = (RelativeLayout) layout.findViewById(R.id.rl_company);



        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }
}
