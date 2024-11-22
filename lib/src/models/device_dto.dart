class DeviceDto {
  DeviceDto({
    required this.name,
    required this.address,
  });

  final String name;
  final String address;

  factory DeviceDto.fromJson(Map<String, dynamic> json) => DeviceDto(
        name: json['name'] as String,
        address: json['address'] as String,
      );
}
