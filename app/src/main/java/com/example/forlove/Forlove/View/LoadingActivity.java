package com.example.forlove.Forlove.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.forlove.R;

public class LoadingActivity extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView;

    public static MyLoginFragment newInstance() {
        return new MyLoginFragment();
    }

    private Handler handler = new Handler();


    public void setLottieAnimationView() {
        //设置动画文件
        lottieAnimationView.setAnimation("login.json");
        //是否循环执行
        lottieAnimationView.loop(true);
        //执行动画
        lottieAnimationView.playAnimation();
    }
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        onDestroy();
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全面屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);
        lottieAnimationView=findViewById(R.id.lottie_layer_name);
        setLottieAnimationView();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("loginToken",0);
                String account = sp.getString("account",null);
                if(account==null) {
                    Intent intent = new Intent();
                    intent.setClass(LoadingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                Intent intent = new Intent();
                intent.putExtra("account",account);
                intent.setClass(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        }, 2000);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}