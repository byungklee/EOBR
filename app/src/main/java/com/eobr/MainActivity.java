package com.eobr;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements GPSListener {

    public static List<MyLocation> myLocationList = new LinkedList<MyLocation>();
    public static boolean isRunning = false;
    public static String tripType = "";
    public static String TRUCK_ID = "1";
    public static int CURRENT_TRIP_ID = -1;
    public static Intent GPSIntent;
    private static final String TAG = "MainActivity";
    private static GPSReceiver gpsReceiver;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        TRUCK_ID = info.getMacAddress();

		if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.container, new LoginFragment()).commit();
		}

        gpsReceiver = new GPSReceiver(this);

        IntentFilter mLocationOnceFilter = new IntentFilter(Constants.BROAD_CAST_LOCATION_ONCE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                gpsReceiver,
                mLocationOnceFilter);
		getOverflowMenu();		
	}

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("MainActivity", "Resuming");
    }
	
	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch(NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            Log.i("MainActivity", "PopbackStack called.");
            getSupportFragmentManager().popBackStack();
            removeCurrentFragment();
        } else {
            Log.i("MainActivity", "super.back called");
            super.onBackPressed();
        }
    }

    public void removeCurrentFragment()
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment currentFrag =  getSupportFragmentManager().findFragmentById(R.id.container);

        if (currentFrag != null) {
            transaction.remove(currentFrag);
            transaction.commit();
        }

    }


    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch(id) {
			case R.id.action_home:
				Toast.makeText(getApplicationContext(), "To_home", Toast.LENGTH_SHORT).show();
                int index = getSupportFragmentManager().getBackStackEntryCount();
                while(index > 0) {

                    getSupportFragmentManager().popBackStack();
                    removeCurrentFragment();
                    index--;
                }

				break;
			case R.id.action_new_trip:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    removeCurrentFragment();
                }
                if(MainActivity.isRunning) {
                    Toast.makeText(getApplicationContext(), "There is currently a running trip.", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentManager fragmentManager2 = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    NewTripFragment fragment2 = new NewTripFragment();
                    fragmentTransaction2.replace(R.id.container, fragment2);
                    fragmentTransaction2.addToBackStack(null);
                    fragmentTransaction2.commit();
                }

				break;
			case R.id.action_view_trip:
				Toast.makeText(getApplicationContext(), "View", Toast.LENGTH_SHORT).show();
                if(MainActivity.isRunning) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                        removeCurrentFragment();
                    }

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction frt = fm.beginTransaction();
                    StatusFragment sfm = new StatusFragment();
                    frt.replace(R.id.container, sfm);
                    frt.addToBackStack(null);
                    frt.commit();
                } else {
                    Toast.makeText(getApplicationContext(), "There is no running trip.", Toast.LENGTH_SHORT).show();
                }
				break;
            case R.id.action_stop_trip:
                if(MainActivity.isRunning) {
                    Intent i = new Intent(getApplicationContext(), GPSIntentService.class);
                    i.putExtra("type", "stop");
                    startService(i);
                } else {
                    Toast.makeText(getApplicationContext(), "There is no running trip.", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    removeCurrentFragment();
                }
                break;
		}
		return false;
	}

    @Override
    public void execute(MyLocation location) {

    }

    @Override
    public void executeForSingle(String type, double latitude, double longitude) {
        if(type == null) {
            return;
        }
        if(type.equals("stop")) {
            MyLocation location = new MyLocation(type,
                    latitude,
                    longitude);
            MainActivity.myLocationList.add(location);
            DbAdapter db = new DbAdapter(getApplicationContext());
            SQLiteDatabase sqlDb = db.getWritableDatabase();
            Log.i(TAG, "Checking time string " + location.getTimeString());
            sqlDb.execSQL("insert into trips (trip_id, truck_id, trip_type, type, latitude, longitude, time) " +
                    "values (" + MainActivity.CURRENT_TRIP_ID + ", \"" + MainActivity.TRUCK_ID + "\", \"" + MainActivity.tripType + "\", \"" +
                    location.getType() + "\", " +
                    location.getLatitude() + ", " + location.getLongitude() + ", \"" + location.getTimeString() + "\")");
            Log.i(TAG, type + " " + latitude + " " + longitude);
            sqlDb.close();
            db.close();
            stopService(MainActivity.GPSIntent);
            CURRENT_TRIP_ID = -1;
            isRunning = false;
            myLocationList.clear();
        }
    }
}
