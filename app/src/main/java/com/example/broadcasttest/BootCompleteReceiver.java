package com.example.broadcasttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 广播接收器不允许开启线程，当onReceive运行较长时间而没有结束，系统就会报错
 * 广播接收器扮演一种打开程序其他组件的角色，比如创建一条状态栏通知，或者启动一个服务等
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "boot complete", Toast.LENGTH_SHORT).show();
    }
}