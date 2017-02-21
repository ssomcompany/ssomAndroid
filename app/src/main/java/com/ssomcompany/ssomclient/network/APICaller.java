package com.ssomcompany.ssomclient.network;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.ssomcompany.ssomclient.activity.BaseActivity;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.api.AddHeartCount;
import com.ssomcompany.ssomclient.network.api.CreateChattingRoom;
import com.ssomcompany.ssomclient.network.api.DeleteChattingRoom;
import com.ssomcompany.ssomclient.network.api.DeleteTodayPhoto;
import com.ssomcompany.ssomclient.network.api.FacebookLogin;
import com.ssomcompany.ssomclient.network.api.GetApplicationVersion;
import com.ssomcompany.ssomclient.network.api.GetChattingList;
import com.ssomcompany.ssomclient.network.api.GetChattingRoomList;
import com.ssomcompany.ssomclient.network.api.GetHeartCount;
import com.ssomcompany.ssomclient.network.api.GetSsomList;
import com.ssomcompany.ssomclient.network.api.GetUserCount;
import com.ssomcompany.ssomclient.network.api.GetUserProfile;
import com.ssomcompany.ssomclient.network.api.SendChattingMessage;
import com.ssomcompany.ssomclient.network.api.SsomChatUnreadCount;
import com.ssomcompany.ssomclient.network.api.SsomExistMyPost;
import com.ssomcompany.ssomclient.network.api.SsomImageUpload;
import com.ssomcompany.ssomclient.network.api.SsomLogin;
import com.ssomcompany.ssomclient.network.api.SsomLoginWithoutID;
import com.ssomcompany.ssomclient.network.api.SsomLogout;
import com.ssomcompany.ssomclient.network.api.SsomMeetingRequest;
import com.ssomcompany.ssomclient.network.api.SsomPostCreate;
import com.ssomcompany.ssomclient.network.api.SsomPostDelete;
import com.ssomcompany.ssomclient.network.api.SsomProfileImageUpload;
import com.ssomcompany.ssomclient.network.api.SsomRegisterUser;
import com.ssomcompany.ssomclient.network.api.UpdateChattingRoom;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.BaseResponse;
import com.ssomcompany.ssomclient.network.model.SsomRequest;
import com.ssomcompany.ssomclient.network.model.SsomResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class APICaller {
    private static final int TIME_OUT_LONG = 10000;

    public static <T extends BaseResponse> void getSsomList(String userId, String typeFilter, String ageFilter, String countFilter,
                                                            double lat, double lng, NetworkManager.NetworkListener<T> listener) {
        GetSsomList.Request request = new GetSsomList.Request().setUserId(userId).setSsomTypeFilter(typeFilter).setAgeFilter(ageFilter)
                .setCountFilter(countFilter).setLat(lat).setLng(lng);

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

    public static <T extends BaseResponse> void ssomLoginWithoutID(String playerId, NetworkManager.NetworkListener<T> listener) {
        SsomLoginWithoutID.Request request = new SsomLoginWithoutID.Request().setPlayerId(playerId);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomLoginWithoutID.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void ssomLogin(String email, String password, String playerId,
                                                          NetworkManager.NetworkListener<T> listener) {
        SsomLogin.Request request = new SsomLogin.Request().setPlayerId(playerId);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION,
                "Basic " + Base64.encodeToString((String.format(Locale.getDefault(),"%s:%s", email, password)).getBytes(), Base64.DEFAULT));

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomLogin.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void ssomLogout(String token, NetworkManager.NetworkListener<T> listener) {
        SsomLogout.Request request = new SsomLogout.Request();
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomLogout.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void facebookLogin(String token, String playerId, NetworkManager.NetworkListener<T> listener) {
        FacebookLogin.Request request = new FacebookLogin.Request().setPlayerId(playerId);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION,
                "Bearer " + token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<FacebookLogin.Response>>() {}.getType(), listener);
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

    public static <T extends BaseResponse> void updateChattingRoom(String token, String chatRoomId, NetworkManager.NetworkListener<T> listener) {
        UpdateChattingRoom.Request request = new UpdateChattingRoom.Request(chatRoomId);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<UpdateChattingRoom.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void getChattingList(String token, String roomId, NetworkManager.NetworkListener<T> listener) {
        GetChattingList.Request request = new GetChattingList.Request(roomId);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<GetChattingList.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void createChattingRoom(String token, String postId,
                                                                   double lat, double lng, NetworkManager.NetworkListener<T> listener) {
        CreateChattingRoom.Request request = new CreateChattingRoom.Request().setPostId(postId).setLat(lat).setLng(lng);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<CreateChattingRoom.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void deleteChattingRoom(String token, String chatroomId, NetworkManager.NetworkListener<T> listener) {
        DeleteChattingRoom.Request request = new DeleteChattingRoom.Request(chatroomId);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<DeleteChattingRoom.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void totalChatUnreadCount(String token, NetworkManager.NetworkListener<T> listener) {
        SsomChatUnreadCount.Request request = new SsomChatUnreadCount.Request();
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomChatUnreadCount.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void sendChattingMessage(String token, String roomId, long lastMessageTime,
                                           String msg, NetworkManager.NetworkListener<T> listener) {
        SendChattingMessage.Request request = new SendChattingMessage.Request(roomId, lastMessageTime).setMsg(msg);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SendChattingMessage.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void sendChattingRequest(String token, String roomId, int methodType,
                                                                    NetworkManager.NetworkListener<T> listener) {
        SsomRequest request;

        switch (methodType) {
            case NetworkConstant.Method.PUT:
                request = new SsomMeetingRequest.PutRequest().setChatroomId(roomId);
                break;
            case NetworkConstant.Method.DELETE:
                request = new SsomMeetingRequest.DeleteRequest(roomId);
                break;
            case NetworkConstant.Method.POST:
            default:
                request = new SsomMeetingRequest.PostRequest().setChatroomId(roomId);
                break;
        }

        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomMeetingRequest.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void ssomProfileImageUpload(String token, String profileImgUrl,
                                                                       NetworkManager.NetworkListener<T> listener) {
        SsomProfileImageUpload.Request request = new SsomProfileImageUpload.Request().setProfileImgUrl(profileImgUrl);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<SsomProfileImageUpload.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void getApplicationVersion(NetworkManager.NetworkListener<T> listener) {
        GetApplicationVersion.Request request = new GetApplicationVersion.Request();

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<GetApplicationVersion.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void deleteTodayPhoto(String token, NetworkManager.NetworkListener<T> listener) {
        DeleteTodayPhoto.Request request = new DeleteTodayPhoto.Request();
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<DeleteTodayPhoto.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void getHeartCount(String token, NetworkManager.NetworkListener<T> listener) {
        GetHeartCount.Request request = new GetHeartCount.Request();
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<GetHeartCount.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void getUserProfile(String token, String userId, NetworkManager.NetworkListener<T> listener) {
        GetUserProfile.Request request = new GetUserProfile.Request(userId);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<GetUserProfile.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void addHeartCount(String token, int count, String purchaseToken,
                                                              NetworkManager.NetworkListener<T> listener) {
        AddHeartCount.Request request = new AddHeartCount.Request().setCount(String.valueOf(count)).setDevice("android").setToken(purchaseToken);
        request.putHeader(NetworkConstant.HeaderParam.AUTHORIZATION, token);

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<AddHeartCount.Response>>() {}.getType(), listener);
    }

    public static <T extends BaseResponse> void getCurrentUserCount(NetworkManager.NetworkListener<T> listener) {
        GetUserCount.Request request = new GetUserCount.Request();

        request.setTimeoutMillis(TIME_OUT_LONG);
        NetworkManager.request(request, new TypeToken<SsomResponse<GetUserCount.Response>>() {}.getType(), listener);
    }

    public static void ssomImageUpload(final BaseActivity activity, Response.Listener<NetworkResponse> listener,
                                       final String picturePath) {
        final String twoHyphens = "--";
        final String lineEnd = "\r\n";
        final String boundary = "apiclient-" + System.currentTimeMillis();
        final String mimeType = "multipart/form-data;boundary=" + boundary;
        final int maxBufferSize = 1024 * 1024;

        final RequestQueue queue = Volley.newRequestQueue(activity);

        Log.d("image upload", "token : " + activity.getToken());
        activity.showProgressDialog(true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                queue.cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        Log.d("image upload", "all requests canceled");
                        return true;
                    }
                });
            }
        });
        BaseVolleyRequest baseVolleyRequest = new BaseVolleyRequest(activity.getToken(), Request.Method.POST,
                NetworkConstant.API.IMAGE_FILE_UPLOAD, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(activity.getClass().getSimpleName(), error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return mimeType;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put(NetworkConstant.HeaderParam.AUTHORIZATION, getToken());
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);

                // orientation 을 통한 이미지 회전
                Log.d("image upload", "bitmap 성생중..");

                int orientation = Util.getOrientationFromUri(picturePath);
                if(orientation != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(orientation);
                    imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
                }

                Log.d("image upload", "data stream 연결 중...");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 1, byteArrayOutputStream);
                byte[] bitmapData = byteArrayOutputStream.toByteArray();
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                try {
                    Log.d("image upload", "data stream 쓰는 중...");
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"pict\";filename=\""
                            + "ssom_upload_from_camera.png" + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(bitmapData);
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    Log.d("image upload", "read file and write it into form...");
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    Log.d("image upload", "send multipart form data necesssary after file data...");
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    return bos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmapData;
            }
        };
        queue.add(baseVolleyRequest);
    }
}
