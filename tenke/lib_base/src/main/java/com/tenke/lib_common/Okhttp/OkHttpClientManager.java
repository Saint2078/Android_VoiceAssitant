package com.tenke.lib_common.Okhttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by marshall on 27/10/2017.
 */

public class OkHttpClientManager {

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient mOkHttpClient;

    private static class Holder {
        private static final OkHttpClientManager INSTANCE = new OkHttpClientManager();
    }

    private OkHttpClientManager() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }

    public static OkHttpClientManager getInstance() {
        return Holder.INSTANCE;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static void getAsync(String url, Callback callback) {
        getInstance()._getAsync(url, callback);
    }

    private void _getAsync(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    public static void postAsync(String url, Callback callback, HashMap<String, Object> params) {
        getInstance()._postAsync(url, callback, params);
    }

    private void _postAsync(String url, Callback callback, HashMap<String, Object> params) {
        JSONObject object = new JSONObject();
        for (String key : params.keySet()) {
            try {
                object.put(key, params.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, object.toString());
        Request request = new Request.Builder().url(url).post(requestBody).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }
}
