package com.ssomcompany.ssomclient.purchase;

import com.ssomcompany.ssomclient.purchase.model.PurchaseResult;

public class StorePurchaseResponse {
    private String api_version;
    private String identifier;
    private String method;
    private PurchaseResult result;

    public String getApi_version() {
        return api_version;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getMethod() {
        return method;
    }

    public PurchaseResult getResult() {
        return result;
    }
}
