package anton.peer_id.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import anton.peer_id.audd.R;


public class ViewPager extends android.support.v4.view.ViewPager {

    public ViewPager(Context context) {
        super(context);
        setId(R.id.pager);
        setPageMargin(0);
        setOffscreenPageLimit(1);
        setBackgroundColor(Color.TRANSPARENT);
    }

    private boolean mIsDisallowIntercept = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (RecordView.status == RecordView.RECORDING) {
            return false;
        }
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Throwable e) {
            return false;
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

}
