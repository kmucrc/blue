package com.crc.blue.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.crc.blue.R
import com.crc.blue.base.Constants
import com.crc.blue.main.MainActivity
import kotlinx.android.synthetic.main.activity_connection.*
import org.jetbrains.anko.startActivity
import java.util.ArrayList
import java.util.HashMap

class ConnectionActivity : AppCompatActivity(), AdapterView.OnItemClickListener  {

    private var mLeBluetoothListAdapter: BluetoothListAdapter? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanning: Boolean = false
    private var mHandler: Handler? = null

    private var mDeviceName: String? = null
    private var mDeviceAddress: String? = null
    private var mBluetoothLeService: BluetoothLeService? = null
    private var mGattCharacteristics: ArrayList<ArrayList<BluetoothGattCharacteristic>>? = ArrayList()
    private var mConnected = false
    private var mNotifyCharacteristic: BluetoothGattCharacteristic? = null

    private val LIST_NAME = "NAME"
    private val LIST_UUID = "UUID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        setContentView(R.layout.activity_connection)

        mHandler = Handler()

        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.str_bluetooth_not_supported, Toast.LENGTH_SHORT).show()
            return
        }

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> when (grantResults) {
                intArrayOf(PackageManager.PERMISSION_GRANTED) -> {
                    Log.d("ScanDeviceActivity", "onRequestPermissionsResult(PERMISSION_GRANTED)")
//                    bluetoothLeScanner.startScan(bleScanner)
                }
                else -> {
                    Log.d("ScanDeviceActivity", "onRequestPermissionsResult(not PERMISSION_GRANTED)")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onStart() {
        super.onStart()
//        bluetoothLeScanner.startScan(bleScanner)
    }

    override fun onResume() {
        super.onResume()

        if (!mBluetoothAdapter!!.isEnabled) {
            if (!mBluetoothAdapter!!.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT)
            }
        }

        // Initializes list view adapter.

        mLeBluetoothListAdapter = BluetoothListAdapter(this)
        lvBluetoothList.adapter = mLeBluetoothListAdapter
        lvBluetoothList.setOnItemClickListener(this)
        scanLeDevice(true)

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
        if (mBluetoothLeService != null) {
            val result = mBluetoothLeService!!.connect(mDeviceAddress)
            Log.d(TAG, "Connect request result=" + result)
        }
    }

    override fun onPause() {
        super.onPause()
        scanLeDevice(false)
        mLeBluetoothListAdapter!!.clear()
        unregisterReceiver(mGattUpdateReceiver)
    }

    override fun onStop() {
        super.onStop()
//        bluetoothLeScanner.startScan(bleScanner)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
        mBluetoothLeService = null

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {

            Log.e("eleutheria", "onActivityResult REQUEST_ENABLE_BT : RESULT_CANCELED")
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler!!.postDelayed({
                mScanning = false
                mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
                invalidateOptionsMenu()
            }, Constants.SCAN_PERIOD)

            mScanning = true
            mBluetoothAdapter!!.startLeScan(mLeScanCallback)
        } else {
            mScanning = false
            mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
        }
    }

    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        runOnUiThread {
            mLeBluetoothListAdapter!!.addDevice(device)
            mLeBluetoothListAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val device = mLeBluetoothListAdapter!!.getDevice(position) ?: return

        mDeviceName = device.name
        mDeviceAddress = device.address

        mBluetoothLeService!!.connect(mDeviceAddress)

        if(mScanning) {
            mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
            mScanning = false
        }
    }

    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            if (!mBluetoothLeService!!.initialize()) {
                Log.e("eleutheria", "Unable to initialize Bluetooth")
                finish()
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService!!.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }
    }

    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothLeService.ACTION_GATT_CONNECTED == action) {
                mConnected = true
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED == action) {
                mConnected = false
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED == action) {
                // Show all the supported services and characteristics on the user interface.
                addGattServices(mBluetoothLeService!!.supportedGattServices)
                activeNotification()
                moveMainActivity()
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE == action) {
                parsingData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
            }
        }
    }

    private fun parsingData(data: String?) {
        if (data != null) {
//            Log.e("eleutheria", "data : ${data}")

            //System.out.println(data);
        }
    }

    private fun addGattServices(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) return
        var uuid: String? = null
        val unknownServiceString = resources.getString(R.string.str_bluetooth_unknown_service)
        val unknownCharaString = resources.getString(R.string.str_bluetooth_unknown_characteristic)
        val gattServiceData = ArrayList<HashMap<String, String>>()
        val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String>>>()
        mGattCharacteristics = ArrayList<ArrayList<BluetoothGattCharacteristic>>()

        // Loops through available GATT Services.
        for (gattService in gattServices) {
            val currentServiceData = HashMap<String, String>()
            uuid = gattService.uuid.toString()
            Log.e("eleutheria", "uuid : " + uuid)
            println(uuid)
            currentServiceData.put(
                LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString)
            )
            currentServiceData.put(LIST_UUID, uuid)
            gattServiceData.add(currentServiceData)

            val gattCharacteristicGroupData = ArrayList<HashMap<String, String>>()
            val gattCharacteristics = gattService.characteristics
            val charas = ArrayList<BluetoothGattCharacteristic>()

            // Loops through available Characteristics.
            for (gattCharacteristic in gattCharacteristics) {
                charas.add(gattCharacteristic)
                val currentCharaData = HashMap<String, String>()
                uuid = gattCharacteristic.uuid.toString()
                println(uuid)
                println(currentCharaData)

                currentCharaData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString)
                )
                currentCharaData.put(LIST_UUID, uuid)
                gattCharacteristicGroupData.add(currentCharaData)
            }
            mGattCharacteristics!!.add(charas)
            gattCharacteristicData.add(gattCharacteristicGroupData)
        }
    }

    private fun activeNotification() {
        if (mGattCharacteristics != null) {
            val characteristic = mGattCharacteristics!![2][0]
            val charaProp = characteristic.properties
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService!!.setCharacteristicNotification(
                        mNotifyCharacteristic!!, false)
                    mNotifyCharacteristic = null
                }
                mBluetoothLeService!!.readCharacteristic(characteristic)
            }
            if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                mNotifyCharacteristic = characteristic
                mBluetoothLeService!!.setCharacteristicNotification(
                    characteristic, true)
            }
        }
    }


    private fun moveMainActivity() {
        startActivity<MainActivity>()
    }

    companion object {
        private val TAG = ConnectionActivity::class.java!!.getSimpleName()


        @JvmField var EXTRAS_DEVICE_NAME = "DEVICE_NAME"
        @JvmField var EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"

        private fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
            return intentFilter
        }
    }

}