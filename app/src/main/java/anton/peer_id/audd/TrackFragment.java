package anton.peer_id.audd;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import anton.peer_id.network.API;
import anton.peer_id.network.MusicObject;
import anton.peer_id.network.ResultTrack;
import anton.peer_id.widget.Fragment;
import anton.peer_id.widget.HeaderCell;
import anton.peer_id.widget.RecyclerListView;

public class TrackFragment extends Fragment implements NotificationCenter.NotificationCenterDelegate {

    private ResultTrack resultTrack;
    private Adapter adapter;

    @Override
    public View onViewPage() {
        FrameLayout layout = new FrameLayout(context());
        /* Toolbar toolbar = new Toolbar(context());
        toolbar.setTitle(resultTrack.artist);
        toolbar.setOnToolbarListener(new Toolbar.OnToolbarListener() {
            @Override
            public void onBackClick() {
                finish();
            }
        }); */
        RecyclerListView list = new RecyclerListView(context());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context()) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        list.setVerticalScrollBarEnabled(false);
        list.setItemAnimator(null);
        list.setLayoutAnimation(null);
        list.setClipToPadding(false);
        list.setAdapter(adapter = new Adapter());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // params.setMargins(0, toolbar.height, 0, 0);
        layout.addView(list, params);
        // layout.addView(toolbar, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP));
        return layout;
    }

    @Override
    public void onConnectApp() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.itunesLoaded);
        API.getInstance().getInfo(resultTrack);
    }

    @Override
    public void onDisconnectApp() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.itunesLoaded);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public static TrackFragment newInstance(ResultTrack resultTrack) {
        TrackFragment trackFragment = new TrackFragment();
        trackFragment.resultTrack = resultTrack;
        return trackFragment;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        switch (id) {
            case NotificationCenter.itunesLoaded:
                adapter.addItems((ArrayList) args[0]);
                break;
        }
    }

    private class Adapter extends RecyclerView.Adapter {

        private List items = new ArrayList<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(context());
                    break;
                default:
                    view = new View(context());
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    MusicObject musicObject = (MusicObject) items.get(position);
                    headerCell.setMusic(musicObject.photo, musicObject.title, musicObject.artist);
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            return 1;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        void addItems(ArrayList items) {
            this.items.addAll(items);
            notifyDataSetChanged();
        }
    }
}
