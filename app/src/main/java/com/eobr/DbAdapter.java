package com.eobr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by byung on 12/8/14.
 */
public class DbAdapter extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "eobrdb";
    private static final String TABLE_NAME = "trips";
    private static final int DATABASE_VERSION = 2;
    private Context mContext;
    private SQLiteDatabase mDb;


    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS trips \n" +
            "(id integer primary key autoincrement,\n" +
            "trip_id integer not null,\n" +
            "truck_id text not null,\n" +
            "trip_type text not null,\n" +
            "type text not null, \n" +
            "latitude real not null,\n" +
            " longitude real not null,\n" +
            "  time text not null);";

    public DbAdapter(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = ctx;
        this.mDb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
