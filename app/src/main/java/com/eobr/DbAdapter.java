package com.eobr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by byung on 12/8/14.
 * Database Adapter that handles initialization of the sqlite table.
 */
public class DbAdapter extends SQLiteOpenHelper {
    /**
     * Constant Data and variable declaration
     */
    private static final String DATABASE_NAME = "eobrdb";
    private static final String TABLE_NAME = "trips";
    private static final int DATABASE_VERSION = 5;
    private Context mContext;

    private static final String CREATE_TABLE_TRIPS = "CREATE TABLE IF NOT EXISTS trips \n" +
            "(id integer primary key autoincrement,\n" +
            "trip_id integer not null,\n" +
            "truck_id text not null,\n" +
            "trip_type text not null,\n" +
            "type text not null, \n" +
            "latitude real not null,\n" +
            "longitude real not null,\n" +
            "time text not null, \n" +
            "note text);";

    private static final String CREATE_TABLE_NOT_SENT = "CREATE TABLE IF NOT EXISTS notsent \n" +
            "(trip_id integer primary key not null)";

    private static final String CREATE_TABLE_TRIP_ID = "CREATE TABLE IF NOT EXISTS trip_id \n" +
            "(trip_id integer primary key not null)";


    /**
     *  Constructor
     */
    public DbAdapter(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = ctx;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRIPS);
        db.execSQL(CREATE_TABLE_NOT_SENT);
        db.execSQL(CREATE_TABLE_TRIP_ID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
