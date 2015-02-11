package com.eobr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by byung on 2/3/15.
 * This class cleans up old datas.
 */
public class ResourceManager {
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

    }

    public void clean() {
        checkUnsent();
        tryResendUnsentData();
        cleanDatabase();
        cleanFiles();
    }

    private void tryResendUnsentData() {
        //if(unSentIdList.)
        Iterator it = unSentIdList.iterator();
        HttpPost hp = new HttpPost(ctx, unSentIdList);
        while(it.hasNext()) {
            int trip_id = (Integer) it.next();
            hp.resend(trip_id);
        }
    }

    public void checkUnsent() {
        Log.i(TAG, "checking unsent data");
        DbAdapter db = new DbAdapter(ctx);
        SQLiteDatabase sqlDB = db.getReadableDatabase();
        Cursor cur = sqlDB.rawQuery("select * from notsent", null);
        if(cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                if(unSentIdList.contains(cur.getInt(0)));
                    unSentIdList.add(cur.getInt(0));
            } while(cur.moveToNext());
        }
    }

    private int dateToInt(int year, int month, int day) {
        return year*12*30 + month*30 + day;
    }

    private void cleanFiles() {
        File[] files = ctx.getFilesDir().listFiles();
        System.out.println("Current file length is " + files.length);
        for(File f: files) {
            String[] temp = f.getName().split("_");
            System.out.println(f.getName() + " " + f.lastModified());
            // temp[1] is a trip_id;
            //check if trip_id is contained or not
            if(!unSentIdList.contains(temp[1])) {
                //clean
                long timeDiff = (System.currentTimeMillis()-f.lastModified())/1000/60/60/24;
                if(timeDiff > 3)
                    f.delete();
            }
        }
    }

    public void execute() {
        new InternalStorageCleaner().execute();
    }

    private void cleanDatabase() {
        DbAdapter db = new DbAdapter(ctx);
        SQLiteDatabase sqlDb = db.getReadableDatabase();
        SQLiteDatabase writeDb = db.getWritableDatabase();

        Cursor cur = sqlDb.rawQuery("select * from trips where type='start'", null);
        if (cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                int trip_id = cur.getInt(1);
                if (!unSentIdList.contains(trip_id)) {
                    System.out.println("Cursor count " + cur.getCount() + " " +
                                    cur.getColumnIndex("time") + " "
                    );
                    try {
                        JSONObject obj = new JSONObject(cur.getString(7));
                        System.out.println("JSON Test " + obj.toString() + " " + obj.getInt("month"));

                        int year = obj.getInt("year");
                        int month = obj.getInt("month");
                        int day = obj.getInt("day");
                        Time time = new Time();
                        time.setToNow();

                        // (time.month+1)*30 + time.monthDay

                        if (dateToInt(time.year, time.month + 1, time.monthDay) -
                                dateToInt(year, month, day) > 3) {
                            writeDb.rawQuery("delete from trips where trip_id=\"" + trip_id + "\"", null);
                            //if trip_id is not sent, then skip;
                        }
                    } catch (JSONException e) {
                        Log.i("ResourceManager", e.getMessage());
                    }

                }
            } while (cur.moveToNext());
        }
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

