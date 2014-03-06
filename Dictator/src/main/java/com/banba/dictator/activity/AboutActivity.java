package com.banba.dictator.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.banba.dictator.R;
import com.banba.dictator.fragment.AboutFragment;

/**
 * Created by Ernan on 29/01/14.
 * Copyrite Banba Inc. 2013.
 */
public class AboutActivity extends BaseActivity {

    private AboutFragment aboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aboutFragment = new AboutFragment();
        super.onCreate(savedInstanceState);
    }

    public void onClick(View view) {
        aboutFragment.onClick(view);
    }


    protected int getPageTitle() {
        return R.string.about;
    }

    @Override
    public Fragment getFragment() {
        return aboutFragment;
    }
}
