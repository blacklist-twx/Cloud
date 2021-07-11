package com.example.forlove.Forlove.Data;



import com.example.forlove.Forlove.UploadResponse;

import java.io.File;
import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Cmad on 2016/4/28.
 */
public class HttpClientHelper {

//    /**
//     * 包装OkHttpClient，用于下载文件的回调
//     * @param progressListener 进度回调接口
//     * @return 包装后的OkHttpClient
//     */
//    public static OkHttpClient addProgressResponseListener(final Callback progressListener){
//        OkHttpClient.Builder client = new OkHttpClient.Builder();
//        //增加拦截器
//        client.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                //拦截
//                Response originalResponse = chain.proceed(chain.request());
//
//                //包装响应体并返回
//                return originalResponse.newBuilder()
//                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
//                        .build();
//            }
//        });
//        return client.build();
//    }

    /**
     * 包装OkHttpClient，用于上传文件的回调
     *
     * @param progressListener 进度回调接口
     * @return 包装后的OkHttpClient
     */
    public static OkHttpClient addProgressRequestListener(final UploadResponse progressListener, File file, String type, ProgressRequestBody requestBody) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();

        //增加拦截器
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .method(original.method(), new ProgressRequestBody(file, type, progressListener, requestBody))
                        .build();
                return chain.proceed(request);
            }
        });
        return client.build();
    }
}
