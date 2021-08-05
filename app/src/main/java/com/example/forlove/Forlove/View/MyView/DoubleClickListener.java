package com.example.forlove.Forlove.View.MyView;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.forlove.Forlove.View.Interface.ClickCallBack;



public class DoubleClickListener implements View.OnClickListener,View.OnLongClickListener {
    private volatile int time = 0;
    private ClickCallBack clickCallBack;
    private Runnable runnable;
    private Handler handler;

    public DoubleClickListener(ClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
        this.handler = new Handler();
        this.runnable = new Runnable() {
            @Override
            public void run() {
                if (time == 1) {
                    time = 0;
                    handler.removeCallbacksAndMessages(null);
                    clickCallBack.onClick();
                } else if (time == 2) {
                    time = 0;
                    handler.removeCallbacksAndMessages(null);
                    clickCallBack.onDoubleClick();
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        time++;
        handler.postDelayed(this.runnable, 200);
    }

    @Override
    public boolean onLongClick(View view) {
        clickCallBack.onLongClick();
        return false;
    }
}
