package teq.development.seatech.Schedule.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import teq.development.seatech.R;
import teq.development.seatech.Schedule.JobDetailStaticSchedule;
import teq.development.seatech.Schedule.ScheduleParent;
import teq.development.seatech.Schedule.Skeleton.ScheduleCalendarViewSkeleton;
import teq.development.seatech.databinding.RowPopupcalendarBinding;

public class CalendarPopupAdapter extends RecyclerView.Adapter<CalendarPopupAdapter.ViewHolder> {

    ArrayList<ScheduleCalendarViewSkeleton.Scheduled> arraylistShe;
    Context context;
    ScheduleParent activity;
    private LayoutInflater mInflater;
    View mview;

    CalendarPopupAdapter(Context context,ArrayList<ScheduleCalendarViewSkeleton.Scheduled> arraylistShe,View mview) {
        activity = (ScheduleParent) context;
        this.context = context;
        this.arraylistShe = arraylistShe;
        this.mview = mview;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowPopupcalendarBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_popupcalendar,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           holder.bind(arraylistShe.get(position));
    }

    @Override
    public int getItemCount() {
        return arraylistShe.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowPopupcalendarBinding mbinding;

        public ViewHolder(RowPopupcalendarBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(final ScheduleCalendarViewSkeleton.Scheduled scheduled) {
            LinearLayout llchild = (LinearLayout) mbinding.getRoot().findViewById(R.id.llchild);
      //      mbinding.llchild.setBackgroundColor(Color.parseColor(scheduled.region_color));
            llchild.setBackgroundColor(Color.parseColor(scheduled.region_color));
            mbinding.ticketNo.setTextColor(Color.parseColor(scheduled.region_text_color));
            mbinding.duration.setTextColor(Color.parseColor(scheduled.region_text_color));
            mbinding.Customername.setTextColor(Color.parseColor(scheduled.region_text_color));
            mbinding.apptypeSymbol.setText(scheduled.appointment_type_symbol);
            mbinding.appconfirmSymbol.setText(scheduled.appointment_confirm_symbol);
            mbinding.urgentSymbol.setText(scheduled.urgent_symbol);

            if(scheduled.need_parts.equalsIgnoreCase("1")){
                mbinding.needParts.setVisibility(View.VISIBLE);
            }
            if(scheduled.sales_order.equalsIgnoreCase("")) {
                mbinding.pdficon.setVisibility(View.GONE);
            } else {
                mbinding.pdficon.setVisibility(View.VISIBLE);
            }

           llchild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displyPopup(mview, scheduled);
                }
            });

            mbinding.ticketNo.setTag(scheduled);

            mbinding.ticketNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ScheduleCalendarViewSkeleton.Scheduled schedulede = (ScheduleCalendarViewSkeleton.Scheduled) v.getTag();
//                    JobDetailStaticSchedule jb = new JobDetailStaticSchedule();
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("position", 0);
//                    bundle.putString("position_ticketId",String.valueOf(schedulede.jobid));
//                    jb.setArguments(bundle);
//                    activity.replaceFragmentWithBack(jb);
                }
            });

            mbinding.pdficon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setDataAndType(Uri.parse(scheduled.sales_order), "application/pdf");
                    Intent chooserIntent = Intent.createChooser(intent, "Open Report");
                    ((Activity) context).startActivity(chooserIntent);
                }
            });
            mbinding.setRowpopupcalendar(scheduled);
            mbinding.executePendingBindings();
        }
    }

    private void displyPopup(View view, ScheduleCalendarViewSkeleton.Scheduled ske) {
        final PopupWindow popup = new PopupWindow(context);
        View layout = mInflater.inflate(R.layout.popup_menuschedule, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //popup.showAsDropDown(view,0,-280);

        //Show popup based on screen height
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        if(height-location[1]<220){
            popup.showAsDropDown(view,0,-260);
        }

        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView jobticketvalue = (TextView) layout.findViewById(R.id.jobticketvalue);
        TextView customervalue = (TextView) layout.findViewById(R.id.customervalue);
        TextView startdatetimevalue = (TextView) layout.findViewById(R.id.startdatetimevalue);
        TextView durationvalue = (TextView) layout.findViewById(R.id.durationvalue);
        TextView techniciansvalue = (TextView) layout.findViewById(R.id.techniciansvalue);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);

        jobticketvalue.setText(ske.jobid);
        customervalue.setText(ske.customer_name);
        startdatetimevalue.setText(ske.start_date_time);
        durationvalue.setText(ske.duration+" hrs");
        //techniciansvalue.setText(ske.other_members);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
//        popup.showAtLocation(view, Gravity.BOTTOM, 0,
//                view.getBottom() - 60);
        popup.showAsDropDown(view);
    }
}
