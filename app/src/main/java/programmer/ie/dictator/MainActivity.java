package programmer.ie.dictator;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import programmer.ie.dictator.activity.BaseActivity;
import programmer.ie.dictator.fragment.MainFragment;

public class MainActivity extends BaseActivity {
    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainFragment = new MainFragment();
        super.onCreate(savedInstanceState);
    }

    @Override
    public Fragment getFragment() {
        return mainFragment;
    }

    public int getActionBarTitleResourceId() {
        return R.string.app_name;
    }


    public void onClick(View view) {
        mainFragment.onClick(view);
    }
}
