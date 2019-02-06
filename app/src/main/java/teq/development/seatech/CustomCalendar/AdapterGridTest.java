package teq.development.seatech.CustomCalendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;
import teq.development.seatech.Profile.MyProfileFragment;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;

public class AdapterGridTest extends BaseAdapter implements Filterable {
    private LayoutInflater infalter;
    Context c;
    // ArrayList<CalTimeline_Skeleton> parentArrayList;
    ArrayList<ScheduleFilterSkeleton.SchedulesData> parentArrayList;
    AdapterChildGrid adapter;
    SimpleDateFormat df;

//    public AdapterGridTest(Context c, ArrayList<CalTimeline_Skeleton> parentArrayList) {
//        this.c = c;
//        this.parentArrayList = parentArrayList;
//        infalter = (LayoutInflater) c
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        //ScheduleFilterSkeleton.SchedulesData
//    }

    public AdapterGridTest(Context c, ArrayList<ScheduleFilterSkeleton.SchedulesData> parentArrayList) {
        this.c = c;
        this.parentArrayList = parentArrayList;
        infalter = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        df = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public int getCount() {
        return parentArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.gridtest_item, null);
            viewholder = new ViewHolder();
            viewholder.text = (TextView) convertView
                    .findViewById(R.id.text);
            viewholder.dynamicll = (LinearLayout) convertView.findViewById(R.id.dynamicll);
            viewholder.parentll = (LinearLayout) convertView.findViewById(R.id.parentll);
            // viewholder.recyclerviewChild = (RecyclerView) convertView.findViewById(R.id.recyclerviewChild);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        viewholder.text.setText(parentArrayList.get(position).tech);
      //  df.pa

        try{
            //Log.e("EEEEEEEEE",df.parse(parentArrayList.get(position).date).toString());
            //Log.e("WWWWWWWWWW",new Date().toString());
            if(df.parse(parentArrayList.get(position).date).compareTo(df.parse(df.format(new Date()))) == 0) {
                viewholder.parentll.setBackgroundColor(Color.parseColor("#A9A9A9"));
            }
        } catch (Exception e){}

//        if(parentArrayList != null) {
//            if(parentArrayList.get(position).date.equalsIgnoreCase("2019-01-16")){
//                viewholder.parentll.setBackgroundColor(Color.parseColor("#A9A9A9"));
//            }
//        }

        // viewholder.text.setText(parentArrayList.get(position).getTechname());
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(c);
//        viewholder.recyclerviewChild.setLayoutManager(linearLayoutManager);
//        adapter = new AdapterChildGrid(parentArrayList.get(position).getArrayList());
//        viewholder.recyclerviewChild.setAdapter(adapter);

        View view;
        viewholder.dynamicll.removeAllViews();
        // viewholder.dynamicll.setTag(parentArrayList.get(position).eventData.);

        if (parentArrayList.get(position).eventData != null) {
            for (int i = 0; i < parentArrayList.get(position).eventData.size(); i++) {
                view = infalter.inflate(R.layout.gridchild_item, viewholder.dynamicll, false);
//            TextView ticketNo = (TextView)view.findViewById(R.id.ticketNo);
//            ticketNo.setText(parentArrayList.get(position).getArrayList().get(i).getEventJobID());
//            viewholder.dynamicll.addView(ticketNo);


                final LinearLayout llchild = (LinearLayout) view.findViewById(R.id.llchild);
                final TextView ticketNo = (TextView) llchild.findViewById(R.id.ticketNo);
                TextView duration = (TextView) llchild.findViewById(R.id.duration);
                TextView Customername = (TextView) llchild.findViewById(R.id.Customername);
                final ImageView pdficon = (ImageView) llchild.findViewById(R.id.pdficon);

                // try{
                llchild.setBackgroundColor(Color.parseColor(parentArrayList.get(position).eventData.get(i).region_color));
                //   } catch (NumberFormatException e){}
                ticketNo.setText(parentArrayList.get(position).eventData.get(i).jobid);
                duration.setText(parentArrayList.get(position).eventData.get(i).duration);
                Customername.setText(parentArrayList.get(position).eventData.get(i).customer_name);

                if(parentArrayList.get(position).eventData.get(i).sales_order != null  && !parentArrayList.get(position).eventData.get(i).sales_order.isEmpty()) {
                    pdficon.setVisibility(View.VISIBLE);
                } else {
                    pdficon.setVisibility(View.GONE);
                }

                ticketNo.setId(Integer.parseInt(parentArrayList.get(position).eventData.get(i).jobid));
                pdficon.setId(i);
                llchild.setId(i);
                ticketNo.setTextColor(Color.parseColor(parentArrayList.get(position).eventData.get(i).region_text_color));
                duration.setTextColor(Color.parseColor(parentArrayList.get(position).eventData.get(i).region_text_color));
                Customername.setTextColor(Color.parseColor(parentArrayList.get(position).eventData.get(i).region_text_color));

                ticketNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // HandyObject.showAlert(c, String.valueOf(ticketNo.getId()));
                    }
                });

                pdficon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = pdficon.getId();
                        Intent intent = new Intent();
                        intent.setDataAndType(Uri.parse(parentArrayList.get(position).eventData.get(id).sales_order), "application/pdf");
                        Intent chooserIntent = Intent.createChooser(intent, "Open Report");
                        ((Activity) c).startActivity(chooserIntent);
                    }
                });

                llchild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = llchild.getId();
                        //   HandyObject.showAlert(c,String.valueOf(parentArrayList.get(position).eventData.get(id).jobid));
                        displyPopup(v, parentArrayList.get(position).eventData.get(id));
                    }
                });
                viewholder.dynamicll.addView(llchild);
            }
        }

        return convertView;
    }

    private void displyPopup(View view, ScheduleFilterSkeleton.EventData ske) {
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
        durationvalue.setText(ske.duration);
        techniciansvalue.setText(ske.other_members);

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

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder {
        TextView text;
        // RecyclerView recyclerviewChild;
        LinearLayout dynamicll,parentll;
    }
}
