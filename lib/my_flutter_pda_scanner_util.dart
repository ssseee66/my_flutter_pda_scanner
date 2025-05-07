import 'package:flutter/services.dart';
import 'pda_scanner_operation_code.dart';

class MyFlutterPdaScannerUtil {
  MyFlutterPdaScannerUtil._();

  factory MyFlutterPdaScannerUtil() => _instance;
  static final MyFlutterPdaScannerUtil _instance = MyFlutterPdaScannerUtil._();

  BasicMessageChannel flutterChannel = const BasicMessageChannel(
      "my_flutter_pda_scanner",
      StandardMessageCodec()
  );
  BasicMessageChannel messageChannel = const BasicMessageChannel(
      "null",
      StandardMessageCodec()
  );
  void sendMessageToAndroid(String methodName, dynamic arg) async {
    messageChannel.send({methodName: arg});
  }
  void sendChannelName(String methodName, dynamic channelName) async {
    flutterChannel.send({methodName: channelName});
  }
  void setMessageChannel(String channelName, Future<dynamic> Function(dynamic message) handler) {
    messageChannel = BasicMessageChannel(channelName, const StandardMessageCodec());
    messageChannel.setMessageHandler(handler);
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
}