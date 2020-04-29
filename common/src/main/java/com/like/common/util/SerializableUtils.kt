package com.like.common.util

import android.content.Context
import java.io.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Serializable类型数据序列化工具类。
 */
class SerializableUtils private constructor() {
    private lateinit var serializeDir: String

    companion object {
        private const val NOT_INIT_EXCEPTION = "you must init SerializableUtils by init() first"
        private const val SERIALIZE_FILE_SUFFIX = ".serialize"

        @JvmStatic
        fun getInstance(): SerializableUtils {
            return Holder.instance
        }
    }

    private object Holder {
        val instance = SerializableUtils()
    }

    fun init(context: Context) {
        if (!::serializeDir.isInitialized) {
            serializeDir = context.applicationContext.filesDir.toString()
        }
    }

    @Throws(IllegalArgumentException::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, default: T? = null): T? {
        require(::serializeDir.isInitialized) { NOT_INIT_EXCEPTION }
        return try {
            ObjectInputStream(FileInputStream(getSerializeFileName(key))).use {
                it.readObject() as? T
            }
        } catch (e: Exception) {
            e.printStackTrace()
            default
        }
    }

    @Throws(IllegalArgumentException::class)
    fun put(key: String, value: Any?) {
        require(::serializeDir.isInitialized) { NOT_INIT_EXCEPTION }
        try {
            val dir = File(serializeDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            ObjectOutputStream(FileOutputStream(getSerializeFileName(key))).use { it.writeObject(value) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 移除某个key对应的那一条数据
     * @param key
     */
    @Throws(IllegalArgumentException::class)
    fun remove(key: String) {
        require(::serializeDir.isInitialized) { NOT_INIT_EXCEPTION }
        try {
            File(getSerializeFileName(key)).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除所有数据
     */
    @Throws(IllegalArgumentException::class)
    fun clear() {
        require(::serializeDir.isInitialized) { NOT_INIT_EXCEPTION }
        try {
            File(serializeDir).deleteRecursively()
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
        require(::serializeDir.isInitialized) { NOT_INIT_EXCEPTION }
        return try {
            val dir = File(serializeDir)
            val serializeFile = File(getSerializeFileName(key))
            serializeFile.exists() && dir.exists() && dir.walkTopDown().contains(serializeFile)
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
        require(::serializeDir.isInitialized) { NOT_INIT_EXCEPTION }
        return try {
            val result = mutableMapOf<String, Any?>()
            File(serializeDir).walkTopDown().forEachIndexed { index, file ->
                if (index > 0) {// 除开目录
                    val key = file.nameWithoutExtension
                    result[key] = get(key, null)
                }
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 返回所有的键
     * @return
     */
    @Throws(IllegalArgumentException::class)
    fun getKeys(): List<String>? {
        require(::serializeDir.isInitialized) { NOT_INIT_EXCEPTION }
        return try {
            val result = mutableListOf<String>()
            File(serializeDir).walkTopDown().forEachIndexed { index, file ->
                if (index > 0) {// 除开目录
                    result.add(file.nameWithoutExtension)
                }
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getSerializeFileName(key: String) = "$serializeDir/$key$SERIALIZE_FILE_SUFFIX"

    /**
     * Serializable属性委托
     * 支持实现了Serializable接口的数据类
     *
     * 示例：var xxx by Delegate()
     *
     * @property context
     * @property sharedPreferencesFileName  sharedPreferences对于的文件名字
     * @property key                        存储的key
     * @property default                    获取失败时，返回的默认值
     */
    class Delegate<T>(
            private val context: Context,
            private val sharedPreferencesFileName: String,
            private val key: String,
            private val default: T?
    ) : ReadWriteProperty<Any?, T?> {
        init {
            SPUtils.getInstance().init(context, sharedPreferencesFileName)
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return SPUtils.getInstance().get(key, default)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            SPUtils.getInstance().put(key, value)
        }

    }
}