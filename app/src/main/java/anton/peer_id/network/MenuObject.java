package anton.peer_id.network;

import android.content.Context;
import android.view.View;

import anton.peer_id.widget.MenuCell;

public class MenuObject {

    public static final int VK = 1, GOOGLE_MUSIC = 2, YANDEX_MUSIC = 3, SOUNDCLOUD = 4, ITUNES = 5;

    private final int id, icon;
    private final String title;

    public MenuObject(int id, int icon, String title) {
        this.id = id;
        this.icon = icon;
        this.title = title;
    }

    @Override
    public String toString() {
        return id + "=" + title;
    }

    public View createView(Context context, View.OnClickListener clickListener) {
        MenuCell menuCell = new MenuCell(context);
        menuCell.setData(title, icon, id);
        menuCell.setOnClickListener(clickListener);
        return menuCell;
    }
}
