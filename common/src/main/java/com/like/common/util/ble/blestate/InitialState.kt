package com.like.common.util.ble.blestate

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.like.common.util.PermissionUtils
import com.like.common.util.bindToLifecycleOwner
import com.like.common.util.ble.model.BleResult
import com.like.common.util.ble.model.BleStatus
import com.like.common.view.callback.RxCallback

/**
 * 蓝牙的初始状态
 * 可以进行初始化
 */
class InitialState(
        private val mActivity: FragmentActivity,
        private val mBleResultLiveData: MutableLiveData<BleResult>
) : BaseBleState() {
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val mPermissionUtils: PermissionUtils by lazy { PermissionUtils(mActivity) }
    private val mRxCallback: RxCallback by lazy { RxCallback(mActivity) }

    @SuppressLint("CheckResult")
    override fun init() {
        mBleResultLiveData.postValue(BleResult(BleStatus.INIT))

        // 设备不支持BLE
        if (!mActivity.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            mBleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE, errorMsg = "phone does not support Bluetooth"))
            return
        }

        mPermissionUtils.checkPermissions(
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                onDenied = {
                    mBleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE, errorMsg = "the permissions was denied."))
                },
                onError = {
                    mBleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE, errorMsg = it.message ?: "unknown error"))
                },
                onGranted = {
                    if (isBlePrepared()) {// 蓝牙已经初始化
                        mBleResultLiveData.postValue(BleResult(BleStatus.INIT_SUCCESS))
                        return@checkPermissions
                    }

                    mBluetoothManager = mActivity.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
                    if (mBluetoothManager == null) {
                        mBleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE, errorMsg = "failed to get BluetoothManager"))
                        return@checkPermissions
                    }

                    mBluetoothAdapter = mBluetoothManager?.adapter
                    if (mBluetoothAdapter == null) {
                        mBleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE, errorMsg = "failed to get BluetoothAdapter"))
                        return@checkPermissions
                    }

                    if (isBlePrepared()) {// 蓝牙初始化成功
                        mBleResultLiveData.postValue(BleResult(BleStatus.INIT_SUCCESS))
                    } else {// 蓝牙功能未打开
                        mBluetoothManager = null
                        mBluetoothAdapter = null
                        // 弹出开启蓝牙的对话框
                        mRxCallback.startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)).subscribe(
                                {
                                    if (it.resultCode == Activity.RESULT_OK) {
                                        mBleResultLiveData.postValue(BleResult(BleStatus.INIT_SUCCESS))
                                    } else {
                                        mBleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE, errorMsg = "failed to open Bluetooth"))
                                    }
                                },
                                {
                                    mBleResultLiveData.postValue(BleResult(BleStatus.INIT_FAILURE, errorMsg = it.message
                                            ?: "unknown error"))
                                }
                        ).bindToLifecycleOwner(mActivity)
                    }
                })
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