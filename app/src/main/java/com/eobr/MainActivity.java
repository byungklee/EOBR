package com.eobr;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
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

import com.eobr.model.LocationList;
import com.eobr.model.MyLocation;
import com.eobr.model.NoteList;
import com.eobr.resource.ResourceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 *
 * The Main driver class.
 * Top level in hierarchy which handles required data, and all the views are handled by this activity.
 */
public class MainActivity extends ActionBarActivity implements GPSListener {

    public enum ServiceState {READY, RUNNING, WAIT}
    public static ServiceState state = ServiceState.READY;
    public static String tripType = "";
    public static String TRUCK_ID = "1";
    public static int CURRENT_TRIP_ID = -1;
    public static Intent GPSIntent;
    private static final String TAG = "MainActivity";
    public static final String serverIp = "http://134.139.249.76";
//    public static final String serverIp = "http://192.168.0.56";
//      public static final String serverIp = "http://192.168.0.23";
    public static final int port = 8888;
    public static final String serverIpAndPort = serverIp+":"+port;

    public static int id=0;
    public static Context mContext;

    private static GPSReceiver gpsReceiver;
    public static ResourceManager rm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        //Get the Mac Address to use it as Unique ID
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        TRUCK_ID = info.getMacAddress();

		if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.container, new LoginFragment()).commit();
		}

        //Register receiver to listen on stop.


	}

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("MainActivity", "Resuming");
        gpsReceiver = new GPSReceiver(this);
        IntentFilter mLocationOnceFilter = new IntentFilter(Constants.BROAD_CAST_LOCATION_ONCE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                gpsReceiver,
                mLocationOnceFilter);
        getOverflowMenu();

        DbAdapter db = new DbAdapter(getApplicationContext());
        db.onCreate(db.getWritableDatabase());
        rm = new ResourceManager(this);
        rm.execute();
        mContext = this;
    }

    //Using this method because Samsung does not allow menu on the action bar for testing.
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
        FragmentManager fm = getSupportFragmentManager();
        int stackCount = fm.getBackStackEntryCount();
        if (stackCount > 1) {
            String temp =fm.getBackStackEntryAt(stackCount - 1).getName();
            if(temp.equals("new")) {
                fm.popBackStack("main", android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {
                super.onBackPressed();
            }
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

    private void popToMain() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack("main", android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Implmentation of menu
     * @param item
     * @return
     */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch(id) {
			case R.id.action_home:
				Toast.makeText(getApplicationContext(), "To_home", Toast.LENGTH_SHORT).show();
                popToMain();
				break;
			case R.id.action_new_trip:
                popToMain();
                if(MainActivity.state == ServiceState.RUNNING) {
                    Toast.makeText(getApplicationContext(), "There is currently a running trip.", Toast.LENGTH_SHORT).show();

                } else {
                    FragmentManager fragmentManager2 = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    NewTripFragment fragment2 = new NewTripFragment();
                    fragmentTransaction2.replace(R.id.container, fragment2);
                    fragmentTransaction2.addToBackStack("main");
                    fragmentTransaction2.commit();
                }
				break;
			case R.id.action_view_trip:
				Toast.makeText(getApplicationContext(), "View", Toast.LENGTH_SHORT).show();
                viewTrip();
				break;
            case R.id.action_stop_trip:
                if(MainActivity.state == ServiceState.RUNNING) {
                    state = ServiceState.WAIT;
                    Intent i = new Intent(getApplicationContext(), GPSIntentService.class);
                    i.putExtra("type", "stop");
                    startService(i);
                    LoginFragment.setStartButton(true);

                } else {
                    Toast.makeText(getApplicationContext(), "There is no running trip.", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();
                popToMain();
                break;
		}
		return false;
	}

    public void viewTrip() {
        if(MainActivity.state == ServiceState.RUNNING) {
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
    }

    @Override
    public void execute() {
        //Do nothing
    }

    /**
     * Listener to catch the stop call.
     * @param myLocation
     */

    @Override
    public void executeForSingle(MyLocation myLocation) {
        if(myLocation.getType() == null) {
            return;
        }
        //when stop has been called
        if(myLocation.getType().equals("stop")) {
            stopService(MainActivity.GPSIntent);
           toInitialState();
            rm.execute();
        }

    }


    /**
     * Creates KML File on a device or an external drive. Currently, not using because the server is
     * generating.
     * @param locationList
     */
    public void createKMLFile(List<MyLocation> locationList) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
                "\t<Document>\n");

        for(MyLocation ml:locationList) {
            int i = 1;
            sb.append("\t\t<Placemark>\n")
                    .append("\t\t\t<name>").append("point").append(i).append("</name>\n")
                    .append("\t\t\t<description>").append(ml.getType()).append("</description>\n")
                    .append("\t\t\t<point>\n")
                    .append("\t\t\t\t<coordinates>").append(ml.getLatitude()).append(",").append(ml.getLongitude()).append("</coordinates>")
                    .append("\t\t\t</point>\n</Placemark>\n");
        }
        sb.append("\t</Document>\n" +
                "</kml>\n");
        String filename = MainActivity.TRUCK_ID + " " + MainActivity.CURRENT_TRIP_ID + ".kml";
        if(isExternalStorageWritable()) {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/eobr");
            Log.i("Main", dir.getAbsolutePath());
            dir.mkdirs();
            File file = new File(dir, filename);
            try {
                FileOutputStream f = new FileOutputStream(file);
                f.write(sb.toString().getBytes());
                f.close();
            } catch(FileNotFoundException e) {
                 Log.e(TAG, e.getLocalizedMessage());
            } catch(IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

        } else {
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(sb.toString().getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Method to check there is an external storage.
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static void toInitialState() {
        Log.i(TAG, "To initial State");
        //saveData();
        NoteList.getInstance().clear();
        LocationList.getInstance().clear();
        CURRENT_TRIP_ID = -1;
        id = 0;
        state = ServiceState.READY;
    }
}
