package com.migapro.jiraclient.services;

import com.migapro.jiraclient.Util.Constants;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class ServiceGenerator {

    public static JiraService generateService() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        httpClient.setReadTimeout(60, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(60, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(JiraService.class);
    }
}
