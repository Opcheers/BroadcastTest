package com.example.broadcasttest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 *注册广播：
 * 1.动态注册：在代码中注册
 *  步骤：
 *  1-1 创建IntentFilter，使用addAction()添加广播，广播接收器想要监听什么样的广播就在这里添加相应的action
 *  1-2 创建Receiver实例，继承自BroadcastReceiver，重写onReceive
 *  1-3 registerReceiver(BroadcastReceiver receiver, IntentFilter intentFilter);//动态注册
 *  1-4 在onDestroy中 unregisterReceiver(BroadcastReceiver receiver)
 *  缺点：
 *  程序启动后才能收到广播
 * 2.静态注册：在AndroidManifest.xml中注册
 */
public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;

    private LocalReceiver localReceiver;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);//获取本地广播管理器实例
        Button button = this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("com.example.broadcasttest.LOCAL_BROADCAST");
                localBroadcastManager.sendBroadcast(intent);//发送本地广播
            }
        });

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.broadcasttest.LOCAL_BROADCAST");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);//注册本地广播监听器


        //动态注册
        /*
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//网络状态发生变化时，系统发出广播"android.net.conn.CONNECTIVITY_CHANGE"
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);//动态注册
         */

        //发送自定义广播
        /*
        Button button = this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("com.example.broadcasttest.MY_BROADCAST");
                sendOrderedBroadcast(intent, null);
            }
        });
         */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo!=null && networkInfo.isAvailable()) {
                Toast.makeText(context, "Network is available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Network is unavailable", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "received local broadcast", Toast.LENGTH_SHORT).show();
        }
    }
}