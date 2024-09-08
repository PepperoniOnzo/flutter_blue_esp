package com.example.flutter_blue_esp

import android.util.Log
import androidx.annotation.NonNull
import com.example.flutter_blue_esp.constants.Configs
import com.example.flutter_blue_esp.services.BleService

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** FlutterBlueEspPlugin */
class FlutterBlueEspPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private var bleService: BleService = BleService()

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        Log.d(Configs.log.PLUGIN_TAG, "onAttachedToEngine")

        bleService.initService(flutterPluginBinding.applicationContext)
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_blue_esp")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        Log.d(Configs.log.PLUGIN_TAG, "onMethodCall, method: ${call.method}")

        bleService.call(call, result)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        Log.d(Configs.log.PLUGIN_TAG, "onDetachedFromEngine")

        channel.setMethodCallHandler(null)
    }
}
