package com.example.my_flutter_pda_scanner;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.StandardMessageCodec;

/** MyRfidReaderPlugin */
public class MyFlutterPdaScannerPlugin implements FlutterPlugin{
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    // Flutter端与Android端通讯的通道名称
    private static final String FLUTTER_TO_ANDROID_CHANNEL = "my_flutter_pda_scanner";
    // Flutter端与Android端通讯的通道
    private BasicMessageChannel<Object> flutterChannel;
    // Android端发送给Flutter端的消息Map
    private final Map<String, Object> messageMap = new HashMap<>();
    // Flutter端发送过来的消息Map
    private Map<String, Object> arguments;
    // 动作映射Map,将各个动作对应的方法的引用存放在Map中以减少if分支
    private final Map<String, Consumer<String>> actionMap = new HashMap<>();
    // 应用上下文
    private Context applicationContext;
    // 广播接收器
    private BroadcastReceiver broadcastReceiver;
    // 广播动作
    private String BROADCAST_ACTION;
    // 广播数据标签列表
    private final List<String> BROADCAST_DATA_TAGS = new ArrayList<>();
    // 数据处理完毕标志
    private boolean DISPOSE_OVER = false;

    public MyFlutterPdaScannerPlugin() {
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        Log.i("onAttachedToEngine", "onAttachedToEngine");
        applicationContext = flutterPluginBinding.getApplicationContext();

        flutterChannel = new BasicMessageChannel<>(
                flutterPluginBinding.getBinaryMessenger(),
                FLUTTER_TO_ANDROID_CHANNEL,
                StandardMessageCodec.INSTANCE
        );
        setAction_map();

        flutterChannel.setMessageHandler((message, reply) -> {
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
        return key;
    }
    private void setAction_map() {
        actionMap.put("setAction",           this :: setAction);
        actionMap.put("setDataTag",          this :: setDataTag);
        actionMap.put("addDataTag",          this :: addDataTag);
        actionMap.put("startListener",       this :: startListener);
        actionMap.put("stopListener",        this :: stopListener);
        actionMap.put("disposeOver",         this :: disposeOver);
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
        flutterChannel.send(messageMap);
    }
    private void setDataTag(String key) {
        List<String> dataTags = castList(arguments.get(key), String.class);
        if (dataTags == null) return;
        BROADCAST_DATA_TAGS.addAll(dataTags);
        messageMap.clear();
        messageMap.put("message", "The broadcast data tags was set successfully");
        messageMap.put("isSuccessful", true);
        messageMap.put("operationCode", 1);
        flutterChannel.send(messageMap);
    }
    private void addDataTag(String key) {
        String dataTag = (String) arguments.get(key);
        if (dataTag == null) return;
        BROADCAST_DATA_TAGS.add(dataTag);
        messageMap.clear();
        messageMap.put("message", "The broadcast data tag was add successfully");
        messageMap.put("isSuccessful", true);
        messageMap.put("operationCode", 2);
        flutterChannel.send(messageMap);
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
                    flutterChannel.send(messageMap);
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
        flutterChannel.send(messageMap);
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
        flutterChannel.send(messageMap);
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
        flutterChannel.send(messageMap);
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
    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        flutterChannel = null;
    }
}