package com.crc.blue.bluetooth

import java.util.HashMap

object SampleGattAttributes {
    var attributes: HashMap<String, String> = HashMap()
    var HEART_RATE_MEASUREMENT =                    "00002a37-0000-1000-8000-00805f9b34fb"
    var SERVICE_COLOR =                             "0000de10-0000-1000-8000-00805f9b34fb"
    var SERVICE_COLOR_DATA =                        "00002c40-0000-1000-8000-00805f9b34fb"
    var SERVICE_NOTI_DATA =                         "00002c41-0000-1000-8000-00805f9b34fb"


    init {
        // Sample Services.
        attributes["0000180d-0000-1000-8000-00805f9b34fb"] =  "Heart Rate Service"
        attributes["0000180a-0000-1000-8000-00805f9b34fb"] =  "Device Information Service"
        // Sample Characteristics.
        attributes[HEART_RATE_MEASUREMENT] =  "Heart Rate Measurement"
        attributes["00002a29-0000-1000-8000-00805f9b34fb"] =  "Manufacturer Name String"


        // Using unknown GATT profile, must debug other end
        attributes["19B10000-E8F2-537E-4F6C-D104768A1214"] = "ioTank"
        //color service
        attributes["0000de10-0000-1000-8000-00805f9b34fb"] = "Color Service"
        attributes["00002c40-0000-1000-8000-00805f9b34fb"] = "Color Number"
    }


    fun lookup(uuid: String, defaultName: String): String {
        val name = attributes.get(uuid)
        return name ?: defaultName
    }
}