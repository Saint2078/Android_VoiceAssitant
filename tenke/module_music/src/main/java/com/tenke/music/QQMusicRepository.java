package com.tenke.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class QQMusicRepository {

    private static QQMusicRepository instance;

    private QQMusicRepository() {
    }

    public static QQMusicRepository getInstance() {
        if (instance == null) {
            instance = new QQMusicRepository();
        }

        return instance;
    }

    //改变思路
    public void searchSongs(final String keywords, final Observer<HashMap<String, List>> observer) {
        getLoginApiService().searchMusicByKeywords(getCommonParameters(), keywords)
                .subscribeOn(Schedulers.io())
                .map(new Function<SearchResponse, HashMap<String, List>>() {
                    @Override
                    public HashMap<String, List> apply(SearchResponse searchResponse) throws Exception {
                        List<SongList> songList = searchResponse.getData().getSong().getList();
                        List<Audible> audibleList = new ArrayList<>();
                        for (SongList songlist : songList) {
                            Song song = new Song(songlist.getAlbum(), songlist.getSinger(), songlist.getName(), songlist.getMid(), songlist.getInterval());
                            song.setSongId(songlist.getId());
                            audibleList.add(song);
                        }
                        HashMap<String, List> map = new HashMap<>();
                        map.put(MusicRepository.KEY_AUDIBLE_LIST, audibleList);
                        return map;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    private QQMusicApiService getLoginApiService() {
        Retrofit retrofit = QQMusicRetrofitManager.getInstance().getRetrofitClient(QQMusicRetrofitManager.RequestType.WEB_MUSIC_LOGIN);
        return retrofit.create(QQMusicApiService.class);
    }

}
