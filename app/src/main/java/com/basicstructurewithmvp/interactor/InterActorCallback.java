package com.basicstructurewithmvp.interactor;


/**
 * Created by Darshna Desai
 */

public interface InterActorCallback<T> {

  public void onStart();

  public void onResponse(T response);

  public void onFinish();

  public void onError(String message);

}
