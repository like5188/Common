package com.like.common.util.ble.blestate

import android.bluetooth.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.like.common.util.ble.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * 蓝牙连接状态
 * 可以进行连接、操作数据等等操作
 */
class ConnectState(
        private val mActivity: FragmentActivity,
        private val mBleResultLiveData: MutableLiveData<BleResult>,
        private var mBluetoothManager: BluetoothManager?,
        private var mBluetoothAdapter: BluetoothAdapter?
) : BaseBleState() {

    private val mChannels: MutableMap<String, Channel<BleCommand>> = mutableMapOf()
    private val mConnectedBluetoothGattList = mutableListOf<BluetoothGatt>()
    // 连接蓝牙设备的回调函数
    private val mGattCallback = object : BluetoothGattCallback() {

        /**
         * 添加指定 address 的通道，并开启接收数据
         */
        private fun addChannelAndReceive(address: String, gatt: BluetoothGatt) {
            if (address.isEmpty()) return
            if (!mChannels.containsKey(address)) {
                val channel = Channel<BleCommand>()
                mChannels[address] = channel
                mActivity.lifecycleScope.launch(Dispatchers.IO) {
                    for (command in channel) {
                        when (command) {
                            is BleReadCharacteristicCommand -> command.read(mActivity.lifecycleScope, gatt)
                            is BleWriteCharacteristicCommand -> command.write(mActivity.lifecycleScope, gatt)
                            is BleSetMtuCommand -> command.setMtu(mActivity.lifecycleScope, gatt)
                        }
                    }
                }
            }
        }

        /**
         * 关闭指定 address 的通道，并移除
         */
        private fun closeChannelAndRemove(address: String) {
            if (address.isEmpty()) return
            if (mChannels.containsKey(address)) {
                mChannels[address]?.close()
                mChannels.remove(address)
            }
        }

        // 当连接状态改变
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {// 连接蓝牙设备成功
                    // 连接成功后，发现设备所有的 GATT Service
                    gatt.discoverServices()
                }
                BluetoothGatt.STATE_DISCONNECTED -> {// 连接蓝牙设备失败
                    closeChannelAndRemove(gatt.device.address)
                    mConnectedBluetoothGattList.remove(gatt)
                    mBleResultLiveData.postValue(BleResult(BleStatus.DISCONNECTED, gatt.device.address))
                }
            }
        }

        // 发现蓝牙服务
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val address = gatt.device.address
            if (status == BluetoothGatt.GATT_SUCCESS) {// 发现了蓝牙服务后，才算真正的连接成功。
                addChannelAndReceive(address, gatt)
                mConnectedBluetoothGattList.add(gatt)
                mBleResultLiveData.postValue(BleResult(BleStatus.CONNECTED, gatt.device.address))
            } else {
                closeChannelAndRemove(address)
                mConnectedBluetoothGattList.remove(gatt)
                mBleResultLiveData.postValue(BleResult(BleStatus.DISCONNECTED, gatt.device.address))
            }
        }

        // 外围设备调用 notifyCharacteristicChanged() 通知所有中心设备，数据改变了，此方法被触发。
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_CHARACTERISTIC_CHANGED, characteristic.value))
        }

        // 谁进行读数据操作，然后外围设备才会被动的发出一个数据，而这个数据只能是读操作的对象才有资格获得到这个数据。
        override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_CHARACTERISTIC_READ_SUCCESS, characteristic.value))
            } else {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_CHARACTERISTIC_READ_FAILURE, characteristic.value))
            }
        }

        // 写特征值
        override fun onCharacteristicWrite(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_CHARACTERISTIC_WRITE_SUCCESS, characteristic.value))
            } else {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_CHARACTERISTIC_WRITE_FAILURE, characteristic.value))
            }
        }

        // 读描述值
        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_DESCRIPTOR_READ_SUCCESS, descriptor.value))
            } else {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_DESCRIPTOR_READ_FAILURE, descriptor.value))
            }
        }

        // 写描述值
        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_DESCRIPTOR_WRITE_SUCCESS, descriptor.value))
            } else {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_DESCRIPTOR_WRITE_FAILURE, descriptor.value))
            }
        }

        // 读蓝牙信号值
        override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_READ_REMOTE_RSSI_SUCCESS, rssi))
            } else {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_READ_REMOTE_RSSI_FAILURE, rssi))
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_MTU_CHANGED_SUCCESS, mtu))
            } else {
                mBleResultLiveData.postValue(BleResult(BleStatus.ON_MTU_CHANGED_FAILURE, mtu))
            }
        }

    }

    // 如果要对多个设备发起连接请求，最好是有一个统一的设备连接管理，把发起连接请求用队列管理起来。
    // 前一个设备请求建立连接，后面请求在队列中等待。
    // 如果连接成功了，就处理下一个连接请求。
    // 如果连接失败了（例如出错，或者连接超时失败），就马上调用 BluetoothGatt.disconnect() 来释放建立连接请求，然后处理下一个设备连接请求。
    override fun connect(command: BleConnectCommand) {
        if (isConnected(command.address)) return
        command.connect(mActivity.lifecycleScope, mGattCallback, mBluetoothAdapter) { disconnect(BleDisconnectCommand(mActivity, command.address)) }
    }

    override fun disconnect(command: BleDisconnectCommand) {
        val address = command.address
        if (!isConnected(address)) return
        val listIterator = mConnectedBluetoothGattList.listIterator()
        while (listIterator.hasNext()) {
            val gatt = listIterator.next()
            if (gatt.device.address == address) {
                command.disconnect(mActivity.lifecycleScope, gatt)
                listIterator.remove()
                return
            }
        }
        mBleResultLiveData.postValue(BleResult(BleStatus.DISCONNECTED, address))
    }

    @Synchronized
    override fun read(command: BleReadCharacteristicCommand) {
        val address = command.address
        if (!mChannels.containsKey(address)) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_CHARACTERISTIC_READ_FAILURE, errorMsg = "设备未连接 $command"))
            return
        }

        mActivity.lifecycleScope.launch(Dispatchers.IO) {
            mChannels[address]?.send(command)
        }
    }

    @Synchronized
    override fun write(command: BleWriteCharacteristicCommand) {
        val address = command.address
        if (!mChannels.containsKey(address)) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_CHARACTERISTIC_WRITE_FAILURE, errorMsg = "设备未连接 $command"))
            return
        }

        mActivity.lifecycleScope.launch(Dispatchers.IO) {
            mChannels[address]?.send(command)
        }
    }

    @Synchronized
    override fun setMtu(command: BleSetMtuCommand) {
        val address = command.address
        if (!mChannels.containsKey(address)) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_MTU_CHANGED_FAILURE, errorMsg = "设备未连接 $command"))
            return
        }

        mActivity.lifecycleScope.launch(Dispatchers.IO) {
            mChannels[address]?.send(command)
        }
    }

    override fun close() {
        mChannels.values.forEach {
            it.close()
        }
        mChannels.clear()

        mConnectedBluetoothGattList.forEach {
            it.disconnect()
            it.close()
        }
        mConnectedBluetoothGattList.clear()

        mBluetoothAdapter = null
        mBluetoothManager = null
    }

    override fun getBluetoothAdapter(): BluetoothAdapter? {
        return mBluetoothAdapter
    }

    override fun getBluetoothManager(): BluetoothManager? {
        return mBluetoothManager
    }

    private fun isConnected(address: String) =
            mConnectedBluetoothGattList.any { it.device.address == address }

}