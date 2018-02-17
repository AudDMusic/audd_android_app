package anton.peer_id.widget;

import android.content.Context;
import android.view.View;

public abstract class Page {

    public interface OnPageListener {
        void setPagerPosition(int position);
        void onAddFragment(Fragment fragment);
    }

    final OnPageListener onPageListener;
    private Context context;
    private View view;

    public Page(OnPageListener listener) {
        onPageListener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context context() {
        return context;
    }

    public abstract View onCreateView();
    public abstract void onStartPage();
    public abstract void onFinishPage();
    public abstract void onParentFragmentVisibleStatus(boolean isVisible);

    public void addFragment(Fragment fragment) {
        onPageListener.onAddFragment(fragment);
    }

    public void toPage(int position) {
        onPageListener.setPagerPosition(position);
    }

    View getView() {
        if (view == null) {
            view = onCreateView();
            onStartPage();
        }
        return view;
    }

    public void clear() {
        view = null;
    }
}
