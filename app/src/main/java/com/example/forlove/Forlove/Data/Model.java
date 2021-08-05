package com.example.forlove.Forlove.Data;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.forlove.Constant.MyLog;
import com.example.forlove.Forlove.UploadResponse;
import com.example.forlove.Forlove.ViewModel.LoginViewModel;
import com.example.forlove.Forlove.ViewModel.MainViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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


    public MutableLiveData<Integer> uploadResponse = new MutableLiveData<>();

    private static volatile Model instance;
    private Retrofit retrofit;

    private Model() {
        init();
    }

    public static Model getInstance() {
        if (instance == null) {
            synchronized (Model.class) {
                if (instance == null)
                    instance = new Model();
            }
        }
        return instance;
    }

    /**
     * 提前连接上服务器
     */
    public void init() {
        MyLog.i("init...");
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://1.116.217.171")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        MyLog.i("init over");
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
        Call<Void> upload(@PartMap Map<String, String> map, @Part MultipartBody.Part file);

        @Multipart
        @POST("/upload/")
        Call<String> uploadFile(@Part MultipartBody.Part file);

    }

    public void uploadFile(String account, String path, UploadResponse api) throws IOException {
        if (path == null) {
            Log.d("filepath", "null");
        } else
            Log.d("filepath", path);
        File file = new File(path);
        Log.d("uploadthread", Thread.currentThread().toString());
        RequestBody body = RequestBody.create(MediaType.parse("*/*"), file);
        ProgressRequestBody requestBody = new ProgressRequestBody(file, "*/*", api, body);
        ApiService request = retrofit.create(ApiService.class);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
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

    public void upload(String account, String path, UploadResponse api) throws IOException {
        MyLog.i("upload");
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        File file = new File(path);
        RequestBody body = RequestBody.create(MediaType.parse("*/*"), file);

        ApiService request = retrofit.create(ApiService.class);


        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
        Map<String, String> map = new HashMap<>();

        map.put("account", account);
        Call<Void> call = request.upload(map, part);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                MyLog.i("upload succeed");
                api.succeed();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                MyLog.e("upload fail: " + t.getMessage());
                MyLog.e("upload fail: " + t.getCause());
                t.printStackTrace();
                api.failed();
            }
        });
    }


    public void getUser(String account, LoginViewModel.getOrsetUser api) {
        MyLog.i("getUser");
        ApiService request = retrofit.create(ApiService.class);
        Call<User> call = request.checkUser(account);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                MyLog.i("get user success");
                api.getuser(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                MyLog.e("get user fail: "+t.getMessage());
                MyLog.e("get user fail: " + t.getCause());
                api.NoInternet();
            }
        });
    }

    public void setUser(String account, String password, LoginViewModel.getOrsetUser api) {
        MyLog.i("setUser");
        ApiService request = retrofit.create(ApiService.class);
        Map<String, String> user = new HashMap<>();
        user.put("account", account);
        user.put("password", password);
        JSONObject jsonObj = new JSONObject(user);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObj));

        Call<User> call = request.addUser(body);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                MyLog.i("setUser success");
                api.setuser(response.body().getResult());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                MyLog.e("setUser fail: " + t.getMessage());
                MyLog.e("setUser fail: " + t.getCause());
                api.NoInternet();
            }
        });
    }


    public void getFileList(String account, MainViewModel.SetFileList api) {

        ApiService request = retrofit.create(ApiService.class);
        Call<Filedata> call = request.checkFile(account);

        call.enqueue(new Callback<Filedata>() {
            @Override
            public void onResponse(Call<Filedata> call, Response<Filedata> response) {
                ArrayList arrayList = response.body().getFileList();
                MyLog.i("success");
                //System.out.println(arrayList.get(0).getClass());
                api.setFileList(arrayList);

            }

            @Override
            public void onFailure(Call<Filedata> call, Throwable t) {
                MyLog.e("getFileList fail: " + t.getMessage());
                MyLog.e("getFileList fail: " + t.getCause());
            }
        });
    }
}
