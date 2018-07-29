package com.example.as.healthmonitor.JsonBean;

import java.util.List;

/**
 * Created by as on 2018/5/12.
 */

public class Pagebean {

    private int allPages;
    private List<Contentlist> contentlist;
    private int currentPage;
    private int allNum;
    private int maxResult;
    public void setAllpages(int allPages) {
        this.allPages = allPages;
    }
    public int getAllpages() {
        return allPages;
    }

    public void setContentlist(List<Contentlist> contentlist) {
        this.contentlist = contentlist;
    }
    public List<Contentlist> getContentlist() {
        return contentlist;
    }

    public void setCurrentpage(int currentPage) {
        this.currentPage = currentPage;
    }
    public int getCurrentpage() {
        return currentPage;
    }

    public void setAllnum(int allNum) {
        this.allNum = allNum;
    }
    public int getAllnum() {
        return allNum;
    }

    public void setMaxresult(int maxResult) {
        this.maxResult = maxResult;
    }
    public int getMaxresult() {
        return maxResult;
    }

}