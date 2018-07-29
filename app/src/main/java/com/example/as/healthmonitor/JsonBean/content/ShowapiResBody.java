package com.example.as.healthmonitor.JsonBean.content;

import com.example.as.healthmonitor.JsonBean.Contentlist;

/**
 * Created by as on 2018/5/13.
 */

public class ShowapiResBody {
    private int ret_code;
    private Item item;
    public void setRetCode(int ret_code) {
        this.ret_code = ret_code;
    }
    public int getRetCode() {
        return ret_code;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    public Item getItem() {
        return item;
    }


}
