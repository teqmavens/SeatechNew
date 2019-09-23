package teq.development.seatech.CustomCalendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.List;

import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;
import teq.development.seatech.Profile.MyProfileFragment;
import teq.development.seatech.R;
import teq.development.seatech.Schedule.JobDetailStaticSchedule;
import teq.development.seatech.Schedule.ScheduleParent;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;

public class AdapterGridTest extends BaseAdapter implements Filterable {
    private LayoutInflater infalter;
    Context c;
    // ArrayList<CalTimeline_Skeleton> parentArrayList;
    ArrayList<ScheduleFilterSkeleton.SchedulesData> parentArrayList,original_arraylist;
    AdapterChildGrid adapter;
    private ItemFilter mFilter;
    SimpleDateFormat df;
    ScheduleParent activity;

    public AdapterGridTest(Context c, ArrayList<ScheduleFilterSkeleton.SchedulesData> parentArrayList) {
        this.c = c;
        activity = (ScheduleParent) c;
        this.parentArrayList = parentArrayList;
        original_arraylist = parentArrayList;
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

        try{

            if(df.parse(parentArrayList.get(position).date).compareTo(df.parse(df.format(new Date()))) == 0) {
                viewholder.parentll.setBackgroundColor(Color.parseColor("#A9A9A9"));
            } else {
                viewholder.parentll.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        } catch (Exception e){}

        View view;
        viewholder.dynamicll.removeAllViews();

        if (parentArrayList.get(position).eventData != null) {
            for (int i = 0; i < parentArrayList.get(position).eventData.size(); i++) {

                if(parentArrayList.get(position).eventData.get(i).jobid != null && !parentArrayList.get(position).eventData.get(i).jobid.isEmpty()) {
                    view = infalter.inflate(R.layout.gridchild_item, viewholder.dynamicll, false);
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
                    llchild.setBackgroundColor(Color.parseColor(parentArrayList.get(position).eventData.get(i).region_color));
                    //   } catch (NumberFormatException e){}
                    ticketNo.setText(parentArrayList.get(position).eventData.get(i).jobid);
                    duration.setText(parentArrayList.get(position).eventData.get(i).duration+" hrs");
                    Customername.setText(parentArrayList.get(position).eventData.get(i).customer_name);
                    apptype_symbol.setText(parentArrayList.get(position).eventData.get(i).appointment_type_symbol);
                    appconfirm_symbol.setText(parentArrayList.get(position).eventData.get(i).appointment_confirm_symbol);
                    urgent_symbol.setText(parentArrayList.get(position).eventData.get(i).urgent_symbol);

                    if(parentArrayList.get(position).eventData.get(i).need_parts != null && !parentArrayList.get(position).eventData.get(i).need_parts.isEmpty()){
                        need_parts.setVisibility(View.VISIBLE);
                    }

                    if(parentArrayList.get(position).eventData.get(i).have_parts != null && !parentArrayList.get(position).eventData.get(i).have_parts.isEmpty()){
                        have_parts.setVisibility(View.VISIBLE);
                    }

                    if(parentArrayList.get(position).eventData.get(i).sales_order.equalsIgnoreCase("")) {
                        pdficon.setVisibility(View.GONE);
                    } else {
                        pdficon.setVisibility(View.VISIBLE);
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
                            JobDetailStaticSchedule jb = new JobDetailStaticSchedule();
                            Bundle bundle = new Bundle();
                            bundle.putInt("position", position);
                            bundle.putString("position_ticketId",String.valueOf(ticketNo.getId()));
                            jb.setArguments(bundle);
                            activity.replaceFragmentWithBack(jb);
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
        }

        return convertView;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<ScheduleFilterSkeleton.SchedulesData> list = original_arraylist;

            int count = list.size();
            final ArrayList<ScheduleFilterSkeleton.SchedulesData> nlist = new ArrayList<ScheduleFilterSkeleton.SchedulesData>(count);
            //String filterableString;
            for (int i = 0; i < count; i++) {
                ScheduleFilterSkeleton.SchedulesData filterableString = list.get(i);
                nlist.add(filterableString);
                for(int k=0; k<filterableString.eventData.size();k++) {
                 //   filterableString.eventData.clear();
                    if (filterableString.eventData.get(k).region_name.toLowerCase().contains(filterString)) {
                       // nlist.add(filterableString);
                        nlist.get(i).eventData.set(k,filterableString.eventData.get(k));
                    } else {
                        nlist.get(i).eventData.clear();
                    }
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            parentArrayList = (ArrayList<ScheduleFilterSkeleton.SchedulesData>) results.values;
            notifyDataSetChanged();
        }
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
        durationvalue.setText(ske.duration+" hrs");
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
        if (mFilter == null) {
            mFilter = new ItemFilter();
        }
        return mFilter;
    }

    public class ViewHolder {
        TextView text;
        // RecyclerView recyclerviewChild;
        LinearLayout dynamicll,parentll;
    }
}
