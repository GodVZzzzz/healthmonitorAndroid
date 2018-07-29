package com.example.as.healthmonitor;

import java.io.Serializable;

/**
 * Created by as on 2018/4/22.
 */

public class health implements Serializable{

    private String name;

    private int imageId;

    private String unit;

    private String time;

    private String healthCount;

    private String className;


    public health(String name, int imageId, String unit, String time, String healthCount, String className) {
        this.name = name;
        this.imageId = imageId;
        this.unit = unit;
        this.time = time;
        this.healthCount = healthCount;
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }

    public String getUnit() {
        return unit;
    }

    public String getTime() {
        return time;
    }

    public String getHealthCount() {
        return healthCount;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setImageId(int imageId){
        this.imageId = imageId;
    }

    public void setUnit (String unit){
        this.unit = unit;
    }

    public void setTime (String time){
        this.time = time;
    }

    public void setHealthCount (String healthCount){
        this.healthCount = healthCount;
    }

    public void setClassName (String className) {
        this.className = className;
    }

    public String getClassName (){
        return className;
    }


}
