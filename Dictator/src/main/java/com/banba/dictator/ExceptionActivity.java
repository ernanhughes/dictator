package com.banba.dictator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import com.banba.dictator.data.DaoMaster;
import com.banba.dictator.data.DaoSession;
import com.banba.dictator.data.ExceptionData;
import com.banba.dictator.data.ExceptionDataDao;
import com.banba.dictator.ui.adapter.Binder;
import com.banba.dictator.ui.adapter.SimpleAdapter;
import com.banba.dictator.ui.adapter.interfaces.StringExtractor;

import java.util.Date;
import java.util.List;

/**
 * Created by Ernan on 21/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class ExceptionActivity extends Activity {
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("Exceptions");
        }

        Binder<ExceptionData> binder = new Binder.Builder<ExceptionData>()
                .addString(R.id.timeText, new StringExtractor<ExceptionData>() {
                    @Override
                    public String getStringValue(ExceptionData item, int position) {
                        return new Date(item.getExceptionTime()).toString();
                    }
                })
                .addString(R.id.exceptionMessage, new StringExtractor<ExceptionData>() {
                    @Override
                    public String getStringValue(ExceptionData item, int position) {
                        return item.getExceptionMessage();
                    }
                })
                .addString(R.id.throwClassName, new StringExtractor<ExceptionData>() {
                    @Override
                    public String getStringValue(ExceptionData item, int position) {
                        return item.getThrowClassName();
                    }
                })
                .addString(R.id.throwFileName, new StringExtractor<ExceptionData>() {
                    @Override
                    public String getStringValue(ExceptionData item, int position) {
                        return item.getThrowFileName();
                    }
                })
                .addString(R.id.throwMethodName, new StringExtractor<ExceptionData>() {
                    @Override
                    public String getStringValue(ExceptionData item, int position) {
                        return item.getThrowMethodName();
                    }
                })
                .addString(R.id.throwLineNumber, new StringExtractor<ExceptionData>() {
                    @Override
                    public String getStringValue(ExceptionData item, int position) {
                        return item.getThrowLineNumber();
                    }
                })
                .addString(R.id.stackTrace, new StringExtractor<ExceptionData>() {
                    @Override
                    public String getStringValue(ExceptionData item, int position) {
                        return item.getStackTrace();
                    }
                })
                .build();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "exceptions-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        ExceptionDataDao dataDao = session.getExceptionDataDao();
        List<ExceptionData> exceptions = dataDao.loadAll();
        final SimpleAdapter<ExceptionData> cardsAdapter = new SimpleAdapter<ExceptionData>(this, exceptions, binder, R.layout.list_item_exception);
        ListView cardsList = (ListView) findViewById(R.id.listview);
        cardsList.setAdapter(cardsAdapter);
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exceptions, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_share) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "exceptions-db", null);
            DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
            DaoSession session = daoMaster.newSession();
            ExceptionDataDao dataDao = session.getExceptionDataDao();
            List<ExceptionData> exceptions = dataDao.loadAll();
            StringBuilder builder = new StringBuilder();
            for (ExceptionData ex : exceptions) {
                builder.append(ex.getThrowClassName()).append(",")
                        .append(ex.getThrowLineNumber()).append(",")
                        .append(ex.getExceptionMessage()).append(",")
                        .append(ex.getThrowMethodName());
            }
            share.putExtra(Intent.EXTRA_TEXT, builder.toString());
            startActivity(Intent.createChooser(share, getString(R.string.share_using)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}