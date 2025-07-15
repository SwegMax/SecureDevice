package com.example.securedevice.java;

public final class Constants {

    private Constants() {
    }

    public static final int KEY_SIZE = 128; //AES (128, 192, 256)
    public static final int T_LEN = 96; //GCM (Maybe no need so long/huge)
    public static final String AES_FILE_NAME = "aes_key.bin";
    public static final String PAYLOAD_FILE_NAME = "payload.txt";

    public static final class Encryption {
        public static final String AES = "AES";
        public static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
        public static final int IV_LENGTH_BYTES = 12;
    }
}
