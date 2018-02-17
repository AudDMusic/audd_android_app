package anton.peer_id.network;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultTrack {

    public final String artist, title, album;

    public ResultTrack(JSONObject item) throws JSONException {
        artist = item.getString("artist");
        title = item.getString("title");
        album = item.optString("album", "unknown");
    }

    public ResultTrack() {
        artist = "Face";
        title = "Title";
        album = "Album";
    }

    public ResultTrack(String title, String artist, String album) {
        this.title = title;
        this.artist = artist;
        this.album = album;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject item = new JSONObject();
        item.put("artist", artist);
        item.put("title", title);
        item.put("album", album);
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

    public String getMusicTitle() {
        return String.valueOf(new StringBuilder().append(artist).append(" ").append(title));
    }
}
