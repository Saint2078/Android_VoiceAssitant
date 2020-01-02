package com.tenke.music;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.FormBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by xbai on 6/14/2018.
 */

public interface QQMusicApiService {
    String RECOMMENDATION_BASE_URL = "https://u.y.qq.com/";
    String RECOMMENDATION_DETAIL_BASE_URL = "https://c.y.qq.com/";
    String RANKING_BASE_URL = "https://c.y.qq.com/";
    String FCG_FILE_BASE_URL = "https://c.y.qq.com/";
    String RADIO_ALBUM_LIST_BASE_URL = "https://c.y.qq.com/";
    String RADIO_DETAIL_BASE_URL = "https://u.y.qq.com/";
    String QQ_MUSIC_PLAY = "http://dl.stream.qqmusic.qq.com/";


    @GET("https://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.song&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p=1&n=20&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0")
    Observable<SearchResponse> searchMusicByKeywords(@QueryMap Map<String, Object> parameters, @Query("w") String w);


}
