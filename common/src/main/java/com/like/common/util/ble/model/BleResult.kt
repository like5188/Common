package com.like.common.util.ble.model

import com.like.common.util.Logger

/**
 * 蓝牙相关的操作的返回结果。
 */
data class BleResult(val status: BleStatus, val data: Any? = null) {
    init {
        Logger.i(this.toString())
    }
}