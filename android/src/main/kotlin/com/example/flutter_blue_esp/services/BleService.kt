package com.example.flutter_blue_esp.services

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.espressif.provisioning.DeviceConnectionEvent
import com.espressif.provisioning.ESPConstants
import com.espressif.provisioning.ESPDevice
import com.espressif.provisioning.ESPProvisionManager
import com.espressif.provisioning.WiFiAccessPoint
import com.espressif.provisioning.listeners.BleScanListener
import com.espressif.provisioning.listeners.ProvisionListener
import com.espressif.provisioning.listeners.WiFiScanListener
import com.example.flutter_blue_esp.constants.Configs
import com.example.flutter_blue_esp.models.CallContext
import com.example.flutter_blue_esp.models.DeviceModel
import com.example.flutter_blue_esp.models.WiFiModel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

class BleService {

    private lateinit var platformContext: Context
    private var permissionService: PermissionService = PermissionService()
    private var platformActivity: Activity? = null
    private val espManager: ESPProvisionManager
        get() = ESPProvisionManager.getInstance(
            platformContext
        )

    class DeviceScanResult(val device: BluetoothDevice, val scan: ScanResult)

    private val scannedDevices = mutableMapOf<String, DeviceScanResult>()

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
            Configs.methods.SCAN_DEVICE_WIFI -> scanDeviceWifi(CallContext(call, result))
            Configs.methods.PROVISION_DEVICE -> provisionDevice(CallContext(call, result))
            else -> result.notImplemented()
        }

    }

    private fun connect(
        deviceName: String,
        proofOfPossession: String,
        ctx: CallContext,
        onConnectCallback: (ESPDevice) -> Unit,
    ) {
        if (!scannedDevices.containsKey(deviceName)) {
            ctx.error(
                code = Configs.errors.CONNECT_DEVICE_NOT_FOUND_CODE,
                message = Configs.errors.CONNECT_DEVICE_NOT_FOUND_MESSAGE(deviceName)
            )

            return
        }

        val espDevice = espManager.createESPDevice(
            ESPConstants.TransportType.TRANSPORT_BLE, ESPConstants.SecurityType.SECURITY_1
        )

        EventBus.getDefault().register(object {
            @Subscribe(threadMode = ThreadMode.MAIN)
            fun onEvent(event: DeviceConnectionEvent) {
                Log.d(Configs.log.PLUGIN_TAG, "Connect bus event $event ${event.eventType}")
                when (event.eventType) {
                    ESPConstants.EVENT_DEVICE_CONNECTED -> {
                        EventBus.getDefault().unregister(this)
                        espDevice.proofOfPossession = proofOfPossession
                        onConnectCallback(espDevice)
                    }
                }
            }
        })

        // primaryServiceUuid check
        espDevice.connectBLEDevice(
            scannedDevices[deviceName]!!.device,
            scannedDevices[deviceName]!!.scan.scanRecord?.serviceUuids?.get(0)?.toString()
        )
    }

    private fun scanDevices(ctx: CallContext) {
        val prefix = ctx.getArg("prefix") ?: return;
        val foundDevices = mutableListOf<DeviceModel>()
        scannedDevices.clear()

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

                    scannedDevices[name] = DeviceScanResult(var1, var2!!)
                    foundDevices.add(DeviceModel(name, address))
                }

                override fun scanCompleted() {
                    ctx.success(
                        Json.encodeToString(
                            ListSerializer(DeviceModel.serializer()), foundDevices
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


    private fun scanDeviceWifi(ctx: CallContext) {
        val deviceName = ctx.getArg("deviceName") ?: return;
        val proofOfPossession = ctx.getArg("proofOfPossession") ?: return;

        Log.d(
            Configs.log.PLUGIN_TAG,
            "scanDeviceWifi - deviceName: $deviceName\n proofOfPossession: $proofOfPossession"
        )

        try {
            connect(
                deviceName,
                proofOfPossession,
                ctx,
            ) { espDevice ->
                espDevice.scanNetworks(object : WiFiScanListener {
                    override fun onWifiListReceived(p0: ArrayList<WiFiAccessPoint>?) {
                        Log.d(Configs.log.PLUGIN_TAG, "onWifiListReceived - $p0")
                        if (p0?.isEmpty() != false) {
                            ctx.success({})
                        }

                        val scannedWiFis = p0!!.map {
                            WiFiModel(it.wifiName)
                        }

                        Handler(Looper.getMainLooper()).post {
                            ctx.success(
                                Json.encodeToString(
                                    ListSerializer(WiFiModel.serializer()), scannedWiFis
                                )
                            )
                        }
                    }

                    override fun onWiFiScanFailed(p0: java.lang.Exception?) {
                        Log.d(Configs.log.PLUGIN_TAG, "searchBleEspDevices - onFailure $p0")
                        ctx.error(
                            code = Configs.errors.FAIL_SCAN_DEVICE_WIFI_CODE,
                            message = Configs.errors.FAIL_SCAN_DEVICE_WIFI_MESSAGE,
                            details = p0
                        )
                    }
                })
            }

        } catch (e: Exception) {
            Log.d(Configs.log.PLUGIN_TAG, "scanDeviceWifi - exception: $e")
            ctx.error(
                code = Configs.errors.UNKNOWN_CODE,
                message = Configs.errors.UNKNOWN_MESSAGE,
                details = e
            )
        }
    }

    private fun provisionDevice(ctx: CallContext) {
        val ssid = ctx.getArg("ssid") ?: return
        val passphrase = ctx.getArg("passphrase") ?: return
        val deviceName = ctx.getArg("deviceName") ?: return
        val proofOfPossession = ctx.getArg("proofOfPossession") ?: return


        Log.d(
            Configs.log.PLUGIN_TAG,
            "provisionDevice - ssid: $ssid\n passphrase: $passphrase deviceName: $deviceName\n proofOfPossession: $proofOfPossession"
        )

        try {
            connect(
                deviceName, proofOfPossession, ctx
            ) { espDevice ->
                espDevice.provision(ssid, passphrase, object : ProvisionListener {
                    override fun createSessionFailed(p0: java.lang.Exception?) {
                        Log.d(Configs.log.PLUGIN_TAG, "provisionDevice - exception: $p0")
                        ctx.error(
                            code = Configs.errors.PROVISIONING_FAILED_TO_CREATE_SESSION_CODE,
                            message = Configs.errors.PROVISIONING_FAILED_TO_CREATE_SESSION_MESSAGE,
                            details = p0
                        )
                    }

                    override fun wifiConfigSent() {
                        Log.d(Configs.log.PLUGIN_TAG, "provisionDevice - wifiConfigSent")
                    }

                    override fun wifiConfigFailed(p0: java.lang.Exception?) {
                        Log.d(Configs.log.PLUGIN_TAG, "provisionDevice - exception: $p0")
                        ctx.error(
                            code = Configs.errors.PROVISIONING_WIFI_CONFIG_FAILED_CODE,
                            message = Configs.errors.PROVISIONING_WIFI_CONFIG_FAILED_MESSAGE,
                            details = p0
                        )
                    }

                    override fun wifiConfigApplied() {
                        Log.d(Configs.log.PLUGIN_TAG, "provisionDevice - wifiConfigApplied")
                    }

                    override fun wifiConfigApplyFailed(p0: java.lang.Exception?) {
                        Log.d(Configs.log.PLUGIN_TAG, "provisionDevice - exception: $p0")
                        ctx.error(
                            code = Configs.errors.PROVISIONING_WIFI_APPLY_FAILED_CODE,
                            message = Configs.errors.PROVISIONING_WIFI_APPLY_FAILED_MESSAGE,
                            details = p0
                        )
                    }

                    override fun provisioningFailedFromDevice(p0: ESPConstants.ProvisionFailureReason?) {
                        Log.d(Configs.log.PLUGIN_TAG, "provisionDevice - exception: $p0")
                        ctx.error(
                            code = Configs.errors.PROVISIONING_DEVICE_FAILED_CODE,
                            message = Configs.errors.PROVISIONING_DEVICE_FAILED_MESSAGE,
                            details = p0
                        )
                    }

                    override fun deviceProvisioningSuccess() {
                        Log.d(Configs.log.PLUGIN_TAG, "provisionDevice - deviceProvisioningSuccess")
                        ctx.success(true)
                    }

                    override fun onProvisioningFailed(p0: java.lang.Exception?) {
                        Log.d(Configs.log.PLUGIN_TAG, "provisionDevice - exception: $p0")
                        ctx.error(
                            code = Configs.errors.PROVISIONING_FAILED_CODE,
                            message = Configs.errors.PROVISIONING_FAILED_MESSAGE,
                            details = p0
                        )
                    }
                })
            }
        } catch (e: Exception) {
            Log.d(Configs.log.PLUGIN_TAG, "provisionDevice - exception: $e")
            ctx.error(
                code = Configs.errors.UNKNOWN_CODE,
                message = Configs.errors.UNKNOWN_MESSAGE,
                details = e
            )
        }
    }
}

