/*
 * Copyright (c) 2016.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.basicstructurewithmvp.baseclasses;

import com.basicstructurewithmvp.interactor.AppInteractor;

import java.util.HashMap;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Darshna Desai
 */
public abstract class BasePresenter<V extends BaseView> {

    protected AppInteractor appInteractor;
    private V view;
    private CompositeSubscription subscription = new CompositeSubscription();

    final void attachView(V view) {
        this.view = view;
    }

    final void detachView() {
        this.view = null;
        if (subscription != null) {
            subscription.clear();
        }

    }

    public V getView() {
        return view;
    }

    public boolean hasInternet() {
        return view.hasInternet();
    }

    public boolean isViewAttached() {
        return view != null;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before requesting data to the Presenter");
        }
    }

    protected void addSubscription(Subscription s) {
        subscription.add(s);
    }

    //This method is for adding subscription with key
    protected void addSubscription(Subscription subscription, String key, boolean removePrevious) {
        /*if (removePrevious && subscriptionHashMap.containsKey(key)) {
            Subscription foundSubscription = subscriptionHashMap.get(key);
            if (foundSubscription != null && !foundSubscription.isUnsubscribed()) {
                foundSubscription.unsubscribe();
                subscriptionHashMap.remove(key);
            }
        }
        subscriptionHashMap.put(key, subscription);*/
    }

    protected final AppInteractor getAppInteractor() {
        if (appInteractor == null) {
            appInteractor = new AppInteractor();
        }
        return appInteractor;
    }

    public void callLogoutApi(HashMap<String, String> logoutParams) {
        /*Call your logout api here, given the demo code below just for your reference */
        /*new AppInteractor().callLogoutApi(logoutParams, new InterActorCallback<Response>() {
          @Override
          public void onStart() {
            getView().showProgressDialog(true);
          }

          @Override
          public void onResponse(Response response) {
            if (response.isStatus()) {
              if(response.isAuthentication()) getView().onSuccess(response);
              else getView().onAuthenticationFailure(response.getMessage());
            } else {
              getView().onFailure(response.getMessage());
            }
          }
          @Override
          public void onFinish() {
            getView().showProgressDialog(false);
          }

          @Override
          public void onError(String message) {
            getView().onFailure(message);
          }
        });*/
    }
}