package com.vadevelopment.RedAppetite.adapters.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vadevelopment.RedAppetite.AppController;
import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;
import com.vadevelopment.RedAppetite.searchsection.Skeleton_SearchFirst;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vibrantappz on 3/21/2017.
 */

public class Adapter_FavoriteProfile extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private DashboardActivity mdashboard;
    ArrayList<Skeleton_SearchFirst> arraylist;
    private SharedPreferences preferences;
    private android.app.FragmentManager fm;
    private static final int FOOTER_WITHBUTTON = 1;
    private static final int NORMAL = 2;
    android.app.Fragment frgmnt;
    boolean show_footer;

    public Adapter_FavoriteProfile(Context context, android.app.FragmentManager fm, ArrayList<Skeleton_SearchFirst> arraylist, android.app.Fragment frgmnt, boolean show_footer) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        this.show_footer = show_footer;
        this.frgmnt = frgmnt;
        mdashboard = (DashboardActivity) context;
        this.arraylist = arraylist;
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_favoriteprofile, parent, false);
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
            // viewHolder.ratingstars.setText(arraylist.get(position).getAvgrating());

            if (arraylist.get(position).getAvgrating().length() == 1) {
                viewHolder.ratingstars.setText(arraylist.get(position).getAvgrating() + ".0");
            } else {
                double ii = Double.parseDouble(arraylist.get(position).getAvgrating());
                viewHolder.ratingstars.setText(String.valueOf(new DecimalFormat("##.#").format(ii)));
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
            if (Integer.parseInt(arraylist.get(position).getIsreviewed()) > 0) {
                viewHolder.image_isreviewd.setVisibility(View.VISIBLE);
            } else {
                viewHolder.image_isreviewd.setVisibility(View.INVISIBLE);
            }
            viewHolder.ll_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    com.vadevelopment.RedAppetite.profilesection.DetailDltFrmFvrt_EdtRvw_Fragment fvrtdeatil_frgm = new com.vadevelopment.RedAppetite.profilesection.DetailDltFrmFvrt_EdtRvw_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Consts.PROFILE_CLICKLID, arraylist.get(position).getLid());
                    bundle.putString(Consts.CHECKCLICK_FROM, "fvrt");
                    fvrtdeatil_frgm.setArguments(bundle);
                    mdashboard.displayWithoutViewFragmentWithanimWithoutv4(fvrtdeatil_frgm);

                }
            });

            viewHolder.icondelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayDeleteDialog(arraylist.get(position).getBuildingname(), arraylist.get(position).getLid(), position);
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder fholder = (FooterViewHolder) holder;
            fholder.btn_loadmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((com.vadevelopment.RedAppetite.profilesection.FavoritesProfile_Fragment) frgmnt).onClickLoadMore();
                }
            });
        }
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, address, distance, users, likes, dislikes, ratingstars;
        private RatingBar ratingBar;
        private ImageView imageview, icondelete, image_isreviewd;
        private LinearLayout ll_container;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            name = (TextView) itemLayoutView.findViewById(R.id.name);
            address = (TextView) itemLayoutView.findViewById(R.id.address);
            distance = (TextView) itemLayoutView.findViewById(R.id.distance);
            users = (TextView) itemLayoutView.findViewById(R.id.users);
            ratingstars = (TextView) itemLayoutView.findViewById(R.id.ratingstars);
            likes = (TextView) itemLayoutView.findViewById(R.id.likes);
            dislikes = (TextView) itemLayoutView.findViewById(R.id.dislikes);
            ratingBar = (RatingBar) itemLayoutView.findViewById(R.id.ratingBar);
            imageview = (ImageView) itemLayoutView.findViewById(R.id.imageview);
            icondelete = (ImageView) itemLayoutView.findViewById(R.id.icondelete);
            image_isreviewd = (ImageView) itemLayoutView.findViewById(R.id.image_isreviewd);
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

    private void displayDeleteDialog(final String buildingname, final String lid, final int position) {
        final Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        final Dialog mediaDialog = new Dialog(context);
        mediaDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mediaDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        mediaDialog.setContentView(R.layout.dialog_dltfavorite);
        LinearLayout approx_lay = (LinearLayout) mediaDialog.findViewById(R.id.approx_lay);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w - 80, (h / 4) - 20);
        approx_lay.setLayoutParams(params);
        TextView heading_text = (TextView) mediaDialog.findViewById(R.id.heading_text);
        TextView yes = (TextView) mediaDialog.findViewById(R.id.yes);
        TextView no = (TextView) mediaDialog.findViewById(R.id.no);
        heading_text.setText(context.getString(R.string.want_delete) + " " + buildingname + " " + context.getString(R.string.frm_fvrt) + "?");


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
                Deletefvrt_task(buildingname, lid, position);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog.dismiss();
            }
        });
        mediaDialog.show();
    }

    private void Deletefvrt_task(final String b_name, final String lid, final int position) {
        String tag_json_obj = "json_obj_req";
        com.vadevelopment.RedAppetite.HandyObjects.showProgressDialog(context);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                com.vadevelopment.RedAppetite.HandyObjects.DELETEFVRT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("deletefvrt", response.toString());
                try {
                    JSONObject jboj = new JSONObject(response);
                    if (jboj.getString("message").equalsIgnoreCase("success") || jboj.getString("message").equalsIgnoreCase("Success")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, context.getString(R.string.succdelete_fvrt) + " " + b_name + " " + context.getString(R.string.frm_fvrt));
                        arraylist.remove(position);
                        //notifyItemChanged(position);
                        notifyDataSetChanged();
                    } else if (jboj.getString("message").equalsIgnoreCase("fail")) {
                        com.vadevelopment.RedAppetite.HandyObjects.showAlert(context, jboj.getString("message"));
                    }
                } catch (Exception e) {
                }
                com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                com.vadevelopment.RedAppetite.HandyObjects.stopProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", preferences.getString(Consts.USER_ID, ""));
                params.put("lid", lid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
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

    public void hideFooter() {
        show_footer = false;
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
