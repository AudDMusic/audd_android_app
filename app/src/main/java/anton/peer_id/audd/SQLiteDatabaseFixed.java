package anton.peer_id.audd;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.concurrent.CountDownLatch;


public class SQLiteDatabaseFixed {

    private static SQLiteDatabase db = null;
    private boolean isTransaction = false;
    private CountDownLatch syncLatch = new CountDownLatch(1);

    public SQLiteDatabaseFixed() {
        syncLatch.countDown();
        checkOpen();
    }

    public Cursor rawQuery(String inquiry) {
        try {
            checkOpen();
            return db.rawQuery(inquiry, null);
        } catch (Throwable e) {
            Log.e("SQLiteDatabaseFixed", "rawQuery#2: " + String.valueOf(e));
        }
        return null;
    }

    private void await() {
        try { syncLatch.await(); } catch (Throwable ignored) { }
    }

    public Cursor rawQuery(String inquiry, String[] params) {
        await();
        try {
            checkOpen();
            return db.rawQuery(inquiry, params);
        } catch (Throwable e) {
            Log.e("SQLiteDatabaseFixed", "rawQuery#1: " + String.valueOf(e));
        }
        return null;
    }

    public Cursor query(String table, String[] values, String where, String[] params, String sort) {
        await();
        checkOpen();
        Cursor cursor = null;
        try {
            cursor = db.query(table, values, where, params, null, null, sort);
        } catch (Throwable e) {
            Log.e("SQLiteDatabaseFixed", "query#1: " + String.valueOf(e));
        }
        if (cursor == null) {
            try {
                cursor = db.query(table, values, where, params, null, null, sort);
            } catch (Throwable e) {
                Log.e("SQLiteDatabaseFixed", "query#2: " + String.valueOf(e));
            }
        }
        return cursor;
    }

    public void startTransaction() {
        if (isTransaction) {
            return;
            /*
            endTransaction();
            close();
             */
        }
        isTransaction = true;
        checkOpen();
        try {
            db.beginTransaction();
        } catch (Throwable e) {
            Log.e("SQLiteDatabaseFixed", "startTransaction: " + String.valueOf(e));
        }
    }

    public void endTransaction() {
        if (!isTransaction) {
            return;
        }
        isTransaction = false;
        checkOpen();
        try {
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Throwable e) {
            Log.e("SQLiteDatabaseFixed", "endTransaction: " + String.valueOf(e));
        }
    }

    public long insertOrReplace(String table, ContentValues itemValues) {
        await();
        checkOpen();
        long returnId = -1;
        for (int i = 0; i < 5; i++) {
            if (returnId != -1) {
                return returnId;
            }
            try {
                switch (i) {
                    case 0:
                        returnId = db.replace(table, null, itemValues);
                        break;
                    case 1:
                        returnId = db.insertWithOnConflict(table, null, itemValues, 5);
                        break;
                    case 2:
                        returnId = db.insert(table, null, itemValues);
                        break;
                    case 3:
                        returnId = db.replaceOrThrow(table, null, itemValues);
                        break;
                    case 4:
                        returnId = db.insertOrThrow(table, null, itemValues);
                        break;
                }
            } catch (Throwable ignored) { }
        }
        return returnId;
    }

    public long delete(String table, String where, String[] params) {
        try {
            return db.delete(table, where, params);
        } catch (Throwable e) {
            Log.e("SQLiteDatabaseFixed", "delete#1: " + String.valueOf(e));
            return -1;
        }
    }

    public long delete(String table) {
        try {
            checkOpen();
            return db.delete(table, null, null);
        } catch (Throwable e) {
            Log.e("SQLiteDatabaseFixed", "delete#2: " + String.valueOf(e));
            return -1;
        }
    }

    public long update(String table, ContentValues values, String where, String[] params) {
        try {
            checkOpen();
            return db.update(table, values, where, params);
        } catch (Throwable e) {
            Log.e("SQLiteDatabaseFixed", "update: " + String.valueOf(e));
            return -1;
        }
    }

    public void close() {
        try {
            if (isTransaction || (db != null && db.inTransaction())) {
                endTransaction();
                isTransaction = false;
            }
            if (db != null && db.isOpen()) {
                db.close();
                SQLiteDatabase.releaseMemory();
            }
        } catch (Throwable e) {
            Log.e("SQLiteDatabaseFixed", "close: " + String.valueOf(e));
        }
    }

    private void checkOpen() {
        try {
            SQLiteDatabase.releaseMemory();
            if (db != null && db.isOpen()) {
                return;
            }
            db = Database.getInstance().getWritableDatabase();
            db.execSQL("PRAGMA read_uncommitted = true;");
            db.execSQL("PRAGMA synchronous = OFF");
        } catch (Throwable ignored) { }
    }

    private static SQLiteDatabaseFixed Instance = null;

    public static SQLiteDatabaseFixed getInstance() {
        SQLiteDatabaseFixed localInstance = Instance;
        if (localInstance == null) {
            synchronized (SQLiteDatabaseFixed.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new SQLiteDatabaseFixed();
                }
            }
        }
        return localInstance;
    }
}
