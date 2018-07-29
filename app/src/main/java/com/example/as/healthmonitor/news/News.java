package com.example.as.healthmonitor.news;

import android.graphics.Bitmap;

/**
 * Created by as on 2018/5/13.
 */

public class News {

    private Bitmap imageId;

    private String title;

    private String summary;

    private String id;

    public News(Bitmap imageId,String title, String summary,String id) {
        this.imageId = imageId;
        this.title = title;
        this.summary = summary;
        this.id = id;
    }

    public Bitmap getImageId(){
        return imageId;
    }

    public void setImageId(Bitmap imageId){
        this.imageId = imageId;
    }

    public  String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getSummary(){
        return summary;
    }

    public void setSummary(String summary){
        this.summary = summary;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }
}
