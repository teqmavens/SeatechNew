package teq.development.seatech.JobDetail.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import teq.development.seatech.Dashboard.Skeleton.PartsSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.RowpartsBinding;

public class AdapterParts extends RecyclerView.Adapter<AdapterParts.ViewHolder> {

    Context context;
    ArrayList<PartsSkeleton> arrayListParts;

    public AdapterParts(Context context, ArrayList<PartsSkeleton> arrayListParts) {
        this.context = context;
        this.arrayListParts = arrayListParts;
    }

    @NonNull
    @Override
    public AdapterParts.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowpartsBinding binding = DataBindingUtil.inflate(inflater, R.layout.rowparts, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterParts.ViewHolder holder, int position) {
        holder.bind(arrayListParts.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayListParts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowpartsBinding mbinding;

        public ViewHolder(RowpartsBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(PartsSkeleton partsSkeleton) {
            mbinding.setRowparts(partsSkeleton);
            mbinding.executePendingBindings();
        }
    }
}
