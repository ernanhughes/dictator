package com.banba.dictator;

import android.app.Fragment;

import com.banba.dictator.fragments.PlayFragment;

/**
 * Demo to show how to use VisualizerView
 */
public class PlayActivity extends BaseActivity {
    @Override
    public Fragment getFragment() {
        return new PlayFragment();
    }

    public int getActionBarTitleResourceId() {
        return R.string.play;
    }
}
