package com.basicstructurewithmvp.rest;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Darshna Desai
 */
public interface RestService {

    @FormUrlEncoded
    @POST
    Observable<String> apiPost(@Url String url, @FieldMap HashMap<String, String> params);

    @Multipart
    @POST
    Observable<String> apiMultipartPost(@Url String url, @PartMap HashMap<String, RequestBody> params, @Part MultipartBody.Part body);

    @Streaming
    @GET
    Observable<ResponseBody> downloadData(@Url String fileUrl);
}


