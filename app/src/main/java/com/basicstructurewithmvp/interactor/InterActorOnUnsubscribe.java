package com.basicstructurewithmvp.interactor;


import com.basicstructurewithmvp.models.Response;

import rx.Subscription;
import rx.functions.Action0;

/**
 * This class is used for indicating InterActor process has been
 * started.
 */

class InterActorOnUnsubscribe<T extends Response> implements Action0 {

    private InterActorCallback<T> mInterActorCallback;
    private Subscription subscription;

    InterActorOnUnsubscribe(InterActorCallback<T> mInterActorCallback, Subscription subscription) {
        this.mInterActorCallback = mInterActorCallback;
        this.subscription = subscription;
    }

    @Override
    public void call() {

    }
}
