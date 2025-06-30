cmake_minimum_required(VERSION 3.10)
project(SecureDevice)

add_library(native-lib SHARED securedevice_jni.cpp)

find_library(log-lib log)

target_link_libraries(native-lib ${log-lib})
