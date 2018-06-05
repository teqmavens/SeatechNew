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

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by vibrantappz on 3/23/2017.
 */

public class AdapterNews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private android.app.FragmentManager fm;
    private DashboardActivity mdashboard;
    ArrayList<com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst> arraylist;
    private static final int FOOTER_WITHBUTTON = 1;
    private static final int NORMAL = 2;
    android.app.Fragment frgmnt;
    boolean show_footer;

    public AdapterNews(Context context, android.app.FragmentManager fm, ArrayList<com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst> arraylist, android.app.Fragment frgmnt, boolean show_footer) {
        this.context = context;
        this.fm = fm;
        this.arraylist = arraylist;
        this.show_footer = show_footer;
        this.frgmnt = frgmnt;
        mdashboard = (DashboardActivity) context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
       /* View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rownews, null);
        AdapterNews.ViewHolder viewHolder = new AdapterNews.ViewHolder(itemLayoutView);
        return viewHolder;*/
        if (viewType == FOOTER_WITHBUTTON) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_onlybutton, parent, false);
            return new FooterViewHolder(v);
        } else if (viewType == NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rownews, parent, false);
            return new ViewHolder(v);
        } else return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof RecyclerView.ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.name.setText(arraylist.get(position).getBuildingname());
            viewHolder.address.setText(arraylist.get(position).getAddress());
            // viewHolder.ratingstars.setText(arraylist.get(position).getAvgrating());
            double ii = Double.parseDouble(arraylist.get(position).getAvgrating());
            viewHolder.ratingstars.setText(String.valueOf(new DecimalFormat("##.#").format(ii)));
            viewHolder.distance.setText(arraylist.get(position).getDistance() + " " + "km");
            viewHolder.users.setText(arraylist.get(position).getTotal());
            viewHolder.likes.setText(arraylist.get(position).getHappy());
            viewHolder.dislikes.setText(arraylist.get(position).getSad());
            if (Integer.parseInt(arraylist.get(position).getHappy()) > Integer.parseInt(arraylist.get(position).getSad()) ||
                    Integer.parseInt(arraylist.get(position).getHappy()) == Integer.parseInt(arraylist.get(position).getSad())) {
                viewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileyicon_copy), null, null, null);
                viewHolder.dislikes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileydislikes_copy), null, null, null);
            } else if (Integer.parseInt(arraylist.get(position).getHappy()) < Integer.parseInt(arraylist.get(position).getSad())) {
                viewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileyicon), null, null, null);
                viewHolder.dislikes.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.smileydislikes), null, null, null);
            }
            viewHolder.text_newsdate.setText(arraylist.get(position).getVisited_date());
            viewHolder.text_newstitle.setText(arraylist.get(position).getHeading());
            viewHolder.ratingBar.setRating(Float.parseFloat(arraylist.get(position).getAvgrating()));
            if (arraylist.get(position).getType().equalsIgnoreCase("Hot")) {
                viewHolder.hot_text.setVisibility(View.VISIBLE);
            } else {
                viewHolder.hot_text.setVisibility(View.GONE);
            }
            if (arraylist.get(position).getTypeone().equalsIgnoreCase("Free")) {
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
                    com.vadevelopment.RedAppetite.newssection.NewsDetail_Fragment newsdetail_frgm = new com.vadevelopment.RedAppetite.newssection.NewsDetail_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Consts.NEWS_CLICKNID, arraylist.get(position).getLid());
                    bundle.putString(Consts.ONNEWSDETAIL_FROM, "list_outside");
                    newsdetail_frgm.setArguments(bundle);
                    mdashboard.displayWithoutViewFragmentWithanimWithoutv4(newsdetail_frgm);
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder fholder = (FooterViewHolder) holder;
            fholder.btn_loadmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //HandyObjects.showAlert(context, "clickkkk");
                    //  HandyObjects.showAlert(context, "array" + String.valueOf(arraylist.size()));
                    ((com.vadevelopment.RedAppetite.newssection.NewsFragment) frgmnt).onClickLoadMore();
                }
            });
        }

    }


    // inner class to hold a reference to each item of RecyclerView
    private class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageview, image_isreviewd, image_isfvrt;
        private TextView name, address, ratingstars, distance, users, likes, dislikes, hot_text, text_newsdate, text_newstitle;
        private LinearLayout ll_container;
        private RatingBar ratingBar;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            name = (TextView) itemLayoutView.findViewById(R.id.name);
            address = (TextView) itemLayoutView.findViewById(R.id.address);
            ratingstars = (TextView) itemLayoutView.findViewById(R.id.ratingstars);
            distance = (TextView) itemLayoutView.findViewById(R.id.distance);
            users = (TextView) itemLayoutView.findViewById(R.id.users);
            likes = (TextView) itemLayoutView.findViewById(R.id.likes);
            dislikes = (TextView) itemLayoutView.findViewById(R.id.dislikes);
            hot_text = (TextView) itemLayoutView.findViewById(R.id.hot_text);
            text_newsdate = (TextView) itemLayoutView.findViewById(R.id.text_newsdate);
            text_newstitle = (TextView) itemLayoutView.findViewById(R.id.text_newstitle);
            ratingBar = (RatingBar) itemLayoutView.findViewById(R.id.ratingBar);
            imageview = (ImageView) itemLayoutView.findViewById(R.id.imageview);
            image_isreviewd = (ImageView) itemLayoutView.findViewById(R.id.image_isreviewd);
            image_isfvrt = (ImageView) itemLayoutView.findViewById(R.id.image_isfvrt);
            ll_container = (LinearLayout) itemLayoutView.findViewById(R.id.ll_container);
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

