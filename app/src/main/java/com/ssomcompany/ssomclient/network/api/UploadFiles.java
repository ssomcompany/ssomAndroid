package com.ssomcompany.ssomclient.network.api;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomMultiPartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;


public class UploadFiles extends AsyncTask<String, Integer, String> {
    private ProgressBar progressBar;
    private long totalSize = 0;

    @Override
    protected void onPreExecute() {
        // setting progress bar to zero
//        progressBar.setProgress(0);
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // Making progress bar visible
//        progressBar.setVisibility(View.VISIBLE);

        // updating progress bar value
//        progressBar.setProgress(progress[0]);

        // updating percentage value
//        txtPercentage.setText(String.valueOf(progress[0]) + "%");
    }

    @Override
    protected String doInBackground(String... params) {
        return uploadFile(params);
    }

    @SuppressWarnings("deprecation")
    private String uploadFile(String... params) {
        String responseString;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(NetworkConstant.HTTP_SCHME + NetworkConstant.HOST + NetworkConstant.API.IMAGE_FILE_UPLOAD);

        try {
            SsomMultiPartEntity entity = new SsomMultiPartEntity(
                    new SsomMultiPartEntity.ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            httppost.addHeader(NetworkConstant.HeaderParam.AUTHORIZATION, params[0]);

            File sourceFile = new File(params[1]);
            // Adding file data to http body
            entity.addPart("image", new FileBody(sourceFile));
            // Extra parameters if you want to pass to server
//            entity.addPart("website",
//                    new StringBody("www.androidhive.info"));
//            entity.addPart("email", new StringBody("abc@gmail.com"));
            totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

        } catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }

        return responseString;
    }
}
