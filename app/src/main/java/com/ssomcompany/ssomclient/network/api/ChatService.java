package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.network.NetworkConstant.API;
import com.ssomcompany.ssomclient.network.model.ChatRoomCreateResult;
import com.ssomcompany.ssomclient.network.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.model.ChatUnreadCountResult;
import com.ssomcompany.ssomclient.network.model.ChattingItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatService {
    // chatting
    @GET(API.CHAT_TOTAL_UNREAD_COUNT)
    Call<ChatUnreadCountResult> getChatUnreadCount();

    @GET(API.CHAT_LIST)
    Call<ArrayList<ChattingItem>> requestChatList(
            @Path("roomId") String roomId
    );

    @FormUrlEncoded
    @POST(API.SEND_MESSAGE)
    Call<ArrayList<ChattingItem>> sendChatMessage(
            @Path("roomId") String roomId,
            @Query("lastTimestamp") long lastTimestamp,
            @Field("msg") String msg
    );

    // chat room request
    // POST : 만남신청 , PUT : 만남수락 , DELETE : 만남종료
    @FormUrlEncoded
    @POST(API.MEETING_REQUEST)
    Call<Void> requestMeeting(
            @Field("chatroomId") String chatroomId
    );

    @FormUrlEncoded
    @HTTP(method = "PUT", path = API.MEETING_REQUEST, hasBody = true)
    Call<Void> approveMeeting(
            @Field("chatroomId") String chatroomId
    );

//    @DELETE(API.MEETING_REQUEST + "/" + chatroomId)
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = API.MEETING_REQUEST, hasBody = true)
    Call<Void> cancelMeeting(
            @Field("chatroomId") String chatroomId
    );

    // chatting room
    @GET(API.CHAT_ROOM)
    Call<ArrayList<ChatRoomItem>> requestChatRoomList();

    @FormUrlEncoded
    @POST(API.CHAT_ROOM)
    Call<ChatRoomCreateResult> createChatRoom(
            @Field("postId") String postId,
            @Field("lat") double lat,
            @Field("lng") double lng
    );

    @PUT(API.CHAT_ROOM + "/{chatRoomId}/lastAccessTimestamp")
    Call<Void> updateChatRoom(
            @Path("chatRoomId") String chatRoomId
    );

    @DELETE(API.CHAT_ROOM + "/{chatRoomId}")
    Call<Void> deleteChatRoom(
            @Path("chatRoomId") String chatRoomId
    );

}
