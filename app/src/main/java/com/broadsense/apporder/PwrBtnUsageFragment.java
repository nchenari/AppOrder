package com.broadsense.apporder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nchen on 11/7/2015.
 * Fragment displays user power button usage count
 */

public class PwrBtnUsageFragment extends Fragment {

    private String TAG = "PwrBtnUsageFragment.class";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_app_usage, container, false);
        Context context = v.getContext();

        return v;
    }
}
