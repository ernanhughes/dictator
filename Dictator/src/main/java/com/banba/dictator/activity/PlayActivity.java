package com.banba.dictator.activity;

import android.app.Fragment;

import com.banba.dictator.R;
import com.banba.dictator.fragment.PlayFragment;

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
