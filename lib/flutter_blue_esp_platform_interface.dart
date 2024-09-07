import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_blue_esp_method_channel.dart';

abstract class FlutterBlueEspPlatform extends PlatformInterface {
  /// Constructs a FlutterBlueEspPlatform.
  FlutterBlueEspPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterBlueEspPlatform _instance = MethodChannelFlutterBlueEsp();

  /// The default instance of [FlutterBlueEspPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterBlueEsp].
  static FlutterBlueEspPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterBlueEspPlatform] when
  /// they register themselves.
  static set instance(FlutterBlueEspPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
