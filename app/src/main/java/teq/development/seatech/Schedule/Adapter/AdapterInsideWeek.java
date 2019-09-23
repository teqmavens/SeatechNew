package teq.development.seatech.Schedule.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import teq.development.seatech.R;
import teq.development.seatech.Schedule.JobDetailStaticSchedule;
import teq.development.seatech.Schedule.ScheduleParent;
import teq.development.seatech.Schedule.Skeleton.ScheduleWeekViewSkeleton;
import teq.development.seatech.databinding.RowInsideweekBinding;

public class AdapterInsideWeek extends RecyclerView.Adapter<AdapterInsideWeek.ViewHolder> {

    ArrayList<ScheduleWeekViewSkeleton.Scheduled> arrayList;
    ScheduleParent activity;
    Context c;

    AdapterInsideWeek(ArrayList<ScheduleWeekViewSkeleton.Scheduled> arrayList, Context c) {
        this.c = c;
        this.arrayList = arrayList;
        activity = (ScheduleParent) c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowInsideweekBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_insideweek, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowInsideweekBinding mbinding;

        public ViewHolder(RowInsideweekBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(final ScheduleWeekViewSkeleton.Scheduled scheduled) {
            //   mbinding.set
            //  mbinding.regionimg
            if (scheduled.need_parts != null && !scheduled.need_parts.isEmpty()) {
                mbinding.needpart.setImageResource(R.drawable.greencircle);
            } else {
                mbinding.needpart.setImageResource(R.drawable.transcircle);
            }
            mbinding.regionimg.setBackgroundResource(R.drawable.region_circle);
            mbinding.jobid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JobDetailStaticSchedule jb = new JobDetailStaticSchedule();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", 0);
                    bundle.putString("position_ticketId",scheduled.jobid);
                    jb.setArguments(bundle);
                    activity.replaceFragmentWithBack(jb);
                }
            });
            GradientDrawable bgShape = (GradientDrawable) mbinding.regionimg.getBackground();
            bgShape.setColor(Color.parseColor(scheduled.region_color));
            mbinding.setRowinsideweek(scheduled);
            mbinding.executePendingBindings();
        }
    }
}
