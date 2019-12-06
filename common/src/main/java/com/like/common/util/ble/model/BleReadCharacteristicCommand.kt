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
 * 蓝牙读取特征值数据的命令
 *
 * @param id                        唯一标识，一般用控制码表示
 * @param data                      需要发送的命令数据
 * @param address                   蓝牙设备的地址
 * @param characteristicUuidString  数据交互的蓝牙特征地址
 * @param description               命令描述，用于日志打印、错误提示等
 * @param readTimeout               读取数据超时时间（毫秒）
 * @param maxTransferSize           硬件规定的一次传输的最大字节数
 * core spec里面定义了ATT的默认MTU为23个bytes， 除去ATT的opcode一个字节以及ATT的handle 2个字节之后，剩下的20个字节便是留给GATT的了。
 * 由于ATT的最大长度为512byte，因此一般认为MTU的最大长度为512个byte就够了，再大也没什么意义，你不可能发一个超过512的ATT的数据。
 * @param maxFrameTransferSize      由硬件开发者约定的一帧传输的最大字节数
 * @param onSuccess                 命令执行成功回调
 * @param onFailure                 命令执行失败回调
 */
abstract class BleReadCharacteristicCommand(
        private val activity: Activity,
        private val id: Int,
        private val data: ByteArray,
        address: String,
        private val characteristicUuidString: String,
        private val description: String = "",
        private val readTimeout: Long = 0L,
        private val maxTransferSize: Int = 20,
        private val maxFrameTransferSize: Int = 300,
        private val onSuccess: ((ByteArray?) -> Unit)? = null,
        private val onFailure: ((Throwable) -> Unit)? = null
) : BleCommand(address) {
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

    override fun read(coroutineScope: CoroutineScope, bluetoothGatt: BluetoothGatt?) {
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


