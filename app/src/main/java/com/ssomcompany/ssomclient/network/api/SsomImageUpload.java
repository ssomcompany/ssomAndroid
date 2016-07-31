package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant.API;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

import java.util.ArrayList;

public class SsomImageUpload {

    public static class Request extends SsomPostRequest {
        private byte[] bitmapData;

        public Request() {
            super(API.IMAGE_FILE_UPLOAD);
        }

        public Request setBitmapData(byte[] bitmapData) {
            this.bitmapData = bitmapData;
            return this;
        }

        public byte[] getBitmapData() {
            return bitmapData;
        }

        @Override
        protected boolean isFormParam() {
            return true;
        }
    }

    public static class Response extends ToStringHelperClass {
        private String fileId;

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }
    }
}
