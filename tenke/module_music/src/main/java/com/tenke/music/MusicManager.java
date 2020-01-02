package com.tenke.music;

import java.util.HashMap;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MusicManager {
    private void search(String mKeyword){
        MusicRepository.getInstance().search(mKeyword, new SearchResponse());
    }

    private class SearchResponse implements Observer<HashMap<String, List>>{

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(HashMap<String, List> stringListHashMap) {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    }

}
