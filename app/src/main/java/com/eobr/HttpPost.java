package com.eobr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.eobr.model.MyLocation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by byung on 2/6/15.
 */
public class HttpPost {
    private static final String TAG = "HTTPPOST";


    private Map container;

    //public HttpPost(Context ctx) { this.ctx= ctx; }
    public HttpPost() { container = new HashMap();}
//    public HttpPost(Context ctx, List list, Callback callback) {
//        this.ctx= ctx;
//        this.callback = callback;
//        container = new HashMap();
//    }

    public void isServerAvailable(Callback callback) {
        new CheckServerStatusAsyncTask(callback).execute();
    }

    public void resend(final int trip_id) {
         Log.i(TAG,"Resending trip_id " + trip_id);
        new CheckServerStatusAsyncTask(new Callback() {

            @Override
            public void callbackOnSuccess() {
                new HttpAsyncTask(trip_id).execute();
                new HttpPostMultiEntityAsyncTask(trip_id).execute();
            }

            @Override
            public void callbackOnFail() {
                System.out.println("Server does not work!");
            }
        }).execute();
    }

    public JSONObject createJSON(int trip_id) {

        // Load database
        DbAdapter db = new DbAdapter(MainActivity.mContext);
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
     * Post Json
     * @param url
     * @param jsonObj
     * @return
     */
    private String POST(String url, JSONObject jsonObj){
        if(jsonObj == null) {
            return null;
        }
        System.out.println("HERE!!! in httppost: " + jsonObj.toString());
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            org.apache.http.client.methods.HttpPost httpPost = new org.apache.http.client.methods.HttpPost(url);
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
            //saveData();
        }
        //Log.i(TAG, "RESULT: " + result);
        // 11. return result
        return result;
    }

    //To check server status
    public class CheckServerStatusAsyncTask extends AsyncTask<Void,Void,Void> {

        Callback callback;
        public CheckServerStatusAsyncTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                System.out.println("checking server status...");
                URL url = new URL(MainActivity.serverIp + ":" + MainActivity.port);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setConnectTimeout(3000);

                urlConn.connect();
                urlConn.disconnect();
                this.callback.callbackOnSuccess();
            } catch(MalformedURLException e) {
                System.err.println("Malformed URL Error creating HTTP connection");
                this.callback.callbackOnFail();
            } catch(IOException e) {
                System.err.println("IO EXCEPTION Error creating HTTP connection");
                this.callback.callbackOnFail();
            }
            return null;
        }
    }

    //to post json.
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private int trip_id;
        public HttpAsyncTask(int trip_id) {
            this.trip_id = trip_id;
        }
        @Override
        protected String doInBackground(String... urls) {

            return POST(MainActivity.serverIpAndPort+"/add", createJSON(trip_id));
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "RESULT on Json: " + result);

            if(result != null || result.equals("Success!")) {
                Log.i(TAG, "removing from unsentList");
                checkTripId(trip_id);
            }
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    //POST files
    private class HttpPostMultiEntityAsyncTask extends AsyncTask<String, Void, String> {
        private int trip_id;

        private List fileNames;
        public HttpPostMultiEntityAsyncTask(int trip_id) {
            this.trip_id = trip_id;
            DbAdapter db = new DbAdapter(MainActivity.mContext);
            SQLiteDatabase sqlDb = db.getReadableDatabase();
            fileNames = new ArrayList();
            Cursor cur = sqlDb.rawQuery("select * from trips where trip_id=\"" + trip_id + "\" and type=\"note\"", null);
            if(cur.getCount() != 0) {
                cur.moveToFirst();
                do {
                    fileNames.add(cur.getString(cur.getColumnIndex("note")));
                } while (cur.moveToNext());
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String result="";
            try
            {
                HttpClient client = new DefaultHttpClient();
                org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(MainActivity.serverIpAndPort+"/uploadfile");
                post.setHeader("enctype", "multipart/form-data");
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                //To add fields. In the server side, it reads the fields.
//                entityBuilder.addTextBody(USER_ID, userId);
//                entityBuilder.addTextBody(NAME, name);

                //To add files. In the server side, it reads the files.
                Iterator it = fileNames.iterator();
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
            if(result != null || result.equals("Success!")) {
                Log.i(TAG, "removing from unsentList");
                checkTripId(trip_id);
            }
        }
    }

    public void checkTripId(int trip_id) {
        if(container.containsKey(trip_id)) {
//            list.remove(new Integer(trip_id));
//            callback.callbackOnSuccess();
            container.remove(trip_id);
            DbAdapter db = new DbAdapter(MainActivity.mContext);
            SQLiteDatabase writeDb = db.getWritableDatabase();
            //writeDb.execSQL("delete from notsent where trip_id=" + trip_id);
            writeDb.execSQL("delete from trips where trip_id=" + trip_id);
        } else {
            container.put(trip_id, true);
        }
    }


    /**
     * 1. Check server is open
     * 2. if open, create json and send to a server.
     * 3. else save it to a database
     * @param l
     */
    public void sendJson(final MyLocation l) {
        new CheckServerStatusAsyncTask(new Callback() {
            @Override
            public void callbackOnSuccess() {
                new PostJsonAsyncTask(createJson(l)).execute();
//                new HttpPostMultiEntityAsyncTask(trip_id).execute();
            }
            public void callbackOnFail() {
                saveLocationToDatabase(l);
            }
        }).execute();
    }

    public void sendFiles(final MyLocation l) {
        new CheckServerStatusAsyncTask(new Callback() {
            @Override
            public void callbackOnSuccess() {
                new PostJsonAsyncTask(createJson(l)).execute();
                new PostFilesAsyncTask(createJson(l)).execute();
            }
            public void callbackOnFail() {
                saveLocationToDatabase(l);
            }

        }).execute();
    }

    public void saveLocationToDatabase(MyLocation location) {

        DbAdapter db = new DbAdapter(MainActivity.mContext);
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        sqlDb.execSQL("insert into trips (id, trip_id, truck_id, trip_type, type, latitude, longitude, time) " +
                "values (" + location.getId() + ", " + location.getTrip_id() + ", \"" +MainActivity.TRUCK_ID + "\", \"" + location.getTrip_type() + "\", \"" + location.getType() + "\", " +
                location.getLatitude() + ", " + location.getLongitude() + ", \'" + location.getJsonTime() + "\')");
        sqlDb.close();
        db.close();
        System.out.println("Saving completed " + location.toString());

    }

    public JSONObject createJson(MyLocation l) {
        JSONObject jsonObject = new JSONObject();

            JSONObject tempJson = new JSONObject();
        try {
            tempJson.put("id", l.getId()); // id
            tempJson.put("trip_id", l.getTrip_id()); // trip_id
            tempJson.put("truck_id", MainActivity.TRUCK_ID); //truck_id
            tempJson.put("trip_type", l.getTrip_type()); // trip_type
            tempJson.put("type", l.getType()); //type
            tempJson.put("latitude", l.getLatitude()); //latitude
            tempJson.put("longitude", l.getLongitude()); // longtitude
            tempJson.put("time", l.getJsonTime()); // time
            tempJson.put("note", l.getNote()); // note
            jsonObject.accumulate("record", tempJson);
        } catch(JSONException j) {
            System.err.println("ERRoR IN json");
        }

        Log.i(TAG, jsonObject.toString());
        return jsonObject;
    }


    private class PostJsonAsyncTask extends AsyncTask<String, Void, String> {

        final private JSONObject json;
        public PostJsonAsyncTask(JSONObject j) {
            this.json = j;
        }
        @Override
        protected String doInBackground(String... urls) {
            return POST(MainActivity.serverIpAndPort+"/add", json);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "RESULT on Json: " + result);

            if(result != null || result.equals("Success!")) {

//                Log.i(TAG, "removing from unsentList");
//                checkTripId(trip_id);


            }
        }
    }

    private class PostFilesAsyncTask extends AsyncTask<String, Void, String> {
//        private int trip_id;
        private JSONObject json;
//        private List fileNames;
        public PostFilesAsyncTask(JSONObject o) {
              json = o;
        }

        @Override
        protected String doInBackground(String... params) {
            String result="";
            try
            {
                HttpClient client = new DefaultHttpClient();
                org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(MainActivity.serverIpAndPort+"/uploadfile");
                post.setHeader("enctype", "multipart/form-data");
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                String note = "note";
                FileBody filebody = new FileBody(new File(json.getString("note")));
                entityBuilder.addPart(note +json.getString("id"),filebody);

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
    }

}
