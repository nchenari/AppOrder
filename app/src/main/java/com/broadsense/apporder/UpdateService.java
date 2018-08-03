package com.broadsense.apporder;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Nima Chenari on 10/28/2015.
 */
public class UpdateService extends Service {

    private String TAG = "UpdateService.class";

    @Override
    public void onCreate() {
        super.onCreate();

        // register receiver that handles screen on and off logic
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter2.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver2 = new ScreenReceiver();
        registerReceiver(mReceiver2, filter2);

        // Service called due to ServiceAtBootReceiver.class (extends BroadCastReceiver)
        // Start activity to demonstrate.
        Intent testIntent = new Intent();
        testIntent.setClass(this, MainActivity.class);
        testIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(testIntent);


    } // end Service callback method onCreate

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean screenOn = intent.getBooleanExtra("screen_state", false);

        if(!screenOn) { // if screen is OFF
            // Do not record any app usage data. Conserve battery
            Log.d(TAG, "Screen turned off");

        } else { // if screen is ON
            // Record app usage data at periodic intervals.

            // Test whether service's screen is on
            Log.d(TAG, "Screen turned on");
            Intent testIntent = new Intent();
            testIntent.setClass(this, MainActivity.class);
            testIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(testIntent);
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        // We don't provide binding, so return null
        return null;

    }

    @Override
    public void onDestroy() {

        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();

    }

} // end class UpdateService
