package anton.peer_id.network;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import anton.peer_id.audd.Application;
import anton.peer_id.audd.Database;
import anton.peer_id.audd.DispatchQueue;
import anton.peer_id.audd.NotificationCenter;

public class API {

    private static final String TAG = "APILog";

    public static class Method {
        public static final String RECOGNIZE = "recognize";
        public static final String RECOGNIZE_WITH_OFFSET = "recognizeWithOffset";
    }

    private static volatile API Instance = null;
    private static final String URL = "https://api.audd.io/";
    private static final String API_TOKEN = "ec7918d7d5ef6aeac5d6ac4897a0105d";
    public static final DispatchQueue thread = new DispatchQueue("API");
    private static final int group_id = 138792192;
    private Runnable searchRunnable;

    public void recognizeVoice(final File file, final boolean isHumming) {
        thread.postRunnable(new Runnable() {
            @Override
            public void run() {
                ResultTrack track = null;
                try {
                    String method;
                    JSONObject response = upload(file, method = isHumming ? Method.RECOGNIZE_WITH_OFFSET : Method.RECOGNIZE);
                    Log.e(TAG, method + " = " + String.valueOf(response));
                    if (response.has("result") && !response.isNull("result")) {
                        track = new ResultTrack(response.getJSONObject("result"));
                    }
                } catch (Throwable ignored) { }
                resultTrack(track);
            }
        });
    }

    public void getInfo(final ResultTrack resultTrack) {
        thread.postRunnable(new Runnable() {
            @Override
            public void run() {
                final List<MusicObject> items = new ArrayList<>();
                Params params = new Params();
                try {
                    params.addValue("term", resultTrack.getMusicTitle());
                    String data = Network.getRequest("https://itunes.apple.com/search", String.valueOf(params), 0);
                    JSONArray array = new JSONObject(data).getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        items.add(new MusicObject(array.getJSONObject(i)));
                    }
                } catch (Throwable e) {}
                if (items.size() == 0) {
                    try {
                        params = new Params();
                        params.addValue("group_ids", group_id);
                        params.addValue("v", "5.46");
                        params.addValue("fields", "photo_50,photo_100");
                        JSONObject response = new JSONObject(Network.postRequest("https://api.vk.com/method/groups.getById", String.valueOf(params), 0)).getJSONArray("response").getJSONObject(0);
                        items.add(new MusicObject(resultTrack.artist, resultTrack.title, response.getString("photo_100")));
                    } catch (Throwable ignored) {}
                }
                Application.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.itunesLoaded, items);
                    }
                });
            }
        });
    }

    public void search(final String q) {
        if (q.length() <= 3) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.searchLoaded, new ArrayList<>());
            return;
        }
        if (searchRunnable != null) {
            thread.cancelRunnable(searchRunnable);
        }
        thread.postRunnable(searchRunnable = new Runnable() {
            @Override
            public void run() {
                final List<SongObject> items = new ArrayList<>();
                try {
                    Params params = new Params();
                    params.addValue("q", q);
                    JSONArray array = api("findLyrics", params).getJSONArray("result");
                    Log.e(TAG, "findLyrics = " + String.valueOf(array));
                    if (array.length() != 0) {
                        for (int i = 0; i < array.length(); i++) {
                            items.add(new SongObject(array.getJSONObject(i)));
                        }
                    }
                } catch (Throwable ignored) {}
                Application.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.searchLoaded, items);
                    }
                });
            }
        });
    }

    private void resultTrack(final ResultTrack resultTrack) {
        Application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.resultAPI, resultTrack);
            }
        });
    }

    public void getHistory() {
        thread.postRunnable(new Runnable() {
            @Override
            public void run() {
                final List<MusicObject> items = new ArrayList<>();
                try {
                    JSONArray array = Database.getInstance().getList(Database.LIST_STORY);
                    for (int i = 0; i < array.length(); i++) {
                        items.add(new MusicObject(array.getJSONObject(i)));
                    }
                } catch (Throwable ignored) { }
                Application.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.storyLoaded, items);
                    }
                });
            }
        });
    }

    public JSONObject api(String method, Params params) throws JSONException {
        if (params == null) {
            params = new Params();
        }
        params.addValue("method", method);
        // params.addValue("usage", 1);
        params.addValue("api_token", API_TOKEN);
        return new JSONObject(Network.postRequest(URL, String.valueOf(params), 0));
    }

    public JSONObject api(String method) throws JSONException {
        return api(method, new Params());
    }

    public JSONObject upload(File file, String method, Params params) throws JSONException {
        if (params == null) {
            params = new Params();
        }
        return new JSONObject(Network.uploadFile(URL + "?method=" + method + "&api_token=" + API_TOKEN + "&" + String.valueOf(params), file, "file", 0));
    }

    public JSONObject upload(File file, String method) throws JSONException {
        return upload(file, method, new Params());
    }

    public static API getInstance() {
        API localInstance = Instance;
        if (localInstance == null) {
            synchronized (API.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new API();
                }
            }
        }
        return localInstance;
    }

}
