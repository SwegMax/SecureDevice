#ifndef SECUREDEVICE_SECUREDEVICE_JNI_H
#define SECUREDEVICE_SECUREDEVICE_JNI_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jboolean JNICALL Java_com_example_securedevice_NativeBridge_isDeviceRooted(JNIEnv* env, jobject thiz);
JNIEXPORT jboolean JNICALL Java_com_example_securedevice_NativeBridge_isLibraryTampered(JNIEnv* env, jobject thiz, jstring libraryPath);
JNIEXPORT jboolean JNICALL Java_com_example_securedevice_NativeBridge_isMemoryInjected(JNIEnv* env, jobject thiz);

#ifdef __cplusplus
}
#endif

#endif