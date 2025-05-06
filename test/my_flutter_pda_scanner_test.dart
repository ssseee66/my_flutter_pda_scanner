import 'package:flutter_test/flutter_test.dart';
import 'package:my_flutter_pda_scanner/my_flutter_pda_scanner.dart';
import 'package:my_flutter_pda_scanner/my_flutter_pda_scanner_platform_interface.dart';
import 'package:my_flutter_pda_scanner/my_flutter_pda_scanner_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockMyFlutterPdaScannerPlatform
    with MockPlatformInterfaceMixin
    implements MyFlutterPdaScannerPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final MyFlutterPdaScannerPlatform initialPlatform = MyFlutterPdaScannerPlatform.instance;

  test('$MethodChannelMyFlutterPdaScanner is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelMyFlutterPdaScanner>());
  });

  test('getPlatformVersion', () async {
    MyFlutterPdaScanner myFlutterPdaScannerPlugin = MyFlutterPdaScanner();
    MockMyFlutterPdaScannerPlatform fakePlatform = MockMyFlutterPdaScannerPlatform();
    MyFlutterPdaScannerPlatform.instance = fakePlatform;

    expect(await myFlutterPdaScannerPlugin.getPlatformVersion(), '42');
  });
}
