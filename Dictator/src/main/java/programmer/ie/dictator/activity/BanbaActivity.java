package programmer.ie.dictator.activity;

import programmer.ie.dictator.R;

public class BanbaActivity extends WebPageActivity {
    protected int getPageTitle() {
        return R.string.contact_us;
    }

    protected String getPath() {
        return "file:///android_asset/banba.html";
    }
}
