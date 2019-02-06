package teq.development.seatech.JobDetail.Adapter;

import android.app.DialogFragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import teq.development.seatech.JobDetail.NeedPartDialog;
import teq.development.seatech.R;
import teq.development.seatech.databinding.RowUploadedimagesBinding;

public class AdapterPartUpldImages extends RecyclerView.Adapter<AdapterPartUpldImages.ViewHolder> {

    ArrayList<String> arraylistimages;
    Context context;
    String type;

    public AdapterPartUpldImages(Context context, ArrayList<String> arraylistimages, String type) {
        this.context = context;
        this.type = type;
        this.arraylistimages = arraylistimages;
    }

    @NonNull
    @Override
    public AdapterPartUpldImages.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowUploadedimagesBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_uploadedimages, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPartUpldImages.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return arraylistimages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowUploadedimagesBinding mbinding;

        public ViewHolder(RowUploadedimagesBinding binding) {
            super(binding.getRoot());
            mbinding = binding;
        }

        public void bind(final int position) {
            Glide.with(context).load(arraylistimages.get(position)).placeholder(R.drawable.no_media).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(mbinding.imageview);
            if(type.equalsIgnoreCase("camera")) {
                mbinding.cross.setVisibility(View.VISIBLE);
                mbinding.cross.setTag(position);
                mbinding.cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NeedPartDialog.OnClickImagecross();
                        int posi = (Integer) v.getTag();
                        arraylistimages.remove(posi);
                        notifyDataSetChanged();
                    }
                });
            }

            mbinding.executePendingBindings();
        }
    }

    public void addAll(ArrayList<String> files) {

        try {
            this.arraylistimages.clear();
            this.arraylistimages.addAll(files);

        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }


}
