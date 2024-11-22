package com.example.flutter_blue_esp.services

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import com.example.flutter_blue_esp.constants.Configs

internal class PermissionService {
    /**
     * Required permissions for the current version of the SDK.
     */
    private val permissions: Array<String>
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN
                )
            }
        }

    fun ensurePermissionsGranted(activity: Activity): Boolean {
        val requestPermissions: MutableList<String> = mutableListOf()

        for (permission in permissions) {

            if (ActivityCompat.checkSelfPermission(
                    activity.applicationContext, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(Configs.log.PLUGIN_TAG, "ensurePermissionsGranted - not granted $permission")
                requestPermissions.add(permission)
            }
        }

        if (requestPermissions.isEmpty()) return true

        ActivityCompat.requestPermissions(activity, requestPermissions.toTypedArray(), 0)

        return false
    }
}