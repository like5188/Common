package com.like.common.util.ble.model

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope

abstract class BleCommand(val address: String) {
    lateinit var mLiveData: MutableLiveData<BleResult>

    internal open fun write(coroutineScope: CoroutineScope, bluetoothGatt: BluetoothGatt?) {}

    internal open fun connect(coroutineScope: CoroutineScope, gattCallback: BluetoothGattCallback, bluetoothAdapter: BluetoothAdapter?, disconnect: () -> Unit) {}
}


