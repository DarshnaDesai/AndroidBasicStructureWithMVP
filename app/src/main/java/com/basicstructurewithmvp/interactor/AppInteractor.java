package com.basicstructurewithmvp.interactor;

import com.google.gson.Gson;
import com.basicstructurewithmvp.constants.ApiRequestUrlEnum;
import com.basicstructurewithmvp.constants.AppConstants;
import com.basicstructurewithmvp.models.LoginResponse;
import com.basicstructurewithmvp.models.Response;
import com.basicstructurewithmvp.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.basicstructurewithmvp.rest.RestClient.getPrimaryService;
import static com.basicstructurewithmvp.rest.RestClient.getService;

/**
 * Created by Darshna Desai
 */
public class AppInteractor {

    public boolean isCancel;

    public AppInteractor() {

    }

    private void sendResponse(InterActorCallback callback, Response response) {
        if (!isCancel) {
            callback.onResponse(response);
        }
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void cancel() {
        isCancel = true;
    }

    private void displayRequestParams(HashMap<String, String> hashMap) {
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            AppUtils.logD((String) pair.getKey(), (String) pair.getValue());
        }
    }

    /*An example of calling simple post api*/
    public Subscription callLoginApi(final HashMap<String, String> params,
                                     final InterActorCallback<LoginResponse> callback) {

        return getPrimaryService().apiPost(ApiRequestUrlEnum.USER_LOGIN.getValue(), params)
                .map(new Func1<String, LoginResponse>() {
                    @Override
                    public LoginResponse call(String s) {
                        displayRequestParams(params);
                        AppUtils.logD("callLoginApi", "" + s);
                        return new Gson().fromJson(s, LoginResponse.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new InterActorOnSubscribe<>(callback))
                .subscribe(new InterActorSubscriber<LoginResponse>(callback, this) {
                    @Override
                    public void onNext(LoginResponse response) {
                        sendResponse(callback, response);
                    }
                });
    }

    /*An example of calling multipart api*/
    public Subscription callUploadProfilePicApi(final HashMap<String, RequestBody> params, MultipartBody.Part body,
                                                final InterActorCallback<LoginResponse> callback) {

        return getPrimaryService().apiMultipartPost(ApiRequestUrlEnum.USER_LOGIN.getValue(), params, body)
                .map(new Func1<String, LoginResponse>() {
                    @Override
                    public LoginResponse call(String s) {
                        AppUtils.logD("callUploadProfilePicApi", "" + s);
                        return new Gson().fromJson(s, LoginResponse.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new InterActorOnSubscribe<>(callback))
                .subscribe(new InterActorSubscriber<LoginResponse>(callback, this) {
                    @Override
                    public void onNext(LoginResponse response) {
                        sendResponse(callback, response);
                    }
                });
    }

    /*This function is used to download any type of file from url*/
    public Subscription downloadFile(final String url, final InterActorCallback<File> callback) {
        callback.onStart();
        return getService(AppConstants.API_BASE_URL).downloadData(AppConstants.API_BASE_URL + url).map(new Func1<ResponseBody, File>() {
            @Override
            public File call(ResponseBody responseBody) {
                  File path = AppUtils.getWorkingDirectory();
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + url);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(responseBody.bytes());
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return file;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new InterActorOnSubscribe<>(callback))
                .subscribe(new InterActorSubscriber<File>(callback, this) {

                @Override
                public void onNext(File file) {
                    callback.onResponse(file);
                }
        });

    }

}

