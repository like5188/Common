package com.like.common.util.ble.model

import android.app.Activity
import android.bluetooth.BluetoothGatt
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.like.common.util.Logger
import com.like.common.util.ble.utils.batch
import com.like.common.util.ble.utils.findCharacteristic
import com.like.common.util.ble.utils.toByteArrayOrNull
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.util.concurrent.TimeoutException

/**
 * 蓝牙通信的命令
 */
abstract class BleReadCommand(
        activity: Activity,
        id: Int,
        data: ByteArray,
        address: String,
        characteristicUuidString: String,
        description: String = "",
        readTimeout: Long = 0L,
        maxTransferSize: Int = 20,
        maxFrameTransferSize: Int = 300,
        onSuccess: ((ByteArray?) -> Unit)? = null,
        onFailure: ((Throwable) -> Unit)? = null
) : BleCommand(activity, id, data, address, characteristicUuidString, description, readTimeout, maxTransferSize, maxFrameTransferSize, onSuccess, onFailure) {
    // 缓存返回数据，因为一帧有可能分为多次接收
    private var resultCache: ByteBuffer = ByteBuffer.allocate(maxFrameTransferSize)
    // 过期时间
    private val expired = readTimeout + System.currentTimeMillis()

    // 是否过期
    private fun isExpired() = expired - System.currentTimeMillis() <= 0

    /**
     * 此条命令是否已经完成。成功或者失败
     */
    var isCompleted = false
        set(value) {
            if (value) {
                activity.runOnUiThread {
                    mLiveData.removeObserver(mWriteObserver)
                }
                field = value
            }
        }

    /**
     * 判断返回的是否是完整的一帧数据
     *
     * @param data  当前接收到的所有数据
     */
    abstract fun isWholeFrame(data: ByteBuffer): Boolean

    private var job: Job? = null
    private val mWriteObserver = Observer<BleResult> { bleResult ->
        if (bleResult?.status == BleStatus.ON_CHARACTERISTIC_READ_SUCCESS) {
            if (isCompleted) {// 说明超时了，避免超时后继续返回数据（此时没有发送下一条数据）
                return@Observer
            }
            resultCache.put(bleResult.data as ByteArray)
            if (isWholeFrame(resultCache)) {
                isCompleted = true
                onSuccess?.invoke(resultCache.toByteArrayOrNull())
            }
        } else if (bleResult?.status == BleStatus.ON_CHARACTERISTIC_READ_FAILURE) {
            job?.cancel()
            isCompleted = true
            onFailure?.invoke(RuntimeException("读取特征值失败：$characteristicUuidString"))
        }
    }

    override fun write(coroutineScope: CoroutineScope, bluetoothGatt: BluetoothGatt?) {
        if (isCompleted || bluetoothGatt == null) {
            onFailure?.invoke(IllegalArgumentException("bluetoothGatt 无效 或者 此命令已经完成"))
            return
        }

        if (activity !is LifecycleOwner) {
            onFailure?.invoke(IllegalArgumentException("activity 不是 LifecycleOwner"))
            return
        }

        val characteristic = bluetoothGatt.findCharacteristic(characteristicUuidString)
        if (characteristic == null) {
            onFailure?.invoke(IllegalArgumentException("特征值不存在：$characteristicUuidString"))
            return
        }

        Logger.w("--------------------开始执行 $description 命令--------------------")
        coroutineScope.launch(Dispatchers.Main) {
            mLiveData.observe(activity, mWriteObserver)

            job = launch(Dispatchers.IO) {
                data.batch(maxTransferSize).forEach {
                    characteristic.value = it
                    bluetoothGatt.readCharacteristic(characteristic)
                    delay(1000)
                }
            }

            withContext(Dispatchers.IO) {
                while (!isCompleted) {
                    delay(100)
                    if (isExpired()) {// 说明是超时了
                        job?.cancel()
                        isCompleted = true
                        onFailure?.invoke(TimeoutException())
                        return@withContext
                    }
                }
            }
        }

    }
}


