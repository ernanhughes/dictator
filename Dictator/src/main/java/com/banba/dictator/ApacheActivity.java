package com.banba.dictator;

import com.banba.dictator.ui.WebPageActivity;

/**
 * Created by Ernan on 29/01/14.
 * Copyrite Banba Inc. 2013.
 */
public class ApacheActivity extends WebPageActivity {
    protected int getPageTitle() {
        return R.string.apache;
    }

    protected String getPath() {
        return "file:///android_asset/apache.html";
    }
}
