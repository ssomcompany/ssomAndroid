package com.ssomcompany.ssomclient.network.model;

import com.google.gson.annotations.SerializedName;
import com.ssomcompany.ssomclient.common.CommonConst;

public class FileResponse {
    @SerializedName(CommonConst.Intent.FILE_ID)
    private String fileId;

    public String getFileId() {
        return fileId;
    }
}
