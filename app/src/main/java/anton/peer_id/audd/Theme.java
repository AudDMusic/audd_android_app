package anton.peer_id.audd;

import android.graphics.Color;

public class Theme {

    public static final int
            colorPrimary = Color.parseColor("#64B5F6"),
            colorPrimaryDark = Color.parseColor("#2196F3"),
            colorRed = Color.parseColor("#E53935"),
            colorBlack = Color.parseColor("#212121"),
            colorGray = Color.parseColor("#616161");

    public static int setAlphaComponent(int color, int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

}
