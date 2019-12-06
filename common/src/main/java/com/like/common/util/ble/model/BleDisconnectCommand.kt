package com.like.common.util.ble.model

import android.app.Activity
import android.bluetooth.BluetoothGatt
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 蓝牙断开连接的命令
 */
class BleDisconnectCommand(
        private val activity: Activity,
        address: String,
        private val onSuccess: (() -> Unit)? = null,
        private val onFailure: ((Throwable) -> Unit)? = null
) : BleCommand(address) {

    override fun disconnect(coroutineScope: CoroutineScope, bluetoothGatt: BluetoothGatt?) {
        if (bluetoothGatt == null) {
            onFailure?.invoke(IllegalArgumentException("断开蓝牙连接失败"))
            return
        }

        if (activity !is LifecycleOwner) {
            onFailure?.invoke(IllegalArgumentException("activity 不是 LifecycleOwner"))
            return
        }

        coroutineScope.launch(Dispatchers.IO) {
            var observer: Observer<BleResult>? = null
            observer = Observer { bleResult ->
                if (bleResult?.status == BleStatus.CONNECTED) {
                    removeObserver(observer)
                    onFailure?.invoke(RuntimeException("断开蓝牙连接失败"))
                } else if (bleResult?.status == BleStatus.DISCONNECTED) {
                    removeObserver(observer)
                    onSuccess?.invoke()
                }
            }

            withContext(Dispatchers.Main) {
                mLiveData.observe(activity, observer)
            }

            bluetoothGatt.disconnect()
        }
    }

    private fun removeObserver(observer: Observer<BleResult>?) {
        observer ?: return
        activity.runOnUiThread {
            mLiveData.removeObserver(observer)
        }
    }
}


