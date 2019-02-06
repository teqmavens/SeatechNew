package teq.development.seatech.PickUpJobs.Adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import teq.development.seatech.Dashboard.Skeleton.PickUpJobsSkeleton;
import teq.development.seatech.PickUpJobs.PickUpJobsFragment;
import teq.development.seatech.R;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.RowPickupjobsBinding;

public class AdapterPickUpJobs extends RecyclerView.Adapter<AdapterPickUpJobs.ViewHolder> implements Filterable {


    Context context;
    RowPickupjobsBinding binding;
    ArrayList<PickUpJobsSkeleton> arrayList, original_arraylist;
    FragmentManager fm;
    Fragment fragment;
    private ItemFilter mFilter;

    public AdapterPickUpJobs(Context context, ArrayList<PickUpJobsSkeleton> arrayList, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
        this.arrayList = arrayList;
        original_arraylist = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        binding = DataBindingUtil.inflate(inflater, R.layout.row_pickupjobs, parent, false);
//        return new ViewHolder(binding);
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pickupjobs, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.customername.setText(arrayList.get(position).getCustomerName());
        holder.ticketno.setText(arrayList.get(position).getJobTicketNo());
        holder.customertype.setText(arrayList.get(position).getCustomerType());
        holder.regionname.setText(arrayList.get(position).getRegionName());

        holder.ticketno.setTag(position);
        holder.ticketno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posi = (Integer) v.getTag();
                ((PickUpJobsFragment) fragment).onClickTicketNo(arrayList.get(posi).getJobTicketNo());
            }
        });

        holder.action.setTag(position);
        holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posi = (Integer) v.getTag();
                ((PickUpJobsFragment) fragment).onClickSchedule(arrayList.get(posi).getEstimate_hours(),arrayList.get(posi).getJobTicketNo());
            }
        });

      //  holder.bind(arrayList.get(position),position);
//        binding.getRoot().findViewById(R.id.action).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // HandyObject.showAlert(context,"dfsfa");
//                //displaypopupaction(v);
//
//                ((PickUpJobsFragment) fragment).onClickSchedule(position);
//            }
//        });
//
//        binding.getRoot().findViewById(R.id.ticketno).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // ((PickUpJobsFragment) fragment).onClickTicketNo(position);
//                ((PickUpJobsFragment) fragment).onClickTicketNo(arrayList.get(position).getJobTicketNo());
//                // HandyObject.showAlert(context,"dfsfa");
//            }
//        });
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

        TextView customername,ticketno,customertype,regionname,action;

            public ViewHolder(View itemView) {
                super(itemView);
                customername = (TextView) itemView.findViewById(R.id.customername);
                ticketno = (TextView) itemView.findViewById(R.id.ticketno);
                customertype = (TextView) itemView.findViewById(R.id.customertype);
                regionname = (TextView) itemView.findViewById(R.id.regionname);
                action = (TextView) itemView.findViewById(R.id.action);
            }


//        RowPickupjobsBinding mbinding;
//
//        public ViewHolder(RowPickupjobsBinding binding) {
//            super(binding.getRoot());
//            mbinding = binding;
//        }
//
//        public void bind(final PickUpJobsSkeleton pickUpJobsSkeleton,final int position) {
//            mbinding.setRowpickupjobs(pickUpJobsSkeleton);
//            binding.getRoot().findViewById(R.id.action).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // HandyObject.showAlert(context,"dfsfa");
//                    //displaypopupaction(v);
//
//                    ((PickUpJobsFragment) fragment).onClickSchedule(position);
//                }
//            });
//            binding.getRoot().findViewById(R.id.ticketno).setTag(position);
//            binding.getRoot().findViewById(R.id.ticketno).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int posi = (Integer) v.getTag();
//                     ((PickUpJobsFragment) fragment).onClickTicketNo(arrayList.get(posi).getJobTicketNo());
//                  //  ((PickUpJobsFragment) fragment).onClickTicketNo(arrayList.get(position).getJobTicketNo());
//                    // HandyObject.showAlert(context,"dfsfa");
//                }
//            });
//            mbinding.executePendingBindings();
//        }
    }


//    public class ViewHolder extends RecyclerView.ViewHolder {
//        RowPickupjobsBinding mbinding;
//
//        public ViewHolder(RowPickupjobsBinding binding) {
//            super(binding.getRoot());
//            mbinding = binding;
//        }
//
//        public void bind(final PickUpJobsSkeleton pickUpJobsSkeleton,final int position) {
//            mbinding.setRowpickupjobs(pickUpJobsSkeleton);
//            binding.getRoot().findViewById(R.id.action).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // HandyObject.showAlert(context,"dfsfa");
//                    //displaypopupaction(v);
//
//                    ((PickUpJobsFragment) fragment).onClickSchedule(position);
//                }
//            });
//            binding.getRoot().findViewById(R.id.ticketno).setTag(position);
//            binding.getRoot().findViewById(R.id.ticketno).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int posi = (Integer) v.getTag();
//                     ((PickUpJobsFragment) fragment).onClickTicketNo(arrayList.get(posi).getJobTicketNo());
//                  //  ((PickUpJobsFragment) fragment).onClickTicketNo(arrayList.get(position).getJobTicketNo());
//                    // HandyObject.showAlert(context,"dfsfa");
//                }
//            });
//            mbinding.executePendingBindings();
//        }
//    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<PickUpJobsSkeleton> list = original_arraylist;

            int count = list.size();
            final ArrayList<PickUpJobsSkeleton> nlist = new ArrayList<PickUpJobsSkeleton>(count);
            //String filterableString;
            for (int i = 0; i < count; i++) {
                PickUpJobsSkeleton filterableString = list.get(i);
                if (filterableString.getCustomerName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                } else if (filterableString.getJobTicketNo().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                } else if (filterableString.getRegionName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<PickUpJobsSkeleton>) results.values;
            if (arrayList.size() == 0) {
                Intent intent = new Intent("nodatareceiver");
                intent.putExtra("checkData","blank");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                //HandyObject.showAlert(context, "zerooo");
            } else {
                Intent intent = new Intent("nodatareceiver");
                intent.putExtra("checkData","fill");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
               //PickUpJobsFragment.binding.rcyviewPickupjobs.setVisibility(View.VISIBLE);
               // PickUpJobsFragment.binding.llnodata.setVisibility(View.INVISIBLE);
            }
            notifyDataSetChanged();
        }
    }
}
