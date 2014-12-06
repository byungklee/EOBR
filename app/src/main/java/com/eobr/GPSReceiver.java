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
    private List<MyLocation> locationList = new LinkedList<MyLocation>();

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
        Log.i("GPSReceiver", "GPS info has been received");
        MyLocation location = new MyLocation(intent.getDoubleExtra("latitude",0), intent.getDoubleExtra("longitude",0));
        locationList.add(location);
        gpsListener.execute("Updated", location.getLatitude(), location.getLongitude());
        //throw new UnsupportedOperationException("Not yet implemented");
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
