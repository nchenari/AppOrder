package com.broadsense.apporder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Nima Chenari on 10/28/2015.
 * Listens for screen ON and OFF events
 */

public class ScreenReceiver extends BroadcastReceiver {
    private boolean screenOff;

    private String TAG = "ScreenReceiver.class";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
        } else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
        }
        Intent i = new Intent(context, UpdateService.class);
        i.putExtra("screen_state", screenOff);
        context.startService(i);
    } // end method onReceive

} // end class ScreenReceiver
