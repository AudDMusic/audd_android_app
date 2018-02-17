package anton.peer_id.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import anton.peer_id.audd.ColorEvaluator;
import anton.peer_id.audd.Screen;
import anton.peer_id.audd.Theme;

public class SwitchView extends View {

    public interface OnSwitchListener {
        void onSwitch(boolean isActive);
    }

    private boolean isActive = false;
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int radius = Screen.dp(16), trackHeight = Screen.dp(18), thumbSize = Screen.dp(30) / 2, width = Screen.dp(52), shadow = Screen.dp(2);
    private int thumbPosition = thumbSize;
    private AnimatorSet currentAnimation;
    private OnSwitchListener listener;

    public SwitchView(Context context) {
        super(context);
        thumbPaint.setShadowLayer(3.0f, 0.0f, 0.0f, Theme.setAlphaComponent(Theme.colorGray, 150));
        setViewColor(Theme.colorGray);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                toggle();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.translate(shadow, shadow);
        canvas.drawRoundRect(thumbSize, (getViewHeight() / 2) - (trackHeight / 2), getViewWidth() - thumbSize, (getViewHeight() / 2) + (trackHeight / 2), radius, radius, trackPaint);
        canvas.drawCircle(thumbPosition, thumbSize, thumbSize, thumbPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec((width + thumbSize) + (shadow * 2), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((thumbSize * 2) + (shadow * 2), MeasureSpec.EXACTLY));
    }

    public void postActive(boolean isActive) {
        if (isActive == this.isActive) {
            return;
        }
        toggle();
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
        if (isActive) {
            setViewColor(Theme.colorPrimary);
            setThumbPosition(getViewWidth() - thumbSize);
        } else {
            setViewColor(Theme.colorGray);
            setThumbPosition(thumbSize);
        }
    }

    private void toggle() {
        if (currentAnimation != null) {
            currentAnimation.cancel();
        }
        ArrayList<Animator> animators = new ArrayList<>();
        if (isActive) {
            animators.add(ObjectAnimator.ofObject(this, "viewColor", new ColorEvaluator(), Theme.colorPrimary, Theme.colorGray));
            animators.add(ObjectAnimator.ofInt(this, "thumbPosition", (getViewWidth() - thumbSize), thumbSize));
        } else {
            animators.add(ObjectAnimator.ofObject(this, "viewColor", new ColorEvaluator(), Theme.colorGray, Theme.colorPrimary));
            animators.add(ObjectAnimator.ofInt(this, "thumbPosition", thumbSize, (getViewWidth() - thumbSize)));
        }
        currentAnimation = new AnimatorSet();
        currentAnimation.playTogether(animators);
        currentAnimation.setDuration(120);
        currentAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isActive = !isActive;
                currentAnimation = null;
                if (listener != null) {
                    listener.onSwitch(isActive);
                }
            }
        });
        currentAnimation.start();
    }

    public float getThumbPosition() {
        return thumbPosition;
    }

    public void setThumbPosition(int thumbPosition) {
        this.thumbPosition = thumbPosition;
        invalidate();
    }

    public void setViewColor(int color) {
        trackPaint.setColor(Theme.setAlphaComponent(color, 155));
        thumbPaint.setColor(color);
    }

    public int getViewWidth() {
        return (width + thumbSize);
    }

    public int getViewHeight() {
        return (thumbSize * 2);
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
