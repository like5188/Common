package com.like.common.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


/*
目前坐标系有三种，分别是WGS84、GCJ02、BD09，国内基本用的是后两种。

WGS84：国际坐标系，为一种大地坐标系，也是目前广泛使用的GPS全球卫星定位系统使用的坐标系。
GCJ02：火星坐标系，是由中国国家测绘局制订的地理信息系统的坐标系统。由WGS84坐标系经加密后的坐标系。高德、腾讯都是用的这种。
BD09：为百度坐标系，在GCJ02坐标系基础上再次加密。其中BD09ll表示百度经纬度坐标，BD09mc表示百度墨卡托米制坐标。百度地图sdk默认输出的是BD09ll，定位sdk默认输出的是GCJ02。
自Android v4.3起，一次声明GCJ02坐标类型，全应用自动执行坐标转换，即输入GCJ02坐标，返回GCJ02坐标。但此方法仅适用于国内（包括港澳台地区）且输入坐标为GCJ02坐标的情况。
原文链接：https://blog.csdn.net/Ever69/article/details/82427085
 */
/**
 * 导航工具。支持百度地图、高德地图、腾讯地图导航
 * 注意：坐标系类型为"gcj02"
 */
object NavigationUtils {
    private val maps = mapOf(
        "百度地图" to "com.baidu.BaiduMap",
        "高德地图" to "com.autonavi.minimap",
        "腾讯地图" to "com.tencent.map",
    )

    fun navigation(context: Context, endlatitude: Double, endlongitude: Double) {
        val installedNames = getInstalledMapNames(context)
        if (installedNames.isEmpty()) {
            return
        }
        val names = maps.keys.toTypedArray()
        if (installedNames.size == 1) {
            when (installedNames[0]) {
                names[0] -> navigationByBaiDuMap(context, endlatitude, endlongitude)
                names[1] -> navigationByAMap(context, endlatitude, endlongitude)
                names[2] -> navigationByQQMap(context, endlatitude, endlongitude)
            }
            return
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            .setTitle("请选择地图")
            .setItems(installedNames.toTypedArray()) { _, i ->
                when (installedNames[i]) {
                    names[0] -> navigationByBaiDuMap(context, endlatitude, endlongitude)
                    names[1] -> navigationByAMap(context, endlatitude, endlongitude)
                    names[2] -> navigationByQQMap(context, endlatitude, endlongitude)
                }
            }
        builder.create().show()
    }

    fun getInstalledMapNames(context: Context): List<String> {
        val names = mutableListOf<String>()
        maps.forEach {
            if (isInstalled(context, it.value)) {
                names.add(it.key)
            }
        }
        if (names.isEmpty()) {
            Toast.makeText(context, "您的手机尚未安装百度地图、高德地图、腾讯地图！", Toast.LENGTH_SHORT).show()
        }
        return names
    }

    fun navigationByBaiDuMap(context: Context, endlatitude: Double, endlongitude: Double) {
        if (isInstalled(context, maps.values.toTypedArray()[0])) {
            context.startActivity(Intent().apply {
                this.data =
                    Uri.parse("baidumap://map/direction?mode=driving&coord_type=gcj02&destination=$endlatitude,$endlongitude&src=${context.packageName}")
            })
        } else {
            Toast.makeText(context, "您的手机尚未安装百度地图！", Toast.LENGTH_SHORT).show()
        }
    }

    fun navigationByAMap(context: Context, endlatitude: Double, endlongitude: Double) {
        if (isInstalled(context, maps.values.toTypedArray()[1])) {
            context.startActivity(Intent().apply {
                this.data =
                    Uri.parse("amapuri://route/plan/?dlat=$endlatitude&dlon=$endlongitude&dev=0&t=0&sourceApplication=${context.packageName}")
            })
        } else {
            Toast.makeText(context, "您的手机尚未安装高德地图！", Toast.LENGTH_SHORT).show()
        }
    }

    fun navigationByQQMap(context: Context, endlatitude: Double, endlongitude: Double) {
        if (isInstalled(context, maps.values.toTypedArray()[2])) {
            context.startActivity(Intent().apply {
                this.data = Uri.parse("qqmap://map/routeplan?type=drive&tocoord=$endlatitude,$endlongitude")
            })
        } else {
            Toast.makeText(context, "您的手机尚未安装腾讯地图！", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 验证各种导航地图是否安装
     */
    private fun isInstalled(context: Context, packageName: String): Boolean {
        //获取所有已安装程序的包信息
        val packageInfos = context.packageManager.getInstalledPackages(0)
        return packageInfos.any { it.packageName == packageName }
    }

}