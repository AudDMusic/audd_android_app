package anton.peer_id.network;

import org.json.JSONException;
import org.json.JSONObject;

public class SongObject {

    public final int song_id, artist_id;
    public final String title, title_with_featured, full_title, artist, lyrics;

    public SongObject(JSONObject item) throws JSONException {
        song_id = Integer.valueOf(String.valueOf(item.get("song_id")));
        artist_id = Integer.valueOf(String.valueOf(item.get("artist_id")));
        title = item.optString("title", "");
        title_with_featured = item.optString("title_with_featured", "");
        full_title = item.optString("full_title", "");
        artist = item.optString("artist", "");
        lyrics = item.optString("lyrics", "");
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject item = new JSONObject();
        item.put("song_id", song_id);
        item.put("artist_id", artist_id);
        item.put("title", title);
        item.put("title_with_featured", title_with_featured);
        item.put("full_title", full_title);
        item.put("artist", artist);
        item.put("lyrics", lyrics);
        return item;
    }

    @Override
    public String toString() {
        try {
            return String.valueOf(toJSON());
        } catch (Throwable e) {
            return "{}";
        }
    }
}
