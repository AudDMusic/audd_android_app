package anton.peer_id.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import anton.peer_id.audd.Screen;
import anton.peer_id.audd.Theme;

public class MenuCell extends TextView {

    private int id = -1;

    public MenuCell(Context context) {
        super(context);
        setPadding(Screen.dp(12), 0, Screen.dp(8) ,0);
        setTextColor(Theme.colorBlack);
        setTextSize(15);
        setSingleLine();
        setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        setCompoundDrawablePadding(Screen.dp(34));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Screen.dp(50), MeasureSpec.EXACTLY));
    }

    public void setData(String title, int icon, int id) {
        this.id = id;
        setText(title);
        Drawable drawable = getResources().getDrawable(icon);
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.colorGray, PorterDuff.Mode.MULTIPLY));
        }
        setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    @Override
    public int getId() {
        return id;
    }
}
