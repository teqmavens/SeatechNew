package com.vadevelopment.RedAppetite.newssection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vadevelopment.RedAppetite.Consts;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;
import com.vadevelopment.RedAppetite.moresection.AdvertisementFragment;

import java.util.ArrayList;

/**
 * Created by vibrantappz on 3/23/2017.
 */

public class NewsSeemore_Fragment extends android.app.Fragment {

    private DashboardActivity mdashboard;
    private RecyclerView recyclerView;
    private com.vadevelopment.RedAppetite.adapters.Adapters.AdapterNewsSeeMore adapter;
    private ArrayList<NewsSkeletonInside> arraylist;
    private TextView categoryvalue, buildingname;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgm_newsseemore, container, false);
        mdashboard = (DashboardActivity) getActivity();
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mdashboard.setting.setVisibility(View.VISIBLE);
        mdashboard.setting.setText("Back");
        mdashboard.textsearchimage.setVisibility(View.GONE);
        mdashboard.rl_headingwithcount.setVisibility(View.VISIBLE);
        mdashboard.text_heading.setText(getString(R.string.Events));


        mdashboard.search.setText("Events");
        mdashboard.centertextsearch.setVisibility(View.GONE);
        mdashboard.mapicon.setVisibility(View.GONE);
        //  mdashboard.mapButton.setVisibility(View.INVISIBLE);
        //  mdashboard.mapButton.setText("Add");

        mdashboard.setting.setBackgroundResource(R.drawable.backbtn);
        categoryvalue = (TextView) view.findViewById(R.id.categoryvalue);
        buildingname = (TextView) view.findViewById(R.id.buildingname);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (getArguments() != null) {
            arraylist = new ArrayList<>();
            arraylist = getArguments().getParcelableArrayList(Consts.NEWS_INSIDEARRAYLIST);

            mdashboard.text_headingcount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mdashboard.text_headingcount.setText(String.valueOf(arraylist.size()));
            categoryvalue.setText(getArguments().getString(Consts.NEWS_CATEGORYTYPE));
            buildingname.setText(getArguments().getString(Consts.NEWS_BUILDINGNAME));
            adapter = new com.vadevelopment.RedAppetite.adapters.Adapters.AdapterNewsSeeMore(getActivity(), getFragmentManager(), arraylist);
            recyclerView.setAdapter(adapter);
        }

        mdashboard.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        mdashboard.textsearchimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mdashboard.displayWithoutViewFragment(new Filter_NewsFragment());
            }
        });

        mdashboard.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdashboard.displayWithoutViewFragmentWithoutv4(new AdvertisementFragment());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mdashboard.rl_headingwithcount.setVisibility(View.GONE);
    }
}

