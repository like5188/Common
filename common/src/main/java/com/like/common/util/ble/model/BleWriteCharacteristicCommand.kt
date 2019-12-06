package com.like.common.util.ble.model

import android.app.Activity
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.like.common.util.Logger
import com.like.common.util.ble.utils.batch
import com.like.common.util.ble.utils.findCharacteristic
import kotlinx.coroutines.*
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger

/**
 * 蓝牙写特征值的命令
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
class BleWriteCharacteristicCommand(
        private val activity: Activity,
        private val id: Int,
        private val data: ByteArray,
        address: String,
        private val characteristicUuidString: String,
        private val description: String = "",
        private val readTimeout: Long = 0L,
        private val maxTransferSize: Int = 20,
        private val maxFrameTransferSize: Int = 300,
        private val onSuccess: (() -> Unit)? = null,
        private val onFailure: ((Throwable) -> Unit)? = null
) : BleCommand(address) {
    private val mDataList: List<ByteArray> by lazy { data.batch(maxTransferSize) }
    // 记录所有的数据批次，在所有的数据都发送完成后，才调用onSuccess()
    private val mBatchCount: AtomicInteger by lazy { AtomicInteger(mDataList.size) }
    // 过期时间
    private val expired = readTimeout + System.currentTimeMillis()

    // 是否过期
    private fun isExpired() = expired - System.currentTimeMillis() <= 0

    override fun write(coroutineScope: CoroutineScope, bluetoothGatt: BluetoothGatt?) {
        if (mBatchCount.get() == 0 || bluetoothGatt == null) {
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
        /*
        写特征值前可以设置写的类型setWriteType()，写类型有三种，如下：
            WRITE_TYPE_DEFAULT  默认类型，需要外围设备的确认，也就是需要外围设备的回应，这样才能继续发送写。
            WRITE_TYPE_NO_RESPONSE 设置该类型不需要外围设备的回应，可以继续写数据。加快传输速率。
            WRITE_TYPE_SIGNED  写特征携带认证签名，具体作用不太清楚。
         */
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT

        Logger.w("--------------------开始执行 $description 命令--------------------")
        var job: Job? = null
        var observer: Observer<BleResult>? = null
        observer = Observer { bleResult ->
            if (bleResult?.status == BleStatus.ON_CHARACTERISTIC_WRITE_SUCCESS) {
                mBatchCount.decrementAndGet()
            } else if (bleResult?.status == BleStatus.ON_CHARACTERISTIC_WRITE_FAILURE) {
                job?.cancel()
                removeObserver(observer)
                onFailure?.invoke(RuntimeException("写特征值失败：$characteristicUuidString"))
            }
        }

        coroutineScope.launch(Dispatchers.Main) {
            mLiveData.observe(activity, observer)

            job = launch(Dispatchers.IO) {
                mDataList.forEach {
                    characteristic.value = it
                    bluetoothGatt.writeCharacteristic(characteristic)
                    delay(1000)
                }
            }

            withContext(Dispatchers.IO) {
                while (mBatchCount.get() > 0) {
                    delay(100)
                    if (isExpired()) {// 说明是超时了
                        job?.cancel()
                        removeObserver(observer)
                        onFailure?.invoke(TimeoutException())
                        return@withContext
                    }
                }

                removeObserver(observer)
                onSuccess?.invoke()
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


