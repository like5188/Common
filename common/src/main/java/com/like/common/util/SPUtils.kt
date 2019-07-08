package com.like.common.util

import android.content.Context
import android.content.SharedPreferences
import java.io.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * SharedPreferences存储工具类。
 * 如果不是SharedPreferences支持的类型，就使用序列化，文件名字就是key。
 */
class SPUtils private constructor() {
    private lateinit var prefs: SharedPreferences
    private lateinit var serializeDir: String

    companion object {
        const val NOT_INIT_EXCEPTION = "you must addNotificationChannel SPUtils by addNotificationChannel() first"
        const val SHARED_PREFERENCES_FILE_SUFFIX = ".sharedPreferences"
        const val SERIALIZE_FILE_SUFFIX = ".serialize"

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
        serializeDir = context.applicationContext.filesDir.toString()
        prefs = context.applicationContext.getSharedPreferences("$sharedPreferencesFileName$SHARED_PREFERENCES_FILE_SUFFIX", Context.MODE_PRIVATE)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, default: T): T {
        if (!::prefs.isInitialized) throw RuntimeException(NOT_INIT_EXCEPTION)
        return with(prefs) {
            when (default) {
                is String -> getString(key, default) as T
                is Boolean -> getBoolean(key, default) as T
                is Int -> getInt(key, default) as T
                is Long -> getLong(key, default) as T
                is Float -> getFloat(key, default) as T
                is Serializable -> getObject(key) ?: default
                else -> default
            }
        }
    }

    fun <T> put(key: String, value: T) {
        if (!::prefs.isInitialized) throw RuntimeException(NOT_INIT_EXCEPTION)
        with(prefs.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Serializable -> {
                    saveObject(key, value)
                    null
                }
                else -> null
            }?.apply()
        }
    }

    private fun getSerializeFileName(key: String) = "$serializeDir/$key$SERIALIZE_FILE_SUFFIX"

    /**
     * 移除某个key对应的那一条数据
     * @param key
     */
    fun remove(key: String) {
        if (!::prefs.isInitialized) throw RuntimeException(NOT_INIT_EXCEPTION)
        prefs.edit().remove(key).apply()
        removeObject(key)
    }

    /**
     * 清除所有数据
     */
    fun clear() {
        if (!::prefs.isInitialized) throw RuntimeException(NOT_INIT_EXCEPTION)
        prefs.edit().clear().apply()
        clearObject()
    }

    /**
     * 查询某个key是否已经存在
     * @param key
     * @return
     */
    fun contains(key: String): Boolean {
        if (!::prefs.isInitialized) throw RuntimeException(NOT_INIT_EXCEPTION)
        var result = prefs.contains(key)
        if (!result) {
            try {
                val dir = File(serializeDir)
                val serializeFile = File(getSerializeFileName(key))
                result = serializeFile.exists() && dir.exists() && dir.walkTopDown().contains(serializeFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    /**
     * 返回所有的键值对数据
     * @return
     */
    fun getAll(): Map<String, Any?> {
        if (!::prefs.isInitialized) throw RuntimeException(NOT_INIT_EXCEPTION)
        val result = mutableMapOf<String, Any?>()
        try {
            prefs.all.forEach {
                result[it.key] = it.value
            }
            File(serializeDir).walkTopDown().forEachIndexed { index, file ->
                if (index > 0)// 除开目录
                    result[file.nameWithoutExtension] = file
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 序列化对象
     */
    private fun saveObject(key: String, obj: Any) {
        try {
            val dir = File(serializeDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            ObjectOutputStream(FileOutputStream(getSerializeFileName(key))).use { it -> it.writeObject(obj) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 反序列化对象
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> getObject(key: String): T? {
        try {
            ObjectInputStream(FileInputStream(getSerializeFileName(key))).use { it ->
                return it.readObject() as T
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 删除序列化的对象，即删除相应文件
     */
    private fun removeObject(key: String) {
        try {
            File(getSerializeFileName(key)).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除所有序列化的对象
     */
    private fun clearObject() {
        try {
            File(serializeDir).deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * SharedPreferences属性委托
 *
 * 示例：var k: Long by DelegateSharedPreferences(this, "long", 10L)
 *
 * @property context
 * @property sharedPreferencesFileName sharedPreferences对于的文件名字
 * @property key        存储的key
 * @property default    获取失败时，返回的默认值
 */
class DelegateSharedPreferences<T>(val context: Context, val sharedPreferencesFileName: String, val key: String, val default: T) : ReadWriteProperty<Any?, T> {
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