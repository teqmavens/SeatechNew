package teq.development.seatech.JobDetail.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import teq.development.seatech.JobDetail.Skeleton.JobTimeSkeleton;
import teq.development.seatech.R;


public class AdapterJobTime extends BaseAdapter {

    private LayoutInflater inflater = null;
    ArrayList<JobTimeSkeleton> arralistJobTime;
    Context context;

    public AdapterJobTime(Context context, ArrayList<JobTimeSkeleton> arralistJobTime) {
        this.context = context;
        this.arralistJobTime = arralistJobTime;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arralistJobTime.size();
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
            convertView = inflater.inflate(R.layout.row_jobspinner, null);
            viewHolder.textspinner = (TextView) convertView.findViewById(R.id.textspinner);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //  viewHolder.textspinner.setText(arralistJobTime.get(position).getHrminutes()+","+arralistJobTime.get(position).getParsedate());
        viewHolder.textspinner.setText(arralistJobTime.get(position).getHrminutes());
        return convertView;
    }

    public class ViewHolder {
        TextView textspinner;
    }
}
