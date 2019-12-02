package com.like.common.sample.ble

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.sample.databinding.ActivityBlePeripheralBinding
import com.like.common.view.toolbar.ToolbarUtils
import java.util.*


/**
 * 蓝牙外围设备
 * 自安卓5.0后，谷歌加入了对安卓手机作为低功耗蓝牙外围设备，即服务端的支持。使得手机可以通过低功耗蓝牙进行相互通信。
 * 实现这一功能其实只需要分为设置广播和设置服务器两个部分完成即可
 */
class BlePeripheralActivity : AppCompatActivity() {
    companion object {
        private val UUID_SERVICE: UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
        private val UUID_CHARACTERISTIC_READ: UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")
        private val UUID_CHARACTERISTIC_WRITE: UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb")
        private val UUID_DESCRIPTOR: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    private val mBinding: ActivityBlePeripheralBinding by lazy {
        DataBindingUtil.setContentView<ActivityBlePeripheralBinding>(this, com.like.common.sample.R.layout.activity_ble_peripheral)
    }
    private val mToolbarUtils: ToolbarUtils by lazy {
        ToolbarUtils(this, mBinding.flToolbarContainer)
                .showTitle("蓝牙外围设备", com.like.common.sample.R.color.common_text_white_0)
                .showNavigationButton(com.like.common.sample.R.drawable.icon_back, View.OnClickListener {
                    finish()
                })
    }

    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothGattServer: BluetoothGattServer? = null
    private var mBluetoothLeAdvertiser: BluetoothLeAdvertiser? = null

    private var mAdvertiseCallback: AdvertiseCallback? = null
    private val bluetoothGattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            appendText("onConnectionStateChange device=$device status=$status newState=$newState")
        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService) {
            appendText("onServiceAdded status=$status service=${service.uuid}")
        }

        override fun onCharacteristicReadRequest(device: BluetoothDevice, requestId: Int, offset: Int, characteristic: BluetoothGattCharacteristic) {
            appendText("onCharacteristicReadRequest device=$device requestId=$requestId offset=$offset characteristic=$characteristic")
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0))
        }

        override fun onCharacteristicWriteRequest(device: BluetoothDevice, requestId: Int, characteristic: BluetoothGattCharacteristic, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray) {
            appendText("onCharacteristicWriteRequest device=$device requestId=$requestId characteristic=$characteristic preparedWrite=$preparedWrite responseNeeded=$responseNeeded offset=$offset value=$value")
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, byteArrayOf(0, 9, 8, 7, 6, 5, 4, 3, 2, 1))
        }

        override fun onDescriptorReadRequest(device: BluetoothDevice, requestId: Int, offset: Int, descriptor: BluetoothGattDescriptor) {
            appendText("onDescriptorReadRequest device=$device requestId=$requestId offset=$offset descriptor=$descriptor")
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, byteArrayOf(10, 11, 12, 13, 14, 15, 16, 17, 18, 19))
        }

        override fun onDescriptorWriteRequest(device: BluetoothDevice, requestId: Int, descriptor: BluetoothGattDescriptor, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray) {
            appendText("onDescriptorWriteRequest device=$device requestId=$requestId descriptor=$descriptor preparedWrite=$preparedWrite responseNeeded=$responseNeeded offset=$offset value=$value")
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, byteArrayOf(19, 18, 17, 16, 15, 14, 13, 12, 11, 10))
        }

        override fun onExecuteWrite(device: BluetoothDevice, requestId: Int, execute: Boolean) {
            appendText("onExecuteWrite device=$device requestId=$requestId execute=$execute")
        }

        override fun onNotificationSent(device: BluetoothDevice, status: Int) {
            appendText("onNotificationSent device=$device status=$status")
        }

        override fun onMtuChanged(device: BluetoothDevice, mtu: Int) {
            appendText("onMtuChanged device=$device mtu=$mtu")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToolbarUtils
        init()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun init() {
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            if (mBluetoothManager != null) {
                val bluetoothAdapter = mBluetoothManager?.adapter
                if (bluetoothAdapter != null) {
                    val bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
                    if (bluetoothLeAdvertiser == null) {
                        appendText("设备不支持蓝牙广播")
                    } else {
                        appendText("蓝牙初始化成功")
                        mBluetoothLeAdvertiser = bluetoothLeAdvertiser
                    }
                } else {
                    appendText("设备不支持蓝牙")
                }
            } else {
                appendText("设备不支持蓝牙")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startAdvertising(view: View) {
        appendText("开始广播")
        if (mAdvertiseCallback == null) {
            val settings = AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                    .setConnectable(true)
                    .setTimeout(0)
                    .build()
            val advertiseData = AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(true)
                    .build()
            val scanResponse = AdvertiseData.Builder()
                    .addServiceUuid(ParcelUuid(UUID_SERVICE))
                    .setIncludeTxPowerLevel(true)
                    .build()
            mAdvertiseCallback = object : AdvertiseCallback() {
                override fun onStartFailure(errorCode: Int) {
                    super.onStartFailure(errorCode)
                    appendText("广播失败 errorCode=$errorCode")
                }

                override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                    super.onStartSuccess(settingsInEffect)
                    appendText("广播成功")
                    initServices()//该方法是添加一个服务，在此处调用即将服务广播出去
                }
            }

            mBluetoothLeAdvertiser?.startAdvertising(settings, advertiseData, scanResponse, mAdvertiseCallback)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun stopAdvertising(view: View) {
        appendText("停止广播")
        mBluetoothLeAdvertiser?.stopAdvertising(mAdvertiseCallback)
        mAdvertiseCallback = null
    }

    private fun initServices() {
        val bluetoothGattServer = mBluetoothManager?.openGattServer(this, bluetoothGattServerCallback) ?: return
        val service = BluetoothGattService(UUID_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY)

        val characteristicRead = BluetoothGattCharacteristic(
                UUID_CHARACTERISTIC_READ,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ
        )
        val descriptor = BluetoothGattDescriptor(
                UUID_DESCRIPTOR,
                BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        characteristicRead.addDescriptor(descriptor)
        service.addCharacteristic(characteristicRead)

        val characteristicWrite = BluetoothGattCharacteristic(
                UUID_CHARACTERISTIC_WRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE or
                        BluetoothGattCharacteristic.PROPERTY_READ or
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(characteristicWrite)

        bluetoothGattServer.addService(service)
        mBluetoothGattServer = bluetoothGattServer
    }

    private fun appendText(text: String) {
        val sb = StringBuilder(mBinding.tvStatus.text)
        sb.append(text).append("\n")
        mBinding.tvStatus.text = sb.toString()
    }

    fun gotoBleActivity(view: View) {
        startActivity(Intent(this, BleActivity::class.java))
    }
}
