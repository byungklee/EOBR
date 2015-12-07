package com.eobr.model;

import java.util.ArrayList;

/**
 * Created by byung on 1/21/15.
 *
 * Singleton NoteList that contains the filename of sound files.
 */
public class NoteList {
    private static NoteList instance = null;
    private ArrayList<String> notelist;
    private NoteList() {
        notelist = new ArrayList<>();
    }
    public static NoteList getInstance() {
        if(instance == null) {
            instance = new NoteList();
        }
        return instance;
    }
    public void add(String filename) {
        notelist.add(filename);
    }

    public void clear() {
        notelist.clear();
    }
}
