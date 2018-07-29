package com.example.as.healthmonitor;

/**
 * Created by as on 2018/4/24.
 */

public class LinkMan {

    private String name;

    private String id;

    public LinkMan(String name,String id) {
        this.name = name;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }
}
