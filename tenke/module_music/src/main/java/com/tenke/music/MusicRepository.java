package com.tenke.music;

import java.util.HashMap;
import java.util.List;
import io.reactivex.Observer;

public class MusicRepository {
    public static final String TYPE_CHEYUEBAO = "cyb";
    public static final String TYPE_QQ = "qq";
    public static final String TYPE_163 = "163";
    public static final String TYPE_XIAMI = "xia";
    public static final String TYPE_XIMALAYA = "ximalaya";
    public static final String TYPE_SPOTIFY = "spotify";
    public static final String TYPE_PANDORA = "pandora";

    public static final String KEY_AUDIBLE_LIST = "al";
    public static final String KEY_PLAYLIST_LIST = "pl";
    public static final String KEY_XIMALAYA_LIST = "ximalaya list";

    public static final String KEY_RECOMMENDATIONS = "recommendations";
    public static final String KEY_RANKING = "ranking";
    public static final String KEY_MY_MUSIC_LIST = "my music list";

    public static final String ERROR_OF_NEED_TO_LOGIN = "Need to login";

    private static MusicRepository instance = new MusicRepository();

    public static MusicRepository getInstance() {
        return instance;
    }

    private QQMusicRepository mQQMusicRepository = QQMusicRepository.getInstance();

    public void search(String type, final String keywords, final Observer<HashMap<String, List>> observer) {
        mQQMusicRepository.searchSongs(keywords, observer);
    }

}
