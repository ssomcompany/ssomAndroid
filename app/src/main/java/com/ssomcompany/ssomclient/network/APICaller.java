package com.ssomcompany.ssomclient.network;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.ssomcompany.ssomclient.activity.BaseActivity;
import com.ssomcompany.ssomclient.network.api.CreateChattingRoom;
import com.ssomcompany.ssomclient.network.api.GetChattingList;
import com.ssomcompany.ssomclient.network.api.GetChattingRoomList;
import com.ssomcompany.ssomclient.network.api.GetSsomList;
import com.ssomcompany.ssomclient.network.api.SendChattingMessage;
import com.ssomcompany.ssomclient.network.api.SsomChatUnreadCount;
import com.ssomcompany.ssomclient.network.api.SsomExistMyPost;
import com.ssomcompany.ssomclient.network.api.SsomImageUpload;
import com.ssomcompany.ssomclient.network.api.SsomLogin;
import com.ssomcompany.ssomclient.network.api.SsomPostCreate;
import com.ssomcompany.ssomclient.network.api.SsomPostDelete;
import com.ssomcompany.ssomclient.network.api.SsomRegisterUser;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.BaseResponse;
import com.ssomcompany.ssomclient.network.model.SsomResponse;

import java.util.Locale;

public class APICaller {
    private static final int TIME_OUT_LONG = 10000;

    public static <T extends BaseResponse> void getSsomList(double latitude, double longitude, String userId,
                                                            String ageFilter, String countFilter, NetworkManager.NetworkListener<T> listener) {
        GetSsomList.Request request;
        if(TextUtils.isEmpty(ageFilter) || TextUtils.isEmpty(countFilter)) request = new GetSsomList.Request(latitude, longitude, userId);
        else request = new GetSsomList.Request(latitude, longitude, userId, ageFilter, countFilter);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<GetSsomList.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void ssomImageUpload(byte[] bitmapData, NetworkManager.NetworkListener<T> listener) {
        SsomImageUpload.Request request = new SsomImageUpload.Request();

        request.setBitmapData(bitmapData);
        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomImageUpload.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void ssomPostCreate(String token, String postId, String userId, String content,
                                                               String imageUrl, String minAge, String userCount, String ssomType,
                                                               double lat, double lon, NetworkManager.NetworkListener<T> listener) {
        SsomPostCreate.Request request = new SsomPostCreate.Request().setPostId(postId).setUserId(userId).setContent(content).setImageUrl(imageUrl)
                .setMinAge(minAge).setUserCount(userCount).setSsomType(ssomType).setLatitude(lat).setLongitude(lon);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomPostCreate.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void ssomExistMyPost(String token, NetworkManager.NetworkListener<T> listener) {
        SsomExistMyPost.Request request = new SsomExistMyPost.Request();
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomItem>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void ssomPostDelete(String token, String postId, NetworkManager.NetworkListener<T> listener) {
        SsomPostDelete.Request request = new SsomPostDelete.Request(postId);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomPostDelete.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void ssomLogin(String email, String password, String playerId,
                                                          NetworkManager.NetworkListener<T> listener) {
        SsomLogin.Request request = new SsomLogin.Request().setPlayerId(playerId);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION,
                "Basic " + Base64.encodeToString((String.format(Locale.getDefault(),"%s:%s", email, password)).getBytes(), Base64.DEFAULT));

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomLogin.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void ssomRegisterUser(String email, String password, NetworkManager.NetworkListener<T> listener) {
        SsomRegisterUser.Request request = new SsomRegisterUser.Request().setEmail(email).setPassword(password);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomRegisterUser.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void getChattingRoomList(String token, NetworkManager.NetworkListener<T> listener) {
        GetChattingRoomList.Request request = new GetChattingRoomList.Request();
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<GetChattingRoomList.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void getChattingList(String token, int roomId, NetworkManager.NetworkListener<T> listener) {
        GetChattingList.Request request = new GetChattingList.Request(roomId);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<GetChattingList.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void createChattingRoom(String token, String postId, NetworkManager.NetworkListener<T> listener) {
        CreateChattingRoom.Request request = new CreateChattingRoom.Request().setPostId(postId);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<CreateChattingRoom.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void totalChatUnreadCount(String token, NetworkManager.NetworkListener<T> listener) {
        SsomChatUnreadCount.Request request = new SsomChatUnreadCount.Request();
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomChatUnreadCount.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void sendChattingMessage(String token, int roomId, long lastMessageTime,
                                           String msg, NetworkManager.NetworkListener<T> listener) {
        SendChattingMessage.Request request = new SendChattingMessage.Request(roomId, lastMessageTime).setMsg(msg);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SendChattingMessage.Response>>() {}.getType(), listener);
    }
}
