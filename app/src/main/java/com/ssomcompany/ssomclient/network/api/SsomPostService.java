package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomItem;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface SsomPostService {
    @GET(NetworkConstant.API.SSOM_MY_POST)
    Call<SsomItem> requestGetMyPost();

    @GET(NetworkConstant.API.SSOM_LIST)
    Call<ArrayList<SsomItem>> requestSsomList(
            @QueryMap(encoded = true) Map<String, String> params
    );

    @FormUrlEncoded
    @POST(NetworkConstant.API.SSOM_POST)
    Call<Void> createPost(
            @Field("postId") String postId,
            @Field("userId") String userId,
            @Field("content") String content,
            @Field("imageUrl") String imageUrl,
            @Field("minAge") String minAge,
            @Field("userCount") String userCount,
            @Field("ssomType") String ssomType,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude
    );

    @DELETE(NetworkConstant.API.SSOM_POST_DELETE)
    Call<Void> deletePost(
            @Path("postId") String postId
    );
}
