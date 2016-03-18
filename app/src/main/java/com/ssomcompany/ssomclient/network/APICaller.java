package com.ssomcompany.ssomclient.network;

import com.google.gson.reflect.TypeToken;
import com.ssomcompany.ssomclient.network.api.GetSsomList;
import com.ssomcompany.ssomclient.network.api.SsomImageUpload;
import com.ssomcompany.ssomclient.network.api.SsomPostCreate;
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

    public static <T extends BaseResponse> void ssomImageUpload(byte[] bitmapData, NetworkManager.NetworkListener<T> listener) {
        SsomImageUpload.Request request = new SsomImageUpload.Request();

        request.setBitmapData(bitmapData);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomImageUpload.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void ssomPostCreate(String postId, String userId, String content, String imageUrl, int minAge, int userCount, String ssom,
                                                               double lat, double lon, NetworkManager.NetworkListener<T> listener) {
        SsomPostCreate.Request request = new SsomPostCreate.Request().setPostId(postId).setUserId(userId).setContent(content).setImageUrl(imageUrl)
                .setMinAge(minAge).setUserCount(userCount).setSsom(ssom).setLatitude(lat).setLongitude(lon);

        NetworkManager.request(request, new TypeToken<SsomResponse<SsomImageUpload.Response>>() {}.getType(), listener);
    }


}
