package com.like.common.util.ble.blestate

import android.bluetooth.*
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.like.common.util.Logger
import com.like.common.util.ble.model.BleCommand
import com.like.common.util.ble.model.BleResult
import com.like.common.util.ble.model.BleStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

/**
 * 蓝牙准备就绪
 * 可以进行连接、写数据
 */
class ReadyState(
        private val mContext: Context,
        private val mCoroutineScope: CoroutineScope,
        private val mBleResultLiveData: MutableLiveData<BleResult>,
        private var mBluetoothAdapter: BluetoothAdapter?,
        private val mConnectTimeout: Long = 20000// 蓝牙连接超时时间
) : BaseBleState() {
    private val mChannels: MutableMap<String, Channel<BleCommand>> = mutableMapOf()
    private val mConnectedBluetoothGattList = mutableListOf<BluetoothGatt>()
    // 连接蓝牙设备的回调函数
    private val mGattCallback = object : BluetoothGattCallback() {

        // 当连接状态改变
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {// 连接蓝牙设备成功
                    // 连接成功后，发现设备所有的 GATT Service
                    gatt.discoverServices()
                }
                BluetoothGatt.STATE_DISCONNECTED -> {// 连接蓝牙设备失败
                    mConnectedBluetoothGattList.remove(gatt)
                    mBleResultLiveData.postValue(BleResult(BleStatus.DISCONNECTED))
                }
            }
        }

        // 发现蓝牙服务
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {// 发现了蓝牙服务后，才算真正的连接成功。
                mConnectedBluetoothGattList.add(gatt)
                mBleResultLiveData.postValue(BleResult(BleStatus.CONNECTED, gatt.device.name))
            }
        }

        // 读特征值
        override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
        ) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_CHARACTERISTIC_READ, characteristic.value))
        }

        // 特征值改变
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_CHARACTERISTIC_CHANGED, characteristic.value))
        }

        // 写特征值
        override fun onCharacteristicWrite(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
        ) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_CHARACTERISTIC_WRITE, characteristic.value))
        }

        // 读描述值
        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_DESCRIPTOR_READ, descriptor.value))
        }

        // 写描述值
        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_DESCRIPTOR_WRITE, descriptor.value))
        }

        // 读蓝牙信号值
        override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_READ_REMOTE_RSSI, rssi))
        }

    }

    // 如果要对多个设备发起连接请求，最好是有一个统一的设备连接管理，把发起连接请求用队列管理起来。
    // 前一个设备请求建立连接，后面请求在队列中等待。
    // 如果连接成功了，就处理下一个连接请求。
    // 如果连接失败了（例如出错，或者连接超时失败），就马上调用 BluetoothGatt.disconnect() 来释放建立连接请求，然后处理下一个设备连接请求。
    override fun connect(address: String) {
        mBleResultLiveData.postValue(BleResult(BleStatus.CONNECT))
        if (address.isEmpty()) {
            mBleResultLiveData.postValue(BleResult(BleStatus.DISCONNECTED, errorMsg = "连接蓝牙设备失败：地址不能为空"))
            return
        }

        val gatt = getBluetoothGatt(address)
        if (gatt != null) {// 已经连接过了
            // 蓝牙设备已经连接过
            mBleResultLiveData.postValue(BleResult(BleStatus.CONNECTED))
        } else {
            mCoroutineScope.launch(Dispatchers.IO) {
                // 获取远端的蓝牙设备
                val bluetoothDevice = mBluetoothAdapter?.getRemoteDevice(address)
                if (bluetoothDevice == null) {
                    mBleResultLiveData.postValue(BleResult(BleStatus.DISCONNECTED, errorMsg = "连接蓝牙设备失败：设备 $address 未找到"))
                    return@launch
                }

                // 在任何时刻都只能最多一个设备在尝试建立连接。如果同时对多个蓝牙设备发起建立 Gatt 连接请求。如果前面的设备连接失败了，后面的设备请求会被永远阻塞住，不会有任何连接回调。
                // 对BLE设备连接，连接过程要尽量短，如果连接不上，不要盲目进行重连，否这你的电池会很快被消耗掉。
                Logger.v("尝试创建新的连接……")
                bluetoothDevice.connectGatt(mContext, false, mGattCallback)// 第二个参数表示是否自动重连
                withContext((Dispatchers.IO)) {
                    delay(mConnectTimeout)
                    disconnect(address)
                }
            }
        }
    }

    override fun disconnect(address: String) {
        getBluetoothGatt(address)?.disconnect()
    }

    override fun write(command: BleCommand) {
        val address = command.address
        if (!mChannels.containsKey(address)) {
            val channel = Channel<BleCommand>()
            mChannels[address] = channel
            mCoroutineScope.launch(Dispatchers.IO) {
                for (bleCommand in channel) {
                    getBluetoothGatt(address)?.let {
                        bleCommand.write(mCoroutineScope, it)
                    }
                }
            }
        }
        mCoroutineScope.launch(Dispatchers.IO) {
            mChannels[address]?.send(command)
        }
    }

    override fun disconnectAll() {
        mConnectedBluetoothGattList.forEach {
            it.disconnect()
            it.close()
        }
        mConnectedBluetoothGattList.clear()
    }

    override fun close() {
        disconnectAll()
        mBluetoothAdapter = null
    }

    override fun getBluetoothAdapter(): BluetoothAdapter? {
        return mBluetoothAdapter
    }

    private fun getBluetoothGatt(address: String): BluetoothGatt? {
        return mConnectedBluetoothGattList.firstOrNull { it.device.address == address }
    }

}