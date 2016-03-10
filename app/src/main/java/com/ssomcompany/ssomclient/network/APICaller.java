package com.ssomcompany.ssomclient.network;

import com.google.gson.reflect.TypeToken;
import com.ssomcompany.ssomclient.network.api.GetSsomList;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.BaseResponse;
import com.ssomcompany.ssomclient.network.model.SsomResponse;

import java.util.ArrayList;

public class APICaller {
    private static final int TIME_OUT_LONG = 60000;

    public static <T extends BaseResponse> void getSsomList(NetworkManager.NetworkListener<T> listener) {
        GetSsomList.Request request = new GetSsomList.Request();

        NetworkManager.request(request, new TypeToken<SsomResponse<GetSsomList.Response>>() {}.getType(), listener);
    }
}
