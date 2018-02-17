package anton.peer_id.audd;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import anton.peer_id.widget.BottomFragment;
import anton.peer_id.widget.Fragment;
import anton.peer_id.widget.RecordView;

public class BaseActivity extends Activity implements Fragment.FragmentListener {

    public static final int PERMISSIONS_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(R.style.Theme_Default);
        getWindow().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
        Screen.setDisplaySize(this, null);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                Screen.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
        }
        FrameLayout layout = new FrameLayout(this);
        layout.setId(R.id.root);
        setContentView(layout);
        init();
        /* Application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                onAddFragment(MusicFragment.newInstance(null));
            }
        }, 100); */
    }

    @Override
    public void onAddFragment(Fragment fragment) {
        try {
            Screen.hideKeyboard(getCurrentFocus());
            getFragmentManager().beginTransaction().add(R.id.root, fragment).addToBackStack(fragment.getClass().getName()).commitAllowingStateLoss();
        } catch (Throwable ignored) { }
    }

    @Override
    public void onRemoveFragment(Fragment fragment) {
        try {
            Screen.hideKeyboard(getCurrentFocus());
            fragment.setContext(this);
            getFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        } catch (Throwable ignored) { }
    }

    @Override
    public void preparationFragment(Fragment fragment, boolean isFinish) {

    }

    @Override
    public void init() {
        Fragment prevFragment = (Fragment) getFragmentManager().findFragmentById(R.id.root);
        if (prevFragment != null) {
            getFragmentManager().beginTransaction().remove(prevFragment).commitAllowingStateLoss();
        }
        Fragment fragment = isPermissions() ? BaseFragment.newInstance() : PermissionFragment.newInstance();
        fragment.setContext(this);
        getFragmentManager().beginTransaction().replace(R.id.root, fragment).commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = (Fragment) getFragmentManager().findFragmentById(R.id.root);
        if (getFragmentManager().getBackStackEntryCount() == 0 || fragment instanceof BaseFragment) {
            finishInternal();
            return;
        }
        if (fragment instanceof BottomFragment) {
            fragment.finish();
            return;
        }
        if (fragment.onBackPressed()) {
            if (!fragment.isBackSwipe() || getFragmentManager().getBackStackEntryCount() == 0) {
                finishInternal();
            } else {
                fragment.finish();
            }
        }
    }

    public void finishInternal() {
        super.finishAfterTransition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecordView.release();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private boolean isPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Screen.setDisplaySize(this, newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(this, "Для работы приложения нужно разрешить доступ", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
