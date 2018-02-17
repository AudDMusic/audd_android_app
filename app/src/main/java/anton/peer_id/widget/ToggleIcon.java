package anton.peer_id.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;

import anton.peer_id.audd.Config;
import anton.peer_id.audd.R;
import anton.peer_id.audd.Screen;
import anton.peer_id.audd.Theme;

public class ToggleIcon extends View {

    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout staticLayout;
    private int size = Screen.dp(32) * 2, iconSize = size / 2, iconCenter = (size / 2) - (iconSize / 2);
    private boolean singing = Config.getInstance().isSinging();
    private Drawable icon;

    public ToggleIcon(Context context) {
        super(context);
        textPaint.setColor(Theme.colorPrimaryDark);
        textPaint.setTextSize(Screen.dp(12));
        updateIcon();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
    }

    public void setIcon(int resIcon) {
        icon = Screen.getDrawable(resIcon);
        icon.clearColorFilter();
        icon.setColorFilter(Theme.colorPrimaryDark, PorterDuff.Mode.MULTIPLY);
        icon.setBounds(iconCenter, 0, iconSize + iconCenter, iconSize);
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        icon.draw(canvas);
        int count = canvas.save();
        canvas.translate(0, iconSize + (iconCenter / 2));
        staticLayout.draw(canvas);
        canvas.restoreToCount(count);
    }

    public void toggle() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "scaleX", 0f).setDuration(100);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                singing = !singing;
                Config.getInstance().setSinging(singing);
                updateIcon();
                ObjectAnimator.ofFloat(ToggleIcon.this, "scaleX", 1f).setDuration(100).start();
            }
        });
        animator.start();
    }

    private void updateIcon() {
        setIcon(singing ? R.drawable.ic_voice : R.drawable.ic_microphone);
        setText(singing ? "Песня" : "Напевание");
    }

    private void setText(String text) {
        staticLayout = new StaticLayout(text, 0, text.length(), textPaint, size, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
    }

    public int getSize() {
        return size;
    }

    public boolean isSinging() {
        return singing;
    }
}
