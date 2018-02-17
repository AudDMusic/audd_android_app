package anton.peer_id.audd;

import android.content.Context;
import android.content.SharedPreferences;


public class Config {

    private static volatile Config Instance = null;
    private SharedPreferences preferences;

    Config() {
        preferences = Application.context.getSharedPreferences("ConfigUser", Context.MODE_PRIVATE);
    }

    public boolean isSinging() {
        return preferences.getBoolean("singing", true);
    }

    public void setSinging(boolean singing) {
        preferences.edit().putBoolean("singing", singing).apply();
    }

    public static Config getInstance() {
        Config localInstance = Instance;
        if (localInstance == null) {
            synchronized (Config.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new Config();
                }
            }
        }
        return localInstance;
    }
}
