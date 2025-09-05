import 'dart:ffi';

import 'package:flutter/services.dart';
import 'package:logger/logger.dart';
import 'pda_scanner_operation_code.dart';

class MyFlutterPdaScannerUtil {
  /// 工厂模式会导致Util为单例，使用了相同插件的两个页面之间
  /// 跳转会导致使用同一Util实例，通道重合导致返回之后上一个页面无法正常
  /// 与android通讯
  // MyFlutterPdaScannerUtil._();
  //
  // factory MyFlutterPdaScannerUtil() => _instance;
  // static final MyFlutterPdaScannerUtil _instance = MyFlutterPdaScannerUtil._();
  final Logger logger = Logger();

  BasicMessageChannel flutterChannel = const BasicMessageChannel(
      "my_flutter_pda_scanner", StandardMessageCodec());
  BasicMessageChannel messageChannel =
      const BasicMessageChannel("null", StandardMessageCodec());
  void sendMessageToAndroid(String methodName, dynamic arg) async {
    messageChannel.send({methodName: arg});
  }

  void sendChannelName(String methodName, dynamic channelName) async {
    flutterChannel.send({methodName: channelName});
  }

  void setMessageChannel(
      String channelName, Future<dynamic> Function(dynamic message) handler) {
    messageChannel =
        BasicMessageChannel(channelName, const StandardMessageCodec());
    messageChannel.setMessageHandler(handler);
  }
  void initUtil(Future<dynamic> Function(dynamic) handle) {
    String channelName = "${DateTime.now().millisecondsSinceEpoch.toString()}-scanner";
    flutterChannel.setMessageHandler((dynamic message) async {});
    setMessageChannel(channelName, handle);
    sendChannelName("channelName", channelName);
  }

  void setAction(String action) {
    messageChannel.send({"setAction": action});
  }

  void setDataTag(List<String> dataTag) {
    messageChannel.send({"setDataTag": dataTag});
  }

  void addDataTag(String dataTag) {
    messageChannel.send({"addDataTag": dataTag});
  }

  void startListener() {
    messageChannel.send({"startListener": true});
  }

  void stopListener() {
    messageChannel.send({"stopListener": true});
  }

  void disposeOver() {
    messageChannel.send({"disposeOver": true});
  }

  void destroy() {
    messageChannel.send({"destroy": true});
    messageChannel.setMessageHandler(null);
    flutterChannel.setMessageHandler(null);
  }

  Enum getOperationCode(int code) {
    switch (code) {
      case 0:
        return PdaScannerOperationCode.SET_BROADCAST_ACTION;
      case 1:
        return PdaScannerOperationCode.SET_BROADCAST_DATA_TAG;
      case 2:
        return PdaScannerOperationCode.ADD_BROADCAST_DATA_TAG;
      case 3:
        return PdaScannerOperationCode.START_LISTENER_BROADCAST;
      case 4:
        return PdaScannerOperationCode.STOP_LISTENER_BROADCAST;
      case 5:
        return PdaScannerOperationCode.LISTENER_BROADCAST;
      case 6:
        return PdaScannerOperationCode.DISPOSE_OVER;
      default:
        return PdaScannerOperationCode.ERROR_CODE;
    }
  }
  Future<String?> commonListener(dynamic message, List<String> dataTags) async {
    logger.i(message);
    dynamic info = message["message"];
    Enum operationCode = getOperationCode(message["operationCode"]);
    bool isSuccessful = message["isSuccessful"];
    String? barcode;
    if (!isSuccessful) return barcode;
    switch (operationCode) {
      case PdaScannerOperationCode.SET_BROADCAST_ACTION:
        setDataTag(dataTags);
        break;
      case PdaScannerOperationCode.SET_BROADCAST_DATA_TAG:
        startListener();
        break;
      case PdaScannerOperationCode.LISTENER_BROADCAST:
        barcode = info[dataTags.first];
        // disposeOver();
        break;
      case PdaScannerOperationCode.STOP_LISTENER_BROADCAST:
        logger.i(info);
        break;
      default:
        break;
    }
    return barcode;
  }
}
