package anton.peer_id.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;

import anton.peer_id.audd.Screen;

public class TextView extends android.widget.TextView {
    public TextView(Context context) {
        super(context);
        setTypeface(Screen.getTypeface("fonts/roboto_regular.ttf"));
    }

    @Override
    public void setSingleLine() {
        super.setEllipsize(TextUtils.TruncateAt.END);
        super.setSingleLine(true);
        super.setMaxLines(1);
        super.setLines(1);
    }

    public void setTextSize(int size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }
}
