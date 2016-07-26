package programmer.ie.dictator;

import android.app.Application;

import programmer.ie.dictator.util.E;

public class App extends Application implements Thread.UncaughtExceptionHandler {
    public static final String DATABASE_NAME = "dictator.db";

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        E.LogError(this, e);
    }

}
