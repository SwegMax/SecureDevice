package com.example.securedevice.java;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileManager {

    public void writeInBytes(Context context, String fileName, byte[] data) throws Exception{
        File keyFile = new File(context.getFilesDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(keyFile)) {
            fos.write(data);
        }
    }

    public void appendString(Context context, String fileName, String encryptedMsg) throws Exception{
        File stringFile = new File(context.getFilesDir(), fileName);

        FileWriter writer = new FileWriter(stringFile, true);
        writer.append(encryptedMsg).append("\n");
        writer.flush();
        writer.close();
    }



    public String readString(Context context, String fileName) {
        File path = context.getApplicationContext().getFilesDir();
        File readFrom = new File(path, fileName);
        if (!readFrom.exists() || readFrom.length() == 0) {
            return "TestData not found";
        }

        String lastLine = "TestData not found";
        try (FileInputStream fis = new FileInputStream(readFrom);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lastLine = line;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastLine;
    }
}