package anton.peer_id.audd;

import android.view.View;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import anton.peer_id.widget.BottomFragment;
import anton.peer_id.widget.TextView;

public class ErrorFragment extends BottomFragment {
    @Override
    public View onViewPage() {
        Answers.getInstance().logContentView(new ContentViewEvent().putContentName("ErrorFragment"));
        TextView message = new TextView(context());
        message.setTextSize(16);
        message.setTextColor(Theme.colorBlack);
        message.setPadding(Screen.dp(16), Screen.dp(12), Screen.dp(16), Screen.dp(12));
        message.setText("Я не могу распознать эту музыку. Попытайтесь записать другой отрывок который будет не меньше 3 секунд");
        return message;
    }

    @Override
    public void onConnectApp() {

    }

    @Override
    public void onDisconnectApp() {

    }

    public static ErrorFragment newInstance() {
        return new ErrorFragment();
    }
}
