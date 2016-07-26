package programmer.ie.dictator.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ApplicationErrorReport;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class E extends ListActivity {
    public static void showPopUp(final Context context) {
        final ExceptionDatabase db = new ExceptionDatabase(context);
        final List<ExceptionItem> items = db.getAll();
        ExceptionItemAdapter adapter = new ExceptionItemAdapter(context, items);
        ListView listViewItems = new ListView(context);
        listViewItems.setAdapter(adapter);
        listViewItems.setFastScrollEnabled(true);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(listViewItems)
                .setTitle("Exceptions")
                .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        StringBuilder b = new StringBuilder();
                        for (ExceptionItem i : items) {
                            b.append(i.toString()).append("\n");
                        }
                        sendIntent.putExtra(Intent.EXTRA_TEXT, b.toString());
                        sendIntent.setType("text/plain");
                        context.startActivity(sendIntent);
                    }
                })
                .setNegativeButton("Clear Exceptions", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteAll();
                        Toast.makeText(context, "All exceptions cleared", Toast.LENGTH_SHORT).show();
                    }
                }).create();
        dialog.show();
    }

    public static void LogError(Context context, Throwable ex) {
        ApplicationErrorReport report = new ApplicationErrorReport();
        report.time = System.currentTimeMillis();
        report.type = ApplicationErrorReport.TYPE_CRASH;
        report.systemApp = false;

        ApplicationErrorReport.CrashInfo crash = new ApplicationErrorReport.CrashInfo();
        crash.exceptionClassName = ex.getClass().getSimpleName();
        crash.exceptionMessage = ex.getMessage();

        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer);
        ex.printStackTrace(printer);

        crash.stackTrace = writer.toString();

        StackTraceElement stack = ex.getStackTrace()[0];
        crash.throwClassName = stack.getClassName();
        crash.throwFileName = stack.getFileName();
        crash.throwLineNumber = stack.getLineNumber();
        crash.throwMethodName = stack.getMethodName();

        report.crashInfo = crash;

        ExceptionItem item = new ExceptionItem();
        item.packageName = report.packageName;
        item.exceptionTime = report.time;
        item.exceptionType = Long.valueOf(report.type);
        item.exceptionMessage = crash.exceptionMessage;
        item.stackTrace = crash.stackTrace;
        item.throwClassName = crash.throwClassName;
        item.throwFileName = crash.throwFileName;
        item.throwLineNumber = crash.throwLineNumber;
        item.throwMethodName = crash.throwMethodName;

        Log.e("EXCEPTION", item.toString());

        ExceptionDatabase db = new ExceptionDatabase(context);
        db.add(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Exceptions");
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);
        ExceptionDatabase db = new ExceptionDatabase(this);
        final List<ExceptionItem> items = db.getAll();
        ExceptionItemAdapter adapter = new ExceptionItemAdapter(this, items);
        getListView().setAdapter(adapter);
    }

    public static class ExHandler implements Thread.UncaughtExceptionHandler {
        Context mContext;

        public ExHandler(Context context) {
            mContext = context;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            LogError(mContext, ex);
        }
    }

    static class ExceptionItem {
        long id;
        String packageName;
        Long exceptionTime;
        Long exceptionType;
        String exceptionClassName;
        String exceptionMessage;
        String stackTrace;
        String throwClassName;
        String throwFileName;
        int throwLineNumber;
        String throwMethodName;

        @Override
        public String toString() {
            return "Exception:" +
                    "\nTime = " + exceptionTime +
                    "\nPackage Name = " + packageName +
                    "\nType = " + exceptionType +
                    "\nClass Name = " + exceptionClassName +
                    "\nMessage = " + exceptionMessage +
                    "\nthrow Class Name = " + throwClassName +
                    "\nthrow File Name = " + throwFileName +
                    "\nthrow Line Number = " + throwLineNumber +
                    "\nthrow Method Name = " + throwMethodName +
                    "\nStack Trace = [\n" + stackTrace + "\n]";
        }
    }

    static class ExceptionItemHolder {
        TextView className;
        TextView message;
    }

    static class ExceptionItemAdapter extends ArrayAdapter<ExceptionItem> {
        Context context;

        public ExceptionItemAdapter(Context context, List items) {
            super(context, android.R.layout.simple_list_item_2, items);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ExceptionItemHolder holder = null;
            final ExceptionItem item = getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
                holder = new ExceptionItemHolder();
                holder.className = (TextView) convertView.findViewById(android.R.id.text1);
                holder.message = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, item.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                holder = (ExceptionItemHolder) convertView.getTag();
            }

            holder.className.setText(item.exceptionMessage);
            holder.message.setText(item.stackTrace.substring(0, 200));
            return convertView;
        }
    }


    static class ExceptionDatabase extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "exceptions.db";
        private static final String TABLE_EXCEPTIONS = "exceptions";

        private static final String KEY_ID = "ID";
        private static final String PACKAGE_NAME = "PACKAGE_NAME";
        private static final String EXCEPTION_TIME = "EXCEPTION_TIME";
        private static final String EXCEPTION_TYPE = "EXCEPTION_TYPE";
        private static final String EXCEPTION_CLASS_NAME = "EXCEPTION_CLASS_NAME";
        private static final String EXCEPTION_MESSAGE = "EXCEPTION_MESSAGE";
        private static final String STACKTRACE = "STACKTRACE";
        private static final String THROW_CLASS_NAME = "THROW_CLASS_NAME";
        private static final String THROW_FILE_NAME = "THROW_FILE_NAME";
        private static final String THROW_LINE_NUMBER = "THROW_LINE_NUMBER";
        private static final String KEY_METHOD_NAME = "THROW_METHOD_NAME";

        public ExceptionDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_ARTICLES_TABLE = "CREATE TABLE "
                    + TABLE_EXCEPTIONS + " ( "
                    + PACKAGE_NAME + " TEXT , "
                    + EXCEPTION_TIME + " LONG , "
                    + EXCEPTION_TYPE + " TEXT ,"
                    + EXCEPTION_CLASS_NAME + " TEXT ,"
                    + EXCEPTION_MESSAGE + " TEXT , "
                    + STACKTRACE + " TEXT , "
                    + THROW_CLASS_NAME + " TEXT , "
                    + THROW_FILE_NAME + " TEXT , "
                    + THROW_LINE_NUMBER + " INTEGER , "
                    + KEY_METHOD_NAME + " TEXT , "
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT)";
            db.execSQL(CREATE_ARTICLES_TABLE);

//            String trigger = "CREATE TRIGGER delete_tail AFTER INSERT ON "
//                    + TABLE_EXCEPTIONS +
//                    "BEGIN DELETE FROM "
//                    + TABLE_EXCEPTIONS +
//                    " WHERE id%10=NEW.id%10 AND id!=NEW.id; END;";
//            db.execSQL(trigger);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXCEPTIONS);
            onCreate(db);
        }

        public void add(ExceptionItem ex) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PACKAGE_NAME, ex.packageName);
            values.put(EXCEPTION_TIME, ex.exceptionTime);
            values.put(EXCEPTION_TYPE, ex.exceptionType);
            values.put(EXCEPTION_CLASS_NAME, ex.exceptionClassName);
            values.put(EXCEPTION_MESSAGE, ex.exceptionMessage);
            values.put(STACKTRACE, ex.stackTrace);
            values.put(THROW_CLASS_NAME, ex.throwClassName);
            values.put(THROW_FILE_NAME, ex.throwFileName);
            values.put(THROW_LINE_NUMBER, ex.throwLineNumber);
            db.insert(TABLE_EXCEPTIONS, null, values);
            db.close();
        }


        public List<ExceptionItem> getAll() {
            List<ExceptionItem> articleList = new ArrayList<>();
            String selectQuery = "SELECT  * FROM " + TABLE_EXCEPTIONS + " ORDER BY " + EXCEPTION_TIME;
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToLast()) {
                do {
                    ExceptionItem item = new ExceptionItem();
                    item.id = cursor.getLong(0);
                    item.packageName = cursor.getString(0);
                    item.exceptionTime = cursor.getLong(1);
                    item.exceptionType = cursor.getLong(2);
                    item.exceptionClassName = cursor.getString(3);
                    item.exceptionMessage = cursor.getString(4);
                    item.stackTrace = cursor.getString(5);
                    item.throwClassName = cursor.getString(6);
                    item.throwFileName = cursor.getString(7);
                    item.throwLineNumber = cursor.getInt(8);
                    item.throwMethodName = cursor.getString(9);
                    articleList.add(item);
                } while (cursor.moveToPrevious());
            }
            db.close();
            return articleList;
        }

        public void deleteAll() {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_EXCEPTIONS, null, null);
            db.close();
        }
    }
}
