package com.vadevelopment.RedAppetite.moresection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vadevelopment.RedAppetite.HandyObjects;
import com.vadevelopment.RedAppetite.R;
import com.vadevelopment.RedAppetite.dashboard.DashboardActivity;

/**
 * Created by Vibrant Android on 11-03-2017.
 */

public class AppPurchaseFragment extends android.app.Fragment {

    DashboardActivity mDashboardActivity;
    Button btn_register;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDashboardActivity = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.apppurchasefragment, container, false);
        mDashboardActivity.footer.setVisibility(View.VISIBLE);
        mDashboardActivity.centertextsearch.setVisibility(View.VISIBLE);
        mDashboardActivity.centertextsearch.setText(getString(R.string.inaapp_purchase));
        mDashboardActivity.setting.setVisibility(View.VISIBLE);
        mDashboardActivity.setting.setText(getString(R.string.back));
        mDashboardActivity.setting.setBackgroundResource(R.drawable.backbtn);
        btn_register = (Button) view.findViewById(R.id.btn_register);
        HandyObjects.SetButton_TEXGYREHEROES_BOLD(getActivity(),btn_register);

        mDashboardActivity.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        return view;
    }
}
