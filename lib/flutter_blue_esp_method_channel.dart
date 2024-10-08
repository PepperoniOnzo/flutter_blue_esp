import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_blue_esp_platform_interface.dart';

/// An implementation of [FlutterBlueEspPlatform] that uses method channels.
class MethodChannelFlutterBlueEsp extends FlutterBlueEspPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_blue_esp');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
