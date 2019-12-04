package com.like.common.util.ble.model

import android.app.Activity
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.like.common.util.Logger
import com.like.common.util.ble.utils.batch
import com.like.common.util.ble.utils.findCharacteristic
import com.like.common.util.ble.utils.toByteArrayOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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
        bleResultLiveData: MutableLiveData<BleResult>,
        description: String = "",
        hasResult: Boolean = true,
        readTimeout: Long = 0L,
        maxTransferSize: Int = 20,
        maxFrameTransferSize: Int = 300,
        onSuccess: ((ByteArray?) -> Unit)? = null,
        onFailure: ((Throwable) -> Unit)? = null
) : BleCommand(activity, id, data, address, characteristicUuidString, bleResultLiveData, description, hasResult, readTimeout, maxTransferSize, maxFrameTransferSize, onSuccess, onFailure) {
    // 缓存返回数据，因为一帧有可能分为多次发送
    private var resultCache: ByteBuffer = ByteBuffer.allocate(maxFrameTransferSize)
    // 过期时间
    private val expired = readTimeout + System.currentTimeMillis()
    /**
     * 此条命令是否已经完成。成功或者失败
     */
    var isCompleted = false
        set(value) {
            if (value) {
                activity.runOnUiThread {
                    bleResultLiveData.removeObserver(mWriteObserver)
                }
                field = value
            }
        }

    /**
     * 是否过期
     */
    private fun isExpired() = expired - System.currentTimeMillis() <= 0

    /**
     * 判断返回的是否是完整的一帧数据
     *
     * @param data  当前接收到的所有数据
     */
    abstract fun isWholeFrame(data: ByteBuffer): Boolean

    private val mWriteObserver = Observer<BleResult> { bleResult ->
        if (bleResult?.status == BleStatus.ON_CHARACTERISTIC_READ) {
            if (isCompleted) {// 说明超时了，避免超时后继续返回数据（此时没有发送下一条数据）
                return@Observer
            }
            resultCache.put(bleResult.data as ByteArray)
            if (isWholeFrame(resultCache)) {
                Logger.d(">>>>>>>>>>>>>>>>>>>>执行 $description 命令成功 >>>>>>>>>>>>>>>>>>>>")
                isCompleted = true
                onSuccess?.invoke(resultCache.toByteArrayOrNull())
            }
        }
    }

    override suspend fun write(bluetoothGatt: BluetoothGatt?) {
        if (isCompleted || bluetoothGatt == null) {
            Log.e("BleCommand", "bluetoothGatt 无效 或者 此命令已经完成")
            return
        }
        val characteristic = bluetoothGatt.findCharacteristic(characteristicUuidString)
        if (characteristic == null) {
            Log.e("BleCommand", "特征值不存在：$characteristicUuidString")
            return
        }

        Logger.w("--------------------开始执行 $description 命令--------------------")
        if (activity is LifecycleOwner) {
            withContext(Dispatchers.Main) {
                bleResultLiveData.observe(activity, mWriteObserver)
            }
        }

        withContext(Dispatchers.IO) {
            data.batch(maxTransferSize).forEach {
                characteristic.value = it
                /*
                写特征值前可以设置写的类型setWriteType()，写类型有三种，如下：
                    WRITE_TYPE_DEFAULT  默认类型，需要外围设备的确认，也就是需要外围设备的回应，这样才能继续发送写。
                    WRITE_TYPE_NO_RESPONSE 设置该类型不需要外围设备的回应，可以继续写数据。加快传输速率。
                    WRITE_TYPE_SIGNED  写特征携带认证签名，具体作用不太清楚。
                 */
                characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                bluetoothGatt.readCharacteristic(characteristic)
                delay(30)
            }

            if (hasResult) {// 如果有返回值，那么需要循环检测是否超时
                while (!isCompleted) {
                    delay(100)
                    if (isExpired()) {// 说明是超时了
                        Logger.e("执行 $description 命令超时，没有收到返回值！")
                        isCompleted = true
                        onFailure?.invoke(TimeoutException())
                        return@withContext
                    }
                }
            }
        }
    }
}


