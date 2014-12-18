package com.eobr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class GPSReceiver extends BroadcastReceiver {
    public GPSReceiver() {
    }

    List<MyLocation> locationList = new LinkedList<MyLocation>();
    GPSListener gpsListener;
    public GPSReceiver(GPSListener gpsListener) {
        this.gpsListener = gpsListener;
    }

//    public MyReceiver(MainActivity.MyListener ml) {
//
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i("GPSReceiver", "GPS info has been received " + intent.getAction() + " " + intent.getType() );
        if(intent.getAction().equals(Constants.BROAD_CAST_LOCATION)) {
            Log.i("GPSReceiver", "constant" );
            MyLocation location = new MyLocation(intent.getStringExtra("type"),
                    intent.getDoubleExtra("latitude", 0),
                    intent.getDoubleExtra("longitude", 0));
            LocationList ll = LocationList.getInstance();
            ll.add(location);
            gpsListener.execute(location);
        } else if(intent.getAction().equals(Constants.BROAD_CAST_LOCATION_ONCE)) {
            MyLocation location = new MyLocation(intent.getStringExtra("type"),
                    intent.getDoubleExtra("latitude", 0),
                    intent.getDoubleExtra("longitude", 0));
            String noteTemp = intent.getStringExtra("note");
            if(noteTemp != null) {
                gpsListener.executeForSingle(location.getType(), location.getLatitude(), location.getLongitude(), noteTemp);
            } else {
                gpsListener.executeForSingle(location.getType(), location.getLatitude(), location.getLongitude(), null);
            }


        }
    }

    public List getLocationList() {
        return locationList;
    }

    public String getListString() {
        StringBuilder sb = new StringBuilder();
        for(MyLocation ml : locationList) {
            sb.append(ml.getLatitude()).append(" ").append(ml.getLongitude()).append(" ").append(ml.getTimeString()).append("\n");
        }
        return sb.toString();
    }
}
