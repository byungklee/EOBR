package com.eobr;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.eobr.model.LocationList;
import com.eobr.model.MyLocation;

/**
 * GPSService to service the location update constantly while running
 */
public class GPSService extends Service implements LocationListener {

    /**
     * Constant and variables
     */
    private static final String TAG = "GPSService";
    private LocationManager locationManager;
    private final String type = "Running";
    private boolean isFirst;
    public static final int INTERVAL = 10000;

    public GPSService() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL, 0, this);
        Log.i(TAG, "Gps Service Enabled");
        Toast.makeText(getApplicationContext(), "GPS Service Enabled", Toast.LENGTH_SHORT).show();
        isFirst = true;
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
        MyLocation myLocation = new MyLocation(isFirst ? "start" : type, location.getLatitude(),location.getLongitude());
        LocationList.getInstance().add(myLocation);
        saveData(myLocation);

        Intent localIntent= new Intent(Constants.BROAD_CAST_LOCATION);
        //Put the type as first when it's first time.
        localIntent.putExtra("type", myLocation.getType());
        isFirst = false;
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void saveData(MyLocation location) {
        DbAdapter db = new DbAdapter(getApplicationContext());

        SQLiteDatabase sqlDb = db.getWritableDatabase();
        sqlDb.execSQL("insert into trips (trip_id, truck_id, trip_type, type, latitude, longitude, time) " +
                "values (" + MainActivity.CURRENT_TRIP_ID + ", \"" +MainActivity.TRUCK_ID + "\", \"" + MainActivity.tripType + "\", \"" + location.getType() + "\", " +
                location.getLatitude() + ", " + location.getLongitude() + ", \'" + location.getJsonTime() + "\')");
        sqlDb.close();
        db.close();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {
           Log.i(TAG, "Gps Service Enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "Gps Service Disabled");
    }

}
