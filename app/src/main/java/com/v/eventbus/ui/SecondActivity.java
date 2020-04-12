package com.v.eventbus.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.v.eventbus.EventBus;
import com.v.eventbus.R;

public class SecondActivity extends AppCompatActivity {
    private static InnerClass mInner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        if (mInner == null) {
            mInner = new InnerClass();
        }
        EventBus.getDefault().postMessage("123");
    }

    //内部类
    public static class InnerClass {

    }

    public void sendMessage(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().postMessage("123");
            }
        }).start();
    }
}
