package anton.peer_id.widget;

import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import anton.peer_id.audd.Application;
import anton.peer_id.audd.R;
import anton.peer_id.audd.Screen;

public class PileDrawable extends Drawable {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint background = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean isPie = false;

    public PileDrawable() {
        BitmapShader shader = new BitmapShader(BitmapFactory.decodeResource(Application.context.getResources(), R.drawable.pile),  Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paint.setShader(shader);
        background.setColor(Color.parseColor("#80000000"));
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(0, 0, Screen.getWidth(), Screen.getHeight() + Screen.statusBarHeight, background);
        if (isPie) {
            canvas.drawPaint(paint);
        }
    }

    public void setPie(boolean pie) {
        try {
            isPie = pie;
            invalidateSelf();
        } catch (Throwable ignored) { }
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
