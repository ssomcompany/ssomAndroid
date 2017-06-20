package com.ssomcompany.ssomclient.purchase.model;

import java.util.ArrayList;

public class StoreParam {
    private String appid;
    private ArrayList<String> product_id;
    private String action;

    public StoreParam setAppid(String appid) {
        this.appid = appid;
        return this;
    }

    public StoreParam setProduct_id(ArrayList<String> product_id) {
        this.product_id = product_id;
        return this;
    }

    public StoreParam setAction(String action) {
        this.action = action;
        return this;
    }
}
