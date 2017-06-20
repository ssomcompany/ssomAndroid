package com.ssomcompany.ssomclient.purchase.model;

import java.util.ArrayList;

public class PurchaseResult {
    private String code;
    private String message;
    private String txid;
    private String receipt;
    private String count;
    private ArrayList<StoreProduct> product;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getTxid() {
        return txid;
    }

    public String getReceipt() {
        return receipt;
    }

    public String getCount() {
        return count;
    }

    public ArrayList<StoreProduct> getProduct() {
        return product;
    }
}
