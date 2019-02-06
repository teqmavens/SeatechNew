package teq.development.seatech.PickUpJobs.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;

import teq.development.seatech.PickUpJobs.TechinicianSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.databinding.RowTechnicianBinding;

public class AdapterTechnician extends RecyclerView.Adapter<AdapterTechnician.ViewHolder> {

    ArrayList<TechinicianSkeleton> arrayListTech;
    Context context;

    public AdapterTechnician(Context context, ArrayList<TechinicianSkeleton> arrayListTech) {
        this.context = context;
        this.arrayListTech = arrayListTech;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowTechnicianBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_technician, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bind(position);
        holder.mbinding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                arrayListTech.get(holder.getAdapterPosition()).setStatus(isChecked);
            }
        });
        holder.mbinding.checkbox.setChecked(arrayListTech.get(position).isStatus());
    }

    @Override
    public int getItemCount() {
        return arrayListTech.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowTechnicianBinding mbinding;

        public ViewHolder(RowTechnicianBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(int position) {
            mbinding.executePendingBindings();
            mbinding.setRowtechnician(arrayListTech.get(position));
        }
    }
}
