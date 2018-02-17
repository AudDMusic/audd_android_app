package anton.peer_id.widget;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

public class PageAdapter extends android.support.v4.view.PagerAdapter {
    private final Page[] pages;

    public PageAdapter(Page[] pages) {
        this.pages = pages;
    }

    @Override
    public int getCount() {
        return pages.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Page page = pages[position];
        page.setContext(container.getContext());
        container.addView(page.getView(), 0);
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Page page = (Page) object;
        page.onFinishPage();
        container.removeView(page.getView());
        page.clear();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(((Page) object).getView());
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }
}