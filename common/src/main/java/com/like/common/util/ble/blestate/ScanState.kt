package com.like.common.util.ble.blestate

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.like.common.util.ble.model.BleResult
import com.like.common.util.ble.model.BleStatus
import com.like.common.util.ble.scanstrategy.IScanStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 蓝牙扫描状态
 * 可以进行扫描操作
 */
class ScanState(
        private val mActivity: FragmentActivity,
        private val mBleResultLiveData: MutableLiveData<BleResult>,
        private var mBluetoothManager: BluetoothManager?,
        private var mBluetoothAdapter: BluetoothAdapter?
) : BaseBleState() {

    private var mScanning = AtomicBoolean(false)
    private var mScanStrategy: IScanStrategy? = null

    override fun startScan(scanStrategy: IScanStrategy, scanTimeout: Long) {
        mScanStrategy = scanStrategy
        if (mScanning.compareAndSet(false, true)) {
            mBleResultLiveData.postValue(BleResult(BleStatus.START_SCAN_DEVICE))
            scanStrategy.startScan(mBluetoothAdapter)
            mActivity.lifecycleScope.launch(Dispatchers.IO) {
                // 在指定超时时间时取消扫描
                delay(scanTimeout)
                if (mScanning.get()) {
                    stopScan()
                }
            }
        }
    }

    override fun stopScan() {
        if (mScanning.compareAndSet(true, false)) {
            mBleResultLiveData.postValue(BleResult(BleStatus.STOP_SCAN_DEVICE))
            mScanStrategy?.stopScan(mBluetoothAdapter)
        }
    }

    override fun close() {
        stopScan()
        mScanStrategy = null
        mBluetoothAdapter = null
        mBluetoothManager = null
    }

    override fun getBluetoothAdapter(): BluetoothAdapter? {
        return mBluetoothAdapter
    }

    override fun getBluetoothManager(): BluetoothManager? {
        return mBluetoothManager
    }
}