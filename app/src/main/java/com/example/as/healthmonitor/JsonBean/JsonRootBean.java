package com.example.as.healthmonitor.JsonBean;

import java.io.Serializable;

/**
 * Created by as on 2018/5/2.
 */

public class JsonRootBean {

    private int status;
    private String msg;
    private Data data;
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

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

}