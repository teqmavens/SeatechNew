package teq.development.seatech.JobDetail.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.ArrayList;

import teq.development.seatech.Dashboard.Skeleton.DashboardNotes_Skeleton;
import teq.development.seatech.Dashboard.Skeleton.UploadImageNewSkeleton;
import teq.development.seatech.JobDetail.ViewCommentDialog;
import teq.development.seatech.JobDetail.ViewImagesDialog;
import teq.development.seatech.R;
import teq.development.seatech.databinding.RowUploadedimagesBinding;

public class AdapterUploadedImages extends RecyclerView.Adapter<AdapterUploadedImages.ViewHolder> {

    ArrayList<UploadImageNewSkeleton> arraylistimages;
    FragmentManager frgmmanager;
    Context context;

    public AdapterUploadedImages(Context context, ArrayList<UploadImageNewSkeleton> arraylistimages, FragmentManager frgmmanager) {
        this.context = context;
        this.arraylistimages = arraylistimages;
        this.frgmmanager = frgmmanager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowUploadedimagesBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_uploadedimages, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
            mbinding.setRowuploadedimages(AdapterUploadedImages.this);
            Glide.with(context).load(arraylistimages.get(position).getUrl()).placeholder(R.drawable.no_media).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(mbinding.imageview);

            mbinding.imageview.findViewById(R.id.imageview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogViewImages(position, arraylistimages);
                }
            });
            mbinding.executePendingBindings();
        }
    }

    private void dialogViewImages(int posi, ArrayList<UploadImageNewSkeleton> arraylist) {
        FragmentTransaction ft = frgmmanager.beginTransaction();
        Fragment prev = frgmmanager.findFragmentByTag("dialogviewIMAGES");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment newFragment = ViewImagesDialog.newInstance(posi, arraylist);
        newFragment.show(ft, "dialogviewIMAGES");
    }


}
