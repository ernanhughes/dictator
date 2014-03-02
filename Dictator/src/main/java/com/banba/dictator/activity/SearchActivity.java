package com.banba.dictator.activity;

import android.app.Fragment;

import com.banba.dictator.R;
import com.banba.dictator.fragment.SearchFragment;

/**
 * Created by Ernan on 30/01/14.
 * Copyrite Banba Inc. 2013.
 */
public class SearchActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new SearchFragment();
    }

    public int getActionBarTitleResourceId() {
        return R.string.search;
    }
}
