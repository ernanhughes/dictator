package programmer.ie.dictator.activity;

import android.app.Fragment;

import programmer.ie.dictator.R;
import programmer.ie.dictator.fragment.SearchFragment;

public class SearchActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new SearchFragment();
    }

    public int getActionBarTitleResourceId() {
        return R.string.search;
    }
}
