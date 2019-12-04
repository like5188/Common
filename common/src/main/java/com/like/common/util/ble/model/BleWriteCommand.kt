package com.like.common.util.ble.model

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.like.common.util.Logger
import com.like.common.util.ble.utils.batch
import com.like.common.util.ble.utils.findCharacteristic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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
        val lifecycleOwner: LifecycleOwner,
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
        val onSuccess: (() -> Unit)? = null,
        val onFailure: ((Throwable) -> Unit)? = null
) {
    private val mDataList: List<ByteArray> by lazy { data.batch(maxTransferSize) }
    private val batchCount: CountDownLatch by lazy { CountDownLatch(mDataList.size) }

    suspend fun write(bluetoothGatt: BluetoothGatt?) {
        if (batchCount.count == 0L || bluetoothGatt == null) {
            Log.e("BleCommand", "bluetoothGatt 无效 或者 此命令已经完成")
            return
        }
        val characteristic = bluetoothGatt.findCharacteristic(characteristicUuidString)
        if (characteristic == null) {
            Log.e("BleCommand", "特征值不存在：$characteristicUuidString")
            return
        }

        Logger.w("--------------------开始执行 $description 命令--------------------")
        val observer = Observer<BleResult> { bleResult ->
            if (bleResult?.status == BleStatus.WRITE_CHARACTERISTIC) {
                batchCount.countDown()
            }
        }
        withContext(Dispatchers.Main) {
            bleResultLiveData.observe(lifecycleOwner, observer)
        }

        withContext(Dispatchers.IO) {
            mDataList.forEach {
                characteristic.value = it
                /*
                写特征值前可以设置写的类型setWriteType()，写类型有三种，如下：
                    WRITE_TYPE_DEFAULT  默认类型，需要外围设备的确认，也就是需要外围设备的回应，这样才能继续发送写。
                    WRITE_TYPE_NO_RESPONSE 设置该类型不需要外围设备的回应，可以继续写数据。加快传输速率。
                    WRITE_TYPE_SIGNED  写特征携带认证签名，具体作用不太清楚。
                 */
                characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                bluetoothGatt.writeCharacteristic(characteristic)
                delay(30)
            }
            try {
                batchCount.await(readTimeout, TimeUnit.MILLISECONDS)
                Logger.d(">>>>>>>>>>>>>>>>>>>>执行 $description 命令成功 >>>>>>>>>>>>>>>>>>>>")
                onSuccess?.invoke()
            } catch (e: Exception) {
                Logger.e("执行 $description 命令失败！${e.message}")
                onFailure?.invoke(e)
            } finally {
                withContext(Dispatchers.Main) {
                    bleResultLiveData.removeObserver(observer)
                }
            }
        }

    }

}


