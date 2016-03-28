package programmer.ie.dictator.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import programmer.ie.dictator.R;
import programmer.ie.dictator.adapter.Binder;
import programmer.ie.dictator.adapter.SimpleAdapter;
import programmer.ie.dictator.adapter.interfaces.StringExtractor;
import programmer.ie.dictator.data.DaoMaster;
import programmer.ie.dictator.data.DaoSession;
import programmer.ie.dictator.data.ExceptionData;
import programmer.ie.dictator.data.ExceptionDataDao;
import programmer.ie.dictator.util.DateTimeUtil;

//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;

public class ExceptionActivity extends ListActivity {
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        return DateTimeUtil.normalDateFormat(new Date(item.getExceptionTime()));
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
        Collections.sort(exceptions, new Comparator<ExceptionData>() {
            @Override
            public int compare(ExceptionData lhs, ExceptionData rhs) {
                return rhs.getExceptionTime().compareTo(lhs.getExceptionTime());
            }
        });
        final SimpleAdapter<ExceptionData> cardsAdapter = new SimpleAdapter<ExceptionData>(this, exceptions, binder, R.layout.list_item_exception);
        getListView().setAdapter(cardsAdapter);
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


    public void postData(ExceptionData data) {
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httppost = new HttpPost("https://banba.ca/exceptions.php");
//        try {
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//            nameValuePairs.add(new BasicNameValuePair("id", data.getExceptionClassName()));
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//            httpclient.execute(httppost);
//        } catch (Exception e) {
//            L.e(e.getMessage());
//        }
    }

}
