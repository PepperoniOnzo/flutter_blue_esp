import 'dart:async';

import 'package:bloc/bloc.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_blue_esp/flutter_blue_esp.dart';
import 'package:meta/meta.dart';

part 'main_event.dart';
part 'main_state.dart';

class MainBloc extends Bloc<MainEvent, MainState> {
  MainBloc() : super(const MainState()) {
    on<ScanDevices>(_scanDevices);
    on<ScanDeviceWifi>(_scanDeviceWiFi);
    on<ProvisionDevice>(_provisionDevice);
  }

  final FlutterBlueEsp _flutterBlueEspPlugin = FlutterBlueEsp();

  FutureOr<void> _scanDevices(
      ScanDevices event, Emitter<MainState> emit) async {
    emit(state.copyWith(
        selectedDevice: () => null,
        devices: [],
        wifi: [],
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
    } on PlatformException catch (e) {
      emit(state.copyWith(
          message: "CODE: ${e.code}\nMESSAGE: ${e.message}",
          logs: List.from(state.logs)..add('ERROR: $e.')));
    } on Exception catch (e) {
      emit(state.copyWith(logs: List.from(state.logs)..add('ERROR: $e.')));
    }
  }

  FutureOr<void> _scanDeviceWiFi(
      ScanDeviceWifi event, Emitter<MainState> emit) async {
    emit(state.copyWith(
        selectedDevice: () => event.device,
        wifi: [],
        logs: List.from(state.logs)..add('Start scanning for device WiFi.')));

    try {
      final wifi = await _flutterBlueEspPlugin.scanDeviceWiFi(
          deviceName: event.device.name,
          proofOfPossession: event.proofOfPossession);

      emit(state.copyWith(
          wifi: wifi,
          logs: List.from(state.logs.reversed)
            ..add(
              'Found devices\n${wifi.map(
                    (e) => e.ssid,
                  ).join('\n')}.',
            )));
    } on PlatformException catch (e) {
      emit(state.copyWith(
          message: "CODE: ${e.code}\nMESSAGE: ${e.message}",
          logs: List.from(state.logs)..add('ERROR: $e.')));
    } on Exception catch (e) {
      emit(state.copyWith(logs: List.from(state.logs)..add('ERROR: $e.')));
    }
  }

  FutureOr<void> _provisionDevice(
      ProvisionDevice event, Emitter<MainState> emit) async {
    emit(state.copyWith(
        logs: List.from(state.logs)..add('Start provisioning device.')));

    try {
      final provisioned = await _flutterBlueEspPlugin.provisionDevice(
          passphrase: event.passphrase,
          ssid: event.wifi.ssid,
          deviceName: state.selectedDevice!.name,
          proofOfPossession: event.proofOfPossession);

      if (provisioned) {
        emit(state.copyWith(
            message: 'Successfully provisioned device.',
            logs: List.from(state.logs)
              ..add('Successfully provisioned device.')));
      } else {
        emit(state.copyWith(
            logs: List.from(state.logs)..add('Failed to provision device.')));
      }
    } on PlatformException catch (e) {
      emit(state.copyWith(
          message: "CODE: ${e.code}\nMESSAGE: ${e.message}",
          logs: List.from(state.logs)..add('ERROR: $e.')));
    } on Exception catch (e) {
      emit(state.copyWith(logs: List.from(state.logs)..add('ERROR: $e.')));
    }
  }
}
