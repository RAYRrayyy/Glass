/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";


    //<item uuid="0xAA70">Luxometer Service</item>
    //<item uuid="0xAA71" icon="lightsensor">Luxometer Data</item>
    //<item uuid="0xAA72">Luxometer Config</item>
    static {
        // Sample Services.
        //attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        //attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("f000aa70-0451-4000-b000-000000000000", "Luxometer Service");
        attributes.put("0000ffe0-0000-1000-8000-00805f9b34fb", "Simple Keys Service");
        attributes.put("f000aa00-0451-4000-b000-000000000000", "IR Temperature Service");

        // Sample Characteristics.
        //attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        //attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("f000aa71-0451-4000-b000-000000000000", "Luxometer Data");
        attributes.put("f000aa72-0451-4000-b000-000000000000", "Luxometer Config");
        attributes.put("f000aa73-0451-4000-b000-000000000000", "Luxometer Clock");
        attributes.put("0000ffe1-0000-1000-8000-00805f9b34fb", "Key Press State");
        attributes.put("f000aa01-0451-4000-b000-000000000000", "IR Temperature Data");
        attributes.put("f000aa02-0451-4000-b000-000000000000", "IR Temperature Config");
        attributes.put("f000aa03-0451-4000-b000-000000000000", "IR Temperature Clock");


    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
