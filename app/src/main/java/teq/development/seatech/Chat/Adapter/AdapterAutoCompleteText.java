package teq.development.seatech.Chat.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import teq.development.seatech.R;

public class AdapterAutoCompleteText extends ArrayAdapter<String>{

    private LayoutInflater inflater = null;
    java.util.ArrayList<String> ArrayList,suggestions,tempItems;
    int resource, textViewId;
    Context context;

    public AdapterAutoCompleteText(Context context,int resource,int textViewId, ArrayList<String> ArrayList) {
        super(context, resource, textViewId, ArrayList);
        this.context = context;
        this.ArrayList = ArrayList;
        this.resource = resource;
        this.textViewId = textViewId;
        suggestions = new ArrayList<String>();
        tempItems = new ArrayList<>(ArrayList);
        //   inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_jobspinner, parent, false);
        }
     //   People people = items.get(position);
        if (ArrayList != null) {
            TextView lblName = (TextView) view.findViewById(R.id.textspinner);
            if (lblName != null)
                lblName.setText(ArrayList.get(position));
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((String) resultValue);
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (String people : tempItems) {
                    if (people.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<String> filterList = (ArrayList<String>) results.values;
            try {
                if (results != null && results.count > 0) {
                    clear();
                    for (String people : filterList) {
                        add(people);
                        notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {}
        }
    };
}

