package com.like.common.util

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * SharedPreferences存储工具类。
 *
 * 通过 getXXX() 方法获取数据，可能会导致主线程阻塞
 * 不能保证类型安全
 * 加载的数据会一直留在内存中，浪费内存
 * apply() 方法虽然是异步的，可能会发生 ANR，在 8.0 之前和 8.0 之后实现各不相同
 * apply() 方法无法获取到操作成功或者失败的结果
 */
class SPUtils private constructor() {
    private lateinit var prefs: SharedPreferences

    companion object {
        private const val NOT_INIT_EXCEPTION = "you must init SPUtils by init() first"
        private const val KEY_IS_EMPTY_EXCEPTION = "key is empty"
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

    /**
     * 如果[key]存在，则返回对应类型的数据，如果转换数据类型失败，则返回[default]。
     * 如果[key]不存在，则返回[default]；
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalArgumentException::class)
    fun <T> get(key: String, default: T): T {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        getAll()?.forEach {
            if (it.key == key) {
                return try {
                    it.value as T
                } catch (e: Exception) {
                    default
                }
            }
        }
        return default
    }

    /**
     * 如果[key]已经存在，则会覆盖数据
     * @param value     如果为 null，则会移除对应的数据。
     */
    @Throws(IllegalArgumentException::class)
    fun put(key: String, value: Any?) {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        if (value == null) {
            remove(key)
        } else {
            try {
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 移除某个key对应的那一条数据
     * @param key
     */
    @Throws(IllegalArgumentException::class)
    fun remove(key: String) {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        try {
            prefs.edit().remove(key).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除所有数据
     */
    @Throws(IllegalArgumentException::class)
    fun clear() {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        try {
            prefs.edit().clear().apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 查询某个key是否已经存在
     * @param key
     * @return
     */
    @Throws(IllegalArgumentException::class)
    fun contains(key: String): Boolean {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        return try {
            prefs.contains(key)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 返回所有的键值对数据
     * @return
     */
    @Throws(IllegalArgumentException::class)
    fun getAll(): Map<String, Any?>? {
        require(::prefs.isInitialized) { NOT_INIT_EXCEPTION }
        return try {
            prefs.all
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

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
        SPUtils.getInstance().init(context, sharedPreferencesFileName)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return SPUtils.getInstance().get(key, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        SPUtils.getInstance().put(key, value)
    }

}