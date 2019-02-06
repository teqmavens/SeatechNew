package teq.development.seatech.Dashboard.Skeleton;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Observable;

import teq.development.seatech.Dashboard.VMNotifications;
import teq.development.seatech.R;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.RowNotificationBinding;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    ArrayList<NotificationSkeleton.NotificationData> arrayList;
    RowNotificationBinding mbinding;
    VMNotifications vmNotifications;

    public NotificationAdapter(Context context, ArrayList<NotificationSkeleton.NotificationData> arrayList, VMNotifications vmNotifications) {
        this.context = context;
        this.arrayList = arrayList;
        this.vmNotifications = vmNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowNotificationBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_notification, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.bind(position);
        if (arrayList.get(position).have_read.equalsIgnoreCase("1")) {
            mbinding.lltop.setBackgroundColor(Color.WHITE);
        } else {
            mbinding.lltop.setBackgroundColor(Color.parseColor("#A9A9A9"));
        }

        mbinding.lltop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayList.get(position).have_read.equalsIgnoreCase("1")) {
                    //  HandyObject.showAlert(context, context.getString(R.string.alreadyreadnoti));
                } else {
                    vmNotifications.RowClick(arrayList.get(position));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(RowNotificationBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
            //mbinding.executePendingBindings();
        }

        public void bind(int position) {
            mbinding.setRowNotifictaion(arrayList.get(position));
            mbinding.executePendingBindings();
        }
    }
}
