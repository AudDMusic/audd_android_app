package anton.peer_id.audd;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import java.util.ArrayList;
import java.util.List;

import anton.peer_id.Adapters.StoryAdapter;
import anton.peer_id.network.API;
import anton.peer_id.network.MusicObject;
import anton.peer_id.widget.Page;
import anton.peer_id.widget.RecyclerListView;
import anton.peer_id.widget.TextView;

public class RecognizeStoryPage extends Page implements NotificationCenter.NotificationCenterDelegate {

    public RecognizeStoryPage(OnPageListener listener) {
        super(listener);
    }

    private RecyclerListView list;
    private StoryAdapter adapter;
    private TextView placeholder;

    @Override
    public View onCreateView() {
        Answers.getInstance().logContentView(new ContentViewEvent().putContentName("RecognizeStoryPage"));
        FrameLayout layout = new FrameLayout(context());

        layout.setBackgroundColor(Color.WHITE);
        placeholder = new TextView(context());
        placeholder.setGravity(Gravity.CENTER);
        placeholder.setTextColor(Theme.colorBlack);
        placeholder.setTextSize(14);
        placeholder.setVisibility(View.VISIBLE);
        placeholder.setText("Загрузка...");
        layout.addView(placeholder, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
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
        list.setVisibility(View.GONE);
        list.setLayoutAnimation(null);
        list.setClipToPadding(false);
        list.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    return;
                }
                addFragment(MusicFragment.newInstance(adapter.getItem(position)));
            }
        });
        list.setAdapter(adapter = new StoryAdapter());
        layout.addView(list, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return layout;
    }

    @Override
    public void onStartPage() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.storyLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.storyUpdate);
        API.getInstance().getHistory();
    }

    @Override
    public void onFinishPage() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.storyLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.storyUpdate);
    }

    @Override
    public void onParentFragmentVisibleStatus(boolean isVisible) {

    }

    public static RecognizeStoryPage newInstance(OnPageListener listener) {
        return new RecognizeStoryPage(listener);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        switch (id) {
            case NotificationCenter.storyLoaded:
                List<MusicObject> items = (ArrayList) args[0];
                if (items.size() == 0) {
                    placeholder.setText("Нет данных");
                    placeholder.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                } else {
                    placeholder.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                    adapter.setItems(items);
                }
                break;
            case NotificationCenter.storyUpdate:
                adapter.addItem((MusicObject) args[0]);
                break;
        }
    }
}
