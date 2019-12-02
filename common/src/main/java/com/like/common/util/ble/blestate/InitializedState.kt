package com.like.common.util.ble.blestate

import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.like.common.util.ble.model.BleResult
import com.like.common.util.ble.model.BleStatus
import com.like.common.util.ble.scanstrategy.IScanStrategy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 蓝牙初始化完毕的状态
 * 可以进行扫描
 */
class InitializedState(
        private val mContext: Context,
        private val mBleResultLiveData: MutableLiveData<BleResult>,
        private var mBluetoothAdapter: BluetoothAdapter?,
        private var mScanStrategy: IScanStrategy?,
        private val mScanTimeout: Long = 3000// 蓝牙扫描时间的限制
) : BaseBleState() {

    private var mScanning = AtomicBoolean(false)

    override fun init() {
    }

    override fun startScan() {
        val scanStrategy = mScanStrategy ?: return
        if (mScanning.compareAndSet(false, true)) {
            GlobalScope.launch {
                delay(50)
                mBleResultLiveData.postValue(BleResult(BleStatus.START_SCAN_DEVICE))
                scanStrategy.startScan(mBluetoothAdapter)
            }

            // 在指定超时时间时取消扫描
            GlobalScope.launch {
                delay(mScanTimeout)
                if (mScanning.get()) {
                    stopScan()
                }
            }
        }
    }

    override fun stopScan() {
        val scanStrategy = mScanStrategy ?: return
        if (mScanning.compareAndSet(true, false)) {
            GlobalScope.launch {
                delay(50)
                mBleResultLiveData.postValue(BleResult(BleStatus.STOP_SCAN_DEVICE))
                scanStrategy.stopScan(mBluetoothAdapter)
            }
        }
    }

    override fun close() {
        stopScan()
        mBluetoothAdapter = null
        mScanStrategy = null
    }

    override fun getBluetoothAdapter(): BluetoothAdapter? {
        return mBluetoothAdapter
    }

}