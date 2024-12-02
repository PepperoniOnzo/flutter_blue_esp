import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_blue_esp/src/constants/plugin_constants.dart';
import 'package:flutter_blue_esp/src/models/device_dto.dart';
import 'package:flutter_blue_esp/src/models/wifi_dto.dart.dart';

import 'flutter_blue_esp_platform_interface.dart';

/// An implementation of [FlutterBlueEspPlatform] that uses method channels.
class MethodChannelFlutterBlueEsp extends FlutterBlueEspPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel(ChannelConstants.channel);

  @override
  Future<List<DeviceDto>> scanDevices({String prefix = ''}) async {
    final scanResult = await methodChannel.invokeMethod<String>(
        ChannelConstants.methodScanDevices, {"prefix": prefix});

    if (scanResult == null) return [];

    final decodedResult = jsonDecode(scanResult) as List<dynamic>;

    final devices =
        decodedResult.map((json) => DeviceDto.fromJson(json)).toList();

    return devices;
  }

  @override
  Future<List<WiFiDto>> scanDeviceWiFi({
    required String deviceName,
    required String proofOfPossession,
  }) async {
    final scanResult = await methodChannel
        .invokeMethod<String>(ChannelConstants.methodScanDeviceWifi, {
      "deviceName": deviceName,
      "proofOfPossession": proofOfPossession,
    });

    if (scanResult == null) return [];

    final decodedResult = jsonDecode(scanResult) as List<dynamic>;

    final foundWiFi =
        decodedResult.map((json) => WiFiDto.fromJson(json)).toList();

    return foundWiFi;
  }

  @override
  Future<bool> provisionDevice({
    required String ssid,
    required String passphrase,
    required String deviceName,
    required String proofOfPossession,
  }) async {
    final scanResult = await methodChannel
        .invokeMethod<bool>(ChannelConstants.methodProvisionDevice, {
      "ssid": ssid,
      "passphrase": passphrase,
      "deviceName": deviceName,
      "proofOfPossession": proofOfPossession,
    });

    if (scanResult is! bool) return false;

    return scanResult;
  }
}
