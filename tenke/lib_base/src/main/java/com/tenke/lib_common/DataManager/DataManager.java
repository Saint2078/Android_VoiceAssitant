package com.tenke.lib_common.DataManager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class DataManager {
    public static final String ROOT_DB_NAME = "root_db";
    private static DataManager instance;
    private BaseRecorder recorder;

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private DataManager() {
        recorder = new BaseRecorder(ROOT_DB_NAME);
    }

    public Object get(String key) {
        if (key != null) {
            return recorder.get((Object) key);
        }
        return null;
    }

    public String getString(String key) {
        if (key != null) {
            return (String) recorder.get((Object) key);
        }
        return null;
    }

    public void put(String key, String value) {
        if (value == null) {
            delete(key);
        } else {
            recorder.put(key, value);
        }
    }

    public void put(String key, Object value) {
        if (value == null) {
            delete(key);
        } else {
            recorder.put(key, value);
        }
    }

    public void delete(String key) {
        recorder.remove(key);
    }

    public void removeAll() {
        String[] keyCache = {};
        Object[] cache = new Object[keyCache.length];
        for (int i = 0; i < keyCache.length; i++) {
            cache[i] = get(keyCache[i]);
        }

        recorder.removeAll();

        for (int i = 0; i < keyCache.length; i++) {
            put(keyCache[i], cache[i]);
        }
    }

    public Object getCarNumberByContentProvider(Context context, Uri parseUri, String key) {
        Cursor mCursor = null;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            mCursor = contentResolver.query(parseUri, null, key, null, null);
            if (mCursor != null && mCursor.moveToFirst()) {
                byte[] blob = mCursor.getBlob(0);
                return recorder.deserializeObject(blob);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return null;
    }
}
