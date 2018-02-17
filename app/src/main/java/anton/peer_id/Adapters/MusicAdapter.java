package anton.peer_id.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import anton.peer_id.audd.R;
import anton.peer_id.network.MenuObject;
import anton.peer_id.widget.MenuCell;
import anton.peer_id.widget.RecyclerListView;

public class MusicAdapter extends RecyclerListView.Adapter {

    private List items = new ArrayList<>();

    public MusicAdapter() {
        items.add(new MenuObject(MenuObject.VK, R.drawable.ic_vk, "VK"));
        items.add(new MenuObject(MenuObject.GOOGLE_MUSIC, R.drawable.ic_google_play, "Google Music"));
        items.add(new MenuObject(MenuObject.ITUNES, R.drawable.ic_apple, "Apple Music"));
        items.add(new MenuObject(MenuObject.YANDEX_MUSIC, R.drawable.ic_yandex_music, "Яндекс.Музыка"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1:
                view = new MenuCell(parent.getContext());
                break;
            default:
                view = new View(parent.getContext());
                break;
        }
        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof MenuObject) {
            return 1;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
