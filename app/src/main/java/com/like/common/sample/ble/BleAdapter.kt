package com.like.common.sample.ble

import android.app.Activity
import com.like.common.sample.databinding.ItemBleBinding
import com.like.common.util.ble.BleManager
import com.like.common.util.ble.model.*
import com.like.common.util.shortToastCenter
import com.like.livedatarecyclerview.adapter.BaseAdapter
import com.like.livedatarecyclerview.model.IRecyclerViewItem
import com.like.livedatarecyclerview.viewholder.CommonViewHolder
import com.like.retrofit.utils.getCustomNetworkMessage
import java.nio.ByteBuffer

class BleAdapter(private val mActivity: Activity, private val mBleManager: BleManager) : BaseAdapter() {

    override fun bindOtherVariable(holder: CommonViewHolder, position: Int, item: IRecyclerViewItem?) {
        super.bindOtherVariable(holder, position, item)
        if (item !is BleInfo) return
        val binding = holder.binding
        if (binding !is ItemBleBinding) return
        val address = item.address
        binding.tvConnectStatus.setOnClickListener {
            if (item.isConnected.get()) {
                mBleManager.sendCommand(BleDisconnectCommand(
                        mActivity,
                        address,
                        {
                            item.isConnected.set(false)
                        },
                        {
                            item.isConnected.set(true)
                        })
                )
            } else {
                mBleManager.sendCommand(BleConnectCommand(
                        mActivity,
                        address,
                        5000L,
                        {
                            item.isConnected.set(true)
                        },
                        {
                            item.isConnected.set(false)
                        })
                )
            }
        }
        binding.btnReadChar.setOnClickListener {
            mBleManager.sendCommand(object : BleReadCharacteristicCommand(
                    mActivity,
                    address,
                    "0000fff2-0000-1000-8000-00805f9b34fb",
                    5000,
                    5,
                    300,
                    {
                        mActivity.shortToastCenter("读特征成功 ${it?.contentToString()}")
                    },
                    {
                        mActivity.shortToastCenter("读特征失败！${it.getCustomNetworkMessage()}")
                    }
            ) {
                override fun isWholeFrame(data: ByteBuffer): Boolean {
                    return true
                }
            })
        }
        binding.btnWriteChar.setOnClickListener {
            mBleManager.sendCommand(BleWriteCharacteristicCommand(
                    mActivity,
                    1,
                    byteArrayOf(0x1),
                    address,
                    "0000fff2-0000-1000-8000-00805f9b34fb",
                    "模拟的BleCommand",
                    5000,
                    20,
                    300,
                    {
                        mActivity.shortToastCenter("写特征成功")
                    },
                    {
                        mActivity.shortToastCenter("写特征失败！${it.getCustomNetworkMessage()}")
                    }
            ))
        }
        binding.btnSetMtu.setOnClickListener {
            mBleManager.sendCommand(BleSetMtuCommand(
                    mActivity,
                    address,
                    50,
                    {
                        mActivity.shortToastCenter("设置MTU成功 $it")
                    },
                    {
                        mActivity.shortToastCenter("设置MTU失败！${it.getCustomNetworkMessage()}")
                    }
            ))
        }
    }
}