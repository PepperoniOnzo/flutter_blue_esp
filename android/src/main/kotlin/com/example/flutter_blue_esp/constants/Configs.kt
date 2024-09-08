@file:Suppress("FunctionName")

package com.example.flutter_blue_esp.constants

object Configs {
    val log: LogConfigs = LogConfigs
    val methods: Methods = Methods
    val errors: ErrorData = ErrorData

    object LogConfigs {
        const val PLUGIN_TAG = "FlutterBlueEsp"
    }

    object Methods {
        const val SCAN_DEVICES = "ScanDevices"
    }
    object ErrorData {
        const val MISSING_ARGUMENT_CODE = "MA1"
        fun MISSING_ARGUMENT_MESSAGE(argument: String): String {
            return "Called method without required argument: $argument"
        }

        const val START_SCAN_DEVICES_CODE = "S1"
        const val START_SCAN_DEVICES_MESSAGE = "Failed to start scan devices"
        const val FAIL_SCAN_DEVICES_CODE = "S2"
        const val FAIL_SCAN_DEVICES_MESSAGE = "Failed scan devices"

        const val UNKNOWN_CODE = "U1"
        const val UNKNOWN_MESSAGE = "Unknown error"

        const val NO_DESCRIPTION_PROVIDED = "No description provided"
    }
}


