package com.like.common.util

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.net.NetworkInterface
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object PhoneUtils {
    private const val DEFAULT_FILE_NAME = ".phoneutils_device_id"
    private const val KEY_UUID = "phoneutils_key_uuid"
    private val FILE_DOWNLOADS = StorageUtils.ExternalStorageHelper.getPublicDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + DEFAULT_FILE_NAME
    private val FILE_DCIM = StorageUtils.ExternalStorageHelper.getPublicDir(Environment.DIRECTORY_DCIM).toString() + File.separator + DEFAULT_FILE_NAME

    fun getWindowManager(context: Context?) = context?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager

    fun getTelephonyManager(context: Context?) = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

    fun getPowerManager(context: Context?) = context?.getSystemService(Context.POWER_SERVICE) as? PowerManager

    fun getWifiManager(context: Context?) = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    /**
     * android系统版本
     */
    fun getReleaseVersion() = Build.VERSION.RELEASE

    /**
     * 手机品牌
     */
    fun getBrand() = Build.BRAND

    /**
     * 手机型号
     */
    fun getModel() = Build.MODEL

    /**
     * SDK版本号
     */
    fun getSdkVersion() = Build.VERSION.SDK_INT

    /**
     * AndroidId
     * 在设备首次启动时，系统会随机生成一个64位的数字，并把这个数字以16进制字符串的形式保存下来。不需要权限，平板设备通用。获取成功率也较高，缺点是设备恢复出厂设置会重置。另外就是某些厂商的低版本系统会有bug，返回的都是相同的AndroidId。
     */
    fun getAndroidId(context: Context) = Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    /**
     * 屏幕宽度（像素）
     */
    fun getScreenWidth(context: Context): Int {
        getWindowManager(context)?.let {
            val metric = DisplayMetrics()
            it.defaultDisplay.getMetrics(metric)
            return metric.widthPixels
        }
        return 0
    }

    /**
     * 屏幕宽度（DP）
     */
    fun getScreenWidthDpi(context: Context): Int {
        getWindowManager(context)?.let {
            val metric = DisplayMetrics()
            it.defaultDisplay.getMetrics(metric)
            return DimensionUtils.px2dp(context, metric.widthPixels.toFloat())
        }
        return 0
    }

    /**
     * 屏幕高度（像素）
     */
    fun getScreenHeight(context: Context): Int {
        getWindowManager(context)?.let {
            val metric = DisplayMetrics()
            it.defaultDisplay.getMetrics(metric)
            return metric.heightPixels
        }
        return 0
    }

    /**
     * 屏幕高度（DP）
     */
    fun getScreenHeightDpi(context: Context): Int {
        getWindowManager(context)?.let {
            val metric = DisplayMetrics()
            it.defaultDisplay.getMetrics(metric)
            return DimensionUtils.px2dp(context, metric.heightPixels.toFloat())
        }
        return 0
    }

    /**
     * 屏幕密度：1.0     1.5      2         3        3.5
     * dpi：     160     240     320       480       560
     * 分辨率：320x533 480x800 720x1280 1080x1920 1440x2560
     * Res：     mdpi    hdpi    xhdpi   xxhdpi    xxxhdpi
     */
    fun getDensity(context: Context): Float {
        getWindowManager(context)?.let {
            val metric = DisplayMetrics()
            it.defaultDisplay.getMetrics(metric)
            return metric.density
        }
        return 0f
    }

    /**
     * 屏幕密度DPI
     */
    fun getDensityDpi(context: Context): Int {
        getWindowManager(context)?.let {
            val metric = DisplayMetrics()
            it.defaultDisplay.getMetrics(metric)
            return metric.densityDpi
        }
        return 0
    }

    /**
     * MAC地址
     */
    fun getMac(context: Context) =
            if (PermissionUtils.hasPermissions(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                var mac: String? = ""
                if (Build.VERSION.SDK_INT < 23) {
                    mac = getMacBySystemInterface(context)
                } else {
                    mac = getMacByJavaAPI()
                    if (TextUtils.isEmpty(mac)) {
                        mac = getMacBySystemInterface(context)
                    }
                }
                mac
            } else {
                null
            }

    /**
     * deviceId
     * getDeviceId()需要android.permission.READ_PHONE_STATE权限，它在6.0+系统中是需要动态申请的。如果需求要求App启动时上报设备标识符的话，那么第一会影响初始化速度，第二还有可能被用户拒绝授权。
     * android系统碎片化严重，有的手机可能拿不到DeviceId，会返回null或者000000。
     * 这个方法是只对有电话功能的设备有效的，在pad上不起作用。 可以看下方法注释
     */
    @SuppressLint("MissingPermission")
    fun getImei(context: Context): String? {
        val tm = getTelephonyManager(context)
        return if (tm != null && PermissionUtils.hasPermissions(context, Manifest.permission.READ_PHONE_STATE)) {
            tm.deviceId
        } else {
            null
        }
    }

    /**
     * 本机电话号码
     */
    @SuppressLint("MissingPermission")
    fun getPhoneNumber(context: Context): String? {
        val tm = getTelephonyManager(context)
        return if (tm != null &&
                PermissionUtils.hasPermissions(context, Manifest.permission.READ_SMS) &&
                PermissionUtils.hasPermissions(context, Manifest.permission.READ_PHONE_NUMBERS) &&
                PermissionUtils.hasPermissions(context, Manifest.permission.READ_PHONE_STATE)) {
            tm.line1Number
        } else {
            null
        }
    }

    /**
     * 检测屏幕是否开启
     *
     * @return 是否屏幕开启
     */
    fun isScreenOn(context: Context): Boolean {
        val pm = getPowerManager(context)

        return if (pm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                pm.isInteractive
            } else {
                pm.isScreenOn
            }
        } else false
    }

    suspend fun getUuid(fragmentActivity: FragmentActivity): String {
        val context = fragmentActivity.applicationContext
        val permissionUtils = PermissionUtils(fragmentActivity)
        return getUuid(context, permissionUtils)
    }

    suspend fun getUuid(fragment: Fragment): String {
        val context = fragment.context?.applicationContext
        val permissionUtils = PermissionUtils(fragment)
        return if (context != null) {
            getUuid(context, permissionUtils)
        } else {
            ""
        }
    }

    /**
     * 获取UUID。
     * 要使用缓存的话，就必须先初始化 SPUtils 工具类
     */
    private suspend fun getUuid(context: Context, permissionUtils: PermissionUtils) = suspendCoroutine<String> { continuation ->
        var uuid = try {
            SPUtils.getInstance().init(context)
            SPUtils.getInstance().get(KEY_UUID, "")
        } catch (e: Exception) {
            ""
        }
        Log.d("PhoneUtils", "从sp中获取uuid：$uuid")
        permissionUtils.checkStoragePermissions({
            if (!it) {
                continuation.resumeWithException(UnsupportedOperationException("need android.Manifest.permission.WRITE_EXTERNAL_STORAGE permission"))
            }
            if (uuid.isEmpty()) {
                uuid = readUuidFromFile(FILE_DCIM)
                Log.d("PhoneUtils", "从FILE_DCIM中获取uuid：$uuid")
                if (uuid.isEmpty()) {
                    uuid = readUuidFromFile(FILE_DOWNLOADS)
                    Log.d("PhoneUtils", "从FILE_DOWNLOADS中获取uuid：$uuid")
                }
            }
            if (uuid.isEmpty()) {
                uuid = createUuid(context)
                Log.d("PhoneUtils", "新创建了一个uuid：$uuid")
            }
            continuation.resume(uuid)
        })
    }

    /**
     * 创建设备唯一标识
     *
     * @return
     */
    private fun createUuid(context: Context): String {
        val imei = getImei(context)
        val mac = getMac(context)
        val androidId = getAndroidId(context)
        val uuid = if (!imei.isNullOrEmpty()) {
            Log.d("PhoneUtils", "imei为uuid")
            imei
        } else if (!mac.isNullOrEmpty()) {
            Log.d("PhoneUtils", "mac为uuid")
            mac
        } else if (!androidId.isNullOrEmpty()) {
            Log.d("PhoneUtils", "androidId为uuid")
            androidId
        } else {
            Log.d("PhoneUtils", "randomUUID为uuid")
            UUID.randomUUID().toString()
        }
        if (!uuid.isNullOrEmpty()) {
            saveUuidToFile(FILE_DCIM, uuid)
            saveUuidToFile(FILE_DOWNLOADS, uuid)
            try {
                SPUtils.getInstance().put(KEY_UUID, uuid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return uuid
    }

    private fun readUuidFromFile(fileName: String): String {
        return try {
            val file = File(fileName)
            BufferedReader(FileReader(file)).use {
                it.readLine()
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun saveUuidToFile(fileName: String, uuid: String) {
        try {
            FileWriter(File(fileName)).use {
                it.write(uuid)
                it.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @TargetApi(9)
    private fun getMacByJavaAPI(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val netInterface = interfaces.nextElement()
                if ("wlan0" == netInterface.name || "eth0" == netInterface.name) {
                    val addr = netInterface.hardwareAddress
                    if (addr == null || addr.isEmpty()) {
                        return null
                    }
                    val buf = StringBuilder()
                    for (b in addr) {
                        buf.append(String.format("%02X:", b))
                    }
                    if (buf.isNotEmpty()) {
                        buf.deleteCharAt(buf.length - 1)
                    }
                    return buf.toString().toLowerCase(Locale.getDefault())
                }
            }
        } catch (e: Throwable) {
        }

        return null
    }

    private fun getMacBySystemInterface(context: Context): String =
            try {
                val wifi = getWifiManager(context)
                wifi?.connectionInfo?.macAddress ?: ""
            } catch (e: Throwable) {
                ""
            }
}