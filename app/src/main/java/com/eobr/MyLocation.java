package com.eobr;

import android.text.format.Time;

import java.security.Timestamp;

/**
 * Created by byung on 12/5/14.
 */
public class MyLocation {
    private double latitude;
    private double longitude;
    private Time time;

    public MyLocation(double latitude, double longitude) {
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

    public Time getTime() {
        return time;
    }

    public String getTimeString() {
        StringBuilder sb = new StringBuilder();
        sb.append(time.month).append(" ").append(time.monthDay).append(" ").append(time.year).append(" ").append(time.format("%k:%M:%S"));
        return sb.toString();
    }
}
