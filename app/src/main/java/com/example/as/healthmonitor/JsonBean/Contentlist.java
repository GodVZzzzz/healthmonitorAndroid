package com.example.as.healthmonitor.JsonBean;

/**
 * Created by as on 2018/5/12.
 */

public class Contentlist {

    private String id;
    private String content;
    private String author;
    private String title;
    private String time;
    private String img;
    private String tname;
    private String tid;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getAuthor() {
        return author;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getTime() {
        return time;
    }

    public void setImg(String img) {
        this.img = img;
    }
    public String getImg() {
        return img;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }
    public String getTname() {
        return tname;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
    public String getTid() {
        return tid;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}