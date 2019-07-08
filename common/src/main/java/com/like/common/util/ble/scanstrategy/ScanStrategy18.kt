package com.like.common.util.ble.scanstrategy

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter

@TargetApi(18)
class ScanStrategy18(private val callback: BluetoothAdapter.LeScanCallback?) : IScanStrategy {

    override fun startScan(bluetoothAdapter: BluetoothAdapter?) {
        bluetoothAdapter?.startLeScan(callback)
    }

    override fun stopScan(bluetoothAdapter: BluetoothAdapter?) {
        bluetoothAdapter?.stopLeScan(callback)
    }

}