import 'package:flutter_blue_esp/src/models/device_dto.dart';

import 'src/flutter_blue_esp_platform_interface.dart';

class FlutterBlueEsp {
  Future<List<DeviceDto>?> scanDevices() {
    return FlutterBlueEspPlatform.instance.scanDevices();
  }
}
