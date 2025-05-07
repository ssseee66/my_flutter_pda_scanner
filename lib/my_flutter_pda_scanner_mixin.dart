import 'dart:async';

import 'package:flutter/material.dart';
import 'my_flutter_pda_scanner_util.dart';
import 'package:logger/logger.dart';

mixin MyFlutterPdaScannerMixin<T extends StatefulWidget> on State<T> {
  late StreamSubscription streamSubscription;
  final MyFlutterPdaScannerUtil util = MyFlutterPdaScannerUtil();
  Logger logger = Logger();

  @override
  void initState() {
    super.initState();
    util.flutterChannel.setMessageHandler((dynamic message) async {});
    util.setMessageChannel(hashCode.toString(), listenerPdaAndroidHandle);
    util.sendChannelName("channelName", hashCode.toString());
  }

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
    util.destroy();
    util.flutterChannel.setMessageHandler(null);
    util.messageChannel.setMessageHandler(null);
    logger.i("注销通道");
  }

  Future<void> listenerPdaAndroidHandle(dynamic message);

}