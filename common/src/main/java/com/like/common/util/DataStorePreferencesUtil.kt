package com.like.common.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
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
        val instance by lazy { DataStorePreferencesUtil() }
    }

    lateinit var context: Context

    // At the top level of your kotlin file:
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store_preferences")

    fun init(context: Context, fileName: String = context.packageName) {
        this.context = context
    }

    suspend inline fun <reified T> get(key: String, default: T): T {
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        return context.dataStore.data.map { preferences ->
            preferences[preferencesKey(key)] ?: default
        }.first()
    }

    suspend inline fun <reified T> put(key: String, value: T) {
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[preferencesKey(key)] = value
        }
    }

    suspend inline fun <reified T> contains(key: String): Boolean {
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        return context.dataStore.data.map { preferences ->
            preferences.contains(preferencesKey<T>(key))
        }.first()
    }

    suspend inline fun <reified T> remove(key: String): T? {
        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
        var result: T? = null
        context.dataStore.edit { mutablePreferences ->
            result = mutablePreferences.remove(preferencesKey(key))
        }
        return result
    }

    suspend fun clear() {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences.clear()
        }
    }

    suspend fun getAll(): Map<String, Any> {
        return context.dataStore.data.map { preferences ->
            preferences.asMap().mapKeys { it.key.name }
        }.first()
    }

    inline fun <reified T> preferencesKey(key: String): Preferences.Key<T> {
        return when (T::class) {
            Int::class -> {
                intPreferencesKey(key)
            }
            String::class -> {
                stringPreferencesKey(key)
            }
            Boolean::class -> {
                booleanPreferencesKey(key)
            }
            Float::class -> {
                floatPreferencesKey(key)
            }
            Long::class -> {
                longPreferencesKey(key)
            }
            Double::class -> {
                doublePreferencesKey(key)
            }
            Set::class -> {
                throw IllegalArgumentException("Use `preferencesSetKey` to create keys for Sets.")
            }
            else -> {
                throw IllegalArgumentException("Type not supported: ${T::class.java}")
            }
        } as Preferences.Key<T>
    }

}
