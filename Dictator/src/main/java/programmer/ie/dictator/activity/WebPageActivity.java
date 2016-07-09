package programmer.ie.dictator.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import programmer.ie.dictator.R;

@SuppressLint("Registered")
public class WebPageActivity extends Activity {

    protected int getPageTitle() {
        return R.string.help;
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
