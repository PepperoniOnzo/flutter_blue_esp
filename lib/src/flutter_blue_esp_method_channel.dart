import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_blue_esp/src/constants/plugin_constants.dart';
import 'package:flutter_blue_esp/src/models/device_dto.dart';

import 'flutter_blue_esp_platform_interface.dart';

/// An implementation of [FlutterBlueEspPlatform] that uses method channels.
class MethodChannelFlutterBlueEsp extends FlutterBlueEspPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel(ChannelConstants.channel);

  @override
  Future<List<DeviceDto>?> scanDevices() async {
    try {
      await methodChannel.invokeMethod<Map<String, dynamic>>(
          ChannelConstants.methodScanDevices, {"prefix": ""});
    } on Exception catch (e) {
      print('\u001b[32m $e\u001b[0m');
    }
    return [];
  }
}
