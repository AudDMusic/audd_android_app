package anton.peer_id.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import anton.peer_id.audd.Screen;

public class SwipeBackView extends FrameLayout {

    public interface OnSwipeListener {
        void onCloseScreen();
        void onDragging();
    }

    private ViewDragHelper mDragHelper;
    private boolean mIsLocked = false;
    private View bgView;
    private View view;
    private OnSwipeListener mListener;

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return !Screen.isBlockSwipe;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return Screen.clamp(top, 0, Screen.getHeight() + Screen.statusBarHeight);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return Screen.getHeight() + Screen.statusBarHeight;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int top = releasedChild.getTop();
            int settleTop = 0;
            int topThreshold = (int) (getHeight() * 0.25f);
            boolean isVerticalSwiping = Math.abs(yvel) > 5f;
            if (yvel > 0) {
                if (Math.abs(yvel) > 5f && !isVerticalSwiping) {
                    settleTop = Screen.getHeight() + Screen.statusBarHeight;
                } else if (top > topThreshold) {
                    settleTop = Screen.getHeight() + Screen.statusBarHeight;
                }
            } else if (xvel < 0) {
                if (Math.abs(xvel) > 5f && !isVerticalSwiping) {
                    settleTop = -Screen.getHeight() + Screen.statusBarHeight;
                } else if (top < -topThreshold) {
                    settleTop = -Screen.getHeight() + Screen.statusBarHeight;
                }
            } else {
                if (top > topThreshold) {
                    settleTop = Screen.getHeight() + Screen.statusBarHeight;
                } else if (top < -topThreshold) {
                    settleTop = -Screen.getHeight() + Screen.statusBarHeight;
                }
            }
            if (settleTop >= 0) {
                mDragHelper.settleCapturedViewAt(settleTop, releasedChild.getTop());
                invalidate();
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            float percent = 1f - ((float) top / (float) Screen.getHeight() + Screen.statusBarHeight);
            applyScrim(percent);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            switch (state){
                case ViewDragHelper.STATE_IDLE:
                    if (view.getLeft() != 0){
                        mListener.onCloseScreen();
                    }
                    break;
                case ViewDragHelper.STATE_DRAGGING:
                    mListener.onDragging();
                    break;
                case ViewDragHelper.STATE_SETTLING:

                    break;
            }
        }

    };

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
        } catch (Throwable e){
            return false;
        }
        return true;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void applyScrim(float percent) {
        float alpha = ((percent * (0.8f - 0f)) + 0f) / 2.4f;
        bgView.setAlpha(alpha);
    }

    public void setContent(View view, OnSwipeListener onSwipeListener) {
        removeAllViews();
        this.view = view;
        mListener = onSwipeListener;
        bgView = new View(getContext());
        bgView.setBackgroundColor(Color.BLACK);
        bgView.setAlpha(0.8f);
        addView(bgView);
        FrameLayout content = new FrameLayout(getContext());
        content.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        content.addView(view);
        addView(content);
        final float density = getResources().getDisplayMetrics().density;
        final float minVel = 400 * density;
        mDragHelper = ViewDragHelper.create(content, 1f, callback);
        mDragHelper.setMinVelocity(minVel);
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM | ViewDragHelper.EDGE_TOP);
        ViewGroupCompat.setMotionEventSplittingEnabled(content, false);
    }

    public SwipeBackView(Context context) {
        super(context);
    }

}
