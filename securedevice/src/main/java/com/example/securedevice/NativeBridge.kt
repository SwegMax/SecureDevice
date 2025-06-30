package com.example.securedevice

object NativeBridge {
    init {
        System.loadLibrary("securedevice")
    }

    external fun isDeviceRooted(): Boolean
    external fun isLibraryTampered(libraryPath: String): Boolean
    external fun isMemoryInjected(): Boolean
}