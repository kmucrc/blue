package com.crc.blue.bluetooth

import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.crc.blue.base.Constants
import java.util.*

class BluetoothLeService: Service() {

    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothDeviceAddress: String? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mConnectionState = STATE_DISCONNECTED
    private var clickTimer = Timer()
    private var isStartTimer = false
    private var nClickAction = 0

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private val mGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val intentAction: String
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED
                mConnectionState = STATE_CONNECTED
                broadcastUpdate(intentAction)
                Log.i(TAG, "Connected to GATT server.")
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt!!.discoverServices())

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED
                mConnectionState = STATE_DISCONNECTED
                Log.i(TAG, "Disconnected from GATT server.")
                broadcastUpdate(intentAction)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status)
            }
            setHearBeatNotification(true)
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt,
                                          characteristic: BluetoothGattCharacteristic,
                                          status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt,
                                             characteristic: BluetoothGattCharacteristic
        ) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)
        var inData : String = ""

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml

        if (UUID.fromString(SampleGattAttributes.SERVICE_NOTI_DATA) == characteristic.uuid) {
            val data = characteristic.value
            if (data != null && data.size > 0) {
                val stringBuilder = StringBuilder(data.size)
                for (byteChar in data)
                    stringBuilder.append(String.format("%02X ", byteChar))
                inData = stringBuilder.toString()
                Log.d(TAG, "Received: $inData")
            }
            if (inData.contains("01")) {
                // ConnectionScene.actions("SingleClick");
                //                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                //                try{
                //                    Thread.sleep(1000);
                //                }catch (InterruptedException e){ }
                //                if(Constants.beforSignal.equals("01"))
                //                    Constants.beforSignal ="03";
                //                else
                Constants.beforSignal = "01"
            }
            if (inData.contains("02")) {
                Constants.beforSignal = "02"//ConnectionScene.actions("Hold");
            }
            if (inData.contains("00")) {
                if (Constants.beforSignal.contains("01")) {
                    //                    ConnectionScene.actions("SingleClick");
                    val time = System.currentTimeMillis()
                    val elapsedtime = time - Constants.beforClickTime

                    if (isStartTimer) {
                        nClickAction = 2
                    } else {
                        isStartTimer = true
                        nClickAction = 1
                        clickTimer = Timer()
                        val clickTimerTask = object : TimerTask() {

                            override fun run() {
                                if (nClickAction == 2) {
                                    Log.e("eleutheria", "DoubleClick")
                                    ConnectionScene.actions("DoubleClick")
                                } else {
                                    Log.e("eleutheria", "SingleClick")
                                    ConnectionScene.actions("SingleClick")
                                }
                                nClickAction = 0
                                isStartTimer = false
                                clickTimer.cancel()
                                clickTimer.purge()

                            }
                        }
                        clickTimer.schedule(clickTimerTask, 500)
                    }

                    //                    Log.e("eleutheria", "time : %ld" + elapsedtime);
                    //                    if((time-Constants.beforClickTime)/1000.0 < 1){
                    //                        ConnectionScene.actions("DoubleClick");
                    //                    }else{
                    //                        ConnectionScene.actions("SingleClick");
                    //                    }
                    Constants.beforClickTime = time
                }
                if (Constants.beforSignal.contains("02")) {
                    ConnectionScene.actions("Hold")
                    //                }if(Constants.beforSignal.contains("03")){
                    //                    ConnectionScene.actions("DoubleClick");
                }
                Constants.beforSignal = "00"
                //다시 색 출력
                val chooseColor = Constants.basicColor.substring(1)
                var co = "11"
                for (i in 2..7) {
                    val a = 0xf - Integer.parseInt(chooseColor.get(i).toString(), 16)
                    co += Integer.toHexString(a)
                }
                writeColorCharacteristic(co)
                Log.i("BLEService", "writeColor")
            }
            intent.putExtra(EXTRA_DATA, inData.toString())
        }
        sendBroadcast(intent)

    }

    inner class LocalBinder : Binder() {
        internal val service: BluetoothLeService
            get() = this@BluetoothLeService
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close()
        return super.onUnbind(intent)
    }

    private val mBinder = LocalBinder()

    /**
     * Initializes a reference to the local Bluetooth adapter.

     * @return Return true if the initialization is successful.
     */
    fun initialize(): Boolean {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.")
                return false
            }
        }

        mBluetoothAdapter = mBluetoothManager!!.adapter
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }

        return true
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.

     * @param address The device address of the destination device.
     * *
     * *
     * @return Return true if the connection is initiated successfully. The connection result
     * *         is reported asynchronously through the
     * *         `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * *         callback.
     */
    fun connect(address: String?): Boolean {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.")
            return false
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address == mBluetoothDeviceAddress
            && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.")
            if (mBluetoothGatt!!.connect()) {
                mConnectionState = STATE_CONNECTING
                return true
            } else {
                return false
            }
        }

        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.")
            return false
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback)
        Log.d(TAG, "Trying to create a new connection.")
        mBluetoothDeviceAddress = address
        mConnectionState = STATE_CONNECTING
        return true
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    fun disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.disconnect()
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    fun close() {
        if (mBluetoothGatt == null) {
            return
        }
        mBluetoothGatt!!.close()
        mBluetoothGatt = null
    }

    /**
     * Request a read on a given `BluetoothGattCharacteristic`. The read result is reported
     * asynchronously through the `BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)`
     * callback.

     * @param characteristic The characteristic to read from.
     */
    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.readCharacteristic(characteristic)
    }

    /**
     * Enables or disables notification on a give characteristic.

     * @param characteristic Characteristic to act on.
     * *
     * @param enabled If true, enable notification.  False otherwise.
     */
    fun setCharacteristicNotification(characteristic: BluetoothGattCharacteristic,
                                      enabled: Boolean) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.setCharacteristicNotification(characteristic, enabled)

        // This is specific to Heart Rate Measurement.
//        if (UUID_HEART_RATE_MEASUREMENT == characteristic.uuid) {
//            val descriptor = characteristic.getDescriptor(
//                UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG))
//            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
//            mBluetoothGatt!!.writeDescriptor(descriptor)
//        }
    }

    fun setHearBeatNotification(enabled: Boolean) {
        val uuidHBCharateristic = SampleGattAttributes.SERVICE_NOTI_DATA

        var mBluetoothLeService: BluetoothGattService? = null
        var mBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null

        for(service: BluetoothGattService in mBluetoothGatt!!.getServices()) {
            if((service==null)||(service.uuid==null)) continue
            if(SampleGattAttributes.SERVICE_COLOR.equals(service.uuid.toString(), true)) {
                mBluetoothLeService = service
            }
        }
        if(mBluetoothLeService != null) {
            mBluetoothGattCharacteristic = mBluetoothLeService.getCharacteristic(UUID.fromString(uuidHBCharateristic))
        } else {
            Log.e("eleuthria", "mBluetoothLeService is null")
        }

        if(mBluetoothGattCharacteristic != null) {
            setCharacteristicNotification(mBluetoothGattCharacteristic, enabled)
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after `BluetoothGatt#discoverServices()` completes successfully.

     * @return A `List` of supported services.
     */
    val supportedGattServices: List<BluetoothGattService>?
        get() {
            if (mBluetoothGatt == null) return null

            return mBluetoothGatt!!.services
        }

    companion object {
        private val TAG = BluetoothLeService::class.java.simpleName

        private val STATE_DISCONNECTED = 0
        private val STATE_CONNECTING = 1
        private val STATE_CONNECTED = 2

        val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        val ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
        val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
        val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"

        val UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT)
    }
}