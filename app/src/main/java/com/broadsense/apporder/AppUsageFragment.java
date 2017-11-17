package com.broadsense.apporder;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

/**
 * Fragment containing two ListViews showing user's app usage time and order used
 */
public class AppUsageFragment extends Fragment {
    private Context context;
    private UStats uStats;

    private List<String> topUsedList;
    private ArrayAdapter<String> topUsedArrayAdapter;

    private List<String> orderUsedList;
    private ListView sequenceUsedLV;
    private ArrayAdapter<String> orderUsedArrayAdapter;


    private String TAG = "AppUsageFragment.class";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_usage, container, false);
        context = v.getContext();

        // pre-lollipop Poll ActivityManager to get the currently running apps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG, "Build Version is Lollipop or greater. Using UsageStats package");

            // Check if usage stats permission is enabled
            if (UStats.getUsageStatsList(context).isEmpty()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }

            // Initialize UStats class instance
            uStats = new UStats(context);

            // Initialize string list values
            topUsedList = uStats.getUsageStatsListStrings(context);

            orderUsedList = uStats.getUsageSequenceListStrings(context);

            // Setup listView for displaying most recent apps.
            ListView topUsedLV = v.findViewById(R.id.topUsedListView);
            // Initialize arrayAdapter
            topUsedArrayAdapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_list_item_1,
                    topUsedList);
            topUsedLV.setAdapter(topUsedArrayAdapter);

            // Setup listView for displaying sequence of app usage
            sequenceUsedLV = v.findViewById(R.id.sequenceListView);
            // Initialize arrayAdapter
            orderUsedArrayAdapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_list_item_1,
                    orderUsedList);
            sequenceUsedLV.setAdapter(orderUsedArrayAdapter);
            // Scroll to bottom of sequence list view
            sequenceUsedLV.setSelection(orderUsedArrayAdapter.getCount() - 1);

            Button usageBtn = v.findViewById(R.id.usage_btn);
            usageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Update ListView top used apps
                    topUsedArrayAdapter.clear();
                    topUsedList = uStats.getUsageStatsListStrings(context);
                    topUsedArrayAdapter.addAll(topUsedList);
                    topUsedArrayAdapter.notifyDataSetChanged();
                    Log.i(TAG, "User updated usage time stats");
                }
            });

            Button orderBtn = v.findViewById(R.id.order_btn);
            orderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Update ListView sequence of apps used
                    orderUsedArrayAdapter.clear();
                    orderUsedList = uStats.getUsageSequenceListStrings(context);
                    orderUsedArrayAdapter.addAll(orderUsedList);
                    orderUsedArrayAdapter.notifyDataSetChanged();
                    // Scroll to bottom of sequence list view
                    sequenceUsedLV.setSelection(orderUsedArrayAdapter.getCount() - 1);
                    Log.i(TAG, "User updated usage sequence stats");
                }
            });


        } // end conditional if: lollipop+

        return v;
    }
}
