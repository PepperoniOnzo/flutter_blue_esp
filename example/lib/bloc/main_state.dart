part of 'main_bloc.dart';

@immutable
final class MainState {
  const MainState({
    this.devices = const [],
    this.logs = const [],
    this.wifi = const [],
    this.selectedDevice,
    this.message = '',
  });

  final List<String> logs;
  final List<DeviceDto> devices;
  final DeviceDto? selectedDevice;
  final List<WiFiDto> wifi;

  final String message;

  MainState copyWith({
    List<String>? logs,
    List<DeviceDto>? devices,
    List<WiFiDto>? wifi,
    ValueGetter<DeviceDto?>? selectedDevice,
    String message = '',
  }) =>
      MainState(
        message: message,
        selectedDevice:
            selectedDevice == null ? this.selectedDevice : selectedDevice(),
        logs: logs ?? this.logs,
        devices: devices ?? this.devices,
        wifi: wifi ?? this.wifi,
      );
}
