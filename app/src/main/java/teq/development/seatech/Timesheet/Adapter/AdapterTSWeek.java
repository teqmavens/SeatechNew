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
import teq.development.seatech.Timesheet.Skeleton.TSWeekSkeleton;
import teq.development.seatech.Timesheet.TSWeekChildFragment;
import teq.development.seatech.databinding.RowTsmonthBinding;
import teq.development.seatech.databinding.RowTsweekBinding;

public class AdapterTSWeek extends RecyclerView.Adapter<AdapterTSWeek.ViewHolder> {

    ArrayList<TSWeekSkeleton> arrayList;
    Fragment fragment;
    Context context;

    public AdapterTSWeek(Context context, ArrayList<TSWeekSkeleton> arrayList, Fragment fragment) {
        this.context = context;
        this.arrayList = arrayList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public AdapterTSWeek.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowTsweekBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_tsweek, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTSWeek.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowTsweekBinding mbinding;

        public ViewHolder(RowTsweekBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(final int position) {
            mbinding.setRowtsweek(arrayList.get(position));
         //   mbinding.weekdate.setText("Week Ending " + arrayList.get(position).getEnd_date());
            mbinding.getRoot().findViewById(R.id.date).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TSWeekChildFragment) fragment).OnClickDate(arrayList.get(position).getDate());
                }
            });
            mbinding.executePendingBindings();
        }
    }
}
