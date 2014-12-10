package com.eobr;

/**
 * Created by byung on 12/3/14.
 */
public interface GPSListener {
    public void execute(MyLocation location);
    public void executeForSingle(String type, double latitude, double longitude);
}
