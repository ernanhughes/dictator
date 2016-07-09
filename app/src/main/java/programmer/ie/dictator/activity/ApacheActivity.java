package programmer.ie.dictator.activity;


import programmer.ie.dictator.R;

public class ApacheActivity extends WebPageActivity {
    protected int getPageTitle() {
        return R.string.apache;
    }

    protected String getPath() {
        return "file:///android_asset/apache.html";
    }
}
