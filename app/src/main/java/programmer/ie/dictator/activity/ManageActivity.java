package programmer.ie.dictator.activity;

import android.app.Fragment;

import programmer.ie.dictator.R;
import programmer.ie.dictator.fragment.ManageFragment;

public class ManageActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new ManageFragment();
    }

    public int getActionBarTitleResourceId() {
        return R.string.manage;
    }
}