package com.eobr;

import com.eobr.model.MyLocation;

/**
 * Created by byung on 12/3/14.
 */
public interface GPSListener {
    public void execute();
    public void executeForSingle(MyLocation location);
}
