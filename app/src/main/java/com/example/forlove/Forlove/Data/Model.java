package com.example.forlove.Forlove.Data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.forlove.Forlove.UploadResponse;
import com.example.forlove.Forlove.ViewModel.LoginViewModel;
import com.example.forlove.Forlove.ViewModel.MainViewModel;
import com.example.forlove.data.Result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public class Model {


    public MutableLiveData<Integer> uploadResponse=new MutableLiveData<>();

    private static volatile Model instance;
    private Retrofit retrofit;
    private Model(){init(); }
    public static Model getInstance() {
        if (instance == null) {
            synchronized (Model.class){
                if (instance==null)
                    instance = new Model();
            }
        }
        return instance;
    }
    /**
     * 提前连接上服务器
     */
    public void init(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://www.xiaowen520.xyz")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    public interface ApiService {
        @POST("/add2/")
        Call<User> addUser(@Body RequestBody user);
        @GET("/check_account/{account}")
        Call<User> checkUser(@Path("account") String account);
        @GET("/check_file2/{account}")
        Call<Filedata> checkFile(@Path("account") String account);
        //多文件上传
        @Multipart
        @POST("/upload/")
        Call<Void> upload(@PartMap Map<String,String> map, @Part MultipartBody.Part file);
        @Multipart
        @POST("/upload/")
        Call<String> uploadFile(@Part MultipartBody.Part file);

    }

    public void uploadFile(String account,String path , UploadResponse api)throws IOException{
        if(path==null)
        {
            Log.d("filepath","null");
        }
        else
            Log.d("filepath",path);
        File file = new File(path);
        Log.d("uploadthread",Thread.currentThread().toString());
        RequestBody  body=RequestBody.create(MediaType.parse("*/*"),file);
        ProgressRequestBody requestBody = new ProgressRequestBody(file,"*/*",api,body);
        ApiService request = retrofit.create(ApiService.class);
        MultipartBody.Part part =MultipartBody.Part.createFormData("file",file.getName(),requestBody);
        Call<String> call = request.uploadFile(part);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("response", "上传成功");
                api.succeed();
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("response", "上传失败" + t.getMessage());
                Log.i("response", "上传失败原因" + t.getCause());
                t.printStackTrace();
                api.failed();
            }
        });
    }

    public void upload(String account,String path , UploadResponse api) throws IOException {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        File file = new File(path);
        RequestBody  body=RequestBody.create(MediaType.parse("*/*"),file);
//        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS);
//        ProgressRequestBody body1 = new ProgressRequestBody(file,"*/*",api,body);
//        retrofit=new Retrofit.Builder()
//                .baseUrl("https://www.xiaowen520.xyz")
//                .client(builder.build())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
        ApiService request = retrofit.create(ApiService.class);


        MultipartBody.Part part =MultipartBody.Part.createFormData("file",file.getName(),body);
        Map<String, String> map = new HashMap<>();

        map.put("account",account);
        Call<Void> call = request.upload(map,part);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("response","上传成功");
                api.succeed();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i("response","上传失败"+t.getMessage());
                Log.i("response","上传失败原因"+t.getCause());
                t.printStackTrace();

                api.failed();
            }
        });
    }



    public void getUser(String account, LoginViewModel.getOrsetUser api){
        ApiService request = retrofit.create(ApiService.class);
        Call<User> call = request.checkUser(account);
        Log.i("thread","get User On getUser:Current Thread: "+Thread.currentThread());

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                System.out.println("查找结果"+ response.body().getResult());
                Log.i("thread","get User On Response:Current Thread: "+Thread.currentThread());
                api.getuser(response.body());
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("请求失败"+call.request());
                System.out.println(t.getMessage());
                api.NoInternet();
            }
        });
    }
    public void setUser(String account,String password,LoginViewModel.getOrsetUser api){
        ApiService request = retrofit.create(ApiService.class);
        Map<String,String> user = new HashMap<>();
        user.put("account",account);
        user.put("password",password);
        JSONObject jsonObj = new JSONObject(user);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObj));
        System.out.println(String.valueOf(jsonObj));
        Call<User> call = request.addUser(body);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                api.setuser(response.body().getResult());
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("请求失败"+call.request());
                System.out.println(t.getMessage());
                api.NoInternet();
            }
        });
    }


    public void getFileList(String account, MainViewModel.SetFileList api){

        ApiService request = retrofit.create(ApiService.class);
        Call<Filedata> call = request.checkFile(account);
        Log.i("thread","get User On getUser:Current Thread: "+Thread.currentThread());
        call.enqueue(new Callback<Filedata>() {
            @Override
            public void onResponse(Call<Filedata> call, Response<Filedata> response) {
                ArrayList arrayList = response.body().getFileList();
                Log.i("filelist","filelist: "+arrayList);
                //System.out.println(arrayList.get(0).getClass());
                api.setFileList(arrayList);

            }
            @Override
            public void onFailure(Call<Filedata> call, Throwable t) {
                System.out.println("请求失败"+call.request());
                System.out.println(t.getMessage());
            }
        });
    }
}
