package teq.development.seatech.CustomCalendar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import teq.development.seatech.R;

public class AdapterChildGrid extends RecyclerView.Adapter<AdapterChildGrid.ViewHolder> {

    ArrayList<EventSkeleton> arrayList;

    public AdapterChildGrid(ArrayList<EventSkeleton> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public AdapterChildGrid.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridchild_item, parent, false);
        return new AdapterChildGrid.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChildGrid.ViewHolder holder, int position) {
        holder.ticketNo.setText(arrayList.get(position).getEventJobID());
      //  holder.Customername.setText(arrayList.get(position).getEventCustomer());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView ticketNo,Customername;

        public ViewHolder(View itemView) {
            super(itemView);
            ticketNo = (TextView) itemView.findViewById(R.id.ticketNo);
          //  Customername = (TextView) itemView.findViewById(R.id.Customername);
        }
    }
}
