package teq.development.seatech.JobDetail.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import teq.development.seatech.Dashboard.Skeleton.TimeSpentSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.databinding.RowtimespentBinding;

public class AdapterTimeSpent extends RecyclerView.Adapter<AdapterTimeSpent.ViewAdapter> {

    ArrayList<TimeSpentSkeleton> arrayList;
    Context context;

    public AdapterTimeSpent(ArrayList<TimeSpentSkeleton> arrayList,Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterTimeSpent.ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowtimespentBinding binding = DataBindingUtil.inflate(inflater, R.layout.rowtimespent,parent,false);
        return new ViewAdapter(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTimeSpent.ViewAdapter holder, int position) {
          holder.bind(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewAdapter extends RecyclerView.ViewHolder {
        RowtimespentBinding mbinding;

        public ViewAdapter(RowtimespentBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(TimeSpentSkeleton timeSpentSkeleton) {
            mbinding.setRowtimespent(timeSpentSkeleton);
            mbinding.executePendingBindings();
        }
    }
}
