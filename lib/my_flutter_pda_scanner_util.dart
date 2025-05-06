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
  void setAction(String action) {
    flutterChannel.send({"setAction": action});
  }
  void setDataTag(List<String> dataTag) {
    flutterChannel.send({"setDataTag": dataTag});
  }
  void addDataTag(String dataTag) {
    flutterChannel.send({"addDataTag": dataTag});
  }
  void startListener() {
    flutterChannel.send({"startListener": true});
  }
  void stopListener() {
    flutterChannel.send({"stopListener": true});
  }
  void disposeOver() {
    flutterChannel.send({"disposeOver": true});
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