package teq.development.seatech.Dashboard.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import teq.development.seatech.Dashboard.DashBoardFragment;
import teq.development.seatech.R;
import teq.development.seatech.databinding.RowJobsforyouBinding;

public class AdapterJosbForYou extends RecyclerView.Adapter<AdapterJosbForYou.ViewHolder> {

    Context context;
    RowJobsforyouBinding binding;
    Fragment fragment;

    public AdapterJosbForYou(Context context, Fragment fragment){
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
         binding = DataBindingUtil.inflate(inflater, R.layout.row_jobsforyou,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        binding.getRoot().findViewById(R.id.jobticketno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashBoardFragment) fragment).onClickTicketNo();
            }
        });

        binding.getRoot().findViewById(R.id.notesimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashBoardFragment) fragment).onClickNotes();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(RowJobsforyouBinding binding) {
            super(binding.getRoot());
        }
    }
}
