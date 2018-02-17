package anton.peer_id.audd;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import anton.peer_id.network.API;


public class Database extends SQLiteOpenHelper {

    public static final String LIST_STORY = "story";

    public Database() {
        super(Application.context, "database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table lists (position integer primary key autoincrement, json blob, list_id text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void clearList(String listName) {
        SQLiteDatabaseFixed.getInstance().delete("lists", "list_id = ?", new String[] {
                String.valueOf(listName)
        });
    }

    public JSONArray getList(String listName) {
        JSONArray array = new JSONArray();
        SQLiteDatabaseFixed db = SQLiteDatabaseFixed.getInstance();
        Cursor cursor = db.query("lists", new String[] {"json"}, "list_id = ?", new String[] {
                String.valueOf(listName)
        }, "position DESC LIMIT 100");
        try {
            if (cursor != null && cursor.moveToFirst()) {
                int jsonIndex = cursor.getColumnIndex("json");
                do {
                    array.put(new JSONObject(cursor.getString(jsonIndex)));
                } while (cursor.moveToNext());
            }
        } catch (Throwable ignored) { }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return array;
    }

    public void setList(JSONArray array, String listName, boolean clear) {
        SQLiteDatabaseFixed db = SQLiteDatabaseFixed.getInstance();
        if (clear) {
            clearList(listName);
        }
        db.startTransaction();
        ContentValues values;
        JSONObject item;
        try {
            for (int i = 0; i < array.length(); i++) {
                item = array.getJSONObject(i);
                values = new ContentValues();
                values.put("json", String.valueOf(item));
                values.put("list_id", listName);
                db.insertOrReplace("lists", values);
            }
        } catch (Throwable ignored) {}
        db.endTransaction();
    }

    public void addItem(JSONObject item, String listName) {
        SQLiteDatabaseFixed db = SQLiteDatabaseFixed.getInstance();
        try {
            ContentValues values = new ContentValues();
            values.put("json", String.valueOf(item));
            values.put("list_id", listName);
            db.insertOrReplace("lists", values);
        } catch (Throwable ignored) { }
    }

    public void postList(final JSONArray array, final String listName, final boolean clear) {
        API.thread.postRunnable(new Runnable() {
            @Override
            public void run() {
                setList(array, listName, clear);
            }
        });
    }

    public void postItem(final JSONObject item, final String listName) {
        API.thread.postRunnable(new Runnable() {
            @Override
            public void run() {
                addItem(item, listName);
            }
        });
    }

    public static void closeDatabase() {
        SQLiteDatabaseFixed.getInstance().close();
    }

    private static Database Instance = null;

    public static Database getInstance() {
        Database localInstance = Instance;
        if (localInstance == null) {
            synchronized (Database.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new Database();
                }
            }
        }
        return localInstance;
    }
}
