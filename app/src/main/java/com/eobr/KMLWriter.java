package com.eobr;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import android.os.Bundle;

/**
 * Created by byung on 12/9/14.
 */
public class KMLWriter {
//    public void createKMLFile(List<MyLocation> locationList, Context context) {
//        FileOutputStream outputStream;
//        String filename = MainActivity.TRUCK_ID + " " + MainActivity.CURRENT_TRIP_ID;
//
////        //<name>Paths</name>
////        <description>Examples of paths. Note that the tessellate tag is by default
////        set to 0. If you want to create tessellated lines, they must be authored
////                (or edited) directly in KML.</description>
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
//                "\t<Document>\n");
//
//        for(MyLocation ml:locationList) {
//            int i = 1;
//            sb.append("\t\t<Placemark>\n")
//            .append("\t\t\t<name>").append("point").append(i).append("</name>\n")
//            .append("\t\t\t<description>").append(ml.getType()).append("</description>\n")
//            .append("\t\t\t<point>\n")
//            .append("\t\t\t\t<coordinates>").append(ml.getLatitude()).append(",").append(ml.getLongitude()).append("</coordinates>")
//            .append("\t\t\t</point>\n</Placemark>\n");
//        }
//        sb.append("\t</Document>\n" +
//                "</kml>\n");
//
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("config.txt", Context.MODE_PRIVATE));
//            outputStreamWriter.write(sb.toString());
//            outputStreamWriter.close();
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//
//    }
}
