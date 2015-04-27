package com.eobr;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.eobr.model.LocationList;
import com.eobr.model.MyLocation;
import com.eobr.model.NoteList;

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
    private HttpPost httpPost;
    public static int INTERVAL = 60000;
    private BluetoothChecker mBluetoothChecker;

    private BroadcastReceiver mBluetoothConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getIntExtra("Connection", 0) == 0) {
                turnOnGps();
            } else if(intent.getIntExtra("Connection", 0) == 1) {
                Intent i = new Intent(getApplicationContext(), GPSIntentService.class);
                i.putExtra("type", "stop");
                startService(i);
//                turnOffGps();
            }
        }
    };

    public GPSService() {
        System.out.println("GPSService construction");
        mBluetoothChecker = new BluetoothChecker(this);
        httpPost = new HttpPost();
        if(MainActivity.DEBUG) {
            INTERVAL = 5000;
        } else {
            INTERVAL = 60000;
        }
    }

    @Override
    public void onCreate() {
        System.out.println("GPSService onCreate()");
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(BluetoothChecker.BLUETOOTH_CONNECTION);
        LocalBroadcastManager.getInstance(GPSService.this.getApplicationContext()).registerReceiver(mBluetoothConnectionReceiver,intentFilter);
        mBluetoothChecker.runBluetoothChecker();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    public void turnOnGps() {
        if(MainActivity.CURRENT_TRIP_ID == -1) {
            DbAdapter db = new DbAdapter(getApplicationContext());
            SQLiteDatabase sqlDb = db.getReadableDatabase();
            SQLiteDatabase writeDb = db.getWritableDatabase();
            String query="select trip_id from trip_id";
            Cursor curs=sqlDb.rawQuery(query, null);
            curs.moveToFirst();
            if(curs.getCount() != 0) {
                MainActivity.CURRENT_TRIP_ID = curs.getInt(0)+1;
                writeDb.execSQL("update trip_id set trip_id="+ MainActivity.CURRENT_TRIP_ID + " where trip_id=" +(MainActivity.CURRENT_TRIP_ID-1));
            } else {
                MainActivity.CURRENT_TRIP_ID = 1;
                writeDb.execSQL("insert into trip_id (trip_id) values (1)");
            }
            curs.close();
        }

        NoteList.getInstance().clear();
        LocationList.getInstance().clear();
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL, 0, this);
        Log.i(TAG, "Gps Service Enabled");
        Toast.makeText(getApplicationContext(), "GPS Service Enabled", Toast.LENGTH_SHORT).show();
        isFirst = true;
        MainActivity.state = MainActivity.ServiceState.RUNNING;

        Intent localIntent= new Intent(Constants.GPS_SERVICE_STATUS);
        localIntent.putExtra("Status", 1);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    public void turnOffGps() {
        Toast.makeText(getApplicationContext(), "GPS Service Disabled", Toast.LENGTH_SHORT).show();
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.removeUpdates(this);
        MainActivity.state = MainActivity.ServiceState.READY;
        Intent localIntent= new Intent(Constants.GPS_SERVICE_STATUS);
        localIntent.putExtra("Status", 0);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("GPSService on Start command");

//        turnOnGps();
        return super.onStartCommand(intent, flags, startId);
    }

    public void kill() {
        mBluetoothChecker.stopBluetoothChecker();
    }

    public void onDestroy() {
        mBluetoothChecker = null;

        super.onDestroy();
        Log.i("GPSService", "GPSService stopped!");
        if(MainActivity.state == MainActivity.ServiceState.RUNNING) {
            turnOffGps();
        }
        LocalBroadcastManager.getInstance(GPSService.this.getApplicationContext()).unregisterReceiver(mBluetoothConnectionReceiver);

    }


    private IBinder mBinder;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        System.out.println("onBind Called");
        mBinder = new LocalBinder();
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public Service getService() {
            return GPSService.this;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location has been changed! " + location.getLatitude() + " " + location.getLongitude());
        MyLocation myLocation = new MyLocation(isFirst ? "start" : type, location.getLatitude(),location.getLongitude());
        myLocation.setId(MainActivity.id++);
        myLocation.setTrip_id(MainActivity.CURRENT_TRIP_ID);
        myLocation.setTrip_type(MainActivity.tripType);
        LocationList.getInstance().add(myLocation);
        //saveData(myLocation);
        httpPost.sendJson(myLocation);

        Intent localIntent= new Intent(Constants.BROAD_CAST_LOCATION);
//        //Put the type as first when it's first time.
//        localIntent.putExtra("type", myLocation.getType());
        isFirst = false;
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void saveData(MyLocation location) {
        DbAdapter db = new DbAdapter(getApplicationContext());

        SQLiteDatabase sqlDb = db.getWritableDatabase();
        sqlDb.execSQL("insert into trips (id, trip_id, truck_id, trip_type, type, latitude, longitude, time) " +
                "values (" + location.getId() + ", \"" + MainActivity.CURRENT_TRIP_ID + ", \"" +MainActivity.TRUCK_ID + "\", \"" + MainActivity.tripType + "\", \"" + location.getType() + "\", " +
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
