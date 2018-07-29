package com.example.as.healthmonitor.JsonBean;

/**
 * Created by as on 2018/5/5.
 */

public class LinkMsgBean {
    private int status;
    private String msg;
    private LinkmanBean data;
    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setData(LinkmanBean data) {
        this.data = data;
    }
    public LinkmanBean getData() {
        return data;
    }
}
