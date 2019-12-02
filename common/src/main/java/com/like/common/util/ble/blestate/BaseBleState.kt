package com.like.common.util.ble.blestate

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import com.like.common.util.ble.model.BleCommand

/**
 * 蓝牙状态
 */
abstract class BaseBleState {
    /**
     * 初始化蓝牙
     */
    open fun init() {}

    /**
     * 开始扫描设备
     */
    open fun startScan() {}

    /**
     * 停止扫描设备
     */
    open fun stopScan() {}

    /**
     *  连接指定蓝牙设备
     */
    open fun connect(address: String) {}

    /**
     * 写数据
     */
    open fun write(command: BleCommand) {}

    /**
     * 断开指定蓝牙设备
     */
    open fun disconnect(address: String) {}

    /**
     * 断开所有蓝牙设备
     */
    open fun disconnectAll() {}

    /**
     * 释放资源
     */
    open fun close() {}

    /**
     * 获取 BluetoothAdapter
     */
    open fun getBluetoothAdapter(): BluetoothAdapter? {
        return null
    }

    /**
     * 获取 BluetoothGatt
     */
    open fun getBluetoothGatt(address: String): BluetoothGatt? {
        return null
    }
}