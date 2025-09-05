package com.example.my_flutter_pda_scanner;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.StandardMessageCodec;

/** RfidReaderPlugin */
public class MyFlutterPdaScannerPlugin implements FlutterPlugin {
    private static final String FLUTTER_TO_ANDROID_CHANNEL = "my_flutter_pda_scanner";
    private Context applicationContext;
    private MyListener listeners;
    private BasicMessageChannel<Object> flutterChannel;


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        Log.e("onAttachedToEngine", "onAttachedToEngine");
        applicationContext = flutterPluginBinding.getApplicationContext();

        flutterChannel = new BasicMessageChannel<>(
                flutterPluginBinding.getBinaryMessenger(),
                FLUTTER_TO_ANDROID_CHANNEL,
                StandardMessageCodec.INSTANCE
        );
//        listeners = new MyListener(MESSAGE_CHANNEL, applicationContext, flutterPluginBinding.getBinaryMessenger());

        flutterChannel.setMessageHandler((message, reply) -> {
            Map<String, Object> channelMessage = castMap(message, String.class, Object.class);
            if (channelMessage == null) return;
            if (channelMessage.containsKey("channelName")) {
                String channel_name = (String) channelMessage.get("channelName");
                if (channel_name == null) return;
                Log.i("scannerChannelName", channel_name);
                listeners = new MyListener(
                        channel_name,
                        applicationContext,
                        flutterPluginBinding.getBinaryMessenger()
                );
            }
        });
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        listeners = null;
        flutterChannel = null;
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
}
