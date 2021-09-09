package com.like.common.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * Preferences DataStore 相比于 SharedPreferences 优点：
 * 1、DataStore 是基于 Flow 实现的，所以保证了在主线程的安全性
 * 2、以事务方式处理更新数据，事务有四大特性（原子性、一致性、 隔离性、持久性）
 * 3、没有 apply() 和 commit() 等等数据持久的方法
 * 4、自动完成 SharedPreferences 迁移到 DataStore，保证数据一致性，不会造成数据损坏
 * 5、可以监听到操作成功或者失败结果
 */
class DataStorePreferencesUtil private constructor() {
    companion object {
        const val KEY_IS_EMPTY_EXCEPTION = "key is empty"

        fun getInstance(): DataStorePreferencesUtil {
            return Holder.instance
        }
    }

    private object Holder {
        val instance = DataStorePreferencesUtil()
    }

    lateinit var context: Context

    // At the top level of your kotlin file:
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store_preferences")

    fun init(context: Context, fileName: String = context.packageName) {
        this.context = context
    }

    suspend fun <T> get(key: String, default: T): T {
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        return context.dataStore.data.map { preferences ->
            when (default) {
                is Int -> preferences[intPreferencesKey(key)]
                is Double -> preferences[doublePreferencesKey(key)]
                is String -> preferences[stringPreferencesKey(key)]
                is Boolean -> preferences[booleanPreferencesKey(key)]
                is Float -> preferences[floatPreferencesKey(key)]
                is Long -> preferences[longPreferencesKey(key)]
                else -> throw UnsupportedOperationException("DataStore 不支持的数据类型")
            }
        }.firstOrNull() as? T ?: default
    }

    suspend fun <T> put(key: String, value: T) {
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        context.dataStore.edit { mutablePreferences ->
            when (value) {
                is Int -> mutablePreferences[intPreferencesKey(key)] = value
                is Double -> mutablePreferences[doublePreferencesKey(key)] = value
                is String -> mutablePreferences[stringPreferencesKey(key)] = value
                is Boolean -> mutablePreferences[booleanPreferencesKey(key)] = value
                is Float -> mutablePreferences[floatPreferencesKey(key)] = value
                is Long -> mutablePreferences[longPreferencesKey(key)] = value
                else -> throw UnsupportedOperationException("DataStore 不支持的数据类型")
            }
        }
    }

    suspend inline fun <reified T : Any> contains(key: String): Boolean {
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        var result = false
        context.dataStore.edit { mutablePreferences ->
            result = mutablePreferences.contains(
                when (T::class.java) {
                    Int::class.java -> intPreferencesKey(key)
                    Double::class.java -> doublePreferencesKey(key)
                    String::class.java -> stringPreferencesKey(key)
                    Boolean::class.java -> booleanPreferencesKey(key)
                    Float::class.java -> floatPreferencesKey(key)
                    Long::class.java -> longPreferencesKey(key)
                    else -> throw UnsupportedOperationException("DataStore 不支持的数据类型")
                }
            )
        }
        return result
    }

    suspend inline fun <reified T : Any> remove(key: String): T? {
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        var result: T? = null
        context.dataStore.edit { mutablePreferences ->
            result = mutablePreferences.remove(
                when (T::class.java) {
                    Int::class.java -> intPreferencesKey(key)
                    Double::class.java -> doublePreferencesKey(key)
                    String::class.java -> stringPreferencesKey(key)
                    Boolean::class.java -> booleanPreferencesKey(key)
                    Float::class.java -> floatPreferencesKey(key)
                    Long::class.java -> longPreferencesKey(key)
                    else -> throw UnsupportedOperationException("DataStore 不支持的数据类型")
                }
            ) as? T
        }
        return result
    }

    suspend fun clear() {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences.clear()
        }
    }

    suspend fun getAll(): Map<String, Any> {
        var result: Map<String, Any> = emptyMap()
        context.dataStore.edit { mutablePreferences ->
            result = mutablePreferences.asMap().mapKeys { it.key.name }
        }
        return result
    }
}