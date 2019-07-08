package com.like.common.util.ble.scanstrategy

import android.bluetooth.BluetoothAdapter

interface IScanStrategy {
    fun startScan(bluetoothAdapter: BluetoothAdapter?)
    fun stopScan(bluetoothAdapter: BluetoothAdapter?)
}