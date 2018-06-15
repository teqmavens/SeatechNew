package teq.development.seatech.JobDetail;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import teq.development.seatech.R;
import teq.development.seatech.databinding.PopupNeedchangeorderBinding;

public class UploadImageDialog extends DialogFragment implements View.OnClickListener{

    Dialog dialog;

    static UploadImageDialog newInstance(int num) {
        UploadImageDialog f = new UploadImageDialog();
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_uploadimage,container,false);
      /*  PopupNeedchangeorderBinding binding = DataBindingUtil.bind(rootView);
        binding.setPopupneedchangeorder(this);*/
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = width-420;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
    private void initViews(View rootView) {
        Button submit = (Button) rootView.findViewById(R.id.submit);
        ImageView cross = (ImageView) rootView.findViewById(R.id.cross);
        submit.setOnClickListener(this);
        cross.setOnClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    public void onClickCross() {
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                dialog.dismiss();
                break;
            case R.id.cross:
                dialog.dismiss();
                break;
        }
    }
}

