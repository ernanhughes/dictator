package programmer.ie.dictator.activity;

import android.app.Fragment;

import programmer.ie.dictator.R;
import programmer.ie.dictator.fragment.PlayFragment;

public class PlayActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new PlayFragment();
    }

    public int getActionBarTitleResourceId() {
        return R.string.play;
    }
}
