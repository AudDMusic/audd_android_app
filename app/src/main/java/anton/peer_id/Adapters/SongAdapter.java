package anton.peer_id.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import anton.peer_id.network.SongObject;
import anton.peer_id.widget.RecyclerListView;
import anton.peer_id.widget.SongCell;

public class SongAdapter extends RecyclerView.Adapter {

    private final List<SongObject> items = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerListView.Holder(new SongCell(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SongCell) holder.itemView).bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void setItems(List<SongObject> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public SongObject getItem(int position) {
        return items.get(position);
    }
}
