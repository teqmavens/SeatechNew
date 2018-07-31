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
import teq.development.seatech.databinding.RowViewcommentBinding;

public class AdapterViewComment extends RecyclerView.Adapter<AdapterViewComment.ViewHolder> {

    Context context;
    ArrayList<DashboardNotes_Skeleton> arrayListLaborPerform;

    public AdapterViewComment(Context context, ArrayList<DashboardNotes_Skeleton> arrayListLaborPerform) {
        this.context = context;
        this.arrayListLaborPerform = arrayListLaborPerform;
    }

    @NonNull
    @Override
    public AdapterViewComment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowViewcommentBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_viewcomment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewComment.ViewHolder holder, int position) {
        holder.bind(arrayListLaborPerform.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayListLaborPerform.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowViewcommentBinding mbinding;

        public ViewHolder(RowViewcommentBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(DashboardNotes_Skeleton dashboardNotes_skeleton) {
            mbinding.setRowviewcomment(dashboardNotes_skeleton);
            mbinding.executePendingBindings();
        }
    }
}
