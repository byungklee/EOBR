package com.eobr;

import android.text.format.Time;

/**
 * Created by byung on 12/5/14.
 */
public class MyLocation {
    private String type;
    private double latitude;
    private double longitude;
    private Time time;

    public MyLocation(String type, double latitude, double longitude) {
        this.setType(type);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        time = new Time();
        time.setToNow();
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTime() {
        time.setToNow();
    }



    public Time getTime() {
        return time;
    }

    public String getTimeString() {
        StringBuilder sb = new StringBuilder();
        sb.append(time.month).append("/").append(time.monthDay).append("/").append(time.year).append(" ").append(time.format("%k:%M:%S"));

        return sb.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
