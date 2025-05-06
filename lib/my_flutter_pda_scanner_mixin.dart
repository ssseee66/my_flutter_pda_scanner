import 'dart:async';

import 'package:flutter/material.dart';
import 'my_flutter_pda_scanner_util.dart';

mixin MyFlutterPdaScannerMixin<T extends StatefulWidget> on State<T> {
  late StreamSubscription streamSubscription;
  final MyFlutterPdaScannerUtil util = MyFlutterPdaScannerUtil();

  @override
  void initState() {
    super.initState();
    util.flutterChannel.setMessageHandler(listenerPdaAndroidHandle);
  }

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
    util.flutterChannel.setMessageHandler(null);
  }

  Future<void> listenerPdaAndroidHandle(dynamic message);

}