package com.example.securedevice

object JNIHelper {
    init {
        System.loadLibrary("native-lib")
    }

    external fun isDeviceRooted(): Boolean
}
