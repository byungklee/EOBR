package com.eobr;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * GPSIntentService is to service the GPS request once.
 *
 */
public class GPSIntentService extends Service implements LocationListener {

    /**
     * Constant and variables
     */
    private static final String TAG = "GPSIntent";
    private String type;
    private String note;
    private LocationManager locationManager;

    public GPSIntentService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        type = intent.getStringExtra("type");
        note = intent.getStringExtra("note");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //requestionLocationUpdates(Provider, Min_Time in millisecond, Min_Distance in meter, listener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged");

        MyLocation myLocation = new MyLocation(type,
                location.getLatitude(),
                location.getLongitude());
        if(note != null) {
            myLocation.setNote(note);
        }

        if(type != null)
            saveData(myLocation);

        Intent localIntent= new Intent(Constants.BROAD_CAST_LOCATION_ONCE);

        localIntent.putExtra("type", type);
        localIntent.putExtra("latitude", location.getLatitude());
        localIntent.putExtra("longitude", location.getLongitude());

        //Broadcast the location update
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        //turn off the service
        locationManager.removeUpdates(this);
        this.stopSelf();
    }

    private void saveData(MyLocation location) {
        LocationList locationList = LocationList.getInstance();
        locationList.add(location);
        DbAdapter db = new DbAdapter(getApplicationContext());
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        Log.i(TAG, "Checking time string " + location.getTimeString());
        sqlDb.execSQL("insert into trips (trip_id, truck_id, trip_type, type, latitude, longitude, time, note) " +
                "values (" + MainActivity.CURRENT_TRIP_ID + ", \"" +MainActivity.TRUCK_ID + "\", \"" + MainActivity.tripType + "\", \"" +
                location.getType() + "\", " +
                location.getLatitude() + ", " + location.getLongitude() + ", \"" + location.getTimeString() + "\", \"" + location.getNote() + "\")");
        Log.i(TAG, location.getType() + " " + location.getLatitude() + " " + location.getLongitude());
        sqlDb.close();
        db.close();
        //mDetailStatusTextView.setText(getListString());
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
