package com.example.flutter_blue_esp.services

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import com.espressif.provisioning.ESPProvisionManager
import com.espressif.provisioning.listeners.BleScanListener
import com.example.flutter_blue_esp.constants.Configs
import com.example.flutter_blue_esp.models.CallContext
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class BleService {

    private lateinit var platformContext: Context
    private val espManager: ESPProvisionManager
        get() = ESPProvisionManager.getInstance(
            platformContext
        )

    fun initService(context: Context) {
        platformContext = context
    }

    fun call(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            Configs.methods.SCAN_DEVICES -> scanDevices(CallContext(call, result))
            else -> result.notImplemented()
        }

    }

    private fun scanDevices(ctx: CallContext) {
        val prefix = ctx.getArg("prefix") ?: return;
        try {
            espManager.searchBleEspDevices(prefix, object : BleScanListener {
                override fun scanStartFailed() {
                    Log.d(Configs.log.PLUGIN_TAG, "searchBleEspDevices - scanStartFailed")
                    ctx.error(
                        code = Configs.errors.START_SCAN_DEVICES_CODE,
                        message = Configs.errors.START_SCAN_DEVICES_MESSAGE
                    )
                }

                override fun onPeripheralFound(var1: BluetoothDevice?, var2: ScanResult?) {
                    Log.d(Configs.log.PLUGIN_TAG, var1?.name ?: "unk")
                }

                override fun scanCompleted() {

                }

                override fun onFailure(var1: Exception?) {
                    Log.d(Configs.log.PLUGIN_TAG, "searchBleEspDevices - onFailure $var1")
                    ctx.error(
                        code = Configs.errors.FAIL_SCAN_DEVICES_CODE,
                        message = Configs.errors.FAIL_SCAN_DEVICES_MESSAGE,
                        details = var1
                    )
                }

            })
        } catch (e: Exception) {
            Log.d(Configs.log.PLUGIN_TAG, "searchBleEspDevices - exception: $e")
            ctx.error(
                code = Configs.errors.UNKNOWN_CODE,
                message = Configs.errors.UNKNOWN_MESSAGE,
                details = e
            )
        }
    }


}

