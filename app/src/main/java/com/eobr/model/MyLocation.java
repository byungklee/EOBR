package com.eobr.model;

import android.text.format.Time;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by byung on 12/5/14.
 *
 * MyLocation Object to save the data
 */
public class MyLocation {
    /**
     * Fields
     */
    private int trip_id;
    private int id;
    private String type;
    private String trip_type;
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
        this.setTime();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(int trip_id) {
        this.trip_id = trip_id;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }
}
