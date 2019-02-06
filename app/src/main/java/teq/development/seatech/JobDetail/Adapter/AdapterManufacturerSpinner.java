package teq.development.seatech.JobDetail.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import teq.development.seatech.ManufacturerSkeleton;
import teq.development.seatech.R;

public class AdapterManufacturerSpinner extends BaseAdapter {

    private LayoutInflater inflater = null;
    ArrayList<ManufacturerSkeleton> manufacturerArrayList;
    Context context;

    public AdapterManufacturerSpinner(Context context, ArrayList<ManufacturerSkeleton> manufacturerArrayList) {
        this.context = context;
        this.manufacturerArrayList = manufacturerArrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return manufacturerArrayList.size();
      /* int count = manufacturerArrayList.size();
        return count > 0 ? count - 1 : count;*/
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
        viewHolder.textspinner.setText(manufacturerArrayList.get(position).getName());
        return convertView;
    }

    public class ViewHolder {
        TextView textspinner;
    }
}
