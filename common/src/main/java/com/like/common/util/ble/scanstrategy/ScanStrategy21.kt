package com.like.common.util.ble.scanstrategy

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback

@TargetApi(21)
class ScanStrategy21(private val callback: ScanCallback?) : IScanStrategy {

    override fun startScan(bluetoothAdapter: BluetoothAdapter?) {
        bluetoothAdapter?.bluetoothLeScanner?.startScan(callback)
    }

    override fun stopScan(bluetoothAdapter: BluetoothAdapter?) {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(callback)
    }

}