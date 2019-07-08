package com.like.common.util.ble.blestate

import android.arch.lifecycle.MutableLiveData
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import com.like.common.util.ble.model.BleCommand
import com.like.common.util.ble.model.BleResult
import com.like.common.util.ble.model.BleStatus
import com.like.common.util.shortToastCenter

/**
 * 蓝牙尚未初始化的状态，可以进行初始化
 */
class NotInitState(
        private val context: Context,
        private val bleResultLiveData: MutableLiveData<BleResult>
) : IBleState {

    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null

    override fun init() {
        bleResultLiveData.postValue(BleResult(BleStatus.INIT))

        // 设备不支持BLE
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            bleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE))
            return
        }

        if (isBlePrepared()) {// 蓝牙已经初始化
            bleResultLiveData.postValue(BleResult(BleStatus.INIT_SUCCESS))
            return
        }

        mBluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        if (mBluetoothManager == null) {
            context.shortToastCenter("蓝牙初始化失败")
            bleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE))
            return
        }

        mBluetoothAdapter = mBluetoothManager?.adapter
        if (mBluetoothAdapter == null) {
            context.shortToastCenter("蓝牙初始化失败")
            bleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE))
            return
        }

        return if (isBlePrepared()) {// 蓝牙初始化成功
            bleResultLiveData.postValue(BleResult(BleStatus.INIT_SUCCESS))
        } else {// 蓝牙初始化失败，去打开蓝牙权限设置对话框
            bleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE))
            mBluetoothManager = null
            mBluetoothAdapter = null
            context.shortToastCenter("蓝牙初始化失败")
        }
    }

    override fun startScan() {
        context.shortToastCenter("蓝牙尚未初始化")
    }

    override fun stopScan() {
        context.shortToastCenter("蓝牙尚未初始化")
    }

    override fun connect(address: String) {
        context.shortToastCenter("蓝牙尚未初始化")
    }

    override fun write(command: BleCommand) {
    }

    override fun disconnect(address: String) {
        context.shortToastCenter("蓝牙尚未初始化")
    }

    override fun disconnectAll() {
        context.shortToastCenter("蓝牙尚未初始化")
    }

    override fun close() {
        mBluetoothManager = null
        mBluetoothAdapter = null
    }

    override fun getBluetoothAdapter(): BluetoothAdapter? {
        return mBluetoothAdapter
    }

    override fun getBluetoothGatt(address: String): BluetoothGatt? {
        return null
    }

    /**
     * 蓝牙是否就绪
     */
    private fun isBlePrepared() = mBluetoothAdapter?.isEnabled ?: false

}