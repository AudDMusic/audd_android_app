package anton.peer_id.audd;

import android.Manifest;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import anton.peer_id.widget.Fragment;
import anton.peer_id.widget.TextView;

public class PermissionFragment extends Fragment {

    @Override
    public View onViewPage() {
        FrameLayout layout = new FrameLayout(context());
        LinearLayout body = new LinearLayout(context());
        body.setOrientation(LinearLayout.VERTICAL);
        createIcons(body);
        TextView text = new TextView(context());
        text.setGravity(Gravity.CENTER);
        text.setTextSize(16);
        text.setTextColor(Theme.colorBlack);
        text.setPadding(0, Screen.dp(8), 0, Screen.dp(8));
        text.setText("Для работы приложения необходимо разрешить доступ к микрофону и памяти.");
        body.addView(text, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        TextView button = new TextView(context());
        button.setText("OK");
        button.setTextColor(Color.WHITE);
        button.setTextSize(18);
        button.setPadding(0, Screen.dp(8), 0, Screen.dp(8));
        button.setBackgroundDrawable(Screen.getDrawable(R.drawable.button_bg));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions((BaseActivity) context(),
                        new String[] {
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                BaseActivity.PERMISSIONS_REQUEST);
            }
        });
        button.setGravity(Gravity.CENTER);
        body.addView(button, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        params.setMargins(Screen.dp(32), 0, Screen.dp(32), 0);
        layout.addView(body, params);
        return layout;
    }


    private void createIcons(LinearLayout body) {
        LinearLayout icons = new LinearLayout(context());
        icons.setOrientation(LinearLayout.HORIZONTAL);
        ImageView icon = new ImageView(context());
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setImageDrawable(Screen.getDrawable(R.drawable.ic_voice));
        icons.addView(icon, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        icon = new ImageView(context());
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setImageDrawable(Screen.getDrawable(R.drawable.ic_sd));
        icons.addView(icon, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        body.addView(icons, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(56)));
    }

    @Override
    public void onConnectApp() {

    }

    @Override
    public void onDisconnectApp() {

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public boolean isBackSwipe() {
        return false;
    }


    public static PermissionFragment newInstance() {
        return new PermissionFragment();
    }
}
