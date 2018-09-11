package teq.development.seatech.Dashboard.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import teq.development.seatech.Dashboard.DashBoardFragment;
import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.databinding.RowJobsforyouBinding;

public class AdapterJosbForYou extends RecyclerView.Adapter<AdapterJosbForYou.ViewHolder> {

    Context context;
    RowJobsforyouBinding binding;
    ArrayList<AllJobsSkeleton> jobsArrayList;
    Fragment fragment;

    public AdapterJosbForYou(Context context, ArrayList<AllJobsSkeleton> jobsArrayList, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
        this.jobsArrayList = jobsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.row_jobsforyou, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.bind(jobsArrayList.get(position));

        binding.getRoot().findViewById(R.id.jobticketno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashBoardFragment) fragment).onClickTicketNo(position);
            }
        });

        binding.getRoot().findViewById(R.id.notesimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashBoardFragment) fragment).onClickNotes(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowJobsforyouBinding mbinding;

        public ViewHolder(RowJobsforyouBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(AllJobsSkeleton ske) {
            if (ske.getNeedpart().equalsIgnoreCase("yes")) {
                binding.needpart.setImageResource(R.drawable.greencircle);
            } else {
                binding.needpart.setImageResource(R.drawable.transcircle);
            }

            if (ske.getHavepart().toLowerCase().equalsIgnoreCase("yes")) {
                binding.havpart.setImageResource(R.drawable.greencircle);
            } else {
                binding.havpart.setImageResource(R.drawable.transcircle);
            }
            mbinding.setRowjobsforyou(ske);
            mbinding.executePendingBindings();
        }
    }
}
