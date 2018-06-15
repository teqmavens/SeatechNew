package teq.development.seatech.JobDetail;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import teq.development.seatech.JobDetail.Adapter.AdapterViewComment;
import teq.development.seatech.R;
import teq.development.seatech.databinding.PopupNeedchangeorderBinding;

public class ViewCommentDialog extends DialogFragment {
    Dialog dialog;

    AdapterViewComment adapter;

    public static ViewCommentDialog newInstance(int num) {
        ViewCommentDialog f = new ViewCommentDialog();
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_viewcomment,container,false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        RecyclerView recyclerview = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        ImageView cross = (ImageView) rootView.findViewById(R.id.cross);
        LinearLayoutManager lLManagerUrgentJobs = new LinearLayoutManager(getActivity());
        lLManagerUrgentJobs.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(lLManagerUrgentJobs);
        adapter = new AdapterViewComment(getActivity());
        recyclerview.setAdapter(adapter);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
}
