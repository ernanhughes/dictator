package programmer.ie.dictator;

import android.app.Application;
import android.app.ApplicationErrorReport;
import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import programmer.ie.dictator.data.DaoMaster;
import programmer.ie.dictator.data.DaoSession;
import programmer.ie.dictator.data.ExceptionData;
import programmer.ie.dictator.data.ExceptionDataDao;
import programmer.ie.dictator.util.L;

public class DictatorApp extends Application implements Thread.UncaughtExceptionHandler {
    public static final String DATABASE_NAME = "dictator.db";

    public static void reportException(Context context, Throwable e) {
        ApplicationErrorReport report = new ApplicationErrorReport();
        report.packageName = report.processName = context.getPackageName();
        report.time = System.currentTimeMillis();
        report.type = ApplicationErrorReport.TYPE_CRASH;
        report.systemApp = false;

        ApplicationErrorReport.CrashInfo crash = new ApplicationErrorReport.CrashInfo();
        crash.exceptionClassName = e.getClass().getSimpleName();
        crash.exceptionMessage = e.getMessage();

        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer);
        e.printStackTrace(printer);

        crash.stackTrace = writer.toString();

        StackTraceElement stack = e.getStackTrace()[0];
        L.e(stack.toString());
        crash.throwClassName = stack.getClassName();
        L.e(stack.getClassName());
        crash.throwFileName = stack.getFileName();
        L.e(stack.getFileName());
        crash.throwLineNumber = stack.getLineNumber();
        L.e("Line Number: " + stack.getLineNumber());
        crash.throwMethodName = stack.getMethodName();
        L.e(stack.getMethodName());

        report.crashInfo = crash;

        ExceptionData data = new ExceptionData(null, report.packageName,
                report.time, Long.valueOf(report.type), crash.exceptionClassName, crash.exceptionMessage,
                crash.stackTrace, crash.throwClassName,
                crash.throwFileName,
                String.valueOf(crash.throwLineNumber),
                crash.throwMethodName);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "exceptions-db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        ExceptionDataDao dataDao = session.getExceptionDataDao();
        dataDao.insert(data);
        if (dataDao.count() > 20) {
            List<ExceptionData> items = dataDao.loadAll();
            dataDao.delete(items.get(0));
        }
        helper.close();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            L.plant(new L.DebugTree());
        }
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        reportException(this, e);
    }

}
