package com.eobr;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class GPSIntentService extends Service implements LocationListener {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
   private static final String TAG = "GPSIntent";
    private LocationManager locationManager;
    public GPSIntentService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public static void startAction(Context context) {
//        Intent intent = new Intent(context, GPSIntentService.class);
//        intent.setAction("Single Update");
//        context.startService(intent);
//    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, this);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        Log.i(TAG, "onhandled");
//    }

    private String type;

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        type = intent.getStringExtra("type");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //requestionLocationUpdates(Provider, Min_Time in millisecond, Min_Distance in meter, listener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        //Log.i(TAG, "Gps Service Enabled");
        //Toast.makeText(getApplicationContext(), "GPS Service Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged");
        Intent localIntent= new Intent(Constants.BROAD_CAST_LOCATION_ONCE);
        localIntent.putExtra("status", location.getLatitude() + " " + location.getLongitude());
        localIntent.putExtra("type", type);
        localIntent.putExtra("latitude", location.getLatitude());
        localIntent.putExtra("longitude", location.getLongitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        locationManager.removeUpdates(this);
        this.onDestroy();
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
