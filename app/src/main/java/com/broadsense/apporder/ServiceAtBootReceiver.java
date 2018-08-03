package com.broadsense.apporder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Nima Chenari on 10/28/2015.
 * Listens for system boot up to start service with device.
 */


public class ServiceAtBootReceiver extends BroadcastReceiver {

    private String TAG = "ServiceAtBootReceiver.class";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, UpdateService.class);
            context.startService(serviceIntent);
            Log.d(TAG, "System Boot complete. UpdateService started.");
        }

    } // end method onReceive

} // end class ServiceAtBootReceiver

