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

package com.basicstructurewithmvp.rest;

import android.content.Intent;
import android.util.Base64;

import com.basicstructurewithmvp.activities.LoginActivity;
import com.basicstructurewithmvp.application.BaseApplication;
import com.basicstructurewithmvp.constants.AppConstants;
import com.basicstructurewithmvp.utils.MyPrefs;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by Darshna Desai
 */
 /* Helper class that sets up a new services */
public class RestClient {

  private static final int TIME = 120;
  private static final String TAG = RestClient.class.getSimpleName();
  private static RestService restService;
  private static String CREDENTIALS = "admin:admin@123";
  private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE);


  private static OkHttpClient httpClient = new OkHttpClient().newBuilder()
          .connectTimeout(TIME, TimeUnit.SECONDS)
          .readTimeout(TIME, TimeUnit.SECONDS)
          .writeTimeout(TIME, TimeUnit.SECONDS)
          .addInterceptor(logging)
          .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
              Request original = chain.request();
              final String basic =
                      "Basic " + Base64.encodeToString(CREDENTIALS.getBytes(), Base64.NO_WRAP);
              Request.Builder requestBuilder = original.newBuilder().header("Authorization", basic);
              requestBuilder.header("Accept", "application/json");
              requestBuilder.method(original.method(), original.body());
                           /* .header("Authorization",
                                    RestConstant.AUTHHORIZATION); // <-- this is the important line*/

              Request request = requestBuilder.build();
              Response response = chain.proceed(request);

              if (response.isSuccessful()) {
                String data = response.body().string();
                try {
                  JSONObject jsonObject = new JSONObject(data);
                  if (jsonObject.has("authentication") && !jsonObject.optBoolean("authentication")) {
                    logout();
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
                return response.newBuilder().body(ResponseBody.create(response.body().contentType(), data)).build();
              }
              return response;

            }
          })
          .build();

  private static OkHttpClient httpLogoutClient = new OkHttpClient().newBuilder()
          .connectTimeout(TIME, TimeUnit.SECONDS)
          .readTimeout(TIME, TimeUnit.SECONDS)
          .writeTimeout(TIME, TimeUnit.SECONDS)
          .addInterceptor(logging)
          .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
              Request original = chain.request();
              Request.Builder requestBuilder = original.newBuilder();
                           /* .header("Authorization",
                                    RestConstant.AUTHHORIZATION); // <-- this is the important line*/

              Request request = requestBuilder.build();
              Response response = chain.proceed(request);
              if (response.isSuccessful()) {
                String data = response.body().string();

                return response.newBuilder().body(ResponseBody.create(response.body().contentType(), data)).build();
              }
              return response;
            }
          })
          .build();

  private static OkHttpClient httpClientOther = new OkHttpClient().newBuilder()
          .connectTimeout(TIME, TimeUnit.SECONDS)
          .readTimeout(TIME, TimeUnit.SECONDS)
          .writeTimeout(TIME, TimeUnit.SECONDS)
          .addInterceptor(logging)
          .build();

  private static OkHttpClient httpUploadClient = new OkHttpClient().newBuilder()
          .connectTimeout(TIME, TimeUnit.SECONDS)
          .readTimeout(TIME, TimeUnit.SECONDS)
          .writeTimeout(TIME, TimeUnit.SECONDS)
          .build();

  private static void logout() {
    MyPrefs.getInstance(BaseApplication.getBaseApplication()).clearAllPrefs();
    Intent intent = new Intent(BaseApplication.getBaseApplication(), LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    BaseApplication.getBaseApplication().startActivity(intent);
  }

  public static <S> S createService(
          Class<S> serviceClass, String username, String password) {
    String authToken = Credentials.basic(username, password);
    return createService(serviceClass, "admin", "admin@123");
  }

  public static RestService getPrimaryService() {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(AppConstants.API_BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(new ToStringConverterFactory())
            .client(httpClient)
            .build();
    return retrofit.create(RestService.class);
  }

  public static RestService getService() {
    if (restService == null) {
      Retrofit retrofit = new Retrofit.Builder().baseUrl(AppConstants.API_BASE_URL)
              .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
              .addConverterFactory(new ToStringConverterFactory())
              .client(httpLogoutClient)
              .build();
      restService = retrofit.create(RestService.class);
    }
    return restService;
  }

  public static RestService getService(String baseUrl) {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(new ToStringConverterFactory())
            .client(httpClientOther)
            .build();
    return retrofit.create(RestService.class);
  }

  public static OkHttpClient getHttpDownloadClient(final String id, final ProgressRequestBody.UploadCallbacks progressListener) {
    OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    httpClientBuilder.connectTimeout(TIME, TimeUnit.SECONDS);
    httpClientBuilder.writeTimeout(TIME, TimeUnit.SECONDS);
    httpClientBuilder.readTimeout(TIME, TimeUnit.MINUTES);

    httpClientBuilder.addInterceptor(new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(), id, progressListener))
                .build();
      }
    });

    return httpClientBuilder.build();
  }

}
