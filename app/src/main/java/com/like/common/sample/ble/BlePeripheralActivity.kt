package com.like.common.sample.ble

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.text.method.ScrollingMovementMethod
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
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothGattServer: BluetoothGattServer? = null
    private var mBluetoothLeAdvertiser: BluetoothLeAdvertiser? = null

    private val mAdvertiseCallback: AdvertiseCallback = object : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            val errorMsg = when (errorCode) {
                ADVERTISE_FAILED_DATA_TOO_LARGE -> "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes."
                ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> "Failed to start advertising because no advertising instance is available."
                ADVERTISE_FAILED_ALREADY_STARTED -> "Failed to start advertising as the advertising is already started"
                ADVERTISE_FAILED_INTERNAL_ERROR -> "Operation failed due to an internal error"
                ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> "This feature is not supported on this platform"
                else -> "errorCode=$errorCode"
            }
            appendText("广播失败 $errorMsg")
        }

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            appendText("广播成功 txPowerLevel=${settingsInEffect.txPowerLevel} mode=${settingsInEffect.mode} timeout=${settingsInEffect.timeout}")
            initServices()//该方法是添加一个服务，在此处调用即将服务广播出去
        }
    }
    private val bluetoothGattServerCallback = object : BluetoothGattServerCallback() {

        /**
         * @param newState  连接状态，只能为BluetoothProfile.STATE_CONNECTED和BluetoothProfile.STATE_DISCONNECTED。
         */
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            appendText("onConnectionStateChange device=$device status=$status newState=$newState")
        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService) {
            appendText("onServiceAdded status=$status service=${service.uuid}")
        }

        /**
         * @param requestId     请求的标识
         * @param offset        特性值偏移量
         */
        override fun onCharacteristicReadRequest(device: BluetoothDevice, requestId: Int, offset: Int, characteristic: BluetoothGattCharacteristic) {
            appendText("onCharacteristicReadRequest device=$device requestId=$requestId offset=$offset characteristic=$characteristic")
            // 此方法要求作出响应
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, byteArrayOf(0x07, 0x08))// 最后一个参数是传的数据。
        }

        /**
         * @param preparedWrite     true则写操作必须排队等待稍后执行
         * @param responseNeeded    是否需要响应，需要响应则必须调用 sendResponse()
         */
        override fun onCharacteristicWriteRequest(device: BluetoothDevice, requestId: Int, characteristic: BluetoothGattCharacteristic, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray) {
            appendText("onCharacteristicWriteRequest device=$device requestId=$requestId characteristic=$characteristic preparedWrite=$preparedWrite responseNeeded=$responseNeeded offset=$offset value=${value.contentToString()}")
            // 如果 responseNeeded=true（此属性由中心设备的 characteristic.setWriteType() 方法设置），则必须调用 sendResponse()方法回复中心设备，这个方法会触发中心设备的 BluetoothGattCallback.onCharacteristicWrite() 方法，然后中心设备才能继续下次写数据，否则不能再次写入数据。
            // 如果 responseNeeded=false，那么不需要 sendResponse() 方法，也会触发中心设备的 BluetoothGattCallback.onCharacteristicWrite() 方法
            if (responseNeeded) {
                mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, byteArrayOf(0x03, 0x04))
            }
            // 外围设备向中心设备不能发送数据，必须通过notify 或者indicate的方式，andorid只发现notify接口。
            // 调用 notifyCharacteristicChanged() 方法向中心设备发送数据，会触发 onNotificationSent() 方法和中心设备的 BluetoothGattCallback.onCharacteristicChanged() 方法。
//            characteristic.value = byteArrayOf(0x05, 0x06)
//            mBluetoothGattServer?.notifyCharacteristicChanged(device, characteristic, false)// 最后一个参数表示是否需要客户端确认
        }

        override fun onDescriptorReadRequest(device: BluetoothDevice, requestId: Int, offset: Int, descriptor: BluetoothGattDescriptor) {
            appendText("onDescriptorReadRequest device=$device requestId=$requestId offset=$offset descriptor=$descriptor")
            mBluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, byteArrayOf(10, 11, 12, 13, 14, 15, 16, 17, 18, 19))
        }

        override fun onDescriptorWriteRequest(device: BluetoothDevice, requestId: Int, descriptor: BluetoothGattDescriptor, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray) {
            appendText("onDescriptorWriteRequest device=$device requestId=$requestId descriptor=$descriptor preparedWrite=$preparedWrite responseNeeded=$responseNeeded offset=$offset value=${value.contentToString()}")
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
    // 蓝牙打开关闭监听器
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                        BluetoothAdapter.STATE_ON -> {// 蓝牙已打开
                            appendText("蓝牙已打开")
                            init(mBinding.tvStatus)// 初始化蓝牙
                        }
                        BluetoothAdapter.STATE_OFF -> {// 蓝牙已关闭
                            appendText("蓝牙已关闭")
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToolbarUtils
        mBinding.tvStatus.movementMethod = ScrollingMovementMethod()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            appendText("mac = ${android.provider.Settings.Secure.getString(contentResolver, "bluetooth_address")}")
        }
        // 注册蓝牙打开关闭监听
        registerReceiver(mReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    fun init(view: View) {
        // 设备不支持BLE
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            appendText("蓝牙初始化失败，设备不支持蓝牙功能")
            return
        }
        if (isBlePrepared()) {
            appendText("蓝牙已经初始化")
            return
        }

        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        if (mBluetoothManager == null) {
            appendText("蓝牙初始化失败，获取 mBluetoothManager 失败")
            return
        }

        mBluetoothAdapter = mBluetoothManager?.adapter
        if (mBluetoothAdapter == null) {
            appendText("蓝牙初始化失败，获取 mBluetoothAdapter 失败")
            return
        }

        if (isBlePrepared()) {
            appendText("蓝牙初始化成功")
        } else {
            mBluetoothManager = null
            mBluetoothAdapter = null
            appendText("蓝牙初始化失败，蓝牙未打开")
            openBTDialog(1)
        }
    }

    /**
     * 蓝牙是否就绪
     */
    private fun isBlePrepared() = mBluetoothAdapter?.isEnabled ?: false

    /**
     * 弹出开启蓝牙的对话框
     */
    private fun openBTDialog(requestCode: Int) {
        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), requestCode)
    }

    fun startAdvertising(view: View) {
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = mBluetoothAdapter?.bluetoothLeAdvertiser
            if (mBluetoothLeAdvertiser == null) {
                appendText("广播失败")
                return
            }
        }

        appendText("开始广播")
        mBluetoothLeAdvertiser?.startAdvertising(createAdvertiseSettings(), createAdvertiseData(byteArrayOf(0x34, 0x56)), createScanResponseAdvertiseData(), mAdvertiseCallback)
    }

    fun stopAdvertising(view: View) {
        appendText("停止广播")
        mBluetoothLeAdvertiser?.stopAdvertising(mAdvertiseCallback)
    }

    private fun createAdvertiseData(data: ByteArray): AdvertiseData {
        return AdvertiseData.Builder()
                .addManufacturerData(0x01AC, data)
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(true)
                .build()
    }

    private fun createScanResponseAdvertiseData(): AdvertiseData {
        return AdvertiseData.Builder()
                .addServiceData(ParcelUuid(UUID_SERVICE), byteArrayOf(1, 2, 3, 4, 5))
                .setIncludeTxPowerLevel(true)
                .build()
    }

    private fun createAdvertiseSettings(): AdvertiseSettings {
        return AdvertiseSettings.Builder()
                // 设置广播的模式，低功耗，平衡和低延迟三种模式；
                // 对应  AdvertiseSettings.ADVERTISE_MODE_LOW_POWER  ,ADVERTISE_MODE_BALANCED ,ADVERTISE_MODE_LOW_LATENCY
                // 从左右到右，广播的间隔会越来越短
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                // 设置是否可以连接。
                // 广播分为可连接广播和不可连接广播，一般不可连接广播应用在iBeacon设备上，这样APP无法连接上iBeacon设备
                .setConnectable(true)
                // 设置广播的最长时间，最大值为常量AdvertiseSettings.LIMITED_ADVERTISING_MAX_MILLIS = 180 * 1000;  180秒
                // 设为0时，代表无时间限制会一直广播
                .setTimeout(0)
                // 设置广播的信号强度
                // 常量有AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW, ADVERTISE_TX_POWER_LOW, ADVERTISE_TX_POWER_MEDIUM, ADVERTISE_TX_POWER_HIGH
                // 从左到右分别表示强度越来越强.
                // 举例：当设置为ADVERTISE_TX_POWER_ULTRA_LOW时，
                // 手机1和手机2放在一起，手机2扫描到的rssi信号强度为-56左右，
                // 当设置为ADVERTISE_TX_POWER_HIGH  时， 扫描到的信号强度为-33左右，
                // 信号强度越大，表示手机和设备靠的越近
                // ＊ AdvertiseSettings.ADVERTISE_TX_POWER_HIGH -56 dBm @ 1 meter with Nexus 5
                // ＊ AdvertiseSettings.ADVERTISE_TX_POWER_LOW -75 dBm @ 1 meter with Nexus 5
                // ＊ AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM -66 dBm @ 1 meter with Nexus 5
                // ＊ AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW not detected with Nexus 5
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .build()
    }

    private fun initServices() {
        if (mBluetoothGattServer != null) return
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
                BluetoothGattCharacteristic.PROPERTY_READ or
                        BluetoothGattCharacteristic.PROPERTY_WRITE or
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ or
                        BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(characteristicWrite)

        bluetoothGattServer.addService(service)
        mBluetoothGattServer = bluetoothGattServer
    }

    private fun appendText(text: String) {
        runOnUiThread {
            val sb = StringBuilder(mBinding.tvStatus.text)
            sb.append(text).append("\n\n")
            mBinding.tvStatus.text = sb.toString()
            val offset = mBinding.tvStatus.lineCount * mBinding.tvStatus.lineHeight
            mBinding.tvStatus.scrollTo(0, offset - mBinding.tvStatus.height + mBinding.tvStatus.lineHeight)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(mReceiver)
        } catch (e: Exception) {// 避免 java.lang.IllegalArgumentException: Receiver not registered
            e.printStackTrace()
        }
    }

}
