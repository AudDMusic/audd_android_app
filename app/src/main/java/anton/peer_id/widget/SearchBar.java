package anton.peer_id.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import anton.peer_id.audd.R;
import anton.peer_id.audd.Screen;
import anton.peer_id.audd.Theme;

public class SearchBar extends FrameLayout {

    public interface OnSearchBarListener {
        void onSearchText(String q);
    }

    private int size = Screen.dp(56);
    private SearchView searchView;
    private ImageView icon;
    private OnSearchBarListener listener;

    public int getSize() {
        return size + Screen.statusBarHeight;
    }

    public SearchBar(Context context) {
        super(context);
        setPadding(0, Screen.statusBarHeight, 0, 0);
        icon = new ImageView(context);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setImageResource(R.drawable.ic_search);
        icon.getDrawable().setColorFilter(Theme.colorPrimary, PorterDuff.Mode.MULTIPLY);
        addView(icon, new FrameLayout.LayoutParams(size, size, Gravity.LEFT));
        searchView = new SearchView(context);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = String.valueOf(searchView.getText());
                clearStatus(!TextUtils.isEmpty(text));
                if (listener != null) {
                    listener.onSearchText(text);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size, Gravity.CENTER);
        params.setMargins(size + Screen.dp(2), 0, size + Screen.dp(2), 0);
        addView(searchView, params);
        icon = new ImageView(context);
        icon.setImageResource(R.drawable.ic_clear);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setText("");
            }
        });
        addView(icon, new FrameLayout.LayoutParams(size, size, Gravity.RIGHT));
        clearStatus(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size + Screen.statusBarHeight, MeasureSpec.EXACTLY));
    }

    private void clearStatus(boolean isActive) {
        icon.getDrawable().clearColorFilter();
        icon.getDrawable().setColorFilter(isActive ? Theme.colorPrimary : Theme.setAlphaComponent(Theme.colorPrimary, 155), PorterDuff.Mode.MULTIPLY);
    }

    public void setOnSearchBarListener(OnSearchBarListener listener) {
        this.listener = listener;
    }
}
