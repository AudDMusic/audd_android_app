package anton.peer_id.network;

import org.json.JSONException;
import org.json.JSONObject;

public class MusicObject {

    public final String artist, title, collection, photo;
    public String itunes;
    public final boolean isDefault;

    public MusicObject(JSONObject item) throws JSONException {
        artist = item.getString("artistName");
        title = item.getString("trackName");
        collection = item.getString("collectionName");
        itunes = item.getString("artistViewUrl");
        photo = item.getString("artworkUrl100");
        isDefault = true;
    }

    public MusicObject(String artist, String title, String photo_100) {
        this.artist = artist;
        this.title = title;
        this.collection = "";
        this.itunes = "";
        this.photo = photo_100;
        isDefault = false;
    }

    public void setItunes(String itunes) {
        this.itunes = itunes;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject item = new JSONObject();
        item.put("artistName", artist);
        item.put("trackName", title);
        item.put("collectionName", collection);
        item.put("artistViewUrl", itunes);
        item.put("artworkUrl100", photo);
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
