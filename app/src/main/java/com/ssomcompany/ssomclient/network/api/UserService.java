package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.network.NetworkConstant.API;
import com.ssomcompany.ssomclient.network.model.HeartResult;
import com.ssomcompany.ssomclient.network.model.LoginResult;
import com.ssomcompany.ssomclient.network.model.ProfileResult;
import com.ssomcompany.ssomclient.network.model.UserCountResult;
import com.ssomcompany.ssomclient.network.model.VersionResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    // 유져
    @FormUrlEncoded
    @POST(API.SSOM_LOGIN_WITHOUT_ID)
    Call<LoginResult> requestLogin(
            @Field("playerId") String playerId
    );

    @GET(API.USER_PROFILE)
    Call<ProfileResult> getUserProfile(
            @Path("userId") String userId
    );

    // 하트
    @GET(API.USER_HEART)
    Call<HeartResult> getHeart();

    @FormUrlEncoded
    @POST(API.USER_HEART)
    Call<HeartResult> addHeart(
            @Field("count") String count,
            @Field("device") String device,
            @Field("token") String token
    );

    // 앱
    @GET(API.APP_VERSION)
    Call<VersionResult> getAppVersion();

    @GET(API.USER_COUNT)
    Call<UserCountResult> getUserCount();
}
