package programmer.ie.dictator.activity;

import android.app.Fragment;

import programmer.ie.dictator.R;
import programmer.ie.dictator.fragment.PlayListFragment;

public class PlayListActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new PlayListFragment();
    }

    public int getActionBarTitleResourceId() {
        return R.string.play;
    }
}