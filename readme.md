# 广播




### 注册广播

**1.动态注册：在代码中注册**

  步骤：
  1-1 创建IntentFilter，使用addAction()添加广播，广播接收器想要监听什么样的广播就在这里添加相应的action
  1-2 创建Receiver实例，继承自BroadcastReceiver，重写onReceive
  1-3 registerReceiver(BroadcastReceiver receiver, IntentFilter intentFilter);//动态注册
  1-4 在onDestroy中 unregisterReceiver(BroadcastReceiver receiver)
  缺点：
  程序启动后才能收到广播

```Java
public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //动态注册
        intentFilter = new IntentFilter();
    	intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//网络状态发生变化时，系统发出广播"android.net.conn.CONNECTIVITY_CHANGE"
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);//动态注册
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
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
}
```

**2.静态注册：在AndroidManifest.xml中注册**

```xml
         <receiver
            android:name=".BootCompleteReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
```

### 发送自定义广播

**1.标准广播**

标准广播是完全异步执行的广播，在广播发出后，所有的广播接收器几乎会在同一时刻接收到这条广播，他们之间没有任何先后顺序可言。

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //发送自定义广播
        Button button = this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("com.example.broadcasttest.MY_BROADCAST");
                sendBroadcast(intent);
            }
        });
    }
    
//广播接收器
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "received in MyBroadcastReceiver", Toast.LENGTH_SHORT).show();
    }
}
    
```

```xml
<receiver android:name=".MyBroadcastReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="com.example.broadcasttest.MY_BROADCAST"/>
    </intent-filter>
</receiver>
```



**2.有序广播**

有序广播是异步执行的广播，广播发出后，同一时刻只有一个广播接收器能够收到这条广播消息，当这个广播接收器中的逻辑执行完毕后，广播才会继续传递。

```Java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //发送自定义广播
    	Button button = this.findViewById(R.id.button);
    	button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setAction("com.example.broadcasttest.MY_BROADCAST");
            sendOrderedBroadcast(intent, null);
        }
    });
}
//广播接收器
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "received in MyBroadcastReceiver", Toast.LENGTH_SHORT).show();
    }
}
//广播接收器（另一个程序中的）
public class AnotherBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "received in AnotherBroadcastReceiver", Toast.LENGTH_SHORT).show();
        abortBroadcast();//截断广播
    }
}

```



```xml
<receiver
    android:name=".MyBroadcastReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="com.example.broadcasttest.MY_BROADCAST" />
    </intent-filter>
</receiver>

<!--另一个程序中-->
<receiver
    android:name=".AnotherBroadcastReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter android:priority="100"> <!--优先级高的先收到广播-->
        <action android:name="com.example.broadcasttest.MY_BROADCAST"/>
    </intent-filter>
</receiver>
```



### 使用本地广播

**系统全局广播**：发出的广播可以被其他任何程序接收到，并且我们也可以接收来自于其他任何应用程序的广播。

**本地广播**：发出的广播只能在应用程序内部进行传递，并且广播接收器也只能接收来自本应用程序发出的广播。本地广播无法通过静态注册的方式来接收的。

```Java
public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;

    private LocalReceiver localReceiver;

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
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
    
    private class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "received local broadcast", Toast.LENGTH_SHORT).show();
        }
    }
}
```
