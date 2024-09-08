package com.example.flutter_blue_esp.models

import android.util.Log
import com.example.flutter_blue_esp.constants.Configs
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/**
 * Combined data from flutter channel call.
 *
 * [success] - send success with [Any] type result via channel.
 * [error] - send error via channel.
 */
class CallContext(private val call: MethodCall, private val res: MethodChannel.Result) {
    fun getArg(name: String): String? {
        val data = call.argument<String>(name)
        if (data == null) {
            Log.d(Configs.log.PLUGIN_TAG, "Missing argument in call")
            res.error(
                Configs.errors.MISSING_ARGUMENT_CODE,
                Configs.errors.MISSING_ARGUMENT_MESSAGE(name),
                null
            )
        }
        return data
    }

    fun success(result: Any) {
        res.success(result)
    }

    fun error(code: String? = null, message: String? = null, details: Any? = null) {
        res.error(
            code ?: Configs.errors.UNKNOWN_CODE,
            message ?: Configs.errors.NO_DESCRIPTION_PROVIDED,
            details
        )
    }
}
