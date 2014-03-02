package com.banba.dictator.activity;

import android.app.Fragment;

import com.banba.dictator.R;
import com.banba.dictator.fragment.CalendarFragment;


/**
 * Created by Ernan on 25/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class CalendarActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new CalendarFragment();
    }

    public int getActionBarTitleResourceId() {
        return R.string.history;
    }
}
