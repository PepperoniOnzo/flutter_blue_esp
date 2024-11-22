part of 'main_bloc.dart';

@immutable
final class MainState {
  const MainState({this.devices = const [], this.logs = const []});

  final List<String> logs;
  final List<DeviceDto> devices;

  MainState copyWith({
    List<String>? logs,
    List<DeviceDto>? devices,
  }) =>
      MainState(
        logs: logs ?? this.logs,
        devices: devices ?? this.devices,
      );
}
