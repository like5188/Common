package com.like.common.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

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
 */
object NavigationUtils {
    // 百度地图包名
    private const val BAIDU_MAP_APP = "com.baidu.BaiduMap"

    // 高德地图包名
    private const val GAODE_MAP_APP = "com.autonavi.minimap"

    // 腾讯地图包名
    private const val QQ_MAP_APP = "com.tencent.map"

    /**
     * 从当前位置到指定定位点的路径规划。
     * 注意：坐标系类型为"gcj02"
     */
    fun navigation(context: Context, endlatitude: Double, endlongitude: Double) {
        when {
            isAvailable(context, BAIDU_MAP_APP) ->
                context.startActivity(Intent().apply {
                    this.data =
                        Uri.parse("baidumap://map/direction?mode=driving&coord_type=gcj02&destination=$endlatitude,$endlongitude&src=${context.packageName}")
                })
            isAvailable(context, GAODE_MAP_APP) ->
                context.startActivity(Intent().apply {
                    this.data =
                        Uri.parse("amapuri://route/plan/?dlat=$endlatitude&dlon=$endlongitude&dev=0&t=0&sourceApplication=${context.packageName}")
                })
            isAvailable(context, QQ_MAP_APP) ->
                context.startActivity(Intent().apply {
                    this.data = Uri.parse("qqmap://map/routeplan?type=drive&tocoord=$endlatitude,$endlongitude")
                })
            else -> Toast.makeText(context, "您的手机尚未安装百度地图、高德地图、腾讯地图等软件，不能进行导航！", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 验证各种导航地图是否安装
     */
    private fun isAvailable(context: Context, packageName: String): Boolean {
        //获取所有已安装程序的包信息
        val packageInfos = context.packageManager.getInstalledPackages(0)
        return packageInfos.any { it.packageName == packageName }
    }

}