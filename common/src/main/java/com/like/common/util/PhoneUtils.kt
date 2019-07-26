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
import android.support.annotation.RequiresPermission
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.net.NetworkInterface
import java.util.*
import kotlin.jvm.functions.FunctionN

/**
 * 获取mac需要Manifest.permission.ACCESS_WIFI_STATE
 * 获取imei需要Manifest.permission.READ_PHONE_STATE
 * 获取phoneNumber需要Manifest.permission.READ_SMS、Manifest.permission.READ_PHONE_NUMBERS、Manifest.permission.READ_PHONE_STATE
 */
@SuppressLint("MissingPermission")
class PhoneUtils private constructor(private val mContext: Context) {
    companion object : SingletonHolder<PhoneUtils>(object : FunctionN<PhoneUtils> {
        override val arity: Int = 1 // number of arguments that must be passed to constructor

        override fun invoke(vararg args: Any?): PhoneUtils {
            return PhoneUtils(args[0] as Context)
        }
    }) {
        private const val DEFAULT_FILE_NAME = ".phoneutils_device_id"
        private const val KEY_UUID = "phoneutils_key_uuid"
        private val FILE_DOWNLOADS = StorageUtils.ExternalStorageHelper.getPublicDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + DEFAULT_FILE_NAME
        private val FILE_DCIM = StorageUtils.ExternalStorageHelper.getPublicDir(Environment.DIRECTORY_DCIM).toString() + File.separator + DEFAULT_FILE_NAME
    }

    private val mPhoneStatus: PhoneStatus

    init {
        mPhoneStatus = PhoneStatus()
        mPhoneStatus.releaseVersion = Build.VERSION.RELEASE
        mPhoneStatus.brand = Build.BRAND
        mPhoneStatus.model = Build.MODEL
        mPhoneStatus.sdkVersion = Build.VERSION.SDK_INT
        mPhoneStatus.androidId = Settings.System.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)

        val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        if (wm != null) {
            val metric = DisplayMetrics()
            wm.defaultDisplay.getMetrics(metric)
            mPhoneStatus.screenWidth = metric.widthPixels
            mPhoneStatus.screenWidthDpi = DimensionUtils.px2dp(mContext, metric.widthPixels.toFloat())
            mPhoneStatus.screenHeight = metric.heightPixels
            mPhoneStatus.screenHeightDpi = DimensionUtils.px2dp(mContext, metric.heightPixels.toFloat())
            mPhoneStatus.density = metric.density
            mPhoneStatus.densityDpi = metric.densityDpi
        }

        if (PermissionUtils.hasPermissions(mContext, Manifest.permission.ACCESS_WIFI_STATE)) {
            mPhoneStatus.mac = getMac()
        }

        val tm = mContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        if (tm != null && PermissionUtils.hasPermissions(mContext, Manifest.permission.READ_PHONE_STATE)) {
            mPhoneStatus.imei = tm.deviceId
        }
        if (tm != null &&
                PermissionUtils.hasPermissions(mContext, Manifest.permission.READ_SMS) &&
                PermissionUtils.hasPermissions(mContext, Manifest.permission.READ_PHONE_NUMBERS) &&
                PermissionUtils.hasPermissions(mContext, Manifest.permission.READ_PHONE_STATE)) {
            mPhoneStatus.phoneNumber = tm.line1Number
        }
        Log.i("PhoneUtils", mPhoneStatus.toString())
    }

    /**
     * 手机相关的状态信息
     */
    class PhoneStatus {
        /**
         * deviceId
         * getDeviceId()需要android.permission.READ_PHONE_STATE权限，它在6.0+系统中是需要动态申请的。如果需求要求App启动时上报设备标识符的话，那么第一会影响初始化速度，第二还有可能被用户拒绝授权。
         * android系统碎片化严重，有的手机可能拿不到DeviceId，会返回null或者000000。
         * 这个方法是只对有电话功能的设备有效的，在pad上不起作用。 可以看下方法注释
         */
        var imei: String? = null
        /**
         * MAC地址
         */
        var mac: String? = null
        /**
         * AndroidId
         * 在设备首次启动时，系统会随机生成一个64位的数字，并把这个数字以16进制字符串的形式保存下来。不需要权限，平板设备通用。获取成功率也较高，缺点是设备恢复出厂设置会重置。另外就是某些厂商的低版本系统会有bug，返回的都是相同的AndroidId。
         */
        var androidId: String? = null
        /**
         * android系统版本
         */
        var releaseVersion: String? = null
        /**
         * 本机电话号码
         */
        var phoneNumber: String? = null
        /**
         * 手机品牌
         */
        var brand: String? = null
        /**
         * 手机型号
         */
        var model: String? = null
        /**
         * SDK版本号
         */
        var sdkVersion: Int = 0
        /**
         * 屏幕宽度（像素）
         */
        var screenWidth: Int = 0
        /**
         * 屏幕宽度（DP）
         */
        var screenWidthDpi: Int = 0
        /**
         * 屏幕高度（像素）
         */
        var screenHeight: Int = 0
        /**
         * 屏幕高度（DP）
         */
        var screenHeightDpi: Int = 0
        /**
         * 屏幕密度：1.0     1.5      2         3        3.5
         * dpi：     160     240     320       480       560
         * 分辨率：320x533 480x800 720x1280 1080x1920 1440x2560
         * Res：     mdpi    hdpi    xhdpi   xxhdpi    xxxhdpi
         */
        var density: Float = 0.toFloat()
        /**
         * 屏幕密度DPI
         */
        var densityDpi: Int = 0

        override fun toString(): String {
            return "PhoneStatus{" +
                    "imei='" + imei + '\''.toString() +
                    ", mac='" + mac + '\''.toString() +
                    ", androidId='" + androidId + '\''.toString() +
                    ", releaseVersion='" + releaseVersion + '\''.toString() +
                    ", phoneNumber='" + phoneNumber + '\''.toString() +
                    ", brand='" + brand + '\''.toString() +
                    ", model='" + model + '\''.toString() +
                    ", sdkVersion=" + sdkVersion +
                    ", screenWidth=" + screenWidth +
                    ", screenWidthDpi=" + screenWidthDpi +
                    ", screenHeight=" + screenHeight +
                    ", screenHeightDpi=" + screenHeightDpi +
                    ", density=" + density +
                    ", densityDpi=" + densityDpi +
                    '}'.toString()
        }
    }

    /**
     * 检测屏幕是否开启
     *
     * @return 是否屏幕开启
     */
    fun isScreenOn(): Boolean {
        val pm = mContext.applicationContext.getSystemService(Context.POWER_SERVICE) as? PowerManager

        return if (pm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                pm.isInteractive
            } else {
                pm.isScreenOn
            }
        } else false
    }

    /**
     * 获取UUID。
     * 要使用缓存的话，就必须先初始化 SPUtils 工具类
     */
    fun getUuid(): String {
        var uuid = try {
            SPUtils.getInstance().get(KEY_UUID, "")
        } catch (e: Exception) {
            ""
        }
        Log.d("PhoneUtils", "从sp中获取uuid：$uuid")
        if (uuid.isEmpty()) {
            uuid = readUuidFromFile(FILE_DCIM)
            Log.d("PhoneUtils", "从FILE_DCIM中获取uuid：$uuid")
            if (uuid.isEmpty()) {
                uuid = readUuidFromFile(FILE_DOWNLOADS)
                Log.d("PhoneUtils", "从FILE_DOWNLOADS中获取uuid：$uuid")
            }
        }
        if (uuid.isEmpty()) {
            uuid = createUuid()
            Log.d("PhoneUtils", "新创建了一个uuid：$uuid")
        }
        return uuid
    }

    /**
     * 创建设备唯一标识
     *
     * @return
     */
    private fun createUuid(): String {
        val uuid: String?
        if (mPhoneStatus.imei != null && !mPhoneStatus.imei!!.isEmpty()) {
            uuid = mPhoneStatus.imei
            Log.d("PhoneUtils", "imei为uuid")
        } else if (mPhoneStatus.mac != null && !mPhoneStatus.mac!!.isEmpty()) {
            uuid = mPhoneStatus.mac
            Log.d("PhoneUtils", "mac为uuid")
        } else if (mPhoneStatus.androidId != null && !mPhoneStatus.androidId!!.isEmpty()) {
            uuid = mPhoneStatus.androidId
            Log.d("PhoneUtils", "androidId为uuid")
        } else {
            uuid = UUID.randomUUID().toString()
            Log.d("PhoneUtils", "randomUUID为uuid")
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
        return uuid ?: ""
    }

    private fun readUuidFromFile(fileName: String): String {
        var reader: BufferedReader? = null
        return try {
            val file = File(fileName)
            reader = BufferedReader(FileReader(file))
            reader.readLine()
        } catch (e: Exception) {
            ""
        } finally {
            try {
                reader?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveUuidToFile(fileName: String, uuid: String) {
        var writer: FileWriter? = null
        try {
            writer = FileWriter(File(fileName))
            writer.write(uuid)
            writer.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                writer?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    private fun getMac(): String? {
        var mac: String? = ""
        if (Build.VERSION.SDK_INT < 23) {
            mac = getMacBySystemInterface()
        } else {
            mac = getMacByJavaAPI()
            if (TextUtils.isEmpty(mac)) {
                mac = getMacBySystemInterface()
            }
        }
        return mac

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

    private fun getMacBySystemInterface(): String =
            try {
                val wifi = mContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                wifi?.connectionInfo?.macAddress ?: ""
            } catch (e: Throwable) {
                ""
            }
}