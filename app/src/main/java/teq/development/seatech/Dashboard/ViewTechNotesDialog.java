package teq.development.seatech.Dashboard;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.JobDetail.Adapter.AdapterDashboardNotes;
import teq.development.seatech.JobDetail.AddTechNotesDialog;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.database.ParseOpenHelper;

public class ViewTechNotesDialog extends DialogFragment implements View.OnClickListener {

    Dialog dialog;
    SQLiteDatabase database;
    Gson gson;
    ArrayList<AllJobsSkeleton> arralistAllJobs;
    static int position;

    static ViewTechNotesDialog newInstance(int num) {
        ViewTechNotesDialog f = new ViewTechNotesDialog();
        Bundle args = new Bundle();
        //  args.putInt("num", num);
        position = num;
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_viewtechnotes, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        ImageView cross = (ImageView) rootView.findViewById(R.id.cross);
        RecyclerView rcyview_dashbrdnotes = (RecyclerView) rootView.findViewById(R.id.rcyview_dashbrdnotes);
        cross.setOnClickListener(this);

        database = ParseOpenHelper.getInstance(getActivity()).getWritableDatabase();
        gson = new Gson();
        Cursor cursor = database.query(ParseOpenHelper.TABLENAME_ALLJOBS, null, ParseOpenHelper.TECHID + "=?", new String[]{HandyObject.getPrams(getActivity(), AppConstants.LOGINTEQ_ID)}, null, null, null);
        cursor.moveToFirst();
        arralistAllJobs = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            Type type = new TypeToken<AllJobsSkeleton>() {
            }.getType();
            Type typedashnotes = new TypeToken<ArrayList<DashboardNotes_Skeleton>>() {
            }.getType();
            String getSke = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSSKELETON));
            String getSkedashnotes = cursor.getString(cursor.getColumnIndex(ParseOpenHelper.JOBSTECHDASHBOARDNOTES));

            AllJobsSkeleton ske = gson.fromJson(getSke, type);
            ArrayList<DashboardNotes_Skeleton> arrayListDash = gson.fromJson(getSkedashnotes, typedashnotes);
            ske.setArrayList(arrayListDash);
            arralistAllJobs.add(ske);
            //     arrayListDashNotes.addAll(arrayListDash);
            cursor.moveToNext();
        }
        cursor.close();
        LinearLayoutManager lLManagerDashNotes = new LinearLayoutManager(getActivity());
        lLManagerDashNotes.setOrientation(LinearLayoutManager.VERTICAL);
        rcyview_dashbrdnotes.setLayoutManager(lLManagerDashNotes);
        AdapterDashboardNotes adapterdashnotes = new AdapterDashboardNotes(getActivity(), arralistAllJobs.get(position).getArrayList());
        rcyview_dashbrdnotes.setNestedScrollingEnabled(false);
        rcyview_dashbrdnotes.setAdapter(adapterdashnotes);
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
        // params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross:
                dialog.dismiss();
                break;
        }
    }
}
