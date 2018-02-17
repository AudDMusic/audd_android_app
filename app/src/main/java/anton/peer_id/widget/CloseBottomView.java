package anton.peer_id.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import anton.peer_id.audd.Screen;

public class CloseBottomView extends FrameLayout {

    private ViewDragHelper mDragHelper;
    private boolean mIsLocked = false;
    private PileDrawable backgroundDrawable = new PileDrawable(); // new ColorDrawable(Color.parseColor("#80000000"));

    public CloseBottomView(Context context) {
        super(context);
        setWillNotDraw(false);
        setBackground(backgroundDrawable);
        mDragHelper = ViewDragHelper.create(this, 1.0f, callback);
    }

    public void setPie(boolean pie) {
        backgroundDrawable.setPie(pie);
        invalidate();
    }

    private int getScreenHeight() {
        return Screen.getHeight();
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getScreenHeight();
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int result;
            Log.e("CloseBottomView", String.valueOf(top));
            if (top < 0) {
                final int topBound = -getScreenHeight();
                final int bottomBound = getPaddingTop();
                result = Math.min(Math.max(top, topBound), bottomBound);
            } else {
                final int topBound = getPaddingTop();
                final int bottomBound = getScreenHeight();
                result = Math.min(Math.max(top, topBound), bottomBound);
            }
            return result;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int top = releasedChild.getTop();
            if (Math.abs(top) < Screen.dp(70)) {
                mDragHelper.settleCapturedViewAt(0, 0);
            } else {
                if (top > 0) {
                    // mDragHelper.settleCapturedViewAt(0, getScreenHeight());
                } else {
                    mDragHelper.settleCapturedViewAt(0, -getScreenHeight());
                }
            }
            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            try {
                float percent = 1f - ((float)Math.abs(top) / (float) getScreenHeight());
                int alphaDrawable = Math.round(255 * percent);
                backgroundDrawable.setAlpha(alphaDrawable);
            } catch (Throwable ignored) { }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            switch (state){
                case ViewDragHelper.STATE_IDLE:
                    /* if (Math.abs(photoView.getTop()) >= getScreenHeight()) {
                        onParentListener.onDismiss();
                    } */
                    break;
                case ViewDragHelper.STATE_DRAGGING:

                    break;
                case ViewDragHelper.STATE_SETTLING:

                    break;
            }
        }
    };

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean interceptForDrag;
        if (mIsLocked){
            return false;
        }
        try {
            interceptForDrag = mDragHelper.shouldInterceptTouchEvent(ev);
        } catch (Exception e) {
            interceptForDrag = false;
        }
        return interceptForDrag && !mIsLocked;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsLocked) {
            return false;
        }
        try {
            mDragHelper.processTouchEvent(ev);
        } catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }

    public void setContent(ViewGroup view) {
        addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM);
        ViewGroupCompat.setMotionEventSplittingEnabled(view, false);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
