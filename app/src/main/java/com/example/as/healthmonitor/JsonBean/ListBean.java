package com.example.as.healthmonitor.JsonBean;

import java.util.List;

/**
 * Created by as on 2018/5/5.
 */

public class ListBean {
    private int status;
    private String msg;
    private List<LinkmanBean> data;
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

    public void setData(List<LinkmanBean> data) {
        this.data = data;
    }
    public List<LinkmanBean> getData() {
        return data;
    }
}
