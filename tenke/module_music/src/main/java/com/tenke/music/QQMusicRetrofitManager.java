package com.tenke.music;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by xbai on 6/14/2018.
 */

public class QQMusicRetrofitManager {

    public enum RequestType {
        RECOMMENDATION,
        RECOMMENDATION_DETAIL,
        RANKING,
        RANKING_DETAIL,
        FCG_FILE,
        WEB_MUSIC_LOGIN,
        RADIO_ALBUM_LIST,
        RADIO_DETAIL_LIST,
        QQ_MUSIC_PLAY,
    }

    private static QQMusicRetrofitManager instance;
    private Retrofit recommendClient;
    private Retrofit recomDetailClient;
    private Retrofit rankingClient;
    private Retrofit rankingDetailClient;
    private Retrofit fcgFileClient;
    private Retrofit webMusicLoginClient;
    private Map<String, String> cookieMap = new HashMap<>();
    private Retrofit radioAlbumClient;
    private Retrofit radioDetailClient;
    private Retrofit downloadMusicClient;

    private QQMusicRetrofitManager() {
        init();
    }

    public static QQMusicRetrofitManager getInstance() {
        if (instance == null) {
            instance = new QQMusicRetrofitManager();
        }
        return instance;
    }

    private void init() {
        recommendClient = buildRetrofitClient(RequestType.RECOMMENDATION);
        recomDetailClient = buildRetrofitClient(RequestType.RECOMMENDATION_DETAIL);
        rankingClient = buildRetrofitClient(RequestType.RANKING);
        rankingDetailClient = buildRetrofitClient(RequestType.RANKING_DETAIL);
        fcgFileClient = buildRetrofitClient(RequestType.FCG_FILE);
        radioAlbumClient = buildRetrofitClient(RequestType.RADIO_ALBUM_LIST);
        radioDetailClient = buildRetrofitClient(RequestType.RADIO_DETAIL_LIST);
        webMusicLoginClient = buildRetrofitClient(RequestType.WEB_MUSIC_LOGIN);
        downloadMusicClient = buildRetrofitClient(RequestType.QQ_MUSIC_PLAY);
    }

    private Retrofit buildRetrofitClient(RequestType type) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        if (type == RequestType.WEB_MUSIC_LOGIN) {
            cookieMap.clear();
            builder.cookieJar(new CookieJar() {
                private final Map<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url);
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url, cookies);
                    if (cookies == null || cookies.isEmpty()) {
                        return;
                    }
                    for (Cookie cookie : cookies) {
                        if (!TextUtils.isEmpty(cookie.name()) && !TextUtils.isEmpty(cookie.value())) {
                            cookieMap.put(cookie.name(), cookie.value());
                        }
                    }
                }
            });
            builder.connectTimeout(10, TimeUnit.SECONDS);
            builder.readTimeout(20, TimeUnit.SECONDS);
        }
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        switch (type) {
            case RECOMMENDATION:
                retrofitBuilder.baseUrl(QQMusicApiService.RECOMMENDATION_BASE_URL);
                break;
            case RECOMMENDATION_DETAIL:
                retrofitBuilder.baseUrl(QQMusicApiService.RECOMMENDATION_DETAIL_BASE_URL);
                break;
            case RANKING:
            case WEB_MUSIC_LOGIN:
            case RANKING_DETAIL:
                retrofitBuilder.baseUrl(QQMusicApiService.RANKING_BASE_URL);
                break;
            case FCG_FILE:
                retrofitBuilder.baseUrl(QQMusicApiService.FCG_FILE_BASE_URL);
                break;
            case RADIO_ALBUM_LIST:
                retrofitBuilder.baseUrl(QQMusicApiService.RADIO_ALBUM_LIST_BASE_URL);
                break;
            case RADIO_DETAIL_LIST:
                retrofitBuilder.baseUrl(QQMusicApiService.RADIO_DETAIL_BASE_URL);
                break;
            case QQ_MUSIC_PLAY:
                retrofitBuilder.baseUrl(QQMusicApiService.QQ_MUSIC_PLAY);
                break;
        }
        return retrofitBuilder.build();
    }

    public Retrofit getRetrofitClient(RequestType type) {
        switch (type) {
            case RECOMMENDATION:
                return recommendClient;
            case RECOMMENDATION_DETAIL:
                return recomDetailClient;
            case RANKING:
                return rankingClient;
            case RANKING_DETAIL:
                return rankingDetailClient;
            case FCG_FILE:
                return fcgFileClient;
            case WEB_MUSIC_LOGIN:
                return webMusicLoginClient;
            case RADIO_ALBUM_LIST:
                return radioAlbumClient;
            case RADIO_DETAIL_LIST:
                return radioDetailClient;
            case QQ_MUSIC_PLAY:
                return downloadMusicClient;
        }
        return null;
    }

    public Map<String, String> getCookieMap() {
        return cookieMap;
    }

    public void logout() {
        cookieMap.clear();
    }
}
