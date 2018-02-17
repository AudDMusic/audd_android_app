package anton.peer_id.network;


import android.text.TextUtils;
import android.util.ArrayMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Map;

public class Params {

    private final ArrayMap<String, String> params = new ArrayMap<>();

    public Params addValue(String name, Object value) {
        params.put(name, String.valueOf(value));

        return this;
    }

    public boolean containsKey(String key) {
        return params.containsKey(key);
    }

    public boolean containsValue(String value) {
        return params.containsValue(value);
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject result = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.put(entry.getKey(), URLEncoder.encode(entry.getValue(), "utf-8"));
            }
        } catch (Throwable ignored) {}
        return result;
    }

    public static Params newInstance() {
        return new Params();
    }

    @Override
    public String toString() {
        String result = "";
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                result += "&" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "utf-8");
            }
        } catch (Throwable ignored) {}
        return TextUtils.isEmpty(result) ? "from_android=1" : String.valueOf(result.subSequence(1, result.length()));
    }
}