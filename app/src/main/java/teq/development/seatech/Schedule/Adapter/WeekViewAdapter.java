package teq.development.seatech.Schedule.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;
import teq.development.seatech.R;

public class WeekViewAdapter extends RecyclerView.Adapter<WeekViewAdapter.ViewHolder> {
    Context c;
    ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayListWeekView;

    public WeekViewAdapter(Context c,ArrayList<ScheduleFilterSkeleton.SchedulesData> arrayListWeekView){
        this.c = c;
        this.arrayListWeekView = arrayListWeekView;
    }

    @NonNull
    @Override
    public WeekViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.weekview_item, parent, false);
        return new WeekViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewAdapter.ViewHolder holder, int position) {
           holder.day.setText(arrayListWeekView.get(position).day);
           holder.date.setText(arrayListWeekView.get(position).date);
    }

    @Override
    public int getItemCount() {
        return arrayListWeekView.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView day,date;
        public ViewHolder(View itemView) {
            super(itemView);
            day = (TextView) itemView.findViewById(R.id.day);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
