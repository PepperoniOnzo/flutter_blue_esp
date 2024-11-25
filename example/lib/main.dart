import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:flutter_blue_esp_example/bloc/main_bloc.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final TextEditingController proofOfPosController = TextEditingController();
  final TextEditingController passphraseController = TextEditingController();

  @override
  void dispose() {
    proofOfPosController.dispose();
    passphraseController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: BlocProvider(
        create: (context) => MainBloc(),
        child: BlocBuilder<MainBloc, MainState>(
          builder: (context, state) {
            return Scaffold(
              appBar: AppBar(
                title: Text(
                  'Flutter Blue ESP',
                  style: Theme.of(context)
                      .textTheme
                      .titleLarge
                      ?.copyWith(fontWeight: FontWeight.bold),
                ),
                actions: [
                  IconButton(
                      onPressed: () =>
                          context.read<MainBloc>().add(ScanDevices()),
                      icon: const Icon(
                        Icons.bluetooth,
                        color: Colors.black,
                      ))
                ],
              ),
              body: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: PageView(
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        TextField(
                          controller: proofOfPosController,
                          decoration: const InputDecoration(
                            hintText: 'Proof Of Possession',
                          ),
                        ),
                        TextField(
                          controller: passphraseController,
                          decoration: const InputDecoration(
                            hintText: 'WiFi Passphrase',
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.symmetric(vertical: 10),
                          child: Text(
                            "Scanned Devices:",
                            style: Theme.of(context).textTheme.titleLarge,
                          ),
                        ),
                        Expanded(
                          child: ListView.separated(
                            itemCount: state.devices.length,
                            separatorBuilder: (context, index) =>
                                const SizedBox(height: 10),
                            itemBuilder: (context, index) => InkWell(
                              onTap: () {
                                context.read<MainBloc>().add(ScanDeviceWifi(
                                    device: state.devices[index],
                                    proofOfPossession:
                                        proofOfPosController.text));
                              },
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    state.devices[index].name,
                                    style: Theme.of(context)
                                        .textTheme
                                        .bodyMedium
                                        ?.copyWith(fontWeight: FontWeight.bold),
                                  ),
                                  Text(state.devices[index].address),
                                ],
                              ),
                            ),
                          ),
                        ),
                        Text(
                          "Found Networks:",
                          style: Theme.of(context).textTheme.titleLarge,
                        ),
                        const SizedBox(height: 10),
                        Expanded(
                          child: ListView.separated(
                            itemCount: state.wifi.length,
                            separatorBuilder: (context, index) =>
                                const SizedBox(height: 10),
                            itemBuilder: (context, index) => InkWell(
                              onTap: () {
                                context.read<MainBloc>().add(ProvisionDevice(
                                    wifi: state.wifi[index],
                                    passphrase: passphraseController.text,
                                    device: state.devices[index],
                                    proofOfPossession:
                                        proofOfPosController.text));
                              },
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    state.wifi[index].ssid,
                                    style: Theme.of(context)
                                        .textTheme
                                        .bodyMedium
                                        ?.copyWith(fontWeight: FontWeight.bold),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                    ListView.separated(
                      separatorBuilder: (context, index) => const SizedBox(height: 5),
                      itemBuilder: (context, index) => Text(
                        state.logs[index],
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            fontWeight: index % 2 == 0
                                ? FontWeight.bold
                                : FontWeight.normal),
                      ),
                      itemCount: state.logs.length,
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}
