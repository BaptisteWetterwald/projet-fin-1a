package fr.ensisa.ensiblog.models;

import android.net.Uri;

public class Video extends Content{

    private String path;

    public Video(){
        // empty constructor required for firebase
    }

    public Video(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
