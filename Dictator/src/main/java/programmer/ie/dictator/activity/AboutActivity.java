package programmer.ie.dictator.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import programmer.ie.dictator.R;
import programmer.ie.dictator.fragment.AboutFragment;

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
