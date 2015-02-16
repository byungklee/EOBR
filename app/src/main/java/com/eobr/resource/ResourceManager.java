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
import java.util.Iterator;
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
        checkUnsent();
        tryResendUnsentData();
    }

    private void tryResendUnsentData() {
        //if(unSentIdList.)
        Iterator it = unSentIdList.iterator();
        HttpPost hp = new HttpPost(ctx, unSentIdList, this);
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
        db.close();
        sqlDB.close();
    }

    private int dateToInt(int year, int month, int day) {
        return year*12*30 + month*30 + day;
    }

    private void cleanFiles() {
        DbAdapter db = new DbAdapter(ctx);
        SQLiteDatabase writeDb = db.getWritableDatabase();
        File[] files = ctx.getFilesDir().listFiles();
        System.out.println("Cleaning File\n number of files is " + files.length);
        for(File f: files) {
            String[] temp = f.getName().split("_");
            System.out.println(f.getName() + " " + f.lastModified());
            // temp[1] is a trip_id;
            //check if trip_id is contained or not
            if(!unSentIdList.contains(new Integer(temp[1]))) {
                //clean
                Log.d(TAG, "Times " + System.currentTimeMillis() + " " + f.lastModified());
                long timeDiff = (System.currentTimeMillis()-f.lastModified())/1000/60/60/24;
                Log.d(TAG,"Time Diff: " + timeDiff);
                //Files over 3days are removed;
                if(timeDiff > 3) {
                    f.delete();
                }
            }
        }
        db.close();
        writeDb.close();
    }

    public void execute() {
        new InternalStorageCleaner().execute();
    }

    private void cleanDatabase() {
        DbAdapter db = new DbAdapter(ctx);
        SQLiteDatabase writeDb = db.getWritableDatabase();
        SQLiteDatabase sqlDb = db.getReadableDatabase();


        System.out.println("Size of unsentList: " + unSentIdList.size());

        Cursor cur = sqlDb.rawQuery("select * from trips where trip_type='start'", null);
        System.out.println("Query Size : " + cur.getCount());
        if (cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                int trip_id = cur.getInt(1);
                System.out.println("Current Trip id to Care to clean: " + trip_id + " " + unSentIdList.contains(new Integer(trip_id)));

                if (!unSentIdList.contains(new Integer(trip_id))) {
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
                        System.out.println("Data Date: " +  dateToInt(year,month,day) + " " +
                                "Curret Date: " + dateToInt(time.year, time.month+1, time.monthDay));

                        //Data over 3days are removed;
                        if (dateToInt(time.year, time.month + 1, time.monthDay) -
                                dateToInt(year, month, day) >= 3) {
                            writeDb.execSQL("delete from trips where trip_id=\"" + trip_id + "\"");

                            //if trip_id is not sent, then skip;
                        }
                    } catch (JSONException e) {
                        Log.i("ResourceManager", e.getMessage());
                    }
                } else {
                    System.out.println(trip_id + " is still unsent");
                }
            } while (cur.moveToNext());
        }
        db.close();
        sqlDb.close();
        writeDb.close();
    }

    @Override
    public void callback() {
        cleanDatabase();
        cleanFiles();
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

