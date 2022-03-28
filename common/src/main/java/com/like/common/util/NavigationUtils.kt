package com.like.common.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

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
     * 从当前位置到指定定位点的路径规划
     */
    fun navigation(context: Context, endlatitude: Double, endlongitude: Double) {
        when {
            isAvailable(context, BAIDU_MAP_APP) ->
                context.startActivity(Intent().apply {
                    this.data = Uri.parse("baidumap://map/direction?mode=driving&destination=$endlatitude,$endlongitude&src=${context.packageName}")
                })
//            isAvailable(context, GAODE_MAP_APP) ->
//                context.startActivity(Intent().apply {
//                    this.data =
//                        Uri.parse("amapuri://route/plan/?dlat=$endlatitude&dlon=$endlongitude&dev=0&t=0&sourceApplication=${context.packageName}")
//                })
//            isAvailable(context, QQ_MAP_APP) ->
//                context.startActivity(Intent().apply {
//                    this.data = Uri.parse("qqmap://map/routeplan?type=drive&tocoord=$endlatitude,$endlongitude")
//                })
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