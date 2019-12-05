package com.like.common.util.ble.model

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.os.Build
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.like.common.util.Logger
import kotlinx.coroutines.*

/**
 * 蓝牙连接的命令
 */
class BleConnectCommand(
        private val activity: Activity,
        address: String,
        private val connectTimeout: Long = 20000L,
        private val onSuccess: (() -> Unit)? = null,
        private val onFailure: ((Throwable) -> Unit)? = null
) : BleCommand(address) {

    override fun connect(coroutineScope: CoroutineScope, gattCallback: BluetoothGattCallback, bluetoothAdapter: BluetoothAdapter?, disconnect: () -> Unit) {
        mLiveData.postValue(BleResult(BleStatus.CONNECT))
        if (address.isEmpty()) {
            onFailure?.invoke(IllegalArgumentException("连接蓝牙设备失败：地址不能为空"))
            return
        }

        if (activity !is LifecycleOwner) {
            onFailure?.invoke(IllegalArgumentException("activity 不是 LifecycleOwner"))
            return
        }

        coroutineScope.launch(Dispatchers.IO) {
            // 获取远端的蓝牙设备
            val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(address)
            if (bluetoothDevice == null) {
                onFailure?.invoke(IllegalArgumentException("连接蓝牙设备失败：设备 $address 未找到"))
                return@launch
            }

            // 在任何时刻都只能最多一个设备在尝试建立连接。如果同时对多个蓝牙设备发起建立 Gatt 连接请求。如果前面的设备连接失败了，后面的设备请求会被永远阻塞住，不会有任何连接回调。
            // 对BLE设备连接，连接过程要尽量短，如果连接不上，不要盲目进行重连，否这你的电池会很快被消耗掉。
            Logger.v("尝试创建新的连接……")
            var job: Job? = null
            var observer: Observer<BleResult>? = null
            observer = Observer { bleResult ->
                if (bleResult?.status == BleStatus.CONNECTED) {
                    job?.cancel()
                    removeObserver(observer)
                    onSuccess?.invoke()
                } else if (bleResult?.status == BleStatus.DISCONNECTED) {
                    job?.cancel()
                    removeObserver(observer)
                    onFailure?.invoke(RuntimeException("连接蓝牙设备失败"))
                }
            }

            withContext(Dispatchers.Main) {
                mLiveData.observe(activity, observer)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothDevice.connectGatt(activity, false, gattCallback, BluetoothDevice.TRANSPORT_LE)// 第二个参数表示是否自动重连
            } else {
                bluetoothDevice.connectGatt(activity, false, gattCallback)// 第二个参数表示是否自动重连
            }

            job = launch((Dispatchers.IO)) {
                delay(connectTimeout)
                disconnect()
            }
        }
    }

    private fun removeObserver(observer: Observer<BleResult>?) {
        observer ?: return
        activity.runOnUiThread {
            mLiveData.removeObserver(observer)
        }
    }
}


