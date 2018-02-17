package anton.peer_id.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

import anton.peer_id.audd.Application;
import anton.peer_id.audd.R;
import anton.peer_id.audd.Screen;
import anton.peer_id.audd.Theme;

public class SearchView extends EditText {
    public SearchView(Context context) {
        super(context);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        setSingleLine(true);
        setBackgroundResource(0);
        setPadding(0, 0, 0, 0);
        int inputType = getInputType() | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        setInputType(inputType);
        setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN | EditorInfo.IME_ACTION_SEARCH);
        setTextIsSelectable(false);
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {

            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        setHint("Поиск");
        setTextColor(Theme.colorPrimary);
        setHintTextColor(Theme.colorPrimary);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(this, R.drawable.search_carret);
        } catch (Throwable e) { }
    }

    public void show() {
        setVisibility(VISIBLE);
        requestFocus();
        Application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                requestFocus();
                Screen.showKeyboard(SearchView.this);
                requestFocus();
            }
        });
    }

    public void hide() {
        Screen.hideKeyboard(this);
        setVisibility(GONE);
    }
}

