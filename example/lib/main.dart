import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:flutter_blue_esp_example/bloc/main_bloc.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

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
                        Text(
                          "Scanned Devices:",
                          style: Theme.of(context).textTheme.titleLarge,
                        ),
                        const SizedBox(height: 10),
                        Expanded(
                          child: ListView.separated(
                            itemCount: state.devices.length,
                            separatorBuilder: (context, index) =>
                                const SizedBox(height: 10),
                            itemBuilder: (context, index) => InkWell(
                              onTap: () {},
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
                        Expanded(
                            child: ListView.builder(
                          itemBuilder: (context, index) => Text('Network'),
                        ))
                      ],
                    ),
                    ListView.separated(
                      separatorBuilder: (context, index) => SizedBox(height: 5),
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
