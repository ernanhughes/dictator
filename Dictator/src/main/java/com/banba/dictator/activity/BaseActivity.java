package com.banba.dictator.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.banba.dictator.R;
import com.banba.dictator.Util;
import com.banba.dictator.event.PlayRecordingEvent;
import com.banba.dictator.event.SectionEvent;
import com.banba.dictator.lib.activity.ExceptionActivity;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.SubscriberExceptionEvent;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        if (getShareItem() == null) {
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }


    protected Object getShareItem() {
        return null;
    }

    protected boolean doShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        startActivity(Intent.createChooser(share, getString(R.string.share_using)));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_share) {
            return doShare();
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

    public void onEvent(PlayRecordingEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        Intent i = new Intent(this, PlayActivity.class);
        i.putExtra(Util.FILE_NAME, event.recording.getFileName());
        startActivity(i);
    }

    public void onEvent(SectionEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        if (SectionEvent.SEARCH.equalsIgnoreCase(event.section)) {
            Intent i = new Intent(this, SearchActivity.class);
            startActivity(i);
        } else if (SectionEvent.PLAY_LIST.equalsIgnoreCase(event.section)) {
            Intent i = new Intent(this, PlayListActivity.class);
            startActivity(i);
        } else if (SectionEvent.MANAGE.equalsIgnoreCase(event.section)) {
            Intent i = new Intent(this, ManageActivity.class);
            startActivity(i);
        } else if (SectionEvent.CALENDAR.equalsIgnoreCase(event.section)) {
            Intent i = new Intent(this, CalendarActivity.class);
            startActivity(i);
        } else if (SectionEvent.HELP.equalsIgnoreCase(event.section)) {
            Intent i = new Intent(this, HelpActivity.class);
            startActivity(i);
        } else if (SectionEvent.ABOUT.equalsIgnoreCase(event.section)) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        }
    }

    public void onEvent(SubscriberExceptionEvent event) {

    }

    public int getActionBarTitleResourceId() {
        return R.string.app_name;
    }

    public abstract Fragment getFragment();
}