package com.banba.dictator.activity;

import android.app.Fragment;

import com.banba.dictator.R;
import com.banba.dictator.fragment.ManageFragment;

/**
 * Created by Ernan on 24/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class ManageActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new ManageFragment();
    }

    public int getActionBarTitleResourceId() {
        return R.string.manage;
    }
}