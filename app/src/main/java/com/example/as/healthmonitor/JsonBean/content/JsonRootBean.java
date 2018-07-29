package com.example.as.healthmonitor.JsonBean.content;

/**
 * Created by as on 2018/5/13.
 */

public class JsonRootBean {

    private String showapiResError;
    private int showapiResCode;
    private ShowapiResBody showapi_res_body;
    public void setShowapiResError(String showapiResError) {
        this.showapiResError = showapiResError;
    }
    public String getShowapiResError() {
        return showapiResError;
    }

    public void setShowapiResCode(int showapiResCode) {
        this.showapiResCode = showapiResCode;
    }
    public int getShowapiResCode() {
        return showapiResCode;
    }

    public void setShowapiResBody(ShowapiResBody showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }
    public ShowapiResBody getShowapiResBody() {
        return showapi_res_body;
    }

}
