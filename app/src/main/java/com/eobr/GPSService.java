package com.eobr;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class GPSService extends Service implements LocationListener {

    private static final String TAG = "GPSService";
    private LocationManager locationManager;
    //Since we are going to keep adding location to the list
    //Choosing linkedlist over arraylist.

    public GPSService() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //requestionLocationUpdates(Provider, Min_Time in millisecond, Min_Distance in meter, listener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
        Log.i(TAG, "Gps Service Enabled");
        Toast.makeText(getApplicationContext(), "GPS Service Enabled", Toast.LENGTH_SHORT).show();
    }

    public void onDestroy() {
        Log.i("GPSService", "GPSService stopped!");
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationManager.removeUpdates(this);
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location has been changed! " + location.getLatitude() + " " + location.getLongitude());
        Intent localIntent= new Intent(Constants.BROAD_CAST_LOCATION);
        localIntent.putExtra("latitude", location.getLatitude());
        localIntent.putExtra("longitude", location.getLongitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
           Log.i(TAG, "Gps Service Enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "Gps Service Disabled");
    }


}
