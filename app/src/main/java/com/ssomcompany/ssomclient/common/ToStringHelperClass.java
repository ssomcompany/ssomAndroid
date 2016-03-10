
package com.ssomcompany.ssomclient.common;

import com.google.gson.Gson;

public abstract class ToStringHelperClass {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
