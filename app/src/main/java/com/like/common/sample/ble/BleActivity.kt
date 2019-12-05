package com.like.common.sample.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityBleBinding
import com.like.common.util.ble.BleManager
import com.like.common.util.ble.model.BleReadCommand
import com.like.common.util.ble.model.BleResult
import com.like.common.util.ble.scanstrategy.ScanStrategy18
import com.like.common.util.ble.scanstrategy.ScanStrategy21
import com.like.common.util.shortToastCenter
import com.like.common.view.toolbar.ToolbarUtils
import com.like.livedatarecyclerview.adapter.BaseAdapter
import com.like.livedatarecyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.livedatarecyclerview.listener.OnItemClickListener
import com.like.livedatarecyclerview.model.IRecyclerViewItem
import com.like.livedatarecyclerview.viewholder.CommonViewHolder
import com.like.retrofit.utils.getCustomNetworkMessage
import java.nio.ByteBuffer

/**
 * 蓝牙测试
 */
class BleActivity : AppCompatActivity() {
    companion object {
        private val TAG = BleActivity::class.java.simpleName
    }

    private val mBinding: ActivityBleBinding by lazy {
        DataBindingUtil.setContentView<ActivityBleBinding>(this, R.layout.activity_ble)
    }
    private val mBleResultLiveData: MutableLiveData<BleResult> = MutableLiveData()
    private val mBleManager: BleManager by lazy {
        val scanStrategy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ScanStrategy21(object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    Log.d(TAG, "address=${result?.device?.address} rssi=${result?.rssi} scanRecord=${result?.scanRecord}")
                    addItem(result?.device)
                }
            })
        } else {
            ScanStrategy18(BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
                Log.d(TAG, "address=${device.address} rssi=$rssi scanRecord=$scanRecord")
                addItem(device)
            })
        }
        BleManager(this, lifecycleScope, mBleResultLiveData, scanStrategy)
    }
    private val mAdapter: BaseAdapter by lazy { BaseAdapter() }
    private val mToolbarUtils: ToolbarUtils by lazy {
        ToolbarUtils(this, mBinding.flToolbarContainer)
                .showTitle("蓝牙中心设备", R.color.common_text_white_0)
                .showNavigationButton(R.drawable.icon_back, View.OnClickListener {
                    finish()
                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToolbarUtils
        mBleResultLiveData.observe(this, Observer {
            mBinding.tvStatus.text = it?.status?.des
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
                object : BleReadCommand(
                        this,
                        1,
                        byteArrayOf(0x01, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02),
                        curAddress,
                        "0000fff2-0000-1000-8000-00805f9b34fb",
                        mBleResultLiveData,
                        "模拟的BleCommand",
                        5000,
                        5,
                        300,
                        {
                            shortToastCenter("执行命令成功 ${it?.contentToString()}")
                        },
                        {
                            shortToastCenter("执行命令失败！${it.getCustomNetworkMessage()}")
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

    private fun initBle() {
        mBleManager.initBle()
    }

    override fun onDestroy() {
        mBleManager.close()
        super.onDestroy()
    }

}
