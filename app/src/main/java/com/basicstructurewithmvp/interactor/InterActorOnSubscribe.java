package com.basicstructurewithmvp.interactor;


import rx.functions.Action0;

/**
 * Created by Darshna Desai
 * This class is used for indicating InterActor process has been
 * started.
 */

class InterActorOnSubscribe<T> implements Action0 {

  private InterActorCallback<T> mInterActorCallback;

  InterActorOnSubscribe(InterActorCallback<T> mInterActorCallback) {
    this.mInterActorCallback = mInterActorCallback;
  }

  @Override
  public void call() {
    mInterActorCallback.onStart();
  }
}
