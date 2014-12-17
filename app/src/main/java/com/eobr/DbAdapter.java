package com.eobr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by byung on 12/8/14.
 */
public class DbAdapter extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "eobrdb";
    private static final String TABLE_NAME = "trips";
    private static final int DATABASE_VERSION = 3;
    private Context mContext;

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS trips \n" +
            "(id integer primary key autoincrement,\n" +
            "trip_id integer not null,\n" +
            "truck_id text not null,\n" +
            "trip_type text not null,\n" +
            "type text not null, \n" +
            "latitude real not null,\n" +
            " longitude real not null,\n" +
            "  time text not null, \n" +
            "note text);";

    public DbAdapter(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = ctx;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        Cursor cur = db.rawQuery("select * from trips",null);

  //      Log.d("DBAdpater", "Debugging trips " + cur.getCount());
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
