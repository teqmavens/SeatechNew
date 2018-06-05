package com.vadevelopment.RedAppetite.moresection;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vadevelopment.RedAppetite.R;

/**
 * Created by Vibrant Android on 11-03-2017.
 */

public class TellAFriendFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//layout tell friend pending
        View view = inflater.inflate(R.layout.tellafriend, container, false);
        return view;
    }
}
