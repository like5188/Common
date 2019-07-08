package com.like.common.util.ble.blestate

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import com.like.common.util.ble.model.BleCommand

/**
 * 蓝牙状态
 */
interface IBleState {
    /**
     * 初始化蓝牙
     */
    fun init()

    /**
     * 开始扫描设备
     */
    fun startScan()

    /**
     * 停止扫描设备
     */
    fun stopScan()

    /**
     *  连接指定蓝牙设备
     */
    fun connect(address: String)

    /**
     * 写数据
     */
    fun write(command: BleCommand)

    /**
     * 断开指定蓝牙设备
     */
    fun disconnect(address: String)

    /**
     * 断开所有蓝牙设备
     */
    fun disconnectAll()

    /**
     * 释放资源
     */
    fun close()

    /**
     * 获取 BluetoothAdapter
     */
    fun getBluetoothAdapter(): BluetoothAdapter?

    /**
     * 获取 BluetoothGatt
     */
    fun getBluetoothGatt(address: String): BluetoothGatt?
}