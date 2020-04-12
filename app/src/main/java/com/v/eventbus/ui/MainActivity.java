package com.v.eventbus.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.v.eventbus.EventBus;
import com.v.eventbus.OnSubscribe;
import com.v.eventbus.R;
import com.v.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        tvContent = findViewById(R.id.tv_content);
    }

    public void entry(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

    @OnSubscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(String s) {
        tvContent.setText(s);
        Toast.makeText(this, "消息内容：" + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregisger(this);
    }
}
