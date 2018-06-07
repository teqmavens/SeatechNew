package teq.development.seatech.Dashboard.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import teq.development.seatech.R;
import teq.development.seatech.databinding.RowDashurgentmsgBinding;

public class AdapterDashbrdUrgentMsg extends RecyclerView.Adapter<AdapterDashbrdUrgentMsg.ViewHolder> {

    Context context;

    public AdapterDashbrdUrgentMsg(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowDashurgentmsgBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_dashurgentmsg, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(RowDashurgentmsgBinding binding) {
            super(binding.getRoot());
        }
    }
}
