package com.tenke.lib_common.DataManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tenke.lib_common.ApplicationContextLink;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;


public class BaseRecorder {
    private static final String DB_FIELD_ROWID = "_id"; // Not exposed externally
    private static final String DB_FIELD_KEY = "user_key";
    private static final String DB_FIELD_DATA = "user_data";

    private static Set<BaseRecorder> sRsmList = new HashSet<BaseRecorder>();
    private static Object sRsmListMutex = new Object();

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Context mContext;
    private String mClassname;

    public static final String DATABASE_FILENAME_PREFIX = "trackview";
    public static final String DATABASE_FILENAME_SUFFIX = ".db";
    public static final String DATABASE_TABLE = "trackview_rsm";
    private static final int DATABASE_VERSION = 1;

    /**
     * Database creation SQL statement.
     */
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + DATABASE_TABLE + "("
                    + DB_FIELD_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DB_FIELD_KEY + " TEXT, "
                    + DB_FIELD_DATA + " BLOB);";

    /**
     * Construct a new <code>RecordStoreManager</code>.
     *
     * @param classname Used to differentiate records stored by each RecordStoreManager
     *                  instance from each other.  Must be unique within the app.
     */
  public BaseRecorder(String classname) {
        synchronized (sRsmListMutex) {
            sRsmList.add(this);
        }

        mContext = ApplicationContextLink.LinkToApplication();
        mClassname = classname;
    }

    public BaseRecorder(String classname, Context context) {
        synchronized (sRsmListMutex) {
            sRsmList.add(this);
        }
        this.mContext=context;
        mClassname = classname;
    }
    /**
     * Removes all the records managed by all instances of this class.
     */
    static public void removeAllFromAllManagers() {
        synchronized (sRsmListMutex) {
            Iterator<BaseRecorder> iter = sRsmList.iterator();
            while (iter.hasNext()) {
                BaseRecorder rsm = iter.next();
                rsm.removeAll();
            }
        }
    }

    public Cursor putFromContentProvider(String key, Object data) {
        return null;
    }

    /**
     * Helper class for handling DB create & upgrade operations.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context, String filename) {
            super(context, filename, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    /**
     * Open the database for writing.  Required before doing any queries or modifications.
     */
    public SQLiteDatabase open() throws SQLException {
        String filename = DATABASE_FILENAME_PREFIX + mClassname + DATABASE_FILENAME_SUFFIX;
        mDbHelper = new DatabaseHelper(mContext, filename);
        mDb = mDbHelper.getWritableDatabase();
        return mDb;
    }

    /**
     * Close the database.
     */
    public void close() {
        mDbHelper.close();
    }

    /**
     * if there is not sufficient space left, first records will be deleted to make space
     * this is useful when RecordManager is used for Logger to make sure the logs
     * do not take up all the space on the device.
     */
    private int MAX_SIZE = -1; //no preset limit for records

    public int getMaxSize() {
        return MAX_SIZE;
    }

    /**
     * Computes and returns total data size of all the recordstores managed by this instance
     *
     * @return total data size of all the recordstores managed by this instance
     */
    public int getTotalSize() {
        int length = 0;
        try {
            open();

            File dbFile = new File(mDb.getPath());
            length = (int) dbFile.length();
        } finally {
            close();
        }
        return length;
    }

    public void setMaxSize(int max_size) {
        MAX_SIZE = max_size;
    }

    /**
     * Returns a copy of all the record keys. The record
     * keys are maintained in the order of record creation.
     *
     * @return record keys
     */
    public synchronized Vector<String> getKeys() {
        Vector<String> keys = new Vector<String>();
        Cursor cursor = null;
        try {
            open();

            String fields[] = {DB_FIELD_KEY};
            // Match all records.
            cursor = mDb.query(DATABASE_TABLE, fields, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                while (!cursor.isAfterLast()) {
                    String key = cursor.getString(0);
                    keys.add(key);
                    cursor.moveToNext();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return keys;
    }

    /**
     * Save data to the RecordStore.  A key for the data is automatically generated.
     *
     * @param value Data to be stored
     * @return The generated RecordStore key.
     */
    public synchronized String add(byte[] value) {
        if (value == null || value.length == 0) {
            return null;
        }
        String key = "";
        try {
            key = getNextKey();

            open();

            ContentValues vals = new ContentValues();
            vals.put(DB_FIELD_KEY, key);
            vals.put(DB_FIELD_DATA, value);

            mDb.insert(DATABASE_TABLE, null, vals);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return key;
    }

    /**
     * Saves the key/value pair to the RecordStore. If a record with the given key
     * already exists, it is updated with the new value.  Note that the key used
     * must be prefixed with <code>recordStoreName</code>.  If <code>null</code> is
     * passed for key, then a key is generated automatically.
     *
     * @param key   The key for the record
     * @param value Data to be saved as a record
     * @return the key for the recordstore
     */
    public synchronized String put(String key, byte[] value) {
        if (key == null) {
            return add(value);
        } else {
            try {
                open();

                ContentValues vals = new ContentValues();
                vals.put(DB_FIELD_KEY, key);
                vals.put(DB_FIELD_DATA, value);

                // Update the records which match the key.
                int rowsUpdated = mDb.update(DATABASE_TABLE, vals, DB_FIELD_KEY + "=\"" + key + "\"", null);
                if (rowsUpdated == 0) {
                    // No records were updated, so add a new one.
                    mDb.insert(DATABASE_TABLE, null, vals);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
            return key;
        }
    }

    /**
     * Retrieves the record data for the given key
     *
     * @param key The key of the record to be retrieved
     * @return data as byte array
     */
    public synchronized byte[] get(String key) {
        byte[] data = null;
        Cursor cursor = null;
        try {
            open();

            cursor =getCusor(key);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                data = cursor.getBlob(0);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return data;
    }

    public Cursor getCusor(String key) {
        Cursor query = mDb.query(true, DATABASE_TABLE,
                new String[]{DB_FIELD_DATA}, DB_FIELD_KEY + "=\"" + key + "\"",
                null, null, null, null, null);
        return query;
    }

    /**
     * Retrieves the record data size for the given key
     *
     * @param key The key of the record to be retrieved
     * @return data size
     */
    public synchronized int getRecordSize(String key) {
        // TODO: Is there a more memory-efficient way to find out the size of a record?
        byte[] blob = get(key);
        return blob.length;
    }

    /**
     * Removes all the records managed by this instance
     */
    public synchronized void removeAll() {
        try {
            open();

            // Delete all rows.
            mDb.delete(DATABASE_TABLE, null, null);
        } finally {
            close();
        }
    }

    /**
     * Removes the record with the given key from RecordStore
     *
     * @param key The key of the record to be removed
     */
    public synchronized void remove(String key) {
        try {
            open();
            mDb.delete(DATABASE_TABLE, DB_FIELD_KEY + "=\"" + key + "\"", null);
        } finally {
            close();
        }
    }

    /**
     * Generate the next available RecordStore key.
     *
     * @throws Exception
     */
    private String getNextKey() {
        String key = "";
        Cursor cursor = null;
        try {
            open();

            final int MAX_RETRIES = 5;
            int retry = 0;
            do {
                UUID uuid = UUID.randomUUID();
                key = uuid.toString();

                // Make sure the key is not already in use.
                cursor =
                        mDb.query(true, DATABASE_TABLE, new String[]{DB_FIELD_KEY},
                                DB_FIELD_KEY + "=\"" + key + "\"",
                                null, null, null, null, null);
                if (cursor != null && cursor.getCount() == 0) {
                    // Got a good key.
                    return key;
                }
                ++retry;
            } while (retry < MAX_RETRIES);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return key;
    }

    public byte[] serializeObject(Object obj) {
        if (obj != null) {
            byte[] byteArray = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                byteArray = baos.toByteArray();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            return byteArray;
        }
        return null;
    }


    public Object deserializeObject(byte[] byteArray) {
        if (byteArray != null && byteArray.length != 0) {
            Object obj = null;
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
                ObjectInputStream ois = new ObjectInputStream(bais);
                obj = ois.readObject();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            return obj;
        }
        return null;
    }


    public void put(Object key, Object data) {
        if (key != null && data != null) {
            byte[] byteArray = serializeObject(data);
            if (byteArray != null) {
                put((String) key, byteArray);
            }
        }
    }

    public Object get(Object key) {
        if (key != null) {
            byte[] byteArray = get((String) key);
            if (byteArray != null) {
                return deserializeObject(byteArray);
            }
        }
        return null;
    }

    public void remove(Object key) {
        if (key != null) {
            remove((String) key);
        }
    }
}
