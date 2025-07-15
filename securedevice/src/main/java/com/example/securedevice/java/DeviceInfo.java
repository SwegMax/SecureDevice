package com.example.securedevice.java;

import static com.example.securedevice.java.Constants.Encryption.AES_GCM_NO_PADDING;
import static com.example.securedevice.java.Constants.Encryption.IV_LENGTH_BYTES;
import static com.example.securedevice.java.Constants.PAYLOAD_FILE_NAME;
import static com.example.securedevice.java.Constants.T_LEN;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.GCMParameterSpec;

public class DeviceInfo {
    private final Context context;
    private final ExecutorService executorService;

    private final DataRepository dataRepository;
    private final KeyManager keyManager;
    private final FileManager fileManager;
    public static String encryptedPayload;

    public DeviceInfo(Context context) {
        this.context = context;
        this.executorService = Executors.newFixedThreadPool(2);

        this.keyManager = new KeyManager(context);
        this.fileManager = new FileManager();
        this.dataRepository = new DataRepository(context);
    }

    public interface DataCollectionCallback {
        void onDataCollected(boolean success);
    }

    public void collectDeviceSignature(String screenName, DataCollectionCallback callback) {
        executorService.execute(() -> {
            boolean success = false;

            try {
                Future<Map<String, String>> deviceIdFuture = executorService.submit(dataRepository::getDeviceIdentifiers);
                Future<Map<String, String>> networkInfoFuture = executorService.submit(dataRepository::getNetworkInformation);

                Map<String, String> deviceNetworkData = new HashMap<>();
                deviceNetworkData.putAll(deviceIdFuture.get());
                deviceNetworkData.putAll(networkInfoFuture.get());
                //Add the screenName to that map with the key "Screen_Name"
                //Convert it to json format and encrypt the json with encryptPayload method
                //and save the result to a file
                deviceNetworkData.put("Screen_Name", screenName);

                JSONObject jsonObject = new JSONObject(deviceNetworkData);
                String jsonString = jsonObject.toString();

                encryptedPayload = encryptPayload(jsonString);
                //If there are previous data in the file, append the new data to the end of the file
                fileManager.appendString(context, PAYLOAD_FILE_NAME, encryptedPayload);

                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                final boolean finalSuccess = success;
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onDataCollected(finalSuccess);
                    });
                }
            }
        });
    }

    public String getDeviceSignature(String encryptedPayload) {
        //Read the latest data saved in the file, decrypt it using decryptPayload method and
        //return it as a Json String
        //If there are more than one data stored in the file, get the most recent data
        String payload = fileManager.readString(context, PAYLOAD_FILE_NAME);
        try {
            return decryptPayload(payload);
        } catch (Exception e) {
            return "Error decrypting payload";
        }
    }


    private String encryptPayload(String message) throws Exception{
        byte[] messageInBytes = message.getBytes(StandardCharsets.UTF_8);

        byte[] IV = keyManager.generateIV();

        Cipher encryptionCipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, keyManager.getAESKey(), spec);

        byte[] AESCipherBytes = encryptionCipher.doFinal(messageInBytes);

        //plaintext + AES key -> cipherText + IV -> encode & return
        ByteBuffer byteBuffer = ByteBuffer.allocate(IV.length + AESCipherBytes.length);
        byteBuffer.put(IV);
        byteBuffer.put(AESCipherBytes);
        byte[] AESGCMCipherBytes = byteBuffer.array();

        return keyManager.encode(AESGCMCipherBytes);
    }

    private String decryptPayload(String message) throws Exception{
        byte[] decodedMessage = keyManager.decode(message);

        byte[] IV = new byte[IV_LENGTH_BYTES];
        System.arraycopy(decodedMessage, 0, IV, 0, IV_LENGTH_BYTES);

        byte[] cipherText = new byte[decodedMessage.length - IV_LENGTH_BYTES];
        System.arraycopy(decodedMessage, IV_LENGTH_BYTES, cipherText, 0, cipherText.length);

        Cipher decryptionCipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        decryptionCipher.init(Cipher.DECRYPT_MODE, keyManager.getAESKey(), spec);

        byte[] decryptedBytes;
        try {
            decryptedBytes = decryptionCipher.doFinal(cipherText);
        } catch (AEADBadTagException e) {
            throw new SecurityException("Authentication tag mismatch- key/IV is incorrect.", e);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new SecurityException("Bad padding or block size.", e);
        }

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
