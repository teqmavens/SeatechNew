package teq.development.seatech.JobDetail.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.R;
import teq.development.seatech.databinding.RowDetaildashnotesBinding;

public class AdapterDashboardNotes extends RecyclerView.Adapter<AdapterDashboardNotes.ViewHolder> {

    Context context;
    ArrayList<DashboardNotes_Skeleton> arrayListDashNotes;


    public AdapterDashboardNotes(Context context, ArrayList<DashboardNotes_Skeleton> arrayListDashNotes) {
        this.context = context;
        this.arrayListDashNotes = arrayListDashNotes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowDetaildashnotesBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_detaildashnotes, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(arrayListDashNotes.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayListDashNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowDetaildashnotesBinding mbinding;

        public ViewHolder(RowDetaildashnotesBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(DashboardNotes_Skeleton dashboardNotes_skeleton) {
            mbinding.setRowdetaildashnotes(dashboardNotes_skeleton);
            mbinding.executePendingBindings();
        }
    }
}
