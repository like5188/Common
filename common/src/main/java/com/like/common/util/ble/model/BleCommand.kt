package com.like.common.util.ble.model

import android.app.Activity
import android.bluetooth.BluetoothGatt
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope

/**
 * 蓝牙通信的命令
 *
 * @param id                        唯一标识，一般用控制码表示
 * @param data                      需要发送的命令数据
 * @param address                   蓝牙设备的地址
 * @param characteristicUuidString  数据交互的蓝牙特征地址
 * @param bleResultLiveData         数据监听
 * @param description               命令描述，用于日志打印、错误提示等
 * @param readTimeout               读取数据超时时间（毫秒）
 * @param maxTransferSize           硬件规定的一次传输的最大字节数
 * core spec里面定义了ATT的默认MTU为23个bytes， 除去ATT的opcode一个字节以及ATT的handle 2个字节之后，剩下的20个字节便是留给GATT的了。
 * 由于ATT的最大长度为512byte，因此一般认为MTU的最大长度为512个byte就够了，再大也没什么意义，你不可能发一个超过512的ATT的数据。
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
        val description: String = "",
        val readTimeout: Long = 1000L,
        val maxTransferSize: Int = 20,
        val maxFrameTransferSize: Int = 300,
        val onSuccess: ((ByteArray?) -> Unit)? = null,
        val onFailure: ((Throwable) -> Unit)? = null
) {
    lateinit var mLiveData: MutableLiveData<BleResult>

    abstract fun write(coroutineScope: CoroutineScope, bluetoothGatt: BluetoothGatt?)
}


