package com.banba.dictator;

import android.app.Fragment;
import android.view.View;

import com.banba.dictator.activity.BaseActivity;
import com.banba.dictator.fragment.MainFragment;

/**
 * Created by Ernan on 23/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class MainActivity extends BaseActivity {
    MainFragment mainFragment;

    @Override
    public Fragment getFragment() {
        mainFragment = new MainFragment();
        return mainFragment;
    }

    public int getActionBarTitleResourceId() {
        return R.string.app_name;
    }


    public void onClick(View view) {
        mainFragment.onClick(view);
    }
}
