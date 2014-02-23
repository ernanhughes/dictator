package com.banba.dictator.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.banba.dictator.R;

import static com.banba.dictator.R.string.help;

/**
 * Created by Ernan on 29/01/14.
 * Copyrite Banba Inc. 2013.
 */
@SuppressLint("Registered")
public class WebPageActivity extends Activity {

    protected int getPageTitle() {
        return help;
    }

    protected String getPath() {
        return "file:///android_asset/help.html";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(getPageTitle()));
        }
        WebView browser = (WebView) findViewById(R.id.browser);
        browser.loadUrl(getPath());
    }
}
