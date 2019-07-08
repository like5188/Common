package com.like.common.util.ble.model

/**
 * 蓝牙的状态
 */
enum class BleStatus(val des: String) {
    ON("蓝牙已打开"),
    OFF("蓝牙已关闭"),

    INIT("开始初始化蓝牙"),
    INIT_SUCCESS("蓝牙初始化成功"),
    INIT_FAILURE("蓝牙初始化失败"),

    START_SCAN_DEVICE("开始扫描蓝牙设备"),
    STOP_SCAN_DEVICE("停止扫描蓝牙设备"),

    CONNECT("开始连接蓝牙设备"),
    CONNECTED("连接蓝牙设备成功"),
    DISCONNECTED("连接蓝牙设备失败"),

    READ_CHARACTERISTIC("开始读特征值"),
    CHARACTERISTIC_CHANGED("获取到特征值"),
    WRITE_CHARACTERISTIC("开始写特征值"),
    READ_DESCRIPTOR("开始读描述值"),
    WRITE_DESCRIPTOR("开始写描述值"),
    READ_REMOTE_RSSI("开始读蓝牙信号值");

    override fun toString(): String {
        return "BleStatus(des='$des')"
    }

}