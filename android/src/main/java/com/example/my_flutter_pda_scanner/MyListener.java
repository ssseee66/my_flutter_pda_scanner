package com.example.my_flutter_pda_scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;

public class MyListener {
    // Flutter端与Android端通讯的通道
    private BasicMessageChannel<Object> messageChannel;
    // Android端发送给Flutter端的消息Map
    private final Map<String, Object> messageMap = new HashMap<>();
    // Flutter端发送过来的消息Map
    private Map<String, Object> arguments;
    // 动作映射Map,将各个动作对应的方法的引用存放在Map中以减少if分支
    private final Map<String, Consumer<String>> actionMap = new HashMap<>();
    // 应用上下文
    private final Context applicationContext;
    // 广播接收器
    private BroadcastReceiver broadcastReceiver;
    // 广播动作
    private String BROADCAST_ACTION;
    // 广播数据标签列表
    private final List<String> BROADCAST_DATA_TAGS = new ArrayList<>();
    // 数据处理完毕标志
    private boolean DISPOSE_OVER = false;

    MyListener(String chanelName, Context applicationContext, BinaryMessenger binaryMessenger) {
        messageChannel = new BasicMessageChannel<>(
                binaryMessenger,
                chanelName,
                StandardMessageCodec.INSTANCE
        );
        this.applicationContext = applicationContext;

        setAction_map();

        messageChannel.setMessageHandler((message, reply) -> {
            arguments = castMap(message, String.class, Object.class);
            if (arguments == null) return;
            String key = getCurrentKey();
            Log.i("currentKey", key);
            Log.i("action", actionMap.keySet().toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Objects.requireNonNull(actionMap.get(key)).accept(key);
            }
        });
    }

    private String getCurrentKey() {
        String key = null;
        if (arguments.containsKey("setAction"))                  key = "setAction";
        else if (arguments.containsKey("setDataTag"))            key = "setDataTag";
        else if (arguments.containsKey("addDataTag"))            key = "addDataTag";
        else if (arguments.containsKey("startListener"))         key = "startListener";
        else if (arguments.containsKey("stopListener"))          key = "stopListener";
        else if (arguments.containsKey("disposeOver"))           key = "disposeOver";
        else if (arguments.containsKey("destroy"))               key = "destroy";
        return key;
    }
    private void setAction_map() {
        actionMap.put("setAction",           this :: setAction);
        actionMap.put("setDataTag",          this :: setDataTag);
        actionMap.put("addDataTag",          this :: addDataTag);
        actionMap.put("startListener",       this :: startListener);
        actionMap.put("stopListener",        this :: stopListener);
        actionMap.put("disposeOver",         this :: disposeOver);
        actionMap.put("destroy",             this :: destroy);
    }
    private void setAction(String key) {
        String value = (String) arguments.get(key);
        if (value == null) return;
        Log.i("broadcastActionInfo", value);
        BROADCAST_ACTION = value;
        messageMap.clear();
        messageMap.put("message", "The broadcast action was set successfully");
        messageMap.put("isSuccessful", true);
        messageMap.put("operationCode", 0);
        messageChannel.send(messageMap);
    }
    private void setDataTag(String key) {
        List<String> dataTags = castList(arguments.get(key), String.class);
        if (dataTags == null) return;
        BROADCAST_DATA_TAGS.addAll(dataTags);
        messageMap.clear();
        messageMap.put("message", "The broadcast data tags was set successfully");
        messageMap.put("isSuccessful", true);
        messageMap.put("operationCode", 1);
        messageChannel.send(messageMap);
    }
    private void addDataTag(String key) {
        String dataTag = (String) arguments.get(key);
        if (dataTag == null) return;
        BROADCAST_DATA_TAGS.add(dataTag);
        messageMap.clear();
        messageMap.put("message", "The broadcast data tag was add successfully");
        messageMap.put("isSuccessful", true);
        messageMap.put("operationCode", 2);
        messageChannel.send(messageMap);
    }
    private void startListener(String key) {
        Object value = arguments.get(key);
        if (value == null) return;
        if (!(boolean) value) return;
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DISPOSE_OVER) {
                    Map<String, String> pdaCodeMap = new HashMap<>();
                    for (String dataTag : BROADCAST_DATA_TAGS) {
                        String code = intent.getStringExtra(dataTag);
                        pdaCodeMap.put(dataTag, code);
                    }
                    messageMap.clear();
                    messageMap.put("message", pdaCodeMap);
                    messageMap.put("isSuccessful", true);
                    messageMap.put("operationCode", 5);
                    messageChannel.send(messageMap);
                    DISPOSE_OVER = false;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION);
        applicationContext.registerReceiver(
                broadcastReceiver, filter);
        DISPOSE_OVER = true;
        messageMap.clear();
        messageMap.put("message", "Start listener broadcast");
        messageMap.put("isSuccessful", true);
        messageMap.put("operationCode", 3);
        messageChannel.send(messageMap);
    }
    private void stopListener(String key) {
        Object value = arguments.get(key);
        if (value == null) return;
        if (!(boolean) value) return;
        if (broadcastReceiver == null) {
            Log.e("broadcastInfo", "Broadcasting has not been registered yet");
            return;
        }
        applicationContext.unregisterReceiver(broadcastReceiver);
        messageMap.clear();
        messageMap.put("message", "Stop listener broadcast");
        messageMap.put("isSuccessful", true);
        messageMap.put("operationCode", 4);
        messageChannel.send(messageMap);
    }
    private void disposeOver(String key) {
        Object value = arguments.get(key);
        if (value == null) return;
        if (!(boolean) value) return;
        DISPOSE_OVER = true;
        messageMap.clear();
        messageMap.put("message", "Data dispose over");
        messageMap.put("isSuccessful", true);
        messageMap.put("operationCode", 6);
        messageChannel.send(messageMap);
    }
    private void destroy(String key) {
        Object value = arguments.get(key);
        if (value == null) return;
        if (!(boolean) value) return;
        messageChannel = null;
        if (broadcastReceiver != null)
            applicationContext.unregisterReceiver(broadcastReceiver);
    }
    public static <K, V> Map<K, V> castMap(Object obj, Class<K> key, Class<V> value) {
        Map<K, V> map = new HashMap<>();
        if (obj instanceof Map<?, ?>) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                map.put(key.cast(entry.getKey()), value.cast(entry.getValue()));
            }
            return map;
        }
        return null;
    }
    private <V> List<V> castList(Object obj, Class<V> value) {
        /*
        对对象转换为List类型作出检查
         */
        List<V> list = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>)obj) {
                list.add(value.cast(o));
            }
            return list;
        }
        return null;
    }
}
