package com.ssomcompany.ssomclient.purchase;

import com.ssomcompany.ssomclient.purchase.model.StoreParam;

public class StoreProductListRequest {
    private String method;
    private StoreParam param;

    public void setMethod(String method) {
        this.method = method;
    }

    public void setParam(StoreParam param) {
        this.param = param;
    }
}
