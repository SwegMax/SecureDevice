package com.example.securedevice

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.example.securedevice.NativeBridge.isDeviceRooted
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
    //17 - "C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
    //8 - "C:\Program Files\Eclipse Adoptium\jdk-8.0.452.9-hotspot"
}
