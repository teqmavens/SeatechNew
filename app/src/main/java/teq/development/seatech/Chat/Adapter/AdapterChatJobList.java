package teq.development.seatech.Chat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import teq.development.seatech.Chat.ChatLeftFragment;
import teq.development.seatech.Chat.Skeleton.ChatJobListSkeleton;
import teq.development.seatech.Dashboard.Skeleton.PickUpJobsSkeleton;
import teq.development.seatech.PickUpJobs.Adapter.AdapterPickUpJobs;
import teq.development.seatech.PickUpJobs.PickUpJobsFragment;
import teq.development.seatech.R;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.RowChatjobBinding;
import teq.development.seatech.databinding.RowPickupjobsBinding;

public class AdapterChatJobList extends RecyclerView.Adapter<AdapterChatJobList.ViewHolder> implements Filterable {


    Context context;
    ArrayList<ChatJobListSkeleton> arrayList, original_arraylist;
    FragmentManager fm;
    Fragment fragment;
    private ItemFilter mFilter;
    int row_index = -1;
    RowChatjobBinding binding;
    int positionn;

    public AdapterChatJobList(Context context, ArrayList<ChatJobListSkeleton> arrayList, Fragment fragment, int positionn) {
        this.context = context;
        this.fragment = fragment;
        this.arrayList = arrayList;
        original_arraylist = arrayList;
        this.positionn = positionn;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //  LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // binding = DataBindingUtil.inflate(inflater, R.layout.row_chatjob, parent, false);
        // return new ViewHolder(binding);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chatjob, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //  holder.bind(arrayList.get(position),position);
        holder.jobid.setText(arrayList.get(position).getJobid());
        holder.cutomername.setText(arrayList.get(position).getCustomer_name());

        //     if(arrayList.get(position).getNewmsg().equalsIgnoreCase("1")) {
        if (Integer.parseInt(arrayList.get(position).getNewmsg()) > 0) {
            holder.newmsg.setVisibility(View.VISIBLE);
            holder.newmsg.setText(arrayList.get(position).getNewmsg());
        } else {
            holder.newmsg.setVisibility(View.INVISIBLE);
        }

        holder.llwhole.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((ChatLeftFragment) fragment).onClickItem(arrayList.get(position));
                positionn = position;
                notifyDataSetChanged();
            }
        });
        if (positionn == position) {
            holder.llwhole.setBackgroundColor(Color.parseColor("#0C4071"));
            holder.jobid.setTextColor(Color.parseColor("#ffffff"));
            holder.cutomername.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.llwhole.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.jobid.setTextColor(Color.parseColor("#000000"));
            holder.cutomername.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemFilter();
        }
        return mFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // RowChatjobBinding mbinding;
        LinearLayout llwhole;
        TextView jobid, cutomername, newmsg;


        public ViewHolder(final View itemView) {
            super(itemView);
            //   mbinding = binding;
            jobid = (TextView) itemView.findViewById(R.id.jobid);
            llwhole = (LinearLayout) itemView.findViewById(R.id.llwhole);
            cutomername = (TextView) itemView.findViewById(R.id.cutomername);
            newmsg = (TextView) itemView.findViewById(R.id.newmsg);
        }
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<ChatJobListSkeleton> list = original_arraylist;

            int count = list.size();
            final ArrayList<ChatJobListSkeleton> nlist = new ArrayList<ChatJobListSkeleton>(count);
            //String filterableString;
            for (int i = 0; i < count; i++) {
                ChatJobListSkeleton filterableString = list.get(i);
                if (filterableString.getJobid().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                } else if (filterableString.getCustomer_name().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<ChatJobListSkeleton>) results.values;
            if (arrayList.size() == 0) {
                ChatLeftFragment.binding.recyclerview.setVisibility(View.INVISIBLE);
                ChatLeftFragment.binding.nodatafound.setVisibility(View.VISIBLE);
            } else {
                ChatLeftFragment.binding.recyclerview.setVisibility(View.VISIBLE);
                ChatLeftFragment.binding.nodatafound.setVisibility(View.INVISIBLE);
            }
            notifyDataSetChanged();
        }
    }
}

