package anton.peer_id.audd;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Hashtable;

public class Screen {

    public static float density = 1;
    public static int keyboardHeight, statusBarHeight;
    public static Point displaySize = new Point();
    private static final Hashtable<String, Typeface> fontCache = new Hashtable<>();
    public static boolean isBlockSwipe = false;

    static {
        density = Application.context.getResources().getDisplayMetrics().density;
        keyboardHeight = Screen.dp(220);
        statusBarHeight = 0;
    }

    public static void toast(String text) {
        Toast.makeText(Application.context, text, Toast.LENGTH_SHORT).show();
    }

    public static int getHeight() {
        return Screen.displaySize.y;
    }

    public static int getWidth() {
        return Screen.displaySize.x;
    }

    public static int dp(float value) {
        return (int) Math.ceil(density * value);
    }

    public static float dpFloat(float value) {
        return density * value;
    }

    public static int px(int size) {
        return (int) Math.ceil(size / density);
    }

    public static void setDisplaySize(Context context, Configuration newConfiguration) {
        try {
            density = context.getResources().getDisplayMetrics().density;
            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getSize(displaySize);
                }
            }
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenWidthDp * density);
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenHeightDp * density);
                if (Math.abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize;
                }
            }
        } catch (Throwable ignored) {}
    }

    public static int getViewInset(View view) {
        if (view == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        try {
            Field mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
            mAttachInfoField.setAccessible(true);
            Object mAttachInfo = mAttachInfoField.get(view);
            if (mAttachInfo != null) {
                Field mStableInsetsField = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                mStableInsetsField.setAccessible(true);
                Rect insets = (Rect)mStableInsetsField.get(mAttachInfo);
                return insets.bottom;
            }
        } catch (Throwable ignored) {}
        return 0;
    }

    public static void showKeyboard(View view) {
        try {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } catch (Throwable ignored) {}
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!imm.isActive()) {
                return;
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Throwable ignored) {}
    }

    public static boolean isKeyboardShowed(View view) {
        if (view == null) {
            return false;
        }
        try {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            return inputManager.isActive(view);
        } catch (Throwable ignored) {}
        return false;
    }

    public static int clamp(int value, int min, int max){
        return Math.max(min, Math.min(max, value));
    }

    public static Typeface getTypeface(String assetPath) {
        synchronized (fontCache) {
            if (!fontCache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(Application.context.getAssets(), assetPath);
                    fontCache.put(assetPath, t);
                } catch (Throwable e) {
                    Log.e("ErrorGetTypeface", String.valueOf(e));
                    return null;
                }
            }
            return fontCache.get(assetPath);
        }
    }

    public static Drawable getDrawable(int resId) {
        Drawable drawable = Application.context.getResources().getDrawable(resId);
        switch (resId) {
            case R.drawable.ic_share:
                drawable.setColorFilter(Theme.colorGray, PorterDuff.Mode.MULTIPLY);
                break;
            case R.drawable.ic_search:
            case R.drawable.ic_voice:
            case R.drawable.ic_sd:
            case R.drawable.button_bg:
                drawable.setColorFilter(Theme.colorPrimary, PorterDuff.Mode.MULTIPLY);
                break;
        }
        return drawable;
    }
}
