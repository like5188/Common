package com.like.common.util.ble.blestate

import android.bluetooth.*
import android.os.Build
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.like.common.util.ble.model.*
import com.like.common.util.shortToastCenter
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
            } else {
                mBleResultLiveData.postValue(BleResult(BleStatus.DISCONNECTED, gatt.device.name))
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
        command.connect(mActivity.lifecycleScope, mGattCallback, mBluetoothAdapter) { disconnect(command.address) }
    }

    override fun write(command: BleWriteCommand) {
        val address = command.address
        if (!mChannels.containsKey(address)) {
            val channel = Channel<BleCommand>()
            mChannels[address] = channel
            mActivity.lifecycleScope.launch(Dispatchers.IO) {
                for (bleCommand in channel) {
                    mConnectedBluetoothGattList.firstOrNull { it.device.address == address }?.let {
                        bleCommand.write(mActivity.lifecycleScope, it)
                    }
                }
            }
        }
        mActivity.lifecycleScope.launch(Dispatchers.IO) {
            mChannels[address]?.send(command)
        }
    }

    override fun disconnect(address: String) {
        val listIterator = mConnectedBluetoothGattList.listIterator()
        while (listIterator.hasNext()) {
            val gatt = listIterator.next()
            if (gatt.device.address == address) {
                gatt.disconnect()
                listIterator.remove()
                break
            }
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
        mChannels.values.forEach {
            it.close()
        }
        mChannels.clear()
        disconnectAll()
        mBluetoothAdapter = null
        mBluetoothManager = null
    }

    override fun setMtu(address: String, mtu: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mConnectedBluetoothGattList.firstOrNull { it.device.address == address }?.requestMtu(mtu)
        } else {
            mActivity.shortToastCenter("android 5.0 才支持 setMtu() 操作")
        }
    }

    override fun getBluetoothAdapter(): BluetoothAdapter? {
        return mBluetoothAdapter
    }

    override fun getBluetoothManager(): BluetoothManager? {
        return mBluetoothManager
    }
}