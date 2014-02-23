package com.banba.dictator.rx;

import android.os.Handler;

import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.Subscription;
import rx.operators.SafeObservableSubscription;
import rx.util.functions.Func2;

public class HandlerThreadScheduler extends Scheduler {
    private final Handler handler;

    public HandlerThreadScheduler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public <T> Subscription schedule(T state, Func2<? super Scheduler, ? super T, ? extends Subscription> action) {
        return schedule(state, action, 0L, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> Subscription schedule(final T state, final Func2<? super Scheduler, ? super T, ? extends Subscription> action, long delayTime, TimeUnit unit) {
        final SafeObservableSubscription subscription = new SafeObservableSubscription();
        final Scheduler _scheduler = this;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                subscription.wrap(action.call(_scheduler, state));
            }
        }, unit.toMillis(delayTime));
        return subscription;
    }
}

