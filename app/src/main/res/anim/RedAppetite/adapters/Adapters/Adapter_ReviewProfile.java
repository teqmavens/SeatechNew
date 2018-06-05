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
import com.vadevelopment.RedAppetite.profilesection.DetailReviewProfile_Fragment;
import com.vadevelopment.RedAppetite.profilesection.ReviewProfile_Fragment;
import com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vibrantappz on 3/22/2017.
 */

public class Adapter_ReviewProfile extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private android.app.FragmentManager fm;
    private DashboardActivity mdashboard;
    ArrayList<Skeleton_SearchFirst> arraylist;
    private static final int FOOTER_WITHBUTTON = 1;
    private static final int NORMAL = 2;
    android.app.Fragment frgmnt;
    boolean show_footer;

    public Adapter_ReviewProfile(Context context, android.app.FragmentManager fm, ArrayList<Skeleton_SearchFirst> arraylist, android.app.Fragment frgmnt, boolean show_footer) {
        mdashboard = (DashboardActivity) context;
        this.context = context;
        this.show_footer = show_footer;
        this.arraylist = arraylist;
        this.frgmnt = frgmnt;
        this.fm = fm;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_WITHBUTTON) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_onlybutton, parent, false);
            return new FooterViewHolder(v);
        } else if (viewType == NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reviewprofile, parent, false);
            return new ViewHolder(v);
        } else return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.name.setText(arraylist.get(position).getBuildingname());
            viewHolder.address.setText(arraylist.get(position).getAddress().split(",")[0]);
            viewHolder.ratingBar.setRating(Float.parseFloat(arraylist.get(position).getAvgrating()));
            viewHolder.distance.setText(arraylist.get(position).getDistance() + " " + "km");
            viewHolder.visiteddate.setText(arraylist.get(position).getVisited_date());
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
            if (Integer.parseInt(arraylist.get(position).getIsfav()) > 0) {
                viewHolder.image_isfvrt.setVisibility(View.VISIBLE);
            } else {
                viewHolder.image_isfvrt.setVisibility(View.INVISIBLE);
            }
            viewHolder.ll_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    com.vadevelopment.RedAppetite.profilesection.DetailDltFrmFvrt_EdtRvw_Fragment fvrtdeatil_frgm = new com.vadevelopment.RedAppetite.profilesection.DetailDltFrmFvrt_EdtRvw_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Consts.PROFILE_CLICKLID, arraylist.get(position).getLid());
                    bundle.putString(Consts.CHECKCLICK_FROM, "review");
                    fvrtdeatil_frgm.setArguments(bundle);
                    mdashboard.displayWithoutViewFragmentWithanimWithoutv4(fvrtdeatil_frgm);
                    //  mdashboard.displayWithoutViewFragment(new DetailDltFrmFvrt_EdtRvw_Fragment());
                }
            });

            viewHolder.iconedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        DetailReviewProfile_Fragment review_frgm = new DetailReviewProfile_Fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Consts.REVIEW_TYPE, "edit");
                        bundle.putString(Consts.REVIEW_LID, arraylist.get(position).getLid());
                        bundle.putString(Consts.REVIEW_BUILDINGNAME, arraylist.get(position).getBuildingname());
                        bundle.putString(Consts.REVIEW_TOTALRATING, arraylist.get(position).getAvgrating());
                        bundle.putString(Consts.REVIEW_DISTANCE, arraylist.get(position).getDistance());
                        bundle.putString(Consts.REVIEW_USERS, arraylist.get(position).getTotal());
                        bundle.putString(Consts.REVIEW_LIKES, arraylist.get(position).getHappy());
                        bundle.putString(Consts.REVIEW_DISLIKES, arraylist.get(position).getSad());
                        bundle.putString(Consts.REVIEW_BUILDINGADDRESS, arraylist.get(position).getAddress());
                        if (arraylist.get(position).getType().equalsIgnoreCase("Free")) {
                            bundle.putString(Consts.REVIEW_IMAGEURL, arraylist.get(position).getFreeimage());
                        } else {
                            bundle.putString(Consts.REVIEW_IMAGEURL, arraylist.get(position).getPremiumimage());
                        }
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("rvid", arraylist.get(position).getRvid());
                        jsonObj.put("recommendation", arraylist.get(position).getRecommendation());
                        jsonObj.put("rating", arraylist.get(position).getInd_rating());
                        jsonObj.put("visited_date", arraylist.get(position).getVisited_date());
                        if (String.valueOf(jsonObj) != null && !String.valueOf(jsonObj).isEmpty()) {
                            bundle.putString(Consts.REVIEW_EDIT_JSONOBJECT, String.valueOf(jsonObj));
                        }
                        if (arraylist.get(position).getIsfav() != null && !arraylist.get(position).getIsfav().isEmpty()) {
                            bundle.putString(Consts.REVIEW_ISREVIEWD, "1");
                            bundle.putString(Consts.REVIEW_ISFAV, arraylist.get(position).getIsfav());
                        }
                        review_frgm.setArguments(bundle);
                        mdashboard.displayWithoutViewFragmentWithanimWithoutv4(review_frgm);

                    } catch (Exception e) {
                    }
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder fholder = (FooterViewHolder) holder;
            fholder.btn_loadmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ReviewProfile_Fragment) frgmnt).onClickLoadMore();
                }
            });
        }

    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_container;
        private ImageView iconedit, image_isfvrt, imageview;
        private TextView name, address, distance, visiteddate, likes, dislikes;
        private RatingBar ratingBar;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            ll_container = (LinearLayout) itemLayoutView.findViewById(R.id.ll_container);
            iconedit = (ImageView) itemLayoutView.findViewById(R.id.iconedit);
            image_isfvrt = (ImageView) itemLayoutView.findViewById(R.id.image_isfvrt);
            imageview = (ImageView) itemLayoutView.findViewById(R.id.imageview);
            name = (TextView) itemLayoutView.findViewById(R.id.name);
            address = (TextView) itemLayoutView.findViewById(R.id.address);
            distance = (TextView) itemLayoutView.findViewById(R.id.distance);
            visiteddate = (TextView) itemLayoutView.findViewById(R.id.visiteddate);
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

    @Override
    public int getItemViewType(int position) {
        // if (show_footer) {
        if (position > 49) {
            if (ispositionFooter(position)) {
                return FOOTER_WITHBUTTON;
            } else {
                return NORMAL;
            }
        } else {
            return NORMAL;
        }
        /*} else {
            return NORMAL;
        }*/
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
