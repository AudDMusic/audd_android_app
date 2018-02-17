package anton.peer_id.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import anton.peer_id.audd.Screen;
import anton.peer_id.audd.Theme;

public class AvatarView extends SimpleDraweeView {

    private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG), paintBg = new Paint(Paint.ANTI_ALIAS_FLAG);;
    private static TextPaint namePaint;

    public static void clear() {
        namePaint = null;
    }

    public AvatarView(Context context) {
        super(context);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(true);
        getHierarchy().setRoundingParams(roundingParams);
    }

    public void set(Uri uri, String name) {
        setImageURI("");
        setBackgroundDrawable(new AvatarDrawable(name));
        if (uri != null) {
            setImageURI(uri);
        }
    }

    public void setIcon(Drawable drawable) {
        setImageURI("");
        setScaleType(ScaleType.CENTER_INSIDE);
        setImageDrawable(drawable);
        setBackgroundDrawable(new AvatarDrawable("null"));
    }

    public void setIcon(int resId) {
        setIcon(Screen.getDrawable(resId));
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    private static class AvatarDrawable extends Drawable {

        private StaticLayout textLayout;
        private StringBuilder stringBuilder = new StringBuilder(5);
        private float textWidth;
        private float textHeight;
        private float textLeft;

        private void init() {
            if (namePaint == null) {
                namePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                namePaint.setColor(Color.WHITE);
                namePaint.setTextSize(Screen.dp(16));
            }
        }

        public AvatarDrawable(String name) {
            super();
            init();
            setInfo(name);
        }

        public void setInfo(String name) {
            if (TextUtils.isEmpty(name)) {
                name = "VK";
            } else if (name.equals("null")) {
                name = "";
            } else {
                name = name.replaceAll("\\.","");
            }
            stringBuilder.setLength(0);
            if (name != null && name.length() > 0) {
                stringBuilder.append(name.substring(0, 1));
            }
            if (name != null && name.length() > 0) {
                for (int a = name.length() - 1; a >= 0; a--) {
                    if (name.charAt(a) == ' ') {
                        if (a != name.length() - 1 && name.charAt(a + 1) != ' ') {
                            if (Build.VERSION.SDK_INT >= 16) {
                                stringBuilder.append("\u200C");
                            }
                            stringBuilder.append(name.substring(a + 1, a + 2));
                            break;
                        }
                    }
                }
            }

            if (stringBuilder.length() > 0) {
                String text = stringBuilder.toString().toUpperCase();
                try {
                    textLayout = new StaticLayout(text, namePaint, Screen.dp(100), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (textLayout.getLineCount() > 0) {
                        textLeft = textLayout.getLineLeft(0);
                        textWidth = textLayout.getLineWidth(0);
                        textHeight = textLayout.getLineBottom(0);
                    }
                } catch (Throwable e) { }
            } else {
                // setInfo("VK");
            }
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            int size = bounds.width();
            paintBg.setColor(Color.WHITE);
            canvas.drawCircle(size / 2, size / 2, size / 2, paintBg);

            paint.setColor(Theme.colorPrimary);
            canvas.save();
            canvas.translate(bounds.left, bounds.top);
            canvas.drawCircle(size / 2, size / 2, size / 2, paint);
            if (textLayout != null) {
                canvas.translate((size - textWidth) / 2 - textLeft, (size - textHeight) / 2);
                textLayout.draw(canvas);
            }
            canvas.restore();
        }


        @Override
        public void setAlpha(int i) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }
    }

}
