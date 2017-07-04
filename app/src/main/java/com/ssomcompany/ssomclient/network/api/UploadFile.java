package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.FileResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadFile {
    @Multipart
    @POST(NetworkConstant.API.IMAGE_FILE_UPLOAD)
    Call<FileResponse> uploadFile(
            @Part MultipartBody.Part file
    );
}
