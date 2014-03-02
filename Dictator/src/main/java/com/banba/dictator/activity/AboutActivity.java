package com.banba.dictator.activity;

import com.banba.dictator.R;
import com.banba.dictator.lib.activity.WebPageActivity;

/**
 * Created by Ernan on 29/01/14.
 * Copyrite Banba Inc. 2013.
 */
public class AboutActivity extends WebPageActivity {
    protected int getPageTitle() {
        return R.string.about;
    }

    protected String getPath() {
        return "file:///android_asset/banba.html";
    }
}
