package teq.development.seatech.Dashboard.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.Dashboard.DashBoardFragment;
import teq.development.seatech.Dashboard.Skeleton.AllJobsSkeleton;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;
import teq.development.seatech.databinding.RowJobsforyouBinding;

public class AdapterJosbForYou extends RecyclerView.Adapter<AdapterJosbForYou.ViewHolder> {

    Context context;
    RowJobsforyouBinding binding;
    ArrayList<AllJobsSkeleton> jobsArrayList;
    boolean is_running;
    Fragment fragment;
    ArrayAdapter<CharSequence> lcAdapter;

    public AdapterJosbForYou(Context context, ArrayList<AllJobsSkeleton> jobsArrayList, Fragment fragment,boolean is_running) {
        this.context = context;
        this.fragment = fragment;
        this.jobsArrayList = jobsArrayList;
        this.is_running = is_running;
        lcAdapter = ArrayAdapter.createFromResource(context, R.array.DahboardspinnerType,
        android.R.layout.simple_spinner_item);
    //    HandyObject.showAlert(context,running_jobid);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.row_jobsforyou, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.bind(jobsArrayList.get(position));

       // if(is_running == true) {
        if(HandyObject.getPrams(context, AppConstants.ISJOB_RUNNING).equalsIgnoreCase("yes")) {
            if(jobsArrayList.get(position).getJobticketNo().equalsIgnoreCase(HandyObject.getPrams(context, AppConstants.JOBRUNNING_ID))) {
                if(HandyObject.parseDateToYMDNew(DashBoardFragment.binding.currentdate.getText().toString()).split("-")[2].compareTo((new SimpleDateFormat("yyyy-MM-dd").format(new Date()).split("-")[2])) == 0) {
                    binding.startimage.setText("View");
                } else {
                    binding.startimage.setText("Start");
                }
               // binding.startimage.setText("View");
            } else {
                binding.startimage.setText("Start");
            }
        }

//        if(jobsArrayList.get(position).getJobticketNo().equalsIgnoreCase("111111")) {
//            binding.lltop.setBackgroundColor(Color.TRANSPARENT);
//        } else {
//            if(Integer.parseInt(jobsArrayList.get(position).getJob_completed()) > 0) {
//                binding.lltop.setBackgroundColor(Color.parseColor("#A9A9A9"));
//            } else {
//                binding.lltop.setBackgroundColor(Color.TRANSPARENT);
//            }
//        }


        binding.getRoot().findViewById(R.id.startimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashBoardFragment) fragment).onClickStart(position);
            }
        });

        binding.getRoot().findViewById(R.id.uploadimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashBoardFragment) fragment).onClickUploadImage(position);
            }
        });

        binding.getRoot().findViewById(R.id.jobticketno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashBoardFragment) fragment).onClickTicketNo(position);
            }
        });

//        binding.getRoot().findViewById(R.id.notesimage).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((DashBoardFragment) fragment).onClickNotes(position);
//            }
//        });

        binding.selecttype.setAdapter(lcAdapter);
        binding.selecttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int posi, long id) {
                if (posi == 0) {
                    //HandyObject.showAlert(context,"please select");
                } else if (posi == 1) {
                    ((DashBoardFragment) fragment).onClickNotes(position,posi);
                } else if (posi == 2) {
                    ((DashBoardFragment) fragment).onClickNotes(position,posi);
                   // HandyObject.showAlert(context,String.valueOf(posi));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return jobsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowJobsforyouBinding mbinding;
        public ViewHolder(RowJobsforyouBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(AllJobsSkeleton ske) {
            if (ske.getNeedpart().equalsIgnoreCase("yes")) {
                binding.needpart.setImageResource(R.drawable.greencircle);
            } else {
                binding.needpart.setImageResource(R.drawable.transcircle);
            }

            if (ske.getHavepart().toLowerCase().equalsIgnoreCase("yes")) {
                binding.havpart.setImageResource(R.drawable.greencircle);
            } else {
                binding.havpart.setImageResource(R.drawable.transcircle);
            }
            mbinding.setRowjobsforyou(ske);
            mbinding.executePendingBindings();
        }
    }
}
