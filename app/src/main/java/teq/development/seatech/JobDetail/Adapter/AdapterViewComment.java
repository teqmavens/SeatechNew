package teq.development.seatech.JobDetail.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import teq.development.seatech.R;
import teq.development.seatech.databinding.RowViewcommentBinding;

public class AdapterViewComment extends RecyclerView.Adapter<AdapterViewComment.ViewHolder> {

    Context context;

    public AdapterViewComment(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterViewComment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowViewcommentBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_viewcomment,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewComment.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(RowViewcommentBinding binding) {
            super(binding.getRoot());
        }
    }
}
