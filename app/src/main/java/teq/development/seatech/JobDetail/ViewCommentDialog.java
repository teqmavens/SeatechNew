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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.JobDetail.Adapter.AdapterViewComment;
import teq.development.seatech.R;
import teq.development.seatech.databinding.PopupNeedchangeorderBinding;

public class ViewCommentDialog extends DialogFragment {
    Dialog dialog;

    AdapterViewComment adapter;
    static ArrayList<DashboardNotes_Skeleton> arrayListLaborPerform;
    static String typeDialog;

    public static ViewCommentDialog newInstance(ArrayList<DashboardNotes_Skeleton> arrayListLaborPerf,String type) {
        ViewCommentDialog f = new ViewCommentDialog();
        Bundle args = new Bundle();
        arrayListLaborPerform = arrayListLaborPerf;
        typeDialog = type;
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_viewcomment, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        RecyclerView recyclerview = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        ImageView cross = (ImageView) rootView.findViewById(R.id.cross);
        TextView toptext = (TextView) rootView.findViewById(R.id.toptext);
        TextView norecord = (TextView) rootView.findViewById(R.id.norecord);
        LinearLayout lltoprecycler = (LinearLayout) rootView.findViewById(R.id.lltoprecycler);
        LinearLayoutManager lLManagerUrgentJobs = new LinearLayoutManager(getActivity());
        lLManagerUrgentJobs.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(lLManagerUrgentJobs);

        if(arrayListLaborPerform.size() == 0) {
            lltoprecycler.setVisibility(View.GONE);
            recyclerview.setVisibility(View.GONE);
            norecord.setVisibility(View.VISIBLE);
            if(typeDialog.equalsIgnoreCase("viewcommment")) {
                norecord.setText(getString(R.string.noprevious_lbrentries));
            } else {
                norecord.setText(getString(R.string.noprevious_offthereacord));
            }
        } else {
            lltoprecycler.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.VISIBLE);
            norecord.setVisibility(View.GONE);
            adapter = new AdapterViewComment(getActivity(),arrayListLaborPerform);
            recyclerview.setAdapter(adapter);
        }

        if(typeDialog.equalsIgnoreCase("viewcommment")) {
            toptext.setText(getString(R.string.previouslaborcmmnt));
        } else {
            toptext.setText(getString(R.string.viewprev_offrecordnote));
        }

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
        params.width = width - 220;
    //    params.height = ViewGroup.LayoutParams.WRAP_CONTENT-300;
        params.height = height - 320;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    public void onClickCross() {
        dialog.dismiss();
    }
}
