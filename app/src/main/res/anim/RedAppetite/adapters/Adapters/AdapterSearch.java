package com.vadevelopment.RedAppetite.adapters.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;
import com.vadevelopment.RedAppetite.searchsection.SearchFirstFragment;
import com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Vibrant Android on 06-01-2017.
 */


public class AdapterSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private android.app.FragmentManager fm;
    private DashboardActivity mdashboard;
    private AdapterSearchFirstFragment adapter;
    ArrayList<Skeleton_SearchFirst> arraylist;
    private static final int FOOTER_WITHBUTTON = 1;
    private static final int NORMAL = 2;
    int max;
    android.app.Fragment frgmnt;
    boolean show_footer;

    public AdapterSearch(Context context, android.app.FragmentManager fm, ArrayList<Skeleton_SearchFirst> arraylist, int max, android.app.Fragment frgmnt, boolean show_footer) {
        this.context = context;
        this.fm = fm;
        this.show_footer = show_footer;
        this.arraylist = arraylist;
        this.max = max;
        this.frgmnt = frgmnt;
        mdashboard = (DashboardActivity) context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_WITHBUTTON) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_onlybutton, parent, false);
            return new FooterViewHolder(v);
        } else if (viewType == NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowrecyclersearch, parent, false);
            return new ViewHolder(v);
        } else return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.name.setText(arraylist.get(position).getBuildingname());
            viewHolder.address.setText(arraylist.get(position).getAddress().split(",")[0]);
            // viewHolder.ratingtext.setText(arraylist.get(position).getAvgrating());
            //
            //  double ii = 3.46;
            if (arraylist.get(position).getAvgrating().length() == 1) {
                viewHolder.ratingtext.setText(arraylist.get(position).getAvgrating() + ".0");
            } else {
                double ii = Double.parseDouble(arraylist.get(position).getAvgrating());
                viewHolder.ratingtext.setText(String.valueOf(new DecimalFormat("##.#").format(ii)));
            }
            viewHolder.distance.setText(arraylist.get(position).getDistance() + " " + "km");
            viewHolder.users.setText(arraylist.get(position).getTotal());
            viewHolder.likes.setText(arraylist.get(position).getHappy());
            viewHolder.dislikes.setText(arraylist.get(position).getSad());
            viewHolder.ratingBar.setRating(Float.parseFloat(arraylist.get(position).getAvgrating()));
            if (Integer.parseInt(arraylist.get(position).getHappy()) > Integer.parseInt(arraylist.get(position).getSad()) ||
                    Integer.parseInt(arraylist.get(position).getHappy()) == Integer.parseInt(arraylist.get(position).getSad())) {
                viewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileyicon_copy), null, null, null);
                viewHolder.dislikes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileydislikes_copy), null, null, null);
            } else if (Integer.parseInt(arraylist.get(position).getHappy()) < Integer.parseInt(arraylist.get(position).getSad())) {
                viewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileyicon), null, null, null);
                viewHolder.dislikes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileydislikes), null, null, null);
            }
            if (arraylist.get(position).getType().equalsIgnoreCase("Free")) {
                Glide.with(context).load(arraylist.get(position).getFreeimage()).placeholder(R.drawable.clubicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(viewHolder.imageview);
            } else {
                Glide.with(context).load(arraylist.get(position).getPremiumimage()).placeholder(R.drawable.clubicon).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(viewHolder.imageview);
            }

            if (Integer.parseInt(arraylist.get(position).getIsreviewed()) > 0 && Integer.parseInt(arraylist.get(position).getIsfav()) > 0) {
                viewHolder.image_isreviewd.setVisibility(View.VISIBLE);
                viewHolder.image_isfvrt.setVisibility(View.VISIBLE);
            } else if (Integer.parseInt(arraylist.get(position).getIsreviewed()) > 0 && Integer.parseInt(arraylist.get(position).getIsfav()) == 0) {
                viewHolder.image_isreviewd.setVisibility(View.VISIBLE);
                viewHolder.image_isfvrt.setVisibility(View.INVISIBLE);
            } else if (Integer.parseInt(arraylist.get(position).getIsreviewed()) == 0 && Integer.parseInt(arraylist.get(position).getIsfav()) > 0) {
                viewHolder.image_isreviewd.setVisibility(View.INVISIBLE);
                viewHolder.image_isfvrt.setVisibility(View.VISIBLE);
            } else {
                viewHolder.image_isreviewd.setVisibility(View.INVISIBLE);
                viewHolder.image_isfvrt.setVisibility(View.INVISIBLE);
            }

            viewHolder.ll_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    com.vadevelopment.RedAppetite.searchsection.SearchDetail_Fragment sdetailk_frgm = new com.vadevelopment.RedAppetite.searchsection.SearchDetail_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Consts.SEARCH_CLICKLID, arraylist.get(position).getLid());
                    bundle.putString(Consts.CHECKCLICK_FROM, "search");
                    sdetailk_frgm.setArguments(bundle);
                    mdashboard.displayWithoutViewFragmentWithanimWithoutv4(sdetailk_frgm);
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder fholder = (FooterViewHolder) holder;
            fholder.btn_loadmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SearchFirstFragment) frgmnt).onClickLoadMore();
                }
            });
        }
    }


    // inner class to hold a reference to each item of RecyclerView
    private class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageview, image_isreviewd, image_isfvrt;
        private TextView name, address, ratingtext, distance, users, likes, dislikes;
        private LinearLayout ll_container;
        private RatingBar ratingBar;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            ll_container = (LinearLayout) itemLayoutView.findViewById(R.id.ll_container);
            imageview = (ImageView) itemLayoutView.findViewById(R.id.imageview);
            image_isreviewd = (ImageView) itemLayoutView.findViewById(R.id.image_isreviewd);
            image_isfvrt = (ImageView) itemLayoutView.findViewById(R.id.image_isfvrt);
            name = (TextView) itemLayoutView.findViewById(R.id.name);
            address = (TextView) itemLayoutView.findViewById(R.id.address);
            ratingtext = (TextView) itemLayoutView.findViewById(R.id.ratingtext);
            distance = (TextView) itemLayoutView.findViewById(R.id.distance);
            users = (TextView) itemLayoutView.findViewById(R.id.users);
            likes = (TextView) itemLayoutView.findViewById(R.id.likes);
            dislikes = (TextView) itemLayoutView.findViewById(R.id.dislikes);
            ratingBar = (RatingBar) itemLayoutView.findViewById(R.id.ratingBar);
        }
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (show_footer) {
            return arraylist.size() + 1;
        } else {
            return arraylist.size();
        }
    }

    public void hideFooter() {
        show_footer = false;
    }

    public void showfooter() {
        show_footer = true;
    }


    @Override
    public int getItemViewType(int position) {
        if (show_footer) {
            if (position > 49) {
                if (ispositionFooter(position)) {
                    return FOOTER_WITHBUTTON;
                } else {
                    return NORMAL;
                }
            } else {
                return NORMAL;
            }
        } else {
            return NORMAL;
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        private Button btn_loadmore;

        public FooterViewHolder(View view) {
            super(view);
            btn_loadmore = (Button) view.findViewById(R.id.btn_loadmore);
        }
    }

    private boolean ispositionFooter(int position) {
        if (position == arraylist.size()) {
            return true;
        } else {
            return false;
        }
    }
}
