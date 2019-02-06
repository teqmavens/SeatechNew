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
import teq.development.seatech.Timesheet.Skeleton.TSMonthSkeleton;
import teq.development.seatech.Timesheet.TSMonthChildFragment;
import teq.development.seatech.databinding.RowTsmonthBinding;

public class AdapterTSMonth extends RecyclerView.Adapter<AdapterTSMonth.ViewHolder> {

    ArrayList<TSMonthSkeleton> arrayList;
    Fragment fragment;
    Context context;

    public AdapterTSMonth(Context context, ArrayList<TSMonthSkeleton> arrayList, Fragment fragment) {
        this.context = context;
        this.arrayList = arrayList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowTsmonthBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_tsmonth, parent, false);
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

        RowTsmonthBinding mbinding;

        public ViewHolder(RowTsmonthBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(final int position) {
            mbinding.setRowtsmonth(arrayList.get(position));
            mbinding.weekdate.setText("Week Ending " + arrayList.get(position).getEnd_date());
            mbinding.getRoot().findViewById(R.id.viewdetail).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TSMonthChildFragment) fragment).onClickViewDetail(position);
                }
            });
            mbinding.executePendingBindings();
        }
    }
}
