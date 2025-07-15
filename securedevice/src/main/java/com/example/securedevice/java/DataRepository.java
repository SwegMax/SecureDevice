package com.example.securedevice.java;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class DataRepository {
    private Context context;
    public DataRepository(Context context) {
        this.context = context;
    }

    public Map<String, String> getDeviceIdentifiers()
    {
        //Doing some long running tasks
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, String> deviceIdentifiers = new HashMap<>();
        deviceIdentifiers.put("Android_ID", getAndroidID());
        deviceIdentifiers.put("Device_Serial", getDeviceSerial());
        return deviceIdentifiers;
    }

    public Map<String, String> getNetworkInformation() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, String> networkInformation = new HashMap<>();
        networkInformation.put("Carrier", "SINGTEL");

        networkInformation.put("Network_Type", "GSM");

        return networkInformation;
    }
    private String getAndroidID() {
        return "dfee17ed-a492-4eef-9c1f-ce5e0f1e0774";
        //get the actual
    }

    private String getDeviceSerial() {
        return "e513b9b5-51b6-4782-b403-14fe23a90d4e";
    }
}
