package com.ssomcompany.ssomclient.network;

import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.common.SsomPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private static Retrofit retrofit;
    private SsomPreferences session;

    private RetrofitManager() {
        session = new SsomPreferences(BaseApplication.getInstance(), SsomPreferences.LOGIN_PREF);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        retrofit = new Retrofit.Builder().baseUrl(NetworkUtil.getSsomHostUrl())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(@NonNull Chain chain) throws IOException {
                                Request original = chain.request();

                                // Request customization: add request headers
                                Request.Builder requestBuilder = original.newBuilder();
                                if(!session.getString(SsomPreferences.PREF_SESSION_TOKEN, "").isEmpty()) {
                                    requestBuilder = requestBuilder
                                            .header(NetworkConstant.HeaderParam.AUTHORIZATION, "JWT " + session.getString(SsomPreferences.PREF_SESSION_TOKEN, ""));
                                }

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        })
                        .addInterceptor(interceptor)
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getInstance() {
        if(retrofit == null) {
            new RetrofitManager();
        }
        return retrofit;
    }
}
