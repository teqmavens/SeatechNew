package teq.development.seatech.Dashboard.Adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import java.util.Calendar;

import teq.development.seatech.Dashboard.PickUpJobsFragment;
import teq.development.seatech.R;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.RowPickupjobsBinding;

public class AdapterPickUpJobs extends RecyclerView.Adapter<AdapterPickUpJobs.ViewHolder> {


    Context context;
    RowPickupjobsBinding binding;
    FragmentManager fm;
    Fragment fragment;

    public AdapterPickUpJobs(Context context, Fragment fragment){
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.row_pickupjobs,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        binding.getRoot().findViewById(R.id.action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // HandyObject.showAlert(context,"dfsfa");
                displaypopupaction(v);
            }
        });

        binding.getRoot().findViewById(R.id.ticketno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PickUpJobsFragment) fragment).onClickTicketNo();
                // HandyObject.showAlert(context,"dfsfa");

            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(RowPickupjobsBinding binding) {
            super(binding.getRoot());
        }
    }


    private void displaypopupaction(View anchorview) {
        final PopupWindow popup = new PopupWindow(context);
     //   View layout = context.getLayoutInflater().inflate(R.layout.popup_leave, null);
        View layout = LayoutInflater.from(context).inflate(R.layout.pickupschedule, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView cross = (ImageView) layout.findViewById(R.id.cross);
        ImageView timerimage = (ImageView) layout.findViewById(R.id.timerimage);
        Button submit = (Button) layout.findViewById(R.id.submit);
        final EditText et_time = (EditText) layout.findViewById(R.id.et_time);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        timerimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        et_time.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(anchorview);
    }
}
