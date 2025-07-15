package com.example.securedevice.java;

import static com.example.securedevice.java.Constants.Encryption.AES;
import static com.example.securedevice.java.Constants.AES_FILE_NAME;
import static com.example.securedevice.java.Constants.KEY_SIZE;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyManager {
    private final Context context;
    private final FileManager fileManager;
    private SecretKey key;
    private byte[] IV;

    public KeyManager(Context context) {
        this.context = context;
        this.fileManager = new FileManager();
    }

    private SecretKey generateAESKey() throws Exception{
        KeyGenerator generator = KeyGenerator.getInstance(AES);
        generator.init(KEY_SIZE);
        key = generator.generateKey();
        fileManager.writeInBytes(context, AES_FILE_NAME, key.getEncoded());
        return key;
    }

    public SecretKey getAESKey() throws Exception {
        File keyFile = new File(context.getFilesDir(), AES_FILE_NAME);
        if(keyFile.exists() && keyFile.length() > 0) {
            byte[] keyBytes = new byte[(int) keyFile.length()];
            try (FileInputStream fileInputStream = new FileInputStream(keyFile)) {
                int bytesRead = fileInputStream.read(keyBytes);
                if (bytesRead == keyBytes.length) {
                    return new SecretKeySpec(keyBytes, AES);
                } else {
                    keyFile.delete();
                }
            } catch (Exception e) { keyFile.delete(); }
        }
        return generateAESKey();
    }



    public byte[] generateIV(){
        IV = new byte[12];
        new SecureRandom().nextBytes(IV);
        return IV;
    }

    public String encode(byte[] data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(data);
        }
        else {
            return android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
        }
    }

    public byte[] decode(String data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getDecoder().decode(data);
        }
        else {
            return android.util.Base64.decode(data, android.util.Base64.DEFAULT);
        }
    }
}
