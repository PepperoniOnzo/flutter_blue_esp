import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_blue_esp/flutter_blue_esp.dart';
import 'package:flutter_blue_esp/flutter_blue_esp_platform_interface.dart';
import 'package:flutter_blue_esp/flutter_blue_esp_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterBlueEspPlatform
    with MockPlatformInterfaceMixin
    implements FlutterBlueEspPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterBlueEspPlatform initialPlatform = FlutterBlueEspPlatform.instance;

  test('$MethodChannelFlutterBlueEsp is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterBlueEsp>());
  });

  test('getPlatformVersion', () async {
    FlutterBlueEsp flutterBlueEspPlugin = FlutterBlueEsp();
    MockFlutterBlueEspPlatform fakePlatform = MockFlutterBlueEspPlatform();
    FlutterBlueEspPlatform.instance = fakePlatform;

    expect(await flutterBlueEspPlugin.getPlatformVersion(), '42');
  });
}
