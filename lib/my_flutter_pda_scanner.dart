
import 'package:flutter/services.dart';

import 'my_flutter_pda_scanner_platform_interface.dart';

class MyFlutterPdaScanner {
  Future<String?> getPlatformVersion() {
    return MyFlutterPdaScannerPlatform.instance.getPlatformVersion();
  }
  static const MethodChannel _channel =
  MethodChannel('my_flutter_pda_scanner');

  static Future<String?> Init() async {
    final String? code = await _channel.invokeMethod('init');
    return code;
  }
}
