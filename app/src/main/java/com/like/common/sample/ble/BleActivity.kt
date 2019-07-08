package com.like.common.sample.ble

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.view.View
import com.like.common.ui.BaseActivity
import com.like.common.util.PermissionUtils
import com.like.common.util.ble.BleManager
import com.like.common.util.ble.model.BleCommand
import com.like.common.util.ble.model.BleResult
import com.like.common.util.ble.model.BleStatus
import com.like.common.util.ble.scanstrategy.ScanStrategy18
import com.like.common.util.ble.scanstrategy.ScanStrategy21
import com.like.common.view.toolbar.ToolbarUtils
import com.like.common.R
import com.like.common.databinding.ActivityBleBinding
import com.like.livedatarecyclerview.adapter.BaseAdapter
import com.like.livedatarecyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.livedatarecyclerview.listener.OnItemClickListener
import com.like.livedatarecyclerview.model.IRecyclerViewItem
import com.like.livedatarecyclerview.viewholder.CommonViewHolder
import java.nio.ByteBuffer

/**
 * 蓝牙测试
 */
class BleActivity : BaseActivity() {
    companion object {
        /**
         * 打开蓝牙请求码
         */
        const val REQUEST_ENABLE_BT = 1
    }

    private val mBinding: ActivityBleBinding by lazy {
        DataBindingUtil.setContentView<ActivityBleBinding>(this, R.layout.activity_ble)
    }
    private val mBleResultLiveData: MutableLiveData<BleResult> = MutableLiveData()
    private val mBleManager: BleManager by lazy {
        val scanStrategy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ScanStrategy21(object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    addItem(result?.device)
                }
            })
        } else {
            ScanStrategy18(BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
                addItem(device)
            })
        }
        BleManager(this.applicationContext, mBleResultLiveData, scanStrategy)
    }
    private val mAdapter: BaseAdapter by lazy { BaseAdapter() }
    private val mToolbarUtils: ToolbarUtils by lazy {
        ToolbarUtils(this, mBinding.flToolbarContainer)
                .showTitle("低功耗蓝牙测试", R.color.common_text_white_0)
                .showNavigationButton(R.drawable.icon_back, View.OnClickListener {
                    finish()
                })
    }
    private val mPermissionUtils: PermissionUtils by lazy {
        PermissionUtils().apply {
            init(this@BleActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToolbarUtils
        mBleResultLiveData.observe(this, Observer {
            mBinding.tvStatus.text = it?.status?.des
            when (it?.status) {
                BleStatus.INIT_FAILURE -> {
                    mBleManager.openBTDialog(this, REQUEST_ENABLE_BT)
                }
                else -> {
                }
            }
        })
        initBle()
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.adapter = mAdapter
        mAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(holder: CommonViewHolder, position: Int, data: IRecyclerViewItem?) {
                curAddress = (data as BleInfo).address
                mBleManager.connect(curAddress)
            }
        })
    }

    fun scanDevice(view: View) {
        mAdapter.mAdapterDataManager.clear()
        mBleManager.scanBleDevice()
    }

    private var curAddress = ""

    fun sendData(view: View) {
        mBleManager.write(
                object : BleCommand(
                        this,
                        1,
                        "hahah".toByteArray(),
                        curAddress,
                        "0000fec9-0000-1000-8000-00805f9b34fb",
                        mBleResultLiveData,
                        "描述",
                        false,
                        5000,
                        20,
                        300,
                        {

                        },
                        {

                        }
                ) {
                    override fun isWholeFrame(data: ByteBuffer): Boolean {
                        return true
                    }
                }
        )
    }

    private fun addItem(device: BluetoothDevice?) {
        val address = device?.address ?: ""
        val name = device?.name ?: "未知设备"
        if (!mAdapter.mAdapterDataManager.getAll().any { (it as BleInfo).address == address }) {
            mAdapter.mAdapterDataManager.addItemToEnd(BleInfo(name, address))
        }
    }

    @SuppressLint("MissingPermission")
    private fun initBle() {
        mPermissionUtils.checkPermissions(
                {
                    mBleManager.initBle()
                },
                {
                    mBinding.tvStatus.text = BleStatus.INIT_FAILURE.des
                },
                android.Manifest.permission.BLUETOOTH_ADMIN, android.Manifest.permission.BLUETOOTH, android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBleManager.close()
    }

}
