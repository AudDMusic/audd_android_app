package anton.peer_id.widget;


import android.content.Context;

import anton.peer_id.audd.Screen;
import anton.peer_id.audd.Theme;

public class TitleCell extends TextView {

    public TitleCell(Context context) {
        super(context);
        setSingleLine();
        setTextSize(18);
        setTextColor(Theme.colorPrimaryDark);
        setPadding(Screen.dp(16), Screen.dp(16) + Screen.statusBarHeight, Screen.dp(16), Screen.dp(16));
    }
}
