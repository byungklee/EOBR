package com.eobr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * The Main driver class.
 * Top level in hierarchy which handles required data, and all the views are handled by this activity.
 */
public class MainActivity extends ActionBarActivity implements GPSListener, Callback {

    public enum ServiceState {READY, RUNNING, WAIT}
    public static ServiceState state = ServiceState.READY;

    public static String tripType = "";
    public static String TRUCK_ID = "1";
    public static int CURRENT_TRIP_ID = -1;
    public static Intent GPSIntent;
    private static final String TAG = "MainActivity";
    //private static final String serverIpAndPort = "http://134.139.249.76:8888";
    public static final String serverIp = "http://134.139.249.76";
    public static final int port = 8888;
    public static final String serverIpAndPort = serverIp+":"+port;
//    private static final String serverIp = "http://192.168.0.23";

    private static GPSReceiver gpsReceiver;
    private ResourceManager rm;

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
	}

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("MainActivity", "Resuming");
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
    public void callback() {
        Log.i(TAG, "callback for http request");
        new HttpJsonAsyncTask().execute(serverIp+":"+port + "/add");
        new HttpPostMultiEntityAsyncTask().execute("");
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
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
                if(getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStack("main", android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
				break;
			case R.id.action_new_trip:
                if(getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStack("main", android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
//                    isRunning = false;
//                    wait = true;
                    Intent i = new Intent(getApplicationContext(), GPSIntentService.class);
                    i.putExtra("type", "stop");
                    startService(i);
                    LoginFragment.setStartButton(true);

                } else {
                    Toast.makeText(getApplicationContext(), "There is no running trip.", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();

                if(getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStack("main", android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

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
            Log.i(TAG, "Execute Server status");
            new HttpAsyncCheckServerStatus(serverIp, port, this).execute();
        }

//        else {
//            Log.i(TAG, "The server is not available.\n saving data in database.");
////            saveData();
////            NoteList.getInstance().clear();
////            LocationList.getInstance().clear();
////            MainActivity.state = ServiceState.READY;
//        }

//        if(myLocation.getType().equals("stop")) {
//            stopService(MainActivity.GPSIntent);
//            //createKMLFile(locationList.getList()); //Currently, the server is generating.
//            //new com.eobr.HttpPost.HttpAsyncCheckServerStatus(new Callback() {
//
//                @Override
//                public void callback() {
//                    new HttpAsyncTask().execute(serverIp + "/add");
//                    new HttpPostMultiEntityAsyncTask().execute("");
//                }
//            }).execute();

            //MainActivity.isRunning = false;


        Log.i(TAG,"Execute FOR SINGLE " + createJSON(CURRENT_TRIP_ID).toString());

    }

    /**
     * Create JSON from the database
     * @return
     */
    public JSONObject createJSON(int trip_id) {

            // Load database
            DbAdapter db = new DbAdapter(getApplicationContext());
            SQLiteDatabase sqlDb = db.getReadableDatabase();
            Cursor cursor = sqlDb.rawQuery("select * from trips where trip_id=" + trip_id + " order by id", null);

            Log.i(TAG, "cusor count " + cursor.getCount());
            if( cursor.getCount() < 1)
                return null;
            cursor.moveToFirst();

// Information about table
//        0 id getint
//        1 trip_id getint
//        2 truck_id getString
//        3 trip_type text
//        4 type text
//        5 latitude dobule
//        6 longti dobule
//        7 time text
//        8 note text
          JSONObject jsonObject = new JSONObject();
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

    //Post Json
    public String POST(String url, JSONObject jsonObj){
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
            e.printStackTrace();
         //   DbAdapter db = new DbAdapter(getApplicationContext());
            saveData();
        }
        Log.i(TAG, "RESULT: " + result);
        // 11. return result
        return result;
    }

    public void saveData() {
        DbAdapter db = new DbAdapter(getApplicationContext());
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        sqlDb.rawQuery("insert into notsent (trip_id) values (\"" +MainActivity.CURRENT_TRIP_ID + "\")", null);

    }

    /**
     *  TO DO: Refactor below here
     */

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    //to post json.
    private class HttpJsonAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.i(TAG, "JSON HTTP HAS CALLED");
//            LocationList.getInstance().getList();
            return POST(urls[0], createJSON(CURRENT_TRIP_ID));
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "RESULT on Json: " + result);
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
            CURRENT_TRIP_ID = -1;
            state = ServiceState.READY;
            LocationList.getInstance().clear();
        }
    }

    //POST files
    private class HttpPostMultiEntityAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "FIle HTTP HAS CALLED");
            String result="";
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(serverIp+":"+port+"/uploadfile");
                post.setHeader("enctype", "multipart/form-data");
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                //To add fields. In the server side, it reads the fields.
//                entityBuilder.addTextBody(USER_ID, userId);
//                entityBuilder.addTextBody(NAME, name);

                //To add files. In the server side, it reads the files.
                Iterator it = NoteList.getInstance().iterator();
                String note = "note";
                int i=1;
                while(it.hasNext()) {
                    FileBody filebody = new FileBody(new File((String) it.next()));
                    entityBuilder.addPart(note+i, filebody);
                    i++;
                }

                HttpEntity entity = entityBuilder.build();

                post.setEntity(entity);

                HttpResponse response = client.execute(post);

                HttpEntity httpEntity = response.getEntity();

                result = EntityUtils.toString(httpEntity);

                Log.v("result", result);
            }
            catch(Exception e)
            {

                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "RESULT on soundfiles: " + result);
            NoteList.getInstance().clear();
        }
    }

    public class HttpAsyncCheckServerStatus extends AsyncTask<Void,Void,Void> {

        private Callback callback;
        private String serverIp;
        int port;
        public HttpAsyncCheckServerStatus(String serverIp, int port, Callback callback) {
            this.callback = callback;
            this.serverIp = serverIp;
            this.port = port;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(serverIp + ":" + port);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setConnectTimeout(3000);
                urlConn.connect();
                urlConn.disconnect();
                this.callback.callback();
            } catch(MalformedURLException e) {
                System.err.println("Error creating HTTP connection");
                toInitialStateWithSavingData();
            } catch(IOException e) {
                System.err.println("Error creating HTTP connection");
                toInitialStateWithSavingData();
            }
            return null;
        }
    }

    public void toInitialStateWithSavingData() {
        Log.i(TAG, "To initail State");
        saveData();
        NoteList.getInstance().clear();
        LocationList.getInstance().clear();
        CURRENT_TRIP_ID = -1;
        state = ServiceState.READY;
    }
}
