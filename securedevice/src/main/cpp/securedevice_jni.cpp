#include "securedevice_jni.h"
#include <jni.h>
#include <fstream>
#include <android/log.h>
#include <sys/mman.h>

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_securedevice_NativeBridge_isDeviceRooted(JNIEnv* env, jobject) {
    std::ifstream su("/system/xbin/su");
    bool is_rooted = su.good();
    __android_log_print(ANDROID_LOG_INFO, "isDeviceRooted", "Device rooted check: %d", is_rooted);

    return su.good() ? JNI_TRUE : JNI_FALSE;
}


//usually a more complicated hashing algorithm to check
//but for demo purposes first
long calculate_checksum(const std::string& filepath) {
    std::ifstream file(filepath, std::ios::binary);
    long checksum = 0;
    char byte;
    while (file.get(byte)) {
        checksum ^= byte;
    }
    return checksum;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_securedevice_NativeBridge_isLibraryTampered(JNIEnv* env, jobject, jstring libraryPath) {
    const char* path_str = env->GetStringUTFChars(libraryPath, nullptr);
    std::string path(path_str);
    env->ReleaseStringUTFChars(libraryPath, path_str);

    long expected_checksum = 12345;
    long current_checksum = calculate_checksum(path);

    __android_log_print(ANDROID_LOG_INFO, "isLibraryTampered", "Expected: %ld, Current: %ld", expected_checksum, current_checksum);

    return current_checksum != expected_checksum;
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_securedevice_NativeBridge_isMemoryInjected(JNIEnv* env, jobject) {
    volatile char* p = (volatile char*)0x1000;
    bool result = false;
    __android_log_print(ANDROID_LOG_INFO, "isMemoryInjected", "Attempting to read from protected memory address...");
    if (mprotect((void*)0x1000, 4096, PROT_READ) == 0) {
        char value = *p;
        result = true;
    }

    return result;
}