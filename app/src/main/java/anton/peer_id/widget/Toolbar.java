package anton.peer_id.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import anton.peer_id.audd.R;
import anton.peer_id.audd.Screen;
import anton.peer_id.audd.Theme;

public class Toolbar extends FrameLayout {

    public interface OnToolbarListener {
        void onBackClick();
    }

    public int height = Screen.dp(56) + Screen.statusBarHeight;
    private OnToolbarListener onToolbarListener;
    private TextView title;

    public Toolbar(Context context) {
        super(context);
        LinearLayout body = new LinearLayout(context);
        body.setOrientation(LinearLayout.HORIZONTAL);
        body.setPadding(0, Screen.statusBarHeight, 0, 0);
        body.setBackgroundColor(Theme.colorPrimaryDark);
        ImageView backIcon = new ImageView(context);
        backIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onToolbarListener != null) {
                    onToolbarListener.onBackClick();
                }
            }
        });
        backIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        backIcon.setImageResource(R.drawable.ic_back);
        body.addView(backIcon, new FrameLayout.LayoutParams(Screen.dp(56), Screen.dp(56)));
        title = new TextView(context);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(Color.WHITE);
        title.setSingleLine();
        title.setMaxLines(1);
        title.setLines(1);
        title.setPadding(Screen.dp(6), 0, Screen.dp(16), 0);
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        title.setTypeface(Screen.getTypeface("fonts/roboto_regular.ttf"));
        body.addView(title, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        addView(body, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height, Gravity.TOP));
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.shadow);
        addView(shadow, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(3), Gravity.BOTTOM));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height + Screen.dp(3), MeasureSpec.EXACTLY));
    }

    public void setOnToolbarListener(OnToolbarListener onToolbarListener) {
        this.onToolbarListener = onToolbarListener;
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }
}
