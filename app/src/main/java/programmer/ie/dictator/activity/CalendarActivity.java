package programmer.ie.dictator.activity;

import android.app.Fragment;

import programmer.ie.dictator.R;
import programmer.ie.dictator.fragment.CalendarFragment;

public class CalendarActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new CalendarFragment();
    }

    public int getActionBarTitleResourceId() {
        return R.string.history;
    }
}
