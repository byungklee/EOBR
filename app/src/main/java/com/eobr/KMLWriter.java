package com.eobr;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by byung on 12/9/14.
 */
public class KMLWriter {
    public static void writeFromLocations(List<MyLocation> locationList, Context context) {
        FileOutputStream outputStream;
        String filename = MainActivity.TRUCK_ID + " " + MainActivity.CURRENT_TRIP_ID;
//        try {
//            File file = new File(context.getFilesDir(), filename);
//
//            outputStream = FileOutputStream.openFileOutput(filename, Context.MODE_PRIVATE);
//            outputStream.write(string.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
