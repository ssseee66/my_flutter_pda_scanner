import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'my_flutter_pda_scanner_method_channel.dart';

abstract class MyFlutterPdaScannerPlatform extends PlatformInterface {
  /// Constructs a MyFlutterPdaScannerPlatform.
  MyFlutterPdaScannerPlatform() : super(token: _token);

  static final Object _token = Object();

  static MyFlutterPdaScannerPlatform _instance = MethodChannelMyFlutterPdaScanner();

  /// The default instance of [MyFlutterPdaScannerPlatform] to use.
  ///
  /// Defaults to [MethodChannelMyFlutterPdaScanner].
  static MyFlutterPdaScannerPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MyFlutterPdaScannerPlatform] when
  /// they register themselves.
  static set instance(MyFlutterPdaScannerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
