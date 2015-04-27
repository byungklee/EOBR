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

import com.eobr.model.LocationList;
import com.eobr.model.MyLocation;

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
        if(intent.getStringExtra("type") == null)
            type = null;
        else
            type = intent.getStringExtra("type");
        note = intent.getStringExtra("note");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        System.out.println("GPS intent service up for " + type);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged");

        MyLocation myLocation = new MyLocation(type,
                location.getLatitude(),
                location.getLongitude());

        HttpPost post= new HttpPost();
        myLocation.setId(MainActivity.id++);
        myLocation.setTrip_id(MainActivity.CURRENT_TRIP_ID);
        myLocation.setTrip_type(MainActivity.tripType);
        if(type != null) {
            System.out.println("Sending intent gps data");
            if (note != null) {
                myLocation.setNote(note);
                post.sendFiles(myLocation);
            } else {
                post.sendJson(myLocation);
            }
            LocationList.getInstance().add(myLocation);
        }

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

//    private void saveData(MyLocation location) {
//        LocationList locationList = LocationList.getInstance();
//        locationList.add(location);
//        DbAdapter db = new DbAdapter(getApplicationContext());
//        SQLiteDatabase sqlDb = db.getWritableDatabase();
//        Log.i(TAG, "Checking time string " + location.getTimeString());
//        sqlDb.execSQL("insert into trips (trip_id, truck_id, trip_type, type, latitude, longitude, time, note) " +
//                "values (" + MainActivity.CURRENT_TRIP_ID + ", \"" +MainActivity.TRUCK_ID + "\", \"" + MainActivity.tripType + "\", \"" +
//                location.getType() + "\", " +
//                location.getLatitude() + ", " + location.getLongitude() + ", \'" + location.getJsonTime() + "\', \"" + location.getNote() + "\")");
//        Log.i(TAG, location.getType() + " " + location.getLatitude() + " " + location.getLongitude());
//        sqlDb.close();
//        db.close();
//        //mDetailStatusTextView.setText(getListString());
//    }

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
