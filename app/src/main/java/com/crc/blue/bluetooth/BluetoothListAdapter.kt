package com.crc.blue.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.crc.blue.R

class BluetoothListAdapter (val context: Context): BaseAdapter() {
    private val mLeDevicesList: ArrayList<BluetoothDevice>
    private val mInflator: LayoutInflater
    private val mContext: Context

    init {
        mContext = context
        mLeDevicesList = ArrayList<BluetoothDevice>()
        mInflator = LayoutInflater.from(context)
    }

    fun addDevice(device: BluetoothDevice) {
        if(!mLeDevicesList.contains(device)) {
            mLeDevicesList.add(device)
        }
    }

    fun getDevice(position: Int): BluetoothDevice? {
        return mLeDevicesList[position]
    }

    fun clear() {
        mLeDevicesList.clear()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = LayoutInflater.from(context).inflate(R.layout.item_listview_bluetooth, null)

        val tvDeviceName = view.findViewById<TextView>(R.id.tvDeviceName)

        val tvDeviceAddress =  view.findViewById<TextView>(R.id.tvDeviceAddress)

        val device = mLeDevicesList[position]

        val strName: String = if(device.name != null) {
            device.name
        } else {
            mContext.getString(R.string.str_common_unknown)
        }
        tvDeviceName.setText(strName)
        tvDeviceAddress.setText(device.address)

        return view
    }

    override fun getItem(position: Int): Any {
        return mLeDevicesList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return mLeDevicesList.size
    }
}