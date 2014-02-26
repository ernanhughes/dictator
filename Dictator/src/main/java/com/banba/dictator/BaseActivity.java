package com.banba.dictator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.banba.dictator.event.PlayRecordingEvent;
import com.banba.dictator.event.SectionEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by Ernan on 14/02/14.
 * Copyrite Banba Inc. 2013.
 */
public abstract class BaseActivity extends Activity {
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(getActionBarTitleResourceId()));
        }
        EventBus.getDefault().register(this);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, getFragment())
                    .commit();
        }
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_share) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            startActivity(Intent.createChooser(share, getString(R.string.share_using)));
            return true;
        }
        if (id == R.id.menu_item_search) {
            Intent search = new Intent(this, SearchActivity.class);
            startActivity(search);
            return true;
        }
        if (id == R.id.menu_item_ex) {
            Intent search = new Intent(this, ExceptionActivity.class);
            startActivity(search);
            return true;
        }
//        if (id == R.id.menu_item_command) {
//            Intent activity_search = new Intent(this, RunCommandActivity.class);
//            startActivity(activity_search);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    public void onEvent(SectionEvent event) {
//        EventBus.getDefault().cancelEventDelivery(event);

    }

    public void onEvent(PlayRecordingEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        Intent i = new Intent(this, PlayActivity.class);
        i.putExtra(Util.FILE_NAME, event.recording.getFileName());
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        return super.onCreateOptionsMenu(menu);
    }

    public int getActionBarTitleResourceId() {
        return R.string.app_name;
    }

    public abstract Fragment getFragment();
}