package anton.peer_id.audd;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import java.util.ArrayList;
import java.util.List;

import anton.peer_id.Adapters.SongAdapter;
import anton.peer_id.network.API;
import anton.peer_id.network.MusicObject;
import anton.peer_id.network.ResultTrack;
import anton.peer_id.network.SongObject;
import anton.peer_id.widget.Page;
import anton.peer_id.widget.RecordView;
import anton.peer_id.widget.RecyclerListView;
import anton.peer_id.widget.SearchBar;
import anton.peer_id.widget.SwitchView;
import anton.peer_id.widget.ToggleIcon;

public class RecordPage extends Page implements NotificationCenter.NotificationCenterDelegate {

    private RecordView recordView;
    private RecyclerListView list;
    private SongAdapter adapter;

    RecordPage(OnPageListener listener) {
        super(listener);
    }

    @Override
    public View onCreateView() {
        Answers.getInstance().logContentView(new ContentViewEvent().putContentName("RecordPage"));
        FrameLayout layout = new FrameLayout(context());
        layout.setBackgroundColor(Color.WHITE);
        final SearchBar searchBar = new SearchBar(context());
        searchBar.setOnSearchBarListener(new SearchBar.OnSearchBarListener() {
            @Override
            public void onSearchText(String q) {
                list.setVisibility(View.GONE);
                adapter.clear();
                API.getInstance().search(q);
            }
        });
        layout.addView(searchBar, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        list = new RecyclerListView(context());
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
        list.setVisibility(View.GONE);
        list.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SongObject songObject = adapter.getItem(position);
                addFragment(MusicFragment.newInstance(new ResultTrack(songObject.title, songObject.artist, "")));
            }
        });
        list.setAdapter(adapter = new SongAdapter());
        recordView = new RecordView(context());
        layout.addView(recordView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        final ToggleIcon toggleIcon = new ToggleIcon(context());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(toggleIcon.getSize(), toggleIcon.getSize(), Gravity.CENTER | Gravity.BOTTOM);
        params.setMargins(0, 0, 0, Screen.dp(24));
        SwitchView switchView = new SwitchView(context());
        switchView.setActive(Config.getInstance().isSinging());
        switchView.setOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onSwitch(boolean isActive) {
                Config.getInstance().setSinging(isActive);
            }
        });
        layout.addView(switchView, params);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, searchBar.getSize(), 0, 0);
        layout.addView(list, params);
        return layout;
    }

    @Override
    public void onStartPage() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.resultAPI);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.searchLoaded);
    }

    @Override
    public void onFinishPage() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.resultAPI);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.searchLoaded);
    }

    @Override
    public void onParentFragmentVisibleStatus(boolean isVisible) {

    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        switch (id) {
            case NotificationCenter.resultAPI:
                recordView.setDefault();
                if (args[0] == null) {
                    addFragment(ErrorFragment.newInstance());
                } else {
                    addFragment(MusicFragment.newInstance((ResultTrack) args[0]));
                }
                break;
            case NotificationCenter.searchLoaded:
                List<SongObject> songs = (ArrayList) args[0];
                if (songs.size() == 0) {
                    list.setVisibility(View.GONE);
                    adapter.clear();
                } else {
                    list.setVisibility(View.VISIBLE);
                    adapter.setItems(songs);
                }
                break;
        }
    }

    public static RecordPage newInstance(OnPageListener listener) {
        return new RecordPage(listener);
    }
}
