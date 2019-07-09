package com.like.common.sample.ble

import android.databinding.ObservableBoolean
import com.like.common.sample.BR
import com.like.common.sample.R
import com.like.livedatarecyclerview.model.IItem

class BleInfo(val name: String, val address: String) : IItem {
    override var variableId: Int = BR.bleInfo
    override var layoutId: Int = R.layout.item_ble
    var isConnected = ObservableBoolean(false) // 是否连接
}