import 'dart:async';

import 'package:bloc/bloc.dart';
import 'package:flutter_blue_esp/flutter_blue_esp.dart';
import 'package:meta/meta.dart';

part 'main_event.dart';
part 'main_state.dart';

class MainBloc extends Bloc<MainEvent, MainState> {
  MainBloc() : super(const MainState()) {
    on<ScanDevices>(_scanDevices);
  }

  final FlutterBlueEsp _flutterBlueEspPlugin = FlutterBlueEsp();

  FutureOr<void> _scanDevices(
      ScanDevices event, Emitter<MainState> emit) async {
    emit(state.copyWith(
        logs: List.from(state.logs)..add('Start scanning for devices.')));

    try {
      final devices = await _flutterBlueEspPlugin.scanDevices();

      emit(state.copyWith(
          devices: devices,
          logs: List.from(state.logs.reversed)
            ..add(
              'Found devices\n${devices.map(
                    (e) => "${e.name} ${e.address}",
                  ).join('\n')}.',
            )));
    } on Exception catch (e) {
      emit(state.copyWith(logs: List.from(state.logs)..add('ERROR: $e.')));
    }
  }
}
