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
import com.like.common.util.ble.utils.toByteArrayOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.util.concurrent.TimeoutException

/**
 * 蓝牙通信的命令
 *
 * @param id                        唯一标识，一般用控制码表示
 * @param data                      需要发送的命令数据
 * @param address                   蓝牙设备的地址
 * @param characteristicUuidString  数据交互的蓝牙特征地址
 * @param bleResultLiveData         数据监听
 * @param description               命令描述，用于日志打印、错误提示等
 * @param hasResult                 是否有返回值
 * @param readTimeout               读取数据超时时间（毫秒）
 * @param maxTransferSize           硬件规定的一次传输的最大字节数
 * @param maxFrameTransferSize      由硬件开发者约定的一帧传输的最大字节数
 * @param onSuccess                 命令执行成功回调
 * @param onFailure                 命令执行失败回调
 */
abstract class BleCommand(
        val activity: Activity,
        val id: Int,
        val data: ByteArray,
        val address: String,
        val characteristicUuidString: String,
        val bleResultLiveData: MutableLiveData<BleResult>,
        val description: String = "",
        val hasResult: Boolean = true,
        val readTimeout: Long = 0L,
        val maxTransferSize: Int = 20,
        val maxFrameTransferSize: Int = 300,
        val onSuccess: ((ByteArray?) -> Unit)? = null,
        val onFailure: ((Throwable) -> Unit)? = null
) {
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
        if (bleResult?.status == BleStatus.CHARACTERISTIC_CHANGED) {
            if (isCompleted) {// 说明超时了，避免超时后继续返回数据（此时没有发送下一条数据）
                return@Observer
            }
            resultCache.put(bleResult.data as ByteArray)
            if (isWholeFrame(resultCache)) {
                isCompleted = true
                onSuccess?.invoke(resultCache.toByteArrayOrNull())
            }
        }
    }

    suspend fun write(bluetoothGatt: BluetoothGatt?) {
        if (isCompleted || bluetoothGatt == null) {
            Log.e("BleCommand", "bluetoothGatt 无效 或者 此命令已经完成")
            return
        }
        val characteristic = findCharacteristic(bluetoothGatt, characteristicUuidString)
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
                bluetoothGatt.writeCharacteristic(characteristic)
                delay(30)
            }

            if (hasResult) {// 如果有返回值，那么需要循环检测是否超时
                while (!isCompleted) {
                    delay(100)
                    if (isExpired()) {// 说明是超时了
                        Logger.e("执行 $description 命令超时")
                        isCompleted = true
                        onFailure?.invoke(TimeoutException())
                    }
                }
            } else {
                delay(100)
                isCompleted = true
                onSuccess?.invoke(null)
                // 延迟，避免硬件处理不过来
                Logger.d(">>>>>>>>>>>>>>>>>>>>执行 $description 命令成功。不需要返回结果>>>>>>>>>>>>>>>>>>>>")
            }
        }

    }

    // 查找远程设备的特征
    private fun findCharacteristic(gatt: BluetoothGatt, characteristicUuidString: String): BluetoothGattCharacteristic? {
        // 开始查找特征
        val characteristic = gatt.services
                ?.flatMap {
                    it.characteristics
                }
                ?.first {
                    it.uuid.toString() == characteristicUuidString
                }

        if (characteristic != null) {
            // 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发onCharacteristicChanged()
            gatt.setCharacteristicNotification(characteristic, true)
        }
        return characteristic
    }

}


