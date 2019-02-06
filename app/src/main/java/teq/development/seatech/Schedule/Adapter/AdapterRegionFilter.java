package teq.development.seatech.Schedule.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import teq.development.seatech.Dashboard.Skeleton.ScheduleFilterSkeleton;
import teq.development.seatech.R;

public class AdapterRegionFilter extends BaseAdapter {

    private LayoutInflater inflater = null;
    ArrayList<ScheduleFilterSkeleton.RegionData> arrayList;
    Context context;

   public AdapterRegionFilter(Context context, ArrayList<ScheduleFilterSkeleton.RegionData> arrayList){
       this.context = context;
       this.arrayList = arrayList;
       inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_manufacturer, null);
            viewHolder.textspinner = (TextView) convertView.findViewById(R.id.textspinner);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textspinner.setText(arrayList.get(position).region_name);
        return convertView;
    }

    public class ViewHolder {
        TextView textspinner;
    }
}
