package teq.development.seatech.Dashboard.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.R;

public class AdapterJobSpinner extends BaseAdapter {

    private LayoutInflater inflater = null;
    ArrayList<AllJobsSkeleton> arralistAllJobs;
    Context context;

    public AdapterJobSpinner(Context context,ArrayList<AllJobsSkeleton> arralistAllJobs) {
        this.context = context;
        this.arralistAllJobs = arralistAllJobs;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arralistAllJobs.size();
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
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_jobspinner,null);
            viewHolder.textspinner = (TextView) convertView.findViewById(R.id.textspinner);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textspinner.setText(arralistAllJobs.get(position).getJobticketNo()+","+arralistAllJobs.get(position).getCustomerName());
        return convertView;
    }

    public class ViewHolder{
        TextView textspinner;
    }
}
