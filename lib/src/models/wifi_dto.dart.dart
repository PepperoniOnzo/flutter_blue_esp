class WiFiDto {
  WiFiDto({required this.ssid});

  final String ssid;

  factory WiFiDto.fromJson(Map<String, dynamic> json) => WiFiDto(
        ssid: json['ssid'] as String,
      );
}
