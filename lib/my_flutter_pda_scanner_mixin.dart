import 'dart:async';

import 'package:flutter/material.dart';
import 'my_flutter_pda_scanner_util.dart';

mixin MyFlutterPdaScannerMixin<T extends StatefulWidget> on State<T> {
  late StreamSubscription streamSubscription;
  MyFlutterPdaScannerUtil scannerUtil = MyFlutterPdaScannerUtil();

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    scannerUtil.initUtil(listenerPdaAndroidHandle);
  }

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
    scannerUtil.destroy();
  }

  Future<void> listenerPdaAndroidHandle(dynamic message);

}