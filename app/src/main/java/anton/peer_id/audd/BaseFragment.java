package anton.peer_id.audd;

import android.view.View;
import anton.peer_id.widget.Fragment;
import anton.peer_id.widget.Page;
import anton.peer_id.widget.PageAdapter;
import anton.peer_id.widget.ViewPager;

public class BaseFragment extends Fragment implements Page.OnPageListener {

    @Override
    public View onViewPage() {
        ViewPager pager = new ViewPager(context());
        pager.setAdapter(new PageAdapter(new Page[] {
                RecordPage.newInstance(this),
                RecognizeStoryPage.newInstance(this)
        }));
        return pager;
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


    public static BaseFragment newInstance() {
        return new BaseFragment();
    }

    @Override
    public void setPagerPosition(int position) {

    }

    @Override
    public void onAddFragment(Fragment fragment) {
        addFragment(fragment);
    }
}
