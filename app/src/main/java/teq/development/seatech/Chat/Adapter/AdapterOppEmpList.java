package teq.development.seatech.Chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import teq.development.seatech.Chat.Skeleton.AllEmployeeSkeleton;
import teq.development.seatech.JobDetail.Adapter.AdapterManufacturerSpinner;
import teq.development.seatech.ManufacturerSkeleton;
import teq.development.seatech.R;

public class AdapterOppEmpList extends BaseAdapter {

    private LayoutInflater inflater = null;
    ArrayList<AllEmployeeSkeleton> ArrayList;
    Context context;

    public AdapterOppEmpList(Context context, ArrayList<AllEmployeeSkeleton> ArrayList) {
        this.context = context;
        this.ArrayList = ArrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return ArrayList.size();
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
        AdapterOppEmpList.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new AdapterOppEmpList.ViewHolder();
            convertView = inflater.inflate(R.layout.row_jobspinner, null);
            viewHolder.textspinner = (TextView) convertView.findViewById(R.id.textspinner);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AdapterOppEmpList.ViewHolder) convertView.getTag();
        }
        viewHolder.textspinner.setText(ArrayList.get(position).getEmployeename());
        return convertView;
    }

    public class ViewHolder {
        TextView textspinner;
    }
}
