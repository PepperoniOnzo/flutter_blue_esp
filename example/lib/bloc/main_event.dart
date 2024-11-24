part of 'main_bloc.dart';

@immutable
sealed class MainEvent {}

final class ScanDevices extends MainEvent {}

final class ScanDeviceWifi extends MainEvent {
  ScanDeviceWifi({
    required this.device,
    required this.proofOfPossession,
  });

  final DeviceDto device;
  final String proofOfPossession;
}

final class ProvisionDevice extends MainEvent {
  ProvisionDevice({
    required this.wifi,
    required this.passphrase,
    required this.device,
    required this.proofOfPossession,
  });

  final WiFiDto wifi;
  final String passphrase;
  final DeviceDto device;
  final String proofOfPossession;
}
