package com.example.securedevice

import android.content.Context
import android.os.Build
import android.provider.Settings
import java.io.File

class SecureDevice {
    fun collectSecurityInfo(context: Context): Map<String, Any> {
        val info = mutableMapOf<String, Any>()

        info["manufacturer"] = Build.MANUFACTURER
        info["model"] = Build.MODEL
        info["isRooted"] = isDeviceRooted()

        val isDevMode = try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
            ) != 0
        } catch (e: Exception) {
            false
        }
        info["developerMode"] = isDevMode

        return info
    }

    private fun isDeviceRooted(): Any {
        return try {
            val file = File("/system/xbin/su")
            file.exists()
        } catch (e: Exception) {
            false
        }
    }
}
