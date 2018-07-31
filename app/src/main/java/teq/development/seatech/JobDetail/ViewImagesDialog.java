package teq.development.seatech.JobDetail;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.content.res.AppCompatResources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import teq.development.seatech.R;

public class ViewImagesDialog extends DialogFragment {

    public static int position;
    public static ArrayList<String> arraylistImages;
    TextView dynamic_number, fix_number;
    Dialog dialog;

    public static ViewImagesDialog newInstance(int num, ArrayList<String> arraylist) {
        ViewImagesDialog f = new ViewImagesDialog();
        position = num;
        arraylistImages = arraylist;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popup_viewimages, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        final ImageView cross = (ImageView) rootView.findViewById(R.id.cross);
        TextView moveto_prev = (TextView) rootView.findViewById(R.id.moveto_prev);
        TextView moveto_next = (TextView) rootView.findViewById(R.id.moveto_next);
        moveto_prev.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.leftarrow), null, null, null);
        moveto_next.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(getActivity(), R.drawable.rightarrow), null);
        dynamic_number = (TextView) rootView.findViewById(R.id.dynamic_number);
        fix_number = (TextView) rootView.findViewById(R.id.fix_number);

        final ViewPager dialog_viewpager = (ViewPager) rootView.findViewById(R.id.dialog_viewpager);
        dialog_viewpager.setAdapter(new CustomPagerAdapterDialog(getActivity()));
        dialog_viewpager.setCurrentItem(position);
        dynamic_number.setText(String.valueOf(position + 1));

        if (arraylistImages.size() == 1) {
            moveto_prev.setVisibility(View.INVISIBLE);
            moveto_next.setVisibility(View.INVISIBLE);
            dynamic_number.setVisibility(View.INVISIBLE);
            fix_number.setVisibility(View.INVISIBLE);
        } else {
            moveto_prev.setVisibility(View.VISIBLE);
            moveto_next.setVisibility(View.VISIBLE);
            dynamic_number.setVisibility(View.VISIBLE);
            fix_number.setVisibility(View.VISIBLE);
        }

        dialog_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int po = position + 1;
                dynamic_number.setText(String.valueOf(po));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        moveto_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_viewpager.setCurrentItem(dialog_viewpager.getCurrentItem() - 1, true);
            }
        });

        moveto_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_viewpager.setCurrentItem(dialog_viewpager.getCurrentItem() + 1, true);
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = width - 220;
        // params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = height - 420;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    class CustomPagerAdapterDialog extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapterDialog(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arraylistImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.row_dialogfullimage, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.centerimage);
            fix_number.setText("/" + String.valueOf(arraylistImages.size()));
            Glide.with(getActivity()).load(arraylistImages.get(position)).placeholder(R.drawable.no_media).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }
}
