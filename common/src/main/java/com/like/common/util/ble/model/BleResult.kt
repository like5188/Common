package com.like.common.util.ble.model

import com.like.common.util.Logger

/**
 * 蓝牙相关的操作的返回结果。
 *
 * @param status    操作标志
 * @param data      数据
 * @param errorMsg  对失败状态的描述
 */
data class BleResult(val status: BleStatus, val data: Any? = null, val errorMsg: String = "") {
    init {
        Logger.i(this.toString())
    }
}