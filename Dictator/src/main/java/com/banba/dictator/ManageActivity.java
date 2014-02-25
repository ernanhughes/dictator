package com.banba.dictator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.banba.dictator.data.DaoMaster;
import com.banba.dictator.data.DaoSession;
import com.banba.dictator.data.Recording;
import com.banba.dictator.data.RecordingDao;
import com.banba.dictator.ui.tablefixheaders.TableFixHeaders;
import com.banba.dictator.ui.tablefixheaders.adapters.BaseTableAdapter;

import java.util.List;

/**
 * Created by Ernan on 24/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class ManageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.listview);
        setContentView(R.layout.activity_manage);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        TableFixHeaders tableFixHeaders = (TableFixHeaders) findViewById(R.id.history_list);
        tableFixHeaders.setAdapter(new RecordingHistoryTableAdapter(this));

//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "director.db", null);
//        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
//        DaoSession session = daoMaster.newSession();
//        RecordingDao dataDao = session.getRecordingDao();
//        List<Recording> recordings = dataDao.loadAll();
//
//        Binder<Recording> binder = new Binder.Builder<Recording>()
//                .addString(android.R.id.title, new StringExtractor<Recording>() {
//                    @Override
//                    public String getStringValue(Recording item, int position) {
//                        return item.getName();
//                    }
//                })
//                .addString(android.R.id.content, new StringExtractor<Recording>() {
//                    @Override
//                    public String getStringValue(Recording item, int position) {
//                        return item.getFileName();
//                    }
//                })
//                .addStaticImage(android.R.id.icon, new StaticImageLoader<Recording>() {
//                    @Override
//                    public void loadImage(Recording item, ImageView imageView, int position) {
//                    }
//                }).build();
//
//        final SimpleAdapter<Recording> cardsAdapter = new SimpleAdapter<Recording>(this, recordings, binder, R.layout.list_item_card);
//        ListView cardsList = (ListView) findViewById(R.id.listview);
//        cardsList.setAdapter(cardsAdapter);
//        cardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            }
//        });
    }


    static class RecordingHistoryTableAdapter extends BaseTableAdapter {

        private final Context mContext;
        private final LayoutInflater inflater;
        private final int width;
        private final int height;
        List<Recording> recordings;

        private final String headers[] = {
                "Time",
                "Move",
                "Error",
                "%"
        };
        private final int[] widths = {
                140,
                120,
                100,
                60,
                60,
        };

        private final float density;

        public RecordingHistoryTableAdapter(Context context) {
            mContext = context;
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "director.db", null);
            DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
            DaoSession session = daoMaster.newSession();
            RecordingDao dataDao = session.getRecordingDao();
            recordings = dataDao.loadAll();
            inflater = LayoutInflater.from(context);
            Resources resources = context.getResources();
            width = resources.getDimensionPixelSize(R.dimen.table_width);
            height = resources.getDimensionPixelSize(R.dimen.table_height);
            density = context.getResources().getDisplayMetrics().density;
        }

        @Override
        public View getView(int row, int column, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(getLayoutResource(row, column), parent, false);
            }
            setText(convertView, getCellString(row, column));
            final int rowSelect = row + 1;
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (rowSelect < mHistory.size()) {
//                    Command item = mHistory.get(rowSelect);
//                    mGame.setCurrent(item.move);
//                    EventBus.getDefault().post(new GameChangedEvent(mGame));
//                }
//            }
//        });
            return convertView;
        }

        private void setText(View view, String text) {
            ((TextView) view.findViewById(android.R.id.text1)).setText(text);
        }

        @Override
        public int getRowCount() {
            return recordings.size();
        }

        @Override
        public int getColumnCount() {
            return headers.length - 1;
        }

        @Override
        public int getWidth(int column) {
            return Math.round(widths[column + 1] * density);
        }

        @Override
        public int getHeight(int row) {
            return height;
        }


        String getGameColumn(Recording recording, int col) {

            switch (col) {
                case -1:
                    return String.valueOf(recording.getStartTime());
                case 0: {
                    return recording.getName();
                }
                case 1: {
                    return recording.getFileName();
                }
                case 2: {
                    return String.valueOf(recording.getFileSize());
                }
                default:
                    return "ERR";
            }
        }

        public String getCellString(int row, int column) {
            if (row == -1) {
                if (column < (headers.length - 1))
                    return headers[column + 1];
                else {
                    return "ERR";
                }
            }
            Recording recording = recordings.get(row);
            return getGameColumn(recording, column);
        }

        public int getLayoutResource(int row, int column) {
            final int layoutResource;
            switch (getItemViewType(row, column)) {
                case 0:
                    layoutResource = R.layout.games_list_table_header;
                    break;
                case 1:
                    layoutResource = R.layout.games_list_item_blue;
                    break;
                case 2:
                    layoutResource = R.layout.games_list_item_white;
                    break;
                default:
                    throw new RuntimeException("wtf?");
            }
            return layoutResource;
        }

        @Override
        public int getItemViewType(int row, int column) {
            if (row < 0) {
                return 0;
            } else {
                if (row % 2 == 0)
                    return 1;
                return 2;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }
    }
}
