package com.example.as.healthmonitor.JsonBean;

/**
 * Created by as on 2018/5/4.
 */

public class FindBean {

    private int status;
    private String data;
    private String msg;

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getData() {
        return data;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

}
