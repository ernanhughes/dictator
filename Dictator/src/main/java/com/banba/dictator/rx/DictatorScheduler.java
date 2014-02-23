package com.banba.dictator.rx;

import android.os.Handler;
import android.os.Looper;

import rx.Scheduler;

public class DictatorScheduler {

    private static final Scheduler MAIN_THREAD_SCHEDULER =
            new HandlerThreadScheduler(new Handler(Looper.getMainLooper()));

    private DictatorScheduler() {

    }

    public static Scheduler handlerThread(final Handler handler) {
        return new HandlerThreadScheduler(handler);
    }

    public static Scheduler mainThread() {
        return MAIN_THREAD_SCHEDULER;
    }
}
