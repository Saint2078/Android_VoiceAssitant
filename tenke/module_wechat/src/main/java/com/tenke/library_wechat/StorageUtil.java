package com.tenke.library_wechat;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.orhanobut.logger.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class StorageUtil {
    private final static String TAG = "StorageUtil";
    public static final String SUFFIX = ".out";

    private StorageUtil() {
    }

    public static Object getContentByUri(Context context, Uri uri, String db_key) {
        Object result = null;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, db_key, null, null);
        if (cursor != null && cursor.moveToNext()) {
            byte[] byteArray = cursor.getBlob(0);
            if (byteArray != null && byteArray.length != 0) {
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    result = ois.readObject();
                } catch (Throwable ex) {
                    Logger.t(TAG).e("", ex);
                }
            }
            cursor.close();
        }
        return result;
    }

    public static void saveToCache(final Object o, final String fileName, final String cachePath) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        String filePath = cachePath + fileName + SUFFIX;
        Logger.t(TAG).d("saveToCache() fileName = " + filePath);
        try {
            File file = new File(cachePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fos = new FileOutputStream(filePath);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.flush();
        } catch (Exception e) {
            Logger.t(TAG).e(e, "saveToCache()", e.getMessage());
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
                Logger.t(TAG).e(e, "saveToCache()", e.getMessage());
            }
            try {
                fos.close();
            } catch (Exception e) {
                Logger.t(TAG).e(e, "saveToCache()", e.getMessage());
            }
        }
    }

    public static Serializable readFromCache(String fileName, String cachePath) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        String filePath = cachePath + fileName + SUFFIX;
        Logger.t(TAG).d("readFromCache() fileName = " + filePath);
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Logger.t(TAG).e("readFromCache() file does not exist!");
                return null;
            }
            fis = new FileInputStream(filePath);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (Exception e) {
            Logger.t(TAG).e(e, "readFromCache()", e.getMessage());
            if (e instanceof InvalidClassException) {
                File data = new File(filePath);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static boolean removeFromCache(String fileName, String cachePath) {
        String filePath = cachePath + fileName + SUFFIX;
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                return file.delete();
            }
        } catch (Exception e) {
            Logger.t(TAG).d(e.toString());
        }

        return false;
    }

    public static void downloadToCache(final InputStream inputStream, long length, final String fileName, final String cachePath) {
        FileOutputStream fos = null;
        byte[] buf = new byte[2048 * 2];
        String filePath = cachePath + fileName;
        Logger.t(TAG).d("downloadToCache() start fileName = " + filePath);
        File file = null;
        try {
            file = new File(cachePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fos = new FileOutputStream(filePath);
            int len = 0;
            long sum = 0;
            int progress = 0;
            while ((len = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                progress = (int) (sum * 1.0f / length * 100);
            }
            fos.flush();
            if (progress == 100) {
                Logger.t(TAG).d("downloadToCache() completed");
            } else {
                Logger.t(TAG).d("downloadToCache() failed");
                file.delete();
            }
        } catch (Exception e) {
            Logger.t(TAG).e(e, "downloadToCache()", e.getMessage());
            if (file != null) {
                file.delete();
            }
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                Logger.t(TAG).e(e, "downloadToCache()", e.getMessage());
            }
            try {
                fos.close();
            } catch (Exception e) {
                Logger.t(TAG).e(e, "downloadToCache()", e.getMessage());
            }
        }
    }

    public static boolean isExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
}
