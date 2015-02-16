package com.eobr.model;

import java.util.ArrayList;

/**
 * Created by byung on 12/18/14.
 *
 * Singleton LocationList that contains MyLocation objects. This list is accessed by everywhere.
 *
 */

public class LocationList {
    private static LocationList instance = null;
    private ArrayList<MyLocation> mLocationList;
    private LocationList() {
        mLocationList = new ArrayList<MyLocation>();
    }
    public static LocationList getInstance() {
        if(instance == null) {
            instance = new LocationList();
        }
        return instance;
    }

    public void add(MyLocation ml) {
        mLocationList.add(ml);
    }

    public MyLocation get(int index) {
        return mLocationList.get(index);
    }

    public void clear() {
        mLocationList.clear();
    }

    public int size() {
        return mLocationList.size();
    }

    public boolean isEmpty() {
        return mLocationList.isEmpty();
    }

    public ArrayList<MyLocation> getList() {
        return mLocationList;
    }
}
