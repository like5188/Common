package com.like.common.util.ble.model

import android.app.Activity
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.like.common.util.Logger
import com.like.common.util.ble.utils.batch
import com.like.common.util.ble.utils.findCharacteristic
import kotlinx.coroutines.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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
abstract class BleWriteCommand(
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
    private val mDataList: List<ByteArray> by lazy { data.batch(maxTransferSize) }
    private val batchCount: CountDownLatch by lazy { CountDownLatch(mDataList.size) }

    override fun write(coroutineScope: CoroutineScope, bluetoothGatt: BluetoothGatt?) {
        if (batchCount.count == 0L || bluetoothGatt == null) {
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
        val observer = Observer<BleResult> { bleResult ->
            if (bleResult?.status == BleStatus.ON_CHARACTERISTIC_WRITE) {
                batchCount.countDown()
            }
        }

        coroutineScope.launch(Dispatchers.Main) {
            bleResultLiveData.observe(activity, observer)

            launch(Dispatchers.IO) {
                mDataList.forEach {
                    characteristic.value = it
                    bluetoothGatt.writeCharacteristic(characteristic)
                    delay(5000)
                    Logger.w("1 ${batchCount.count}")
                }
            }

            withContext(Dispatchers.IO) {
                try {
                    Logger.w("2 ${batchCount.count}")
                    batchCount.await(readTimeout, TimeUnit.MILLISECONDS)
                    Logger.w("3 ${batchCount.count}")
                    onSuccess?.invoke(null)
                } catch (e: Exception) {
                    Logger.w("4 ${batchCount.count}")
                    onFailure?.invoke(e)
                }
            }

            Logger.w("5 ${batchCount.count}")
            launch(Dispatchers.Main) {
                bleResultLiveData.removeObserver(observer)
            }
        }
    }

}


