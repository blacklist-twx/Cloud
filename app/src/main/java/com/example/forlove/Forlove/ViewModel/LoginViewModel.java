package com.example.forlove.Forlove.ViewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.forlove.Forlove.Data.Model;
import com.example.forlove.Forlove.Data.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private String account;
    private String password;
    private User m_user;
    private MutableLiveData<Integer> status;//登录的响应结果
    private Model datamodel=Model.getInstance();
    private ExecutorService threadpool= Executors.newFixedThreadPool(2);

    public String getAccount() {
        return account;
    }

    public MutableLiveData<Integer> getStatus() {
        if (status == null) {
            status = new MutableLiveData<Integer>();
        }
        return status;
    }
    public interface getOrsetUser{
        void getuser(User user);
        void setuser(int status);
        void NoInternet();
    }

    public void login(String account,String password){
        this.account = account;
        this.password=password;
        threadpool.execute(new Runnable() {
            @Override
            public void run() {
                datamodel.getUser(account, new getOrsetUser() {
                    @Override
                    public void getuser(User user) {
                        threadpool.execute(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("thread","get User:Current Thread: "+Thread.currentThread());
                                m_user = user;
                                if(m_user==null)
                                    return;
                                if(m_user.getResult()==-1)
                                    getStatus().postValue(-1);
                                else
                                if(!m_user.getPassword().equals(password))
                                {
                                    getStatus().postValue(-2);
                                    System.out.println("我的密码"+password);
                                    System.out.println("正确密码"+m_user.getPassword());
                                }
                                else
                                    getStatus().postValue(1);//登陆成功
                            }
                        });

                    }
                    @Override
                    public void setuser(int status) {
                    }

                    @Override
                    public void NoInternet() {
                        getStatus().setValue(-4);
                    }
                });
            }
        });

    }

    public void register(String account,String password){

        this.account = account;
        this.password = password;
        threadpool.execute(new Runnable() {
            @Override
            public void run() {
                getOrsetUser g = new getOrsetUser() {
                    @Override
                    public void getuser(User user) {
                    }
                    @Override
                    public void setuser(int status) {
                        if(status==-1)
                            getStatus().postValue(-3);
                        else
                        {
                            getStatus().postValue(3);
                        }
                    }
                    @Override
                    public void NoInternet() {
                        getStatus().postValue(-4);
                    }
                };
                datamodel.setUser(account,password,g);
            }
        });

    }
}