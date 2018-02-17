package anton.peer_id.widget;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import anton.peer_id.audd.Screen;

public class HeaderCell extends LinearLayout {

    private final SimpleDraweeView image;
    private final TextView title;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Screen.dp(108), MeasureSpec.EXACTLY));
    }

    public HeaderCell(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        image = new SimpleDraweeView(context);
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        LayoutParams params = new LayoutParams(Screen.dp(78), Screen.dp(78));
        params.setMargins(0, Screen.dp(8), 0, 0);
        addView(image, params);
        title = new TextView(context);
        title.setSingleLine();
        title.setMaxLines(1);
        title.setLines(1);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(16));
        params.setMargins(0, Screen.dp(8), 0, 0);
        addView(title, params);
    }

    public void setMusic(String url, String title, String artist) {
        image.setController(Fresco.newDraweeControllerBuilder().setUri(url).build());
        this.title.setText(title);
    }
}
