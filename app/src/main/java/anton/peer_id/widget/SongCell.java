package anton.peer_id.widget;

import android.content.Context;
import android.view.Gravity;

import anton.peer_id.audd.R;
import anton.peer_id.audd.Screen;
import anton.peer_id.audd.Theme;
import anton.peer_id.network.SongObject;

public class SongCell extends TextView {
    public SongCell(Context context) {
        super(context);
        setSingleLine();
        setGravity(Gravity.CENTER | Gravity.LEFT);
        setBackgroundResource(R.drawable.bottom_line);
        setTextSize(17);
        setTextColor(Theme.colorBlack);
        setPadding(Screen.dp(16), Screen.dp(8), Screen.dp(16), Screen.dp(8));
    }

    public void bind(SongObject song) {
        setText(song.title);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Screen.dp(50), MeasureSpec.EXACTLY));
    }

}
