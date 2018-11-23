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

package app.json.com.myjson.Bluetooth;
import java.util.HashMap;
import java.util.UUID;

import app.json.com.myjson.SampleActivity;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<UUID, String> attributes = new HashMap();
    //public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_SERVICE_ID="a0cd417e-100b-4319-90fb-08c7ea201e52";
    public static String DEVICE_CHAR_ID="00001800-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.

        attributes.put(SampleActivity.UUID_DEVICE_SERVICE_ID,"Device Service id");
        attributes.put(SampleActivity.UUID_DEVICE_CHARACTRISTICS_ID,"Device Characteristice");
        attributes.put(SampleActivity.UUID_CLIENT_CHARACTERISTIC_CONFIG,"Client Characteristics Id");
//        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
//        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
//        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
//        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    public static String lookupUUID(UUID uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
