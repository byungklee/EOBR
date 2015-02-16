package com.eobr.model;

import android.text.format.Time;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * Created by byung on 12/5/14.
 *
 * MyLocation Object to save the data
 */
public class MyLocation {
    /**
     * Fields
     */
    private String type;
    private String note;
    private double latitude;
    private double longitude;
    private Time time;
    private JSONObject jsonTime;

    /**
     * Constructor
     * @param type
     * @param latitude
     * @param longitude
     */
    public MyLocation(String type, double latitude, double longitude) {
        this.setType(type);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        time = new Time();
        time.setToNow();
        note = "";
        jsonTime = new JSONObject();
        try {
            jsonTime.accumulate("day", time.monthDay);
            jsonTime.accumulate("month", time.month+1);
            jsonTime.accumulate("year", time.year);
            jsonTime.accumulate("time", time.format("%k:%M:%S").trim());
            jsonTime.accumulate("timezone", time.timezone);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        //JSONObject("{df");


    }

    /**
     * Setters and getters
     */
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
        sb.append(time.month+1).append("/").append(time.monthDay).append("/").append(time.year).append(" ").append(time.format("%k:%M:%S"));
        return sb.toString();
    }

    public String getJsonTime() {
        return jsonTime.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
