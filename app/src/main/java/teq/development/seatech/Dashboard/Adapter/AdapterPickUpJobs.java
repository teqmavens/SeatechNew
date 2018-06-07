package teq.development.seatech.Dashboard.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import teq.development.seatech.R;
import teq.development.seatech.databinding.RowPickupjobsBinding;

public class AdapterPickUpJobs extends RecyclerView.Adapter<AdapterPickUpJobs.ViewHolder> {


    Context context;

    public AdapterPickUpJobs(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowPickupjobsBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_pickupjobs,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(RowPickupjobsBinding binding) {
            super(binding.getRoot());
        }
    }
}
