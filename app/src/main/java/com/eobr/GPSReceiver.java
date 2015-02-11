package com.eobr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eobr.model.MyLocation;

/**
 * GPSReceiver is a listener. for GPS work and signals to whoever is listening.
 */
public class GPSReceiver extends BroadcastReceiver {

    private GPSListener gpsListener;

    public GPSReceiver(){}
    /**
     * Constructor
     * @param gpsListener
     */
    public GPSReceiver(GPSListener gpsListener) {
        this.gpsListener = gpsListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i("GPSReceiver", "GPS info has been received " + intent.getAction() + " " + intent.getType() );
        if(intent.getAction().equals(Constants.BROAD_CAST_LOCATION)) {
            Log.i("GPSReceiver", "constant" );
            gpsListener.execute();
        } else if(intent.getAction().equals(Constants.BROAD_CAST_LOCATION_ONCE)) {
            MyLocation location = new MyLocation(intent.getStringExtra("type"),
                    intent.getDoubleExtra("latitude", 0),
                    intent.getDoubleExtra("longitude", 0));
            gpsListener.executeForSingle(location);
        }
    }
}
