package teq.development.seatech.Timesheet.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import teq.development.seatech.R;
import teq.development.seatech.Timesheet.DayJobStatusDetailFragment;
import teq.development.seatech.Timesheet.DayJobStatus_Skeleton;
import teq.development.seatech.databinding.RowDayjobstatusBinding;

public class AdapterDayJobStatus extends RecyclerView.Adapter<AdapterDayJobStatus.ViewHolder> {

    Context context;
    ArrayList<DayJobStatus_Skeleton> arrayList;
    Fragment frgm;

    public AdapterDayJobStatus(Context context, ArrayList<DayJobStatus_Skeleton> arrayList, Fragment frgm) {
        this.context = context;
        this.arrayList = arrayList;
        this.frgm = frgm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowDayjobstatusBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_dayjobstatus, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowDayjobstatusBinding mbinding;

        public ViewHolder(RowDayjobstatusBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(final int position) {
            mbinding.setDayjobskeleton(arrayList.get(position));
            mbinding.getRoot().findViewById(R.id.jobid).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DayJobStatusDetailFragment) frgm).onClickTicketNo(position);
                }
            });
            mbinding.executePendingBindings();
        }
    }
}
