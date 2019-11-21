package com.like.common.util

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * SharedPreferences存储工具类。
 */
class SPUtils private constructor() {
    private lateinit var prefs: SharedPreferences

    companion object {
        private const val NOT_INIT_EXCEPTION = "you must init SPUtils by init() first"
        private const val SHARED_PREFERENCES_FILE_SUFFIX = ".sharedPreferences"

        @JvmStatic
        fun getInstance(): SPUtils {
            return Holder.instance
        }
    }

    private object Holder {
        val instance = SPUtils()
    }

    /**
     * @param sharedPreferencesFileName sharedPreferences对于的文件名字。默认为包名。
     */
    @JvmOverloads
    fun init(context: Context, sharedPreferencesFileName: String = context.packageName) {
        if (!::prefs.isInitialized) {
            prefs = context.applicationContext.getSharedPreferences("$sharedPreferencesFileName$SHARED_PREFERENCES_FILE_SUFFIX", Context.MODE_PRIVATE)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalArgumentException::class)
    fun <T> get(key: String, default: T): T {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        default ?: throw IllegalArgumentException("default can not be null")
        return with(prefs) {
            when (default) {
                is String -> getString(key, default) as T
                is Boolean -> getBoolean(key, default) as T
                is Int -> getInt(key, default) as T
                is Long -> getLong(key, default) as T
                is Float -> getFloat(key, default) as T
                else -> default
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    fun <T> put(key: String, value: T) {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        value ?: throw IllegalArgumentException("value can not be null")
        with(prefs.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                else -> null
            }?.apply()
        }
    }

    /**
     * 移除某个key对应的那一条数据
     * @param key
     */
    @Throws(IllegalArgumentException::class)
    fun remove(key: String) {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        prefs.edit().remove(key).apply()
    }

    /**
     * 清除所有数据
     */
    @Throws(IllegalArgumentException::class)
    fun clear() {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        prefs.edit().clear().apply()
    }

    /**
     * 查询某个key是否已经存在
     * @param key
     * @return
     */
    @Throws(IllegalArgumentException::class)
    fun contains(key: String): Boolean {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        return prefs.contains(key)
    }

    /**
     * 返回所有的键值对数据
     * @return
     */
    @Throws(IllegalArgumentException::class)
    fun getAll(): Map<String, Any?> {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        return prefs.all
    }

    /**
     * SharedPreferences属性委托
     * 支持基本数据类型：String、Boolean、Int、Long、Float
     *
     * 示例：var xxx by SharedPreferencesDelegate()
     *
     * @property context
     * @property sharedPreferencesFileName  sharedPreferences对于的文件名字
     * @property key                        存储的key
     * @property default                    获取失败时，返回的默认值
     */
    class SharedPreferencesDelegate<T>(
            private val context: Context,
            private val sharedPreferencesFileName: String,
            private val key: String,
            private val default: T
    ) : ReadWriteProperty<Any?, T> {
        init {
            getInstance().init(context, sharedPreferencesFileName)
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return getInstance().get(key, default)!!
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            getInstance().put(key, value)
        }

    }
}