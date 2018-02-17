package anton.peer_id.audd;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.ShareEvent;

import java.util.List;

import anton.peer_id.network.API;
import anton.peer_id.network.MenuObject;
import anton.peer_id.network.MusicObject;
import anton.peer_id.network.ResultTrack;
import anton.peer_id.widget.BottomFragment;
import anton.peer_id.widget.MenuCell;
import anton.peer_id.widget.MusicCell;

public class MusicFragment extends BottomFragment implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {

    private static MenuObject[] items = new MenuObject[] {
            new MenuObject(MenuObject.VK, R.drawable.ic_vk, "VK"),
            new MenuObject(MenuObject.GOOGLE_MUSIC, R.drawable.ic_google_play, "Google Play"),
            new MenuObject(MenuObject.YANDEX_MUSIC, R.drawable.ic_yandex_music, "Yandex.Music"),
            new MenuObject(MenuObject.ITUNES, R.drawable.ic_apple, "Apple Music"),
            new MenuObject(MenuObject.SOUNDCLOUD, R.drawable.ic_soundcloud, "SoundCloud")
    };

    private ResultTrack resultTrack;
    private MusicCell musicCell;
    private MusicObject musicObject;

    @Override
    public View onViewPage() {
        Answers.getInstance().logContentView(new ContentViewEvent().putContentName("MusicFragment").putContentName(resultTrack.artist));
        ScrollView scrollView = new ScrollView(context());
        scrollView.setFillViewport(true);
        LinearLayout layout = new LinearLayout(context());
        layout.setPadding(Screen.dp(12), Screen.dp(12), Screen.dp(12), 0);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(musicCell = new MusicCell(context()));
        if (resultTrack != null) {
            musicCell.setData(resultTrack.artist, resultTrack.title);
        } else if (musicObject != null) {
            musicCell.setData(musicObject.artist, musicObject.title);
            musicCell.setImage(musicObject.photo);
        } else {
            musicCell.setData("Artist", "Title");
        }
        musicCell.setSendListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                intent.putExtra(SearchManager.QUERY, getTitle());
                getActivity().startActivity(intent);
            }
        });
        for (int i = 0; i < items.length; i++) {
            layout.addView(items[i].createView(context(), this));
        }
        scrollView.addView(layout);
        return scrollView;
    }

    private void clickItem() {

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
        return true;
    }

    @Override
    public boolean isBackSwipe() {
        return false;
    }

    public static MusicFragment newInstance(ResultTrack resultTrack) {
        MusicFragment musicFragment = new MusicFragment();
        musicFragment.resultTrack = resultTrack;
        return musicFragment;
    }

    public static MusicFragment newInstance(MusicObject musicObject) {
        MusicFragment musicFragment = new MusicFragment();
        musicFragment.musicObject = musicObject;
        return musicFragment;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        switch (id) {
            case NotificationCenter.itunesLoaded:
                uploadAudios((List<MusicObject>) args[0]);
                break;
        }
    }

    private void uploadAudios(List<MusicObject> items) {
        if (items.size() >= 1) {
            musicObject = items.get(0);
            musicCell.setImage(musicObject.photo);
            try {
                MusicObject musicObject = new MusicObject(resultTrack.artist, resultTrack.title, items.get(0).photo);
                musicObject.setItunes(this.musicObject.itunes);
                Database.getInstance().postItem(musicObject.toJSON(), Database.LIST_STORY);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.storyUpdate, musicObject);
            } catch (Throwable ignored) { }
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof MenuCell) {
            ShareEvent shareEvent = new ShareEvent();
            shareEvent.putContentName(getTitle());
            Intent intent = new Intent();
            switch (view.getId()) {
                case MenuObject.VK:
                    shareEvent.putContentType("VK");
                    intent.setData(Uri.parse("https://m.vk.com/audio?act=search&q=" + getTitle()));
                    break;
                case MenuObject.GOOGLE_MUSIC:
                    shareEvent.putContentType("Google Music");
                    break;
                case MenuObject.YANDEX_MUSIC:
                    shareEvent.putContentType("Yandex.Music");
                    intent.setData(Uri.parse("https://music.yandex.ru/search?text=" + getTitle()));
                    break;
                case MenuObject.SOUNDCLOUD:
                    shareEvent.putContentType("SoundCloud");
                    intent.setData(Uri.parse("https://soundcloud.com/search?q=" + getTitle()));
                    break;
                case MenuObject.ITUNES:
                    shareEvent.putContentType("Apple Music");
                    if (musicObject == null || TextUtils.isEmpty(musicObject.itunes)) {
                        intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                        intent.putExtra(SearchManager.QUERY, getTitle());
                        intent.setPackage("com.apple.android.music");
                    } else {
                        intent.setData(Uri.parse(musicObject.itunes));
                    }
                    break;
                default:
                    shareEvent.putContentType("Other");
                    intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                    intent.putExtra(SearchManager.QUERY, getTitle());
                    intent.setPackage("com.google.android.music");
                    break;
            }
            try {
                getActivity().startActivity(intent);
            } catch (Throwable e) {
                intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                intent.putExtra(SearchManager.QUERY, getTitle());
                getActivity().startActivity(intent);
            }
            Answers.getInstance().logShare(shareEvent);
        }
    }

    private String getTitle() {
        return resultTrack == null ? musicObject.title : resultTrack.title;
    }
}
