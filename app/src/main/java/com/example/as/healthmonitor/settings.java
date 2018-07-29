package com.example.as.healthmonitor;

/**
 * Created by as on 2018/4/24.
 */

public class settings {
    private String name;

    private int imageId;

    public settings(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }
}
