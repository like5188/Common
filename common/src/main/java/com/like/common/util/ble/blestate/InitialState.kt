package com.like.common.util.ble.blestate

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import com.like.common.util.ble.model.BleResult
import com.like.common.util.ble.model.BleStatus

/**
 * 蓝牙的初始状态
 * 可以进行初始化
 */
class InitialState(
        private val mContext: Context,
        private val mBleResultLiveData: MutableLiveData<BleResult>
) : BaseBleState() {

    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null

    override fun init() {
        mBleResultLiveData.postValue(BleResult(BleStatus.INIT))

        // 设备不支持BLE
        if (!mContext.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            mBleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE, errorMsg = "phone does not support Bluetooth"))
            return
        }

        if (isBlePrepared()) {// 蓝牙已经初始化
            mBleResultLiveData.postValue(BleResult(BleStatus.INIT_SUCCESS))
            return
        }

        mBluetoothManager = mContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        if (mBluetoothManager == null) {
            mBleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE, errorMsg = "failed to get BluetoothManager"))
            return
        }

        mBluetoothAdapter = mBluetoothManager?.adapter
        if (mBluetoothAdapter == null) {
            mBleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE, errorMsg = "failed to get BluetoothAdapter"))
            return
        }

        return if (isBlePrepared()) {// 蓝牙初始化成功
            mBleResultLiveData.postValue(BleResult(BleStatus.INIT_SUCCESS))
        } else {// 蓝牙初始化失败，去打开蓝牙权限设置对话框
            mBluetoothManager = null
            mBluetoothAdapter = null
            mBleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE, errorMsg = "bluetooth is not enabled and ready for use"))
        }
    }

    override fun close() {
        mBluetoothManager = null
        mBluetoothAdapter = null
    }

    override fun getBluetoothAdapter(): BluetoothAdapter? {
        return mBluetoothAdapter
    }

    /**
     * 蓝牙是否就绪
     */
    private fun isBlePrepared() = mBluetoothAdapter?.isEnabled ?: false

}