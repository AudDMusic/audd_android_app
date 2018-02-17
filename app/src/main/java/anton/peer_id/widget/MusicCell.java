package anton.peer_id.widget;

import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import anton.peer_id.audd.R;
import anton.peer_id.audd.Screen;
import anton.peer_id.audd.Theme;

public class MusicCell extends FrameLayout {

    private AvatarView avatarView;
    private TextView artist, title;
    private final int photoSize = Screen.dp(56), padding = Screen.dp(12);
    private OnClickListener sendListener;

    public MusicCell(Context context) {
        super(context);
        avatarView = new AvatarView(context);
        avatarView.set(null, getResources().getString(R.string.app_name));
        addView(avatarView, new LayoutParams(photoSize, photoSize, Gravity.CENTER_VERTICAL));
        artist = new TextView(context);
        artist.setTextColor(Theme.colorBlack);
        artist.setSingleLine();
        artist.setText(getResources().getString(R.string.app_name));
        artist.setTextSize(17);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP);
        params.setMargins(photoSize + padding, padding, 0, 0);
        addView(artist, params);
        title = new TextView(context);
        title.setTextColor(Theme.colorGray);
        title.setSingleLine();
        title.setTextSize(14);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        params.setMargins(photoSize + padding, 0, 0, padding);
        addView(title, params);
        ImageView sendIcon = new ImageView(context);
        sendIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        sendIcon.setImageDrawable(Screen.getDrawable(R.drawable.ic_share));
        sendIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sendListener != null) {
                    sendListener.onClick(view);
                }
            }
        });
        addView(sendIcon, new FrameLayout.LayoutParams(photoSize, photoSize, Gravity.RIGHT));
    }

    public void setSendListener(OnClickListener listener) {
        this.sendListener = listener;
    }

    public void setImage(String image) {
        avatarView.set(Uri.parse(image), String.valueOf(artist.getText()));
    }

    public void setData(String artist, String title) {
        this.artist.setText(artist);
        this.title.setText(title);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Screen.dp(64), MeasureSpec.EXACTLY));
    }
}
