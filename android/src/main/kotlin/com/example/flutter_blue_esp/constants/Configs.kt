@file:Suppress("FunctionName")

package com.example.flutter_blue_esp.constants

import com.espressif.provisioning.ESPConstants

object Configs {
    val log: LogConfigs = LogConfigs
    val methods: Methods = Methods
    val errors: ErrorData = ErrorData

    object LogConfigs {
        const val PLUGIN_TAG = "FlutterBlueEsp"
    }

    object Methods {
        const val SCAN_DEVICES = "ScanDevices"
        const val SCAN_DEVICE_WIFI = "ScanDeviceWifi"
        const val PROVISION_DEVICE = "ProvisionDevice"
    }
    object ErrorData {
        const val MISSING_ARGUMENT_CODE = "MA1"
        fun MISSING_ARGUMENT_MESSAGE(argument: String): String {
            return "Called method without required argument: $argument"
        }

        const val PERMISSIONS_NOT_GRANTED_CODE = "P1"
        const val PERMISSIONS_NOT_GRANTED_MESSAGE = "Permissions not granted"

        const val CONNECT_DEVICE_NOT_FOUND_CODE = "CF1"
        fun CONNECT_DEVICE_NOT_FOUND_MESSAGE(argument: String): String {
            return "Failed to connect device with name: $argument"
        }

        const val START_SCAN_DEVICES_CODE = "SD1"
        const val START_SCAN_DEVICES_MESSAGE = "Failed to start scan devices"
        const val FAIL_SCAN_DEVICES_CODE = "SD2"
        const val FAIL_SCAN_DEVICES_MESSAGE = "Failed scan devices"

        const val FAIL_SCAN_DEVICE_WIFI_CODE = "SW1"
        const val FAIL_SCAN_DEVICE_WIFI_MESSAGE = "Failed scan device WiFis"

        const val PROVISIONING_FAILED_TO_CREATE_SESSION_CODE = "PD1"
        const val PROVISIONING_FAILED_TO_CREATE_SESSION_MESSAGE = "Provisioning failed: Unable to create session."
        const val PROVISIONING_WIFI_CONFIG_FAILED_CODE = "PD2"
        const val PROVISIONING_WIFI_CONFIG_FAILED_MESSAGE = "Provisioning failed: Unable to configure Wi-Fi."
        const val PROVISIONING_WIFI_APPLY_FAILED_CODE = "PD3"
        const val PROVISIONING_WIFI_APPLY_FAILED_MESSAGE = "Provisioning failed: Unable to apply Wi-Fi configuration."
        const val PROVISIONING_DEVICE_FAILED_CODE = "PD4"
        const val PROVISIONING_DEVICE_FAILED_MESSAGE = "Provisioning failed: Device encountered an issue."
        const val PROVISIONING_FAILED_CODE = "PD5"
        const val PROVISIONING_FAILED_MESSAGE = "Provisioning failed."

        const val UNKNOWN_CODE = "U1"
        const val UNKNOWN_MESSAGE = "Unknown error"

        const val NO_DESCRIPTION_PROVIDED = "No description provided"
    }
}

