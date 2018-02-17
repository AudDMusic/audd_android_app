package anton.peer_id.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import anton.peer_id.audd.Screen;
import anton.peer_id.network.MenuObject;
import anton.peer_id.network.MusicObject;
import anton.peer_id.widget.MusicCell;
import anton.peer_id.widget.RecyclerListView;
import anton.peer_id.widget.TitleCell;

public class StoryAdapter extends RecyclerView.Adapter {

    private final List items = new ArrayList<>();

    public StoryAdapter() {
        items.add(new Object());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1:
                view = new TitleCell(parent.getContext());
                ((TitleCell) view).setText("История");
                break;
            default:
                view = new MusicCell(parent.getContext());
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(Screen.dp(14), Screen.dp(2), 0, Screen.dp(2));
                view.setLayoutParams(params);
                break;
        }
        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            return;
        }
        MusicObject music = (MusicObject) items.get(position);
        MusicCell musicCell = (MusicCell) holder.itemView;
        musicCell.setImage(music.photo);
        musicCell.setData(music.artist, music.title);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<MusicObject> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(MusicObject musicObject) {
        this.items.add(1, musicObject);
        notifyDataSetChanged();
    }

    public MusicObject getItem(int position) {
        if (position == 0) {
            return null;
        }
        return (MusicObject) items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 1 : 0;
    }
}
