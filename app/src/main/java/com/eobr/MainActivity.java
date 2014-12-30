package com.eobr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity implements GPSListener {

    //public static List<MyLocation> myLocationList = new LinkedList<MyLocation>();
    public static boolean isRunning = false;
    public static String tripType = "";
    public static String TRUCK_ID = "1";
    public static int CURRENT_TRIP_ID = -1;
    public static Intent GPSIntent;
    private static final String TAG = "MainActivity";
    private static final String serverIp = "http://192.168.0.56:8888";
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
                index = getSupportFragmentManager().getBackStackEntryCount();
                while(index > 0) {

                    getSupportFragmentManager().popBackStack();
                    removeCurrentFragment();
                    index--;
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
                viewTrip();
				break;
            case R.id.action_stop_trip:
                if(MainActivity.isRunning) {

                    isRunning = false;
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

    public void viewTrip() {
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
    }

    @Override
    public void executeForSingle(String type, double latitude, double longitude, String note) {
        if(type == null) {
            return;
        }
        //when stop has been called
        if(type.equals("stop")) {
            MyLocation location = new MyLocation(type,
                    latitude,
                    longitude);
            location.setNote(note);
            //MainActivity.myLocationList.add(location);
            LocationList locationList = LocationList.getInstance();
            locationList.add(location);
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
        //    createKMLFile(locationList.getList());
           new HttpAsyncTask().execute(serverIp + "/add");


            Log.i(TAG,createJSON().toString());

            //locationList.clear();
        }
    }
    //Structure =
    //trip_id/trip_id/truck_id/trip_type/type/latitude/longitude/time/description
    public JSONObject createJSON() {
        JSONObject jsonObject = new JSONObject();

            DbAdapter db = new DbAdapter(getApplicationContext());
            SQLiteDatabase sqlDb = db.getReadableDatabase();
            Cursor cursor = sqlDb.rawQuery("select * from trips where trip_id=" + CURRENT_TRIP_ID + " order by id", null);


            Log.i(TAG, "cusor count " + cursor.getCount());
            if( cursor.getCount() < 1)
                return null;
            cursor.moveToFirst();


//        0 id getint
//        1 trip_id getint
//        2 truck_id getString
//        3 trip_type text
//        4 type text
//        5 latitude dobule
//        6 longti dobule
//        7 time text
//        8 note text

            do {
                JSONObject tempJson = new JSONObject();
                try {
                    tempJson.put("id", cursor.getInt(0)); // id
                    tempJson.put("trip_id", cursor.getInt(1)); // trip_id
                    tempJson.put("truck_id", cursor.getString(2)); //truck_id
                    tempJson.put("trip_type", cursor.getString(3)); // trip_type
                    tempJson.put("type", cursor.getString(4)); //type
                    tempJson.put("latitude", cursor.getDouble(5)); //latitude
                    tempJson.put("longitude", cursor.getDouble(6)); // longtitude
                    tempJson.put("time", cursor.getString(7)); // time
                    tempJson.put("note", cursor.getString(8)); // note
                    jsonObject.accumulate("record", tempJson);
                } catch (JSONException e) {
                    Log.d(TAG, e.getLocalizedMessage());
                }
            } while(cursor.moveToNext());
        Log.i(TAG, jsonObject.toString());
        return jsonObject;
    }

    public void createKMLFile(List<MyLocation> locationList) {
        //FileOutputStream outputStream;

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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String POST(String url, JSONObject jsonObj){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";

            // 3. build jsonObject

            // 4. convert JSONObject to JSON to String
            json = jsonObj.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        Log.i(TAG, "RESULT: " + result);
        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            LocationList.getInstance().getList();
            return POST(urls[0], createJSON());
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "RESULT: " + result);
            Toast.makeText(getBaseContext(), "Data Sent! " + result, Toast.LENGTH_LONG).show();
            CURRENT_TRIP_ID = -1;
            LocationList.getInstance().clear();
        }
    }
}
