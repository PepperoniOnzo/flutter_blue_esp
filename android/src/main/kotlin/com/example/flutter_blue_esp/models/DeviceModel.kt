package com.example.flutter_blue_esp.models

import kotlinx.serialization.Serializable

@Serializable
data class DeviceModel(val name: String, val address: String)