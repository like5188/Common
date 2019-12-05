package com.like.common.util.ble.blestate

import android.bluetooth.*
import android.content.Context
import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.like.common.util.Logger
import com.like.common.util.ble.model.BleCommand
import com.like.common.util.ble.model.BleResult
import com.like.common.util.ble.model.BleStatus
import com.like.common.util.ble.scanstrategy.IScanStrategy
import com.like.common.util.shortToastCenter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 蓝牙初始化完毕的状态
 * 可以进行扫描、连接、操作数据等操作
 */
class InitializedState(
        private val mContext: Context,
        private val mCoroutineScope: CoroutineScope,
        private val mBleResultLiveData: MutableLiveData<BleResult>,
        private var mBluetoothAdapter: BluetoothAdapter?,
        private var mScanStrategy: IScanStrategy?,
        private val mScanTimeout: Long = 3000,// 蓝牙扫描时间的限制,
        private val mConnectTimeout: Long = 20000// 蓝牙连接超时时间
) : BaseBleState() {

    private var mScanning = AtomicBoolean(false)
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

        // 谁进行读数据操作，然后外围设备才会被动的发出一个数据，而这个数据只能是读操作的对象才有资格获得到这个数据。
        override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
        ) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_CHARACTERISTIC_READ, characteristic.value))
        }

        // 外围设备调用 notifyCharacteristicChanged() 通知所有中心设备，数据改变了，此方法被触发。
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

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            mBleResultLiveData.postValue(BleResult(BleStatus.ON_MTU_CHANGED, mtu))
        }

    }

    override fun startScan() {
        val scanStrategy = mScanStrategy ?: return
        if (mScanning.compareAndSet(false, true)) {
            mBleResultLiveData.postValue(BleResult(BleStatus.START_SCAN_DEVICE))
            scanStrategy.startScan(mBluetoothAdapter)
            mCoroutineScope.launch(Dispatchers.IO) {
                // 在指定超时时间时取消扫描
                delay(mScanTimeout)
                if (mScanning.get()) {
                    stopScan()
                }
            }
        }
    }

    override fun stopScan() {
        val scanStrategy = mScanStrategy ?: return
        if (mScanning.compareAndSet(true, false)) {
            mBleResultLiveData.postValue(BleResult(BleStatus.STOP_SCAN_DEVICE))
            scanStrategy.stopScan(mBluetoothAdapter)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothDevice.connectGatt(mContext, false, mGattCallback, BluetoothDevice.TRANSPORT_LE)// 第二个参数表示是否自动重连
            } else {
                bluetoothDevice.connectGatt(mContext, false, mGattCallback)// 第二个参数表示是否自动重连
            }

            withContext((Dispatchers.IO)) {
                delay(mConnectTimeout)
                disconnect(address)
            }
        }
    }

    override fun write(command: BleCommand) {
        val address = command.address
        if (!mChannels.containsKey(address)) {
            val channel = Channel<BleCommand>()
            mChannels[address] = channel
            mCoroutineScope.launch(Dispatchers.IO) {
                for (bleCommand in channel) {
                    mConnectedBluetoothGattList.firstOrNull { it.device.address == address }?.let {
                        bleCommand.write(mCoroutineScope, it)
                    }
                }
            }
        }
        mCoroutineScope.launch(Dispatchers.IO) {
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
        stopScan()
        disconnectAll()
        mChannels.values.forEach {
            it.close()
        }
        mChannels.clear()
        mBluetoothAdapter = null
        mScanStrategy = null
    }

    override fun setMtu(address: String, mtu: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mConnectedBluetoothGattList.firstOrNull { it.device.address == address }?.requestMtu(mtu)
        } else {
            mContext.shortToastCenter("android 5.0 才支持 setMtu() 操作")
        }
    }

    override fun getBluetoothAdapter(): BluetoothAdapter? {
        return mBluetoothAdapter
    }

}