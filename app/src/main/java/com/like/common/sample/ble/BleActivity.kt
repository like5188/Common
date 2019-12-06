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
import androidx.lifecycle.Observer
import com.like.common.sample.R
import com.like.common.sample.databinding.ActivityBleBinding
import com.like.common.util.ble.BleManager
import com.like.common.util.ble.scanstrategy.ScanStrategy18
import com.like.common.util.ble.scanstrategy.ScanStrategy21
import com.like.common.view.toolbar.ToolbarUtils
import com.like.livedatarecyclerview.layoutmanager.WrapLinearLayoutManager

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
    private val mScanStrategy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
    private val mBleManager: BleManager by lazy {
        BleManager(this)
    }
    private val mAdapter: BleAdapter by lazy { BleAdapter(this, mBleManager) }
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
        mBleManager.getLiveData().observe(this, Observer {
            mBinding.tvStatus.text = it?.status?.des
        })
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.adapter = mAdapter
    }

    fun initBle(view: View) {
        mBleManager.initBle()
    }

    fun startScan(view: View) {
        mAdapter.mAdapterDataManager.clear()
        mBleManager.scanBleDevice(mScanStrategy)
    }

    fun stopScan(view: View) {
        mBleManager.stopScanBleDevice()
    }

    private fun addItem(device: BluetoothDevice?) {
        val address = device?.address ?: ""
        val name = device?.name ?: "未知设备"
        if (!mAdapter.mAdapterDataManager.getAll().any { (it as BleInfo).address == address }) {
            mAdapter.mAdapterDataManager.addItemToEnd(BleInfo(name, address))
        }
    }

    override fun onDestroy() {
        mBleManager.close()
        super.onDestroy()
    }

}
