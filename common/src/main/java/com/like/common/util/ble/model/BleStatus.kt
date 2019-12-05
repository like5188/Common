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

    ON_CHARACTERISTIC_READ("读取到了特征值"),
    ON_CHARACTERISTIC_CHANGED("特征值改变了"),
    ON_CHARACTERISTIC_WRITE("写入了特征值"),
    ON_DESCRIPTOR_READ("读取到了描述值"),
    ON_DESCRIPTOR_WRITE("写入了描述值"),
    ON_READ_REMOTE_RSSI("读取到了蓝牙信号值"),
    ON_MTU_CHANGED("MTU改变了");

    override fun toString(): String {
        return "BleStatus(des='$des')"
    }

}