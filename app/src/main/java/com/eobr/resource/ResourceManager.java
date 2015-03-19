package com.eobr.resource;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import com.eobr.Callback;
import com.eobr.DbAdapter;
import com.eobr.HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import java.util.List;

/**
 * Created by byung on 2/3/15.
 * This class cleans up old datas.
 */
public class ResourceManager implements Callback {
    private Context ctx;
    private static final String TAG = "ResourceManager";
    public ResourceManager(Context ctx) {
        this();
        this.ctx = ctx;
    }
    private List unSentIdList;

    //May need the list of file that is not sent to the server.
    public ResourceManager() {
        unSentIdList = new ArrayList();
    }

    //To Debug
    public void printFiles() {
        File[] files = ctx.getFilesDir().listFiles();
        System.out.println("Current file length is " + files.length);
        for(File f: files) {
            System.out.println(f.getName() + " " + f.lastModified());
        }

        DbAdapter db = new DbAdapter(ctx);
        SQLiteDatabase sqlDb = db.getReadableDatabase();

        //Delete ALL
//        SQLiteDatabase wql  = db.getWritableDatabase();
//
//        wql.execSQL("DELETE FROM trips");

        Cursor cur = sqlDb.rawQuery("select * from trips where type='start'", null);
        if(cur.getCount() != 0) {
            cur.moveToFirst();
            System.out.println("Cursor count " + cur.getCount() + " " +
                            cur.getColumnIndex("time") + " " + cur.getColumnIndex("trip_id")
            );
            try {
                JSONObject obj = new JSONObject(cur.getString(7));
                System.out.println("JSON Test "  + obj.toString() + " " + obj.getInt("month"));
            } catch (JSONException e) {
                Log.i("ResourceManager", e.getMessage());
            }
        }
        sqlDb.close();
        db.close();
    }

    public void clean() {
        //checkUnsent();
        tryResendUnsentData();
        cleanFiles();
    }

    private void tryResendUnsentData() {
        //if(unSentIdList.)
//        Iterator it = unSentIdList.iterator();
//        HttpPost hp = new HttpPost(ctx, unSentIdList, this);
//        while(it.hasNext()) {
//            int trip_id = (Integer) it.next();
//            hp.resend(trip_id);
//        }
        System.out.println("Trying resend unsentData!");

        final HttpPost post = new HttpPost();
        post.isServerAvailable(new Callback() {

            @Override
            public void callbackOnSuccess() {
                DbAdapter db = new DbAdapter(ctx);
                SQLiteDatabase sqlDB = db.getReadableDatabase();
                System.out.println("Querying unsent data since server is working!");
                Cursor cur = sqlDB.rawQuery("select distinct trip_id from trips", null);

                if(cur.getCount() != 0) {
                    cur.moveToFirst();
                    do {
                        System.out.println("Trying resend!");
                        unSentIdList.add(cur.getInt(0));
                        post.resend(cur.getInt(0));

                        System.out.println(cur.getInt(0));
                    } while(cur.moveToNext());
                }
                db.close();
                sqlDB.close();
            }

            @Override
            public void callbackOnFail() {
                Log.i(TAG,"Server not available to send unsent data");
            }
        });

    }
//
//    public void checkUnsent() {
//
//        DbAdapter db = new DbAdapter(ctx);
//        SQLiteDatabase sqlDB = db.getReadableDatabase();
//        Cursor cur = sqlDB.rawQuery("select * from ", null);
//        if(cur.getCount() != 0) {
//            cur.moveToFirst();
//            do {
//                if(!unSentIdList.contains(cur.getInt(0)))
//                    unSentIdList.add(cur.getInt(0));
//            } while(cur.moveToNext());
//        }
//        db.close();
//        sqlDB.close();
//        Log.i(TAG, "checking unsent data " + unSentIdList.size());
//    }

    private int dateToInt(int year, int month, int day) {
        return year*12*30 + month*30 + day;
    }

    public void cleanFiles() {
        DbAdapter db = new DbAdapter(ctx);
        SQLiteDatabase readDb = db.getReadableDatabase();
        File[] files = ctx.getFilesDir().listFiles();
        System.out.println("Cleaning File\n number of files is " + files.length);
        for(File f: files) {
       //     String[] temp = f.getName().split("_");
            System.out.println(f.getName() + " " + f.lastModified());
            Cursor cur = readDb.rawQuery("select * from trips where note=\""+f.getName()+"\"",null);
            if(cur.getCount() == 0) {
                f.delete();
            }
            // temp[1] is a trip_id;
            //check if trip_id is contained or not
//            if(!unSentIdList.contains(new Integer(temp[1]))) {
//                //clean
//                Log.d(TAG, "Times " + System.currentTimeMillis() + " " + f.lastModified());
//                long timeDiff = (System.currentTimeMillis()-f.lastModified())/1000/60/60/24;
//                Log.d(TAG,"Time Diff: " + timeDiff);
//                //Files over 3days are removed;
//                f.delete();
////                if(timeDiff > 3) {
////
////                }
//            }
        }
        db.close();
        readDb.close();
    }

    public void execute() {

        new InternalStorageCleaner().execute();
       Log.d(TAG,"EXECUTING RESOURCE MANAGER");
    }


//
//    @Override
//    public void callback() {
//
//    }

    @Override
    public void callbackOnSuccess() {
        cleanFiles();
        unSentIdList.clear();
    }

    @Override
    public void callbackOnFail() {

    }

    private class InternalStorageCleaner extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            clean();
            return null;
        }
    }
}

