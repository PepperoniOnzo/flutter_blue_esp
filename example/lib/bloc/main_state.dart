part of 'main_bloc.dart';

@immutable
final class MainState {
  const MainState({
    this.devices = const [],
    this.logs = const [],
    this.wifi = const [],
    this.selectedDevice,
  });

  final List<String> logs;
  final List<DeviceDto> devices;
  final DeviceDto? selectedDevice;
  final List<WiFiDto> wifi;

  MainState copyWith({
    List<String>? logs,
    List<DeviceDto>? devices,
    List<WiFiDto>? wifi,
    ValueGetter<DeviceDto?>? selectedDevice,
  }) =>
      MainState(
        selectedDevice:
            selectedDevice == null ? this.selectedDevice : selectedDevice(),
        logs: logs ?? this.logs,
        devices: devices ?? this.devices,
        wifi: wifi ?? this.wifi,
      );
}
