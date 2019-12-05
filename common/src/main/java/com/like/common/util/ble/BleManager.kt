package com.like.common.util.ble

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.MainThread
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.like.common.util.ble.blestate.BaseBleState
import com.like.common.util.ble.blestate.ConnectState
import com.like.common.util.ble.blestate.InitialState
import com.like.common.util.ble.blestate.ScanState
import com.like.common.util.ble.model.BleConnectCommand
import com.like.common.util.ble.model.BleResult
import com.like.common.util.ble.model.BleStatus
import com.like.common.util.ble.model.BleWriteCommand
import com.like.common.util.ble.scanstrategy.IScanStrategy
import kotlinx.coroutines.CoroutineScope

/**
 * 蓝牙是一种近距离无线通信技术。它的特性就是近距离通信，典型距离是 10 米以内，传输速度最高可达 24 Mbps，支持多连接，安全性高，非常适合用智能设备上。
 * Android 4.3 开始，开始支持BLE功能，但只支持Central Mode（中心模式）
 * Android 5.0开始，开始支持Peripheral Mode（外设模式）
 *
 * GATT，属性配置文件，定义数据的交互方式和含义。
 * GATT 连接是独占的。也就是一个 BLE 外设同时只能被一个中心设备连接。一旦外设被连接，它就会马上停止广播，这样它就对其他设备不可见了。当设备断开，它又开始广播。中心设备和外设需要双向通信的话，唯一的方式就是建立 GATT 连接。
 *
 * GATT 定义了4个概念：配置文件（Profile）、服务（Service）、特征（Characteristic）和描述（Descriptor）。他们的关系是这样的：Profile 就是定义了一个实际的应用场景，一个 Profile包含若干个 Service，一个 Service 包含若干个 Characteristic，一个 Characteristic 可以包含若干 Descriptor。
 * Profile 并不是实际存在于 BLE 外设上的，它只是一个被 Bluetooth SIG 或者外设设计者预先定义的 Service 的集合。例如心率Profile（Heart Rate Profile）就是结合了 Heart Rate Service 和 Device Information Service。
 * Service 是把数据分成一个个的独立逻辑项，它包含一个或者多个 Characteristic。每个 Service 有一个 UUID 唯一标识。 UUID 有 16 bit 的，或者 128 bit 的。16 bit 的 UUID 是官方通过认证的，需要花钱购买，128 bit 是自定义的，这个就可以自己随便设置。官方通过了一些标准 Service，完整列表在这里。以 Heart Rate Service为例，可以看到它的官方通过 16 bit UUID 是  0x180D，包含 3 个 Characteristic：Heart Rate Measurement, Body Sensor Location 和 Heart Rate Control Point，并且定义了只有第一个是必须的，它是可选实现的。
 * Characteristic， 它定义了数值和操作，包含一个Characteristic声明、Characteristic属性、值、值的描述(Optional)。通常我们讲的 BLE 通信，其实就是对 Characteristic 的读写或者订阅通知。比如在实际操作过程中，我对某一个Characteristic进行读，就是获取这个Characteristic的value。
 *
 * Service、Characteristic 还有 Descriptor 都是使用 UUID 唯一标示的。
 * UUID 是全局唯一标识，它是 128bit 的值，为了便于识别和阅读，一般以 “8位-4位-4位-4位-12位”的16进制标示，比如“12345678-abcd-1000-8000-123456000000”。
 * 但是，128bit的UUID 太长，考虑到在低功耗蓝牙中，数据长度非常受限的情况，蓝牙又使用了所谓的 16 bit 或者 32 bit 的 UUID，形式如下：“0000XXXX-0000-1000-8000-00805F9B34FB”。除了 “XXXX” 那几位以外，其他都是固定，所以说，其实 16 bit UUID 是对应了一个 128 bit 的 UUID。这样一来，UUID 就大幅减少了，例如 16 bit UUID只有有限的 65536（16的四次方） 个。与此同时，因为数量有限，所以 16 bit UUID 并不能随便使用。蓝牙技术联盟已经预先定义了一些 UUID，我们可以直接使用，比如“00001011-0000-1000-8000-00805F9B34FB”就一个是常见于BLE设备中的UUID。当然也可以花钱定制自定义的UUID。
 *
 * 应用在使用蓝牙设备的时候必须要声明蓝牙权限 BLUETOOTH 需要这个权限才可以进行蓝牙通信，例如：请求连接、接受连接、和传输数据。
 * <uses-permission android:name="android.permission.BLUETOOTH" />
 * 如果还需要发现或者操作蓝牙设置，则需要声明 BLUETOOTH_ADMIN 权限。使用这个权限的前提是要有 BLUETOOTH 权限。
 * <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
 * <!-- Android6.0 蓝牙扫描需要申请定位权限，因为 BLE 确实有定位的能力-->
 * <uses-permission-sdk-23 android:name="android.permission.ACCESS_COARSE_LOCATION" />
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 *
 * BLE 应用可以分为两大类：基于非连接的和连接的。
 * 基于非连接的，这种应用就是依赖 BLE 的广播，也叫作 Beacon。这里有两个角色，发送广播的一方叫做 Broadcaster，监听广播的一方叫 Observer。
 * 基于连接的，就是通过建立 GATT 连接，收发数据。这里也有两个角色，发起连接的一方，叫做中心设备—Central，被连接的设备，叫做外设—Peripheral。一个中心设备可以连接多个外设，一个外设只能被一个中心设备连接。中心设备和外设之间的通信是双向的。其实一个中心设备能够同时连接的外围设备数量也是有限，一般最多连接 7 个外设。
 * Android 从 4.3(API Level 18) 开始支持低功耗蓝牙，但是只支持作为中心设备（Central）模式，这就意味着 Android 设备只能主动扫描和链接其他外围设备（Peripheral）。从 Android 5.0(API Level 21) 开始两种模式都支持。
 *
 * BLE 所有回调函数都不是在主线程中的
 *
 * 接收通知：有两种方式可以接收通知，indicate和notify。indicate和notify的区别就在于，indicate是一定会收到数据，notify有可能会丢失数据。indicate底层封装了应答机制，如果没有收到中央设备的回应，会再次发送直至成功；而notify不会有central收到数据的回应，可能无法保证数据到达的准确性，优势是速度快。通常情况下，当外围设备需要不断地发送数据给APP的时候，比如血压计在测量过程中的压力变化，胎心仪在监护过程中的实时数据传输，这种频繁的情况下，优先考虑notify形式。当只需要发送很少且很重要的一条数据给APP的时候，优先考虑indicate形式。当然，从Android开发角度的出发，如果硬件放已经考虑了成熟的协议和发送方式，我们需要做的仅仅是根据其配置的数据发送方式进行相应的对接即可。
 *
 * @author like
 * @version 1.0
 * created on 2017/4/14 11:52
 */
class BleManager(private val mActivity: FragmentActivity) {
    private var mBleState: BaseBleState? = null
    private val mAllLiveData = MutableLiveData<BleResult>()
    private val mLiveData: MediatorLiveData<BleResult> by lazy {
        MediatorLiveData<BleResult>().apply {
            addSource(mAllLiveData) {
                // 过滤状态，只发送一部分，其它的用回调替代。
                when {
                    it.status == BleStatus.ON ||
                            it.status == BleStatus.OFF ||
                            it.status == BleStatus.INIT ||
                            it.status == BleStatus.INIT_SUCCESS ||
                            it.status == BleStatus.INIT_FAILURE ||
                            it.status == BleStatus.START_SCAN_DEVICE ||
                            it.status == BleStatus.STOP_SCAN_DEVICE
                    -> postValue(it)
                }
            }
        }
    }

    // 蓝牙打开关闭监听器
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                        BluetoothAdapter.STATE_ON -> {// 蓝牙已打开
                            mAllLiveData.postValue(BleResult(BleStatus.ON))
                            initBle()// 初始化蓝牙
                        }
                        BluetoothAdapter.STATE_OFF -> {// 蓝牙已关闭
                            mAllLiveData.postValue(BleResult(BleStatus.OFF))
                            mBleState?.close()
                        }
                    }
                }
            }
        }
    }
    private val mObserver = Observer<BleResult> {
        when (it?.status) {
            BleStatus.INIT_SUCCESS -> {
                val bluetoothManager = mBleState?.getBluetoothManager() ?: return@Observer
                val bluetoothAdapter = mBleState?.getBluetoothAdapter() ?: return@Observer
                mBleState = ScanState(mActivity, mAllLiveData, bluetoothManager, bluetoothAdapter)
            }
            else -> {
            }
        }
    }

    init {
        // 注册蓝牙打开关闭监听
        mActivity.registerReceiver(mReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        mAllLiveData.observe(mActivity, mObserver)
    }

    fun getLiveData(): LiveData<BleResult> = mLiveData

    /**
     * 初始化蓝牙适配器
     */
    @MainThread
    fun initBle() {
        if (mBleState == null || mBleState !is InitialState) {
            mBleState = InitialState(mActivity, mAllLiveData)
        }
        mBleState?.init()
    }

    /**
     * 扫描蓝牙设备
     */
    fun scanBleDevice(scanStrategy: IScanStrategy, ScanTimeout: Long = 3000) {
        mBleState?.startScan(scanStrategy, ScanTimeout)
    }

    /**
     * 停止扫描蓝牙设备
     */
    fun stopScanBleDevice() {
        mBleState?.stopScan()
    }

    /**
     * 根据蓝牙地址，连接蓝牙设备
     */
    fun connect(command: BleConnectCommand) {
        if (mBleState is ScanState) {
            val bluetoothManager = mBleState?.getBluetoothManager() ?: return
            val bluetoothAdapter = mBleState?.getBluetoothAdapter() ?: return
            mBleState = ConnectState(mActivity, mAllLiveData, bluetoothManager, bluetoothAdapter)
        }
        if (mBleState is ConnectState) {
            command.mLiveData = mAllLiveData
            mBleState?.connect(command)
        }
    }

    fun write(command: BleWriteCommand) {
        command.mLiveData = mAllLiveData
        mBleState?.write(command)
    }

    /**
     * 取消连接
     */
    fun disconnect(address: String) {
        mBleState?.disconnect(address)
    }

    /**
     * 断开所有蓝牙设备
     */
    fun disconnectAll() {
        mBleState?.disconnectAll()
    }

    /**
     * 关闭所有蓝牙连接
     */
    fun close() {
        mBleState?.close()
        mBleState = null
        try {
            mActivity.unregisterReceiver(mReceiver)
            mAllLiveData.removeObserver(mObserver)
        } catch (e: Exception) {// 避免 java.lang.IllegalArgumentException: Receiver not registered
            e.printStackTrace()
        }
    }

    fun setMtu(address: String, mtu: Int) {
        mBleState?.setMtu(address, mtu)
    }
}
