package com.example.flutter_blue_esp.services

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import com.espressif.provisioning.ESPProvisionManager
import com.espressif.provisioning.listeners.BleScanListener
import com.example.flutter_blue_esp.constants.Configs
import com.example.flutter_blue_esp.models.CallContext
import com.example.flutter_blue_esp.models.DeviceModel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class BleService {

    private lateinit var platformContext: Context
    private var permissionService: PermissionService = PermissionService()
    private var platformActivity: Activity? = null
    private val espManager: ESPProvisionManager
        get() = ESPProvisionManager.getInstance(
            platformContext
        )

    fun initService(context: Context) {
        platformContext = context
    }

    fun attachActivity(activity: Activity) {
        platformActivity = activity
    }

    fun detachActivity() {
        platformActivity = null
    }

    fun call(call: MethodCall, result: MethodChannel.Result) {
        if (platformActivity == null) return

        val permissionsGranted = permissionService.ensurePermissionsGranted(platformActivity!!)

        if (!permissionsGranted) return result.error(
            Configs.errors.PERMISSIONS_NOT_GRANTED_CODE,
            Configs.errors.PERMISSIONS_NOT_GRANTED_MESSAGE,
            null
        )

        when (call.method) {
            Configs.methods.SCAN_DEVICES -> scanDevices(CallContext(call, result))
            else -> result.notImplemented()
        }

    }

    private fun scanDevices(ctx: CallContext) {
        val prefix = ctx.getArg("prefix") ?: return;
        val foundDevices = mutableListOf<DeviceModel>()

        Log.d(Configs.log.PLUGIN_TAG, "searchBleEspDevices - prefix: $prefix")

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
                    Log.d(Configs.log.PLUGIN_TAG, "FOUND DEVICE: " + (var1?.name ?: "Unknown Name"))

                    val address = var1?.address
                    val name = var1?.name

                    if (address == null || name == null) return
                    if (foundDevices.any { it.address == address }) return

                    foundDevices.add(DeviceModel(name, address))
                }

                override fun scanCompleted() {
                    ctx.success(
                        Json.encodeToString(
                            ListSerializer(DeviceModel.serializer()),
                            foundDevices
                        )
                    )
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

