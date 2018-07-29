package com.example.as.healthmonitor.JsonBean;

/**
 * Created by as on 2018/5/12.
 */

public class ShowapiResBody {

    private int ret_code;
    private Pagebean pagebean;
    public void setRetCode(int ret_code) {
        this.ret_code = ret_code;
    }
    public int getRetCode() {
        return ret_code;
    }

    public void setPagebean(Pagebean pagebean) {
        this.pagebean = pagebean;
    }
    public Pagebean getPagebean() {
        return pagebean;
    }

}