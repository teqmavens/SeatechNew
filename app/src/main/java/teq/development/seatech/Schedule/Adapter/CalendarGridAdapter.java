package teq.development.seatech.Schedule.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import teq.development.seatech.R;
import teq.development.seatech.Schedule.JobDetailStaticSchedule;
import teq.development.seatech.Schedule.ScheduleParent;
import teq.development.seatech.Schedule.Skeleton.ScheduleCalendarViewSkeleton;
import teq.development.seatech.Utils.HandyObject;

public class CalendarGridAdapter extends ArrayAdapter {
    private static final String TAG = CalendarGridAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<Date> monthlyDates;
    private Calendar currentDate;
    private Context context;
    ScheduleParent activity;
    ArrayList<ScheduleCalendarViewSkeleton.AllData> arrayList;

    //   private List<EventObjects> allEvents;
    public CalendarGridAdapter(Context context, List<Date> monthlyDates, Calendar currentDate, ArrayList<ScheduleCalendarViewSkeleton.AllData> arrayList) {
        super(context, R.layout.single_cell_layout);
        activity = (ScheduleParent) context;
        this.monthlyDates = monthlyDates;
        this.currentDate = currentDate;
        this.arrayList = arrayList;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Date mDate = monthlyDates.get(position);
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(mDate);
        int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCal.get(Calendar.MONTH) + 1;
        int displayYear = dateCal.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);
        ViewHolder viewholder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_cell_layout, null);
            viewholder = new ViewHolder();
            viewholder.calendar_date_id = (TextView) convertView
                    .findViewById(R.id.calendar_date_id);
            viewholder.more = (TextView) convertView.findViewById(R.id.more);
            viewholder.dynamicll = (LinearLayout) convertView.findViewById(R.id.dynamicll);
            // viewholder.recyclerviewChild = (RecyclerView) convertView.findViewById(R.id.recyclerviewChild);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        viewholder.calendar_date_id.setText(String.valueOf(dayValue));
        if (displayMonth == currentMonth && displayYear == currentYear) {
            viewholder.calendar_date_id.setTextColor(Color.parseColor("#000000"));
        } else {
            viewholder.calendar_date_id.setTextColor(Color.parseColor("#cccccc"));
        }

        View vieww;
        viewholder.dynamicll.removeAllViews();
        if (arrayList != null && arrayList.size() > 0) {
            if (arrayList.get(position).scheduled.size() > 0) {
                for (int i = 0; i < arrayList.get(position).scheduled.size(); i++) {
                    if (i < 2 ) {

                        if(arrayList.get(position).scheduled.get(i).jobid != null && !arrayList.get(position).scheduled.get(i).jobid.isEmpty()) {
                            vieww = mInflater.inflate(R.layout.gridchild_item, viewholder.dynamicll, false);
                            final LinearLayout llchild = (LinearLayout) vieww.findViewById(R.id.llchild);
                            final TextView ticketNo = (TextView) llchild.findViewById(R.id.ticketNo);
                            TextView duration = (TextView) llchild.findViewById(R.id.duration);
                            TextView Customername = (TextView) llchild.findViewById(R.id.Customername);
                            final ImageView pdficon = (ImageView) llchild.findViewById(R.id.pdficon);
                            TextView apptype_symbol = (TextView) llchild.findViewById(R.id.apptype_symbol);
                            TextView appconfirm_symbol = (TextView) llchild.findViewById(R.id.appconfirm_symbol);
                            TextView urgent_symbol = (TextView) llchild.findViewById(R.id.urgent_symbol);
                            TextView need_parts = (TextView) llchild.findViewById(R.id.need_parts);

                            llchild.setBackgroundColor(Color.parseColor(arrayList.get(position).scheduled.get(i).region_color));
                            ticketNo.setText(arrayList.get(position).scheduled.get(i).jobid);
                            duration.setText(arrayList.get(position).scheduled.get(i).duration+" hrs");
                            Customername.setText(arrayList.get(position).scheduled.get(i).customer_name);
                            apptype_symbol.setText(arrayList.get(position).scheduled.get(i).appointment_type_symbol);
                            appconfirm_symbol.setText(arrayList.get(position).scheduled.get(i).appointment_confirm_symbol);
                            urgent_symbol.setText(arrayList.get(position).scheduled.get(i).urgent_symbol);

                            if(arrayList.get(position).scheduled.get(i).need_parts != null && !arrayList.get(position).scheduled.get(i).need_parts.isEmpty()){
                                need_parts.setVisibility(View.VISIBLE);
                            }

                            ticketNo.setTextColor(Color.parseColor(arrayList.get(position).scheduled.get(i).region_text_color));
                            duration.setTextColor(Color.parseColor(arrayList.get(position).scheduled.get(i).region_text_color));
                            Customername.setTextColor(Color.parseColor(arrayList.get(position).scheduled.get(i).region_text_color));
                            ticketNo.setId(Integer.parseInt(arrayList.get(position).scheduled.get(i).jobid));
                            pdficon.setId(i);
                            llchild.setId(i);

                            if (arrayList.get(position).scheduled.get(i).sales_order != null && !arrayList.get(position).scheduled.get(i).sales_order.isEmpty()) {
                                pdficon.setVisibility(View.VISIBLE);
                            } else {
                                pdficon.setVisibility(View.GONE);
                            }

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
                                    intent.setDataAndType(Uri.parse(arrayList.get(position).scheduled.get(id).sales_order), "application/pdf");
                                    Intent chooserIntent = Intent.createChooser(intent, "Open Report");
                                    ((Activity) context).startActivity(chooserIntent);
                                }
                            });
                            // pdficon.setVisibility(View.VISIBLE);
                            llchild.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int id = llchild.getId();
                                    //   HandyObject.showAlert(c,String.valueOf(parentArrayList.get(position).eventData.get(id).jobid));
                                    displyPopup(v, arrayList.get(position).scheduled.get(id));
                                }
                            });
                            viewholder.dynamicll.addView(llchild);
                        }

                    }
                }
                if (arrayList.get(position).scheduled.size() > 2) {
                    viewholder.more.setVisibility(View.VISIBLE);
                    viewholder.more.setText("+" + String.valueOf(arrayList.get(position).scheduled.size() - 2) + "More");
                }

            } else {
                viewholder.more.setVisibility(View.GONE);
            }
        }
        viewholder.more.setTag(position);
        viewholder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posi = (Integer) v.getTag();
                displyPopupMore(v, arrayList.get(position).scheduled, arrayList.get(position).date);
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return monthlyDates.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return monthlyDates.get(position);
    }

    @Override
    public int getPosition(Object item) {
        return monthlyDates.indexOf(item);
    }

    public class ViewHolder {
        TextView calendar_date_id, more;
        LinearLayout dynamicll;
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

    private void displyPopupMore(View view, ArrayList<ScheduleCalendarViewSkeleton.Scheduled> arraylistShe, String seldate) {
        final PopupWindow popup = new PopupWindow(context);
        View layout = mInflater.inflate(R.layout.popup_calendarview, null);
        popup.setContentView(layout);
        //  popup.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.dashboardlinebg));
        //popup.showAsDropDown(view,0,-280);

        //Show popup based on screen height
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        if (height - location[1] < 220) {
            popup.showAsDropDown(view, 0, -260);
        }
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // TextView jobticketvalue = (TextView) layout.findViewById(R.id.jobticketvalue);
        RecyclerView recyclerview = (RecyclerView) layout.findViewById(R.id.recyclerview);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        TextView day = (TextView) layout.findViewById(R.id.day);
        CalendarPopupAdapter adapter = new CalendarPopupAdapter(context, arraylistShe,view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(adapter);

        //  jobticketvalue.setText(arraylistShe.get(0).jobid);
        day.setText(HandyObject.parseDateToDMSCalendar(seldate));

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
