package com.tenke.library_wechat;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface WeChatRestService {

    MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String WECHAT_LOGIN_END_POINT = "https://login.weixin.qq.com/";

    @GET("jslogin")
    Observable<String> getUUID(@Query("appid") String appId,
                               @Query("fun") String fun,
                               @Query("lang") String lang,
                               @Query("_") String timeStamp);

    @GET
    Observable<ResponseBody> getQRCode(@Url String url);

    /**
     * Waiting for user to confirm login in phone WeChat
     * @param tip
     *  1 not scanned
     *  0 scanned
     * @param uuid
     * @param timeStamp
     * @return
     */
    @GET("/cgi-bin/mmwebwx-bin/login")
    Observable<String> waitMobileLogin(@Query("tip") String tip,
                                       @Query("uuid") String uuid,
                                       @Query("_") String timeStamp,
                                       @Query("loginicon") boolean needLoginIcon);

    @GET
    Observable<String> redirectNewLoginPage(@Url String redirectUrl);

    @POST
    Observable<String> wechatInit(@Url String url, @Body RequestBody requestBody);

    @POST
    Observable<String> webWxStatusNotify(@Url String url, @Body RequestBody requestBody);

    @GET
    Observable<String> getContacts(@Url String url, @Query("pass_ticket") String passTicket,
                                   @Query("skey") String skey,
                                   @Query("seq") int seq,
                                   @Query("r") long timeStamp);

    @POST
    Observable<String> batchGetGroupContacts(@Url String url, @Body RequestBody requestBody);

    @GET
    Observable<String> pollWeChatMessage(@Url String url, @QueryMap Map<String, String> queryMap);

    @GET
    Observable<String> testSyncCheck(@Url String url, @QueryMap Map<String, String> queryMap);

    @POST
    Observable<String> messageSync(@Url String url, @Body RequestBody requestBody);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url, @Header("Range") String range);

    @POST
    Observable<String> sendMessage(@Url String url, @Body RequestBody requestBody);

}
