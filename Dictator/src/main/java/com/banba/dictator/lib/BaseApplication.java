package com.banba.dictator.lib;

import android.app.Application;
import android.app.ApplicationErrorReport;
import android.content.Context;

import com.banba.dictator.BuildConfig;
import com.banba.dictator.data.DaoMaster;
import com.banba.dictator.data.DaoSession;
import com.banba.dictator.data.ExceptionData;
import com.banba.dictator.data.ExceptionDataDao;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Created by Ernan on 06/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class BaseApplication extends Application implements Thread.UncaughtExceptionHandler {
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
        crash.throwClassName = stack.getClassName();
        crash.throwFileName = stack.getFileName();
        crash.throwLineNumber = stack.getLineNumber();
        crash.throwMethodName = stack.getMethodName();

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

//        Intent intent = new Intent(Intent.ACTION_APP_ERROR);
//        intent.putExtra(Intent.EXTRA_BUG_REPORT, report);
//        context.startActivity(intent);
    }
}
