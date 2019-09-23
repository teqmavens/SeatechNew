package teq.development.seatech.Schedule.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import teq.development.seatech.R;
import teq.development.seatech.Schedule.JobDetailStaticSchedule;
import teq.development.seatech.Schedule.ScheduleParent;
import teq.development.seatech.Schedule.Skeleton.ScheduleCalendarViewSkeleton;
import teq.development.seatech.Schedule.Skeleton.ScheduleDayViewSkeleton;
import teq.development.seatech.Utils.HandyObject;

public class AdapterDayView extends RecyclerView.Adapter<AdapterDayView.ViewHolder> {

    Context c;
    LayoutInflater infalter;
    ScheduleParent activity;
    ArrayList<ScheduleDayViewSkeleton.AllData> arrayListMain;

    public AdapterDayView(Context c,   ArrayList<ScheduleDayViewSkeleton.AllData> arrayListMain) {
        this.c = c;
        activity = (ScheduleParent) c;
        this.arrayListMain = arrayListMain;
        infalter = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public AdapterDayView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_dayview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDayView.ViewHolder holder, final int position) {
        holder.techname.setText(arrayListMain.get(position).tech);
        holder.dynamicll.removeAllViews();
        if (arrayListMain.get(position).event != null) {
            for (int i = 0; i < arrayListMain.get(position).event.size(); i++) {

                if(Integer.parseInt(arrayListMain.get(position).event.get(i).differnce)*50 < 101) {
                    View view = infalter.inflate(R.layout.gridchild_item, holder.dynamicll, false);
                    final LinearLayout llchild = (LinearLayout) view.findViewById(R.id.llchild);
                    final TextView ticketNo = (TextView) llchild.findViewById(R.id.ticketNo);
                    TextView duration = (TextView) llchild.findViewById(R.id.duration);
                    TextView Customername = (TextView) llchild.findViewById(R.id.Customername);
                    final ImageView pdficon = (ImageView) llchild.findViewById(R.id.pdficon);
                    TextView apptype_symbol = (TextView) llchild.findViewById(R.id.apptype_symbol);
                    TextView appconfirm_symbol = (TextView) llchild.findViewById(R.id.appconfirm_symbol);
                    TextView urgent_symbol = (TextView) llchild.findViewById(R.id.urgent_symbol);
                    TextView need_parts = (TextView) llchild.findViewById(R.id.need_parts);
                    TextView have_parts = (TextView) llchild.findViewById(R.id.have_parts);

                    // try{
                    //   llchild.setBackgroundColor(Color.parseColor(parentArrayList.get(position).eventData.get(i).region_color));
                    //   } catch (NumberFormatException e){}

                    try{
                        LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lParam.topMargin = 6;
                        lParam.width = Integer.parseInt(arrayListMain.get(position).event.get(i).differnce)*50;
                        lParam.leftMargin = HandyObject.CheckLeftMarginDay(arrayListMain.get(position).event.get(i).frmt_start_time);
                        llchild.setLayoutParams(lParam);
                    } catch (NumberFormatException e){}

                    llchild.setBackgroundColor(Color.parseColor(arrayListMain.get(position).event.get(i).region_color));
                    ticketNo.setText(arrayListMain.get(position).event.get(i).jobid);
                    duration.setText("- "+ arrayListMain.get(position).event.get(i).duration+" hrs");
                    Customername.setText(arrayListMain.get(position).event.get(i).customer_name);
                    apptype_symbol.setText(arrayListMain.get(position).event.get(i).appointment_type_symbol);
                    appconfirm_symbol.setText(arrayListMain.get(position).event.get(i).appointment_confirm_symbol);
                    urgent_symbol.setText(arrayListMain.get(position).event.get(i).urgent_symbol);

                    if(arrayListMain.get(position).event.get(i).need_parts != null && !arrayListMain.get(position).event.get(i).need_parts.isEmpty()){
                        need_parts.setVisibility(View.VISIBLE);
                    }

                    if(arrayListMain.get(position).event.get(i).have_parts != null && !arrayListMain.get(position).event.get(i).have_parts.isEmpty()){
                        have_parts.setVisibility(View.VISIBLE);
                    }

                    if(arrayListMain.get(position).event.get(i).sales_order.equalsIgnoreCase("")) {
                        pdficon.setVisibility(View.GONE);
                    } else {
                        pdficon.setVisibility(View.VISIBLE);
                    }

                    ticketNo.setId(Integer.parseInt(arrayListMain.get(position).event.get(i).jobid));
                    pdficon.setId(i);
                    llchild.setId(i);
                    ticketNo.setTextColor(Color.parseColor(arrayListMain.get(position).event.get(i).region_text_color));
                    duration.setTextColor(Color.parseColor(arrayListMain.get(position).event.get(i).region_text_color));
                    Customername.setTextColor(Color.parseColor(arrayListMain.get(position).event.get(i).region_text_color));

                    ticketNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JobDetailStaticSchedule jb = new JobDetailStaticSchedule();
                            Bundle bundle = new Bundle();
                            bundle.putInt("position", position);
                            bundle.putString("position_ticketId",String.valueOf(ticketNo.getId()));
                            jb.setArguments(bundle);
                            activity.replaceFragmentWithBack(jb);
                            // HandyObject.showAlert(c, String.valueOf(ticketNo.getId()));
                        }
                    });

                    pdficon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = pdficon.getId();
                            Intent intent = new Intent();
                            intent.setDataAndType(Uri.parse(arrayListMain.get(position).event.get(id).sales_order), "application/pdf");
                            Intent chooserIntent = Intent.createChooser(intent, "Open Report");
                            ((Activity) c).startActivity(chooserIntent);
                        }
                    });

                    llchild.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = llchild.getId();
                            //   HandyObject.showAlert(c,String.valueOf(parentArrayList.get(position).eventData.get(id).jobid));
                            displyPopup(v, arrayListMain.get(position).event.get(id));
                        }
                    });
                    holder.dynamicll.addView(llchild);
                } else {

                    View view = infalter.inflate(R.layout.daychild_item, holder.dynamicll, false);
                    final LinearLayout llchild = (LinearLayout) view.findViewById(R.id.llchild);
                    final TextView ticketNo = (TextView) llchild.findViewById(R.id.ticketNo);
                    TextView duration = (TextView) llchild.findViewById(R.id.duration);
                    TextView Customername = (TextView) llchild.findViewById(R.id.Customername);
                    final ImageView pdficon = (ImageView) llchild.findViewById(R.id.pdficon);
                    TextView apptype_symbol = (TextView) llchild.findViewById(R.id.apptype_symbol);
                    TextView appconfirm_symbol = (TextView) llchild.findViewById(R.id.appconfirm_symbol);
                    TextView urgent_symbol = (TextView) llchild.findViewById(R.id.urgent_symbol);
                    TextView need_parts = (TextView) llchild.findViewById(R.id.need_parts);
                    TextView have_parts = (TextView) llchild.findViewById(R.id.have_parts);

                    // try{
                    //   llchild.setBackgroundColor(Color.parseColor(parentArrayList.get(position).eventData.get(i).region_color));
                    //   } catch (NumberFormatException e){}

                    try{
                        LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lParam.topMargin = 6;
                        lParam.width = Integer.parseInt(arrayListMain.get(position).event.get(i).differnce)*50;
                        lParam.leftMargin = HandyObject.CheckLeftMarginDay(arrayListMain.get(position).event.get(i).frmt_start_time);
                        llchild.setLayoutParams(lParam);
                    } catch (NumberFormatException e){}

                    llchild.setBackgroundColor(Color.parseColor(arrayListMain.get(position).event.get(i).region_color));
                    ticketNo.setText(arrayListMain.get(position).event.get(i).jobid);
                    duration.setText("- "+ arrayListMain.get(position).event.get(i).duration+" hrs");
                    Customername.setText(arrayListMain.get(position).event.get(i).customer_name);
                    apptype_symbol.setText(arrayListMain.get(position).event.get(i).appointment_type_symbol);
                    appconfirm_symbol.setText(arrayListMain.get(position).event.get(i).appointment_confirm_symbol);
                    urgent_symbol.setText(arrayListMain.get(position).event.get(i).urgent_symbol);

                    if(arrayListMain.get(position).event.get(i).need_parts != null && !arrayListMain.get(position).event.get(i).need_parts.isEmpty()){
                        need_parts.setVisibility(View.VISIBLE);
                    }

                    if(arrayListMain.get(position).event.get(i).have_parts != null && !arrayListMain.get(position).event.get(i).have_parts.isEmpty()){
                        have_parts.setVisibility(View.VISIBLE);
                    }

                    if(arrayListMain.get(position).event.get(i).sales_order.equalsIgnoreCase("")) {
                        pdficon.setVisibility(View.GONE);
                    } else {
                        pdficon.setVisibility(View.VISIBLE);
                    }

                    ticketNo.setId(Integer.parseInt(arrayListMain.get(position).event.get(i).jobid));
                    pdficon.setId(i);
                    llchild.setId(i);
                    ticketNo.setTextColor(Color.parseColor(arrayListMain.get(position).event.get(i).region_text_color));
                    duration.setTextColor(Color.parseColor(arrayListMain.get(position).event.get(i).region_text_color));
                    Customername.setTextColor(Color.parseColor(arrayListMain.get(position).event.get(i).region_text_color));

                    ticketNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JobDetailStaticSchedule jb = new JobDetailStaticSchedule();
                            Bundle bundle = new Bundle();
                            bundle.putInt("position", position);
                            bundle.putString("position_ticketId",String.valueOf(ticketNo.getId()));
                            jb.setArguments(bundle);
                            activity.replaceFragmentWithBack(jb);
                            // HandyObject.showAlert(c, String.valueOf(ticketNo.getId()));
                        }
                    });

                    pdficon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = pdficon.getId();
                            Intent intent = new Intent();
                            intent.setDataAndType(Uri.parse(arrayListMain.get(position).event.get(id).sales_order), "application/pdf");
                            Intent chooserIntent = Intent.createChooser(intent, "Open Report");
                            ((Activity) c).startActivity(chooserIntent);
                        }
                    });

                    llchild.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = llchild.getId();
                            //   HandyObject.showAlert(c,String.valueOf(parentArrayList.get(position).eventData.get(id).jobid));
                            displyPopup(v, arrayListMain.get(position).event.get(id));
                        }
                    });
                    holder.dynamicll.addView(llchild);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return arrayListMain.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout dynamicll;
        TextView techname;

        public ViewHolder(View itemView) {
            super(itemView);
            dynamicll = (LinearLayout) itemView.findViewById(R.id.dynamicll);
            techname = (TextView) itemView.findViewById(R.id.techname);
        }
    }

    private void displyPopup(View view, ScheduleDayViewSkeleton.Event ske) {
        final PopupWindow popup = new PopupWindow(c);
        View layout = infalter.inflate(R.layout.popup_menuschedule, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //popup.showAsDropDown(view,0,-280);

        //Show popup based on screen height
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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
