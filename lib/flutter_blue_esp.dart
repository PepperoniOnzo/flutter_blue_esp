import 'package:flutter_blue_esp/flutter_blue_esp.dart';

import 'src/flutter_blue_esp_platform_interface.dart';

export 'src/models/device_dto.dart';
export 'src/models/wifi_dto.dart.dart';

class FlutterBlueEsp {
  Future<List<DeviceDto>> scanDevices() {
    return FlutterBlueEspPlatform.instance.scanDevices();
  }

  Future<List<WiFiDto>> scanDeviceWiFi({
    required String deviceName,
    required String proofOfPossession,
  }) {
    return FlutterBlueEspPlatform.instance.scanDeviceWiFi(
        deviceName: deviceName, proofOfPossession: proofOfPossession);
  }

  Future<bool> provisionDevice({
    required String ssid,
    required String passphrase,
    required String deviceName,
    required String proofOfPossession,
  }) {
    return FlutterBlueEspPlatform.instance.provisionDevice(
      deviceName: deviceName,
      proofOfPossession: proofOfPossession,
      ssid: ssid,
      passphrase: passphrase,
    );
  }
}
