package com.eobr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.Iterator;
import java.util.List;

/**
 * Created by byung on 2/6/15.
 */
public class HttpPost {
    private static final String TAG = "HTTPPOST";
    private Context ctx;
    private Callback callback;
//    private static final String serverIpAndPort = "http://192.168.0.23:8888";
//private static final String serverIpAndPort = "http://134.139.249.76:8888";
//    private static final String serverIp = "http://134.139.249.76";
//    private static final String serverIp="http://192.168.0.23";
 //   private static final int port = 8888;
    private List list;

    public HttpPost(Context ctx) {
        this.ctx= ctx;
    }
    public HttpPost(Context ctx, List list, Callback callback) {
        this.ctx= ctx;
        this.list = list;
        this.callback = callback;
    }

    public void resend(final int trip_id) {
         Log.i(TAG,"Resending trip_id " + trip_id);
        new HttpAsyncCheckServerStatus(new Callback() {
            @Override
            public void callback() {
                new HttpAsyncTask(trip_id).execute();
                new HttpPostMultiEntityAsyncTask(trip_id).execute();
            }
        }).execute();
    }

    public JSONObject createJSON(int trip_id) {

        // Load database
        DbAdapter db = new DbAdapter(ctx);
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

    //Post Json
    public String POST(String url, JSONObject jsonObj){
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
    public class HttpAsyncCheckServerStatus extends AsyncTask<Void,Void,Void> {

        Callback callback;
        public HttpAsyncCheckServerStatus(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(MainActivity.serverIp + ":" + MainActivity.port);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setConnectTimeout(3000);
                urlConn.connect();
                urlConn.disconnect();
                this.callback.callback();
            } catch(MalformedURLException e) {
                System.err.println("Error creating HTTP connection");
               // toInitialStateWithSavingData();
            } catch(IOException e) {
                System.err.println("Error creating HTTP connection");
             //   toInitialStateWithSavingData();
            }
//            try{
//                Socket s = new Socket(serverIp, port);
//                if(s.isConnected()) {
//                    s.close();
//                    this.callback.callback();
//                }
//
//                Log.i(TAG, "Server work");
//            } catch (UnknownHostException e)
//            { // unknown host
//                Log.i(TAG, "Server work no");
//            }
//            catch (IOException e) { // io exception, service probably not running
//                Log.i(TAG, "Server work no ");
//            }
//            catch (NullPointerException e) {
//                Log.i(TAG, "Server work no");
//
//            }
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
                list.remove(new Integer(trip_id));
                callback.callback();
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
            DbAdapter db = new DbAdapter(ctx);
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
          //  NoteList.getInstance().clear();
        }
    }

}
