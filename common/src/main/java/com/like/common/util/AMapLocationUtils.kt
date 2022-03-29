package com.like.common.util

import android.Manifest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.like.activityresultlauncher.RequestMultiplePermissionsLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/*
使用时需要配置：
<meta-data
    android:name="com.amap.api.v2.apikey"
    android:value="你自己的apikey" />
implementation 'com.github.like5188:activityresultlauncher:1.0.0'
implementation 'com.amap.api:location:latest.integration'
 */
/**
 * 高德地图定位工具类
 */
class AMapLocationUtils(
    lifecycleOwner: LifecycleOwner,
    private val requestMultiplePermissionsLauncher: RequestMultiplePermissionsLauncher
) {
    //请在主线程中声明AMapLocationClient类对象，需要传Context类型的参数。推荐用getApplicationContext()方法获取全进程有效的context。
    private val mLocationClient by lazy {
        val context = requestMultiplePermissionsLauncher.context.applicationContext
        // 高德地图隐私合规校验
        /* 设置包含隐私政策，并展示用户授权弹窗 <b>必须在AmapLocationClient实例化之前调用</b>
         *
         * @param context
         * @param isContains: 是隐私权政策是否包含高德开平隐私权政策  true是包含
         * @param isShow: 隐私权政策是否弹窗展示告知用户 true是展示
         * @since 5.6.0
         */
        AMapLocationClient.updatePrivacyShow(context, true, true)
        /*
         * 设置是否同意用户授权政策 <b>必须在AmapLocationClient实例化之前调用</b>
         * @param context
         * @param isAgree:隐私权政策是否取得用户同意  true是用户同意
         *
         * @since 5.6.0
         */
        AMapLocationClient.updatePrivacyAgree(context, true)
        try {
            AMapLocationClient(context).apply {
                val option = AMapLocationClientOption().apply {
                    //设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
                    locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn
                }
                setLocationOption(option)
                //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
                stopLocation()
                startLocation()
            }
        } catch (e: Exception) {
            null
        }
    }

    init {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        //销毁定位客户端，同时销毁本地定位服务。
                        mLocationClient?.onDestroy()
                    }
                }
            })
        }
    }

    /**
     * 定位一次
     * @return [AMapLocation]
     */
    suspend fun location(): AMapLocation? {
        val locationClient = mLocationClient ?: return null
        val requestPermissions = requestMultiplePermissionsLauncher.launch(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        ).all { it.value }
        if (!requestPermissions) {
            return null
        }
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { locationClient.stopLocation() }
            locationClient.setLocationListener {
                if (it?.errorCode == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    Logger.d(it.toString())
                    continuation.resume(it)
                    return@setLocationListener
                }
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Logger.e("location Error, ErrCode:${it?.errorCode}, errInfo:${it?.errorInfo}")
                continuation.resume(null)
            }
            //启动定位
            locationClient.startLocation()
        }
    }

}