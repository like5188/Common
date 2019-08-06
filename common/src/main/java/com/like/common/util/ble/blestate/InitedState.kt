package com.like.common.util.ble.blestate

import androidx.lifecycle.MutableLiveData
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.content.Context
import com.like.common.util.ble.model.BleCommand
import com.like.common.util.ble.model.BleResult
import com.like.common.util.ble.model.BleStatus
import com.like.common.util.ble.scanstrategy.IScanStrategy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 蓝牙初始化完毕的状态，可以进行扫描
 */
class InitedState(
        private val context: Context,
        private val bleResultLiveData: MutableLiveData<BleResult>,
        private var mBluetoothAdapter: BluetoothAdapter?,
        private var mScanStrategy: IScanStrategy?,
        private val scanTimeout: Long = 3000// 蓝牙扫描时间的限制
) : IBleState {

    private var mScanning: Boolean = false

    override fun init() {
    }

    override fun startScan() {
        mScanStrategy ?: return
        if (mScanning) {
            return
        }
        mScanning = true

        GlobalScope.launch {
            delay(50)
            bleResultLiveData.postValue(BleResult(BleStatus.START_SCAN_DEVICE))
            mScanStrategy?.startScan(mBluetoothAdapter)
        }

        // Stops scanning after a pre-defined scan period.
        GlobalScope.launch {
            delay(scanTimeout)
            if (mScanning) {
                stopScan()
            }
        }
    }

    override fun stopScan() {
        if (!mScanning) {
            return
        }
        mScanning = false

        GlobalScope.launch {
            delay(50)
            mScanStrategy?.stopScan(mBluetoothAdapter)
            bleResultLiveData.postValue(BleResult(BleStatus.STOP_SCAN_DEVICE))
        }
    }

    override fun connect(address: String) {
    }

    override fun write(command: BleCommand) {
    }

    override fun disconnect(address: String) {
    }

    override fun disconnectAll() {
    }

    override fun close() {
        stopScan()
        mBluetoothAdapter = null
        mScanStrategy = null
    }

    override fun getBluetoothAdapter(): BluetoothAdapter? {
        return mBluetoothAdapter
    }

    override fun getBluetoothGatt(address: String): BluetoothGatt? {
        return null
    }

}