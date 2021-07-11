package com.example.forlove.Forlove.Data;

public class User {
    private int result;
    private String account;
    private String password;

//    private String nickname;
//    private String gender;
//    private String saying;

    public User(String account,String password){
        this.account = account;
        this.password = password;
//        this.nickname=null;
//        this.gender = null;
//        this.saying = null;
    }

    public int getResult() {
        return result;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }



    //定义 输出返回数据 的方法
    public void show() {
        System.out.println(account);
        System.out.println(password);
//        System.out.println(nickname);
//        System.out.println(gender);
//        System.out.println(saying);

    }
}

