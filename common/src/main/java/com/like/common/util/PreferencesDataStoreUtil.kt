//package com.like.common.util
//
//import android.content.Context
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.*
//import androidx.datastore.preferences.createDataStore
//
///**
// * Preferences DataStore 相比于 SharedPreferences 优点：
// * 1、DataStore 是基于 Flow 实现的，所以保证了在主线程的安全性
// * 2、以事务方式处理更新数据，事务有四大特性（原子性、一致性、 隔离性、持久性）
// * 3、没有 apply() 和 commit() 等等数据持久的方法
// * 4、自动完成 SharedPreferences 迁移到 DataStore，保证数据一致性，不会造成数据损坏
// * 5、可以监听到操作成功或者失败结果
// */
//class PreferencesDataStoreUtil private constructor() {
//    lateinit var dataStore: DataStore<Preferences>
//
//    companion object {
//        const val KEY_IS_EMPTY_EXCEPTION = "key is empty"
//
//        fun getInstance(): PreferencesDataStoreUtil {
//            return Holder.instance
//        }
//    }
//
//    private object Holder {
//        val instance = PreferencesDataStoreUtil()
//    }
//
//    fun init(context: Context, fileName: String = context.packageName) {
//        if (!::dataStore.isInitialized) {
//            dataStore = context.createDataStore(fileName)
//        }
//    }
//
//    suspend inline fun <reified T : Any> get(key: String, default: T): T {
//        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
//        var result = default
//        dataStore.edit { mutablePreferences ->
//            result = mutablePreferences[preferencesKey(key)] ?: default
//        }
//        return result
//    }
//
//    suspend inline fun <reified T : Any> put(key: String, value: T) {
//        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
//        dataStore.edit { mutablePreferences ->
//            mutablePreferences[preferencesKey(key)] = value
//        }
//    }
//
//    suspend inline fun <reified T : Any> contains(key: String): Boolean {
//        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
//        var result = false
//        dataStore.edit { mutablePreferences ->
//            result = mutablePreferences.contains<T>(preferencesKey(key))
//        }
//        return result
//    }
//
//    suspend inline fun <reified T : Any> remove(key: String) {
//        require(key.isNotEmpty()) { KEY_IS_EMPTY_EXCEPTION }
//        dataStore.edit { mutablePreferences ->
//            mutablePreferences.remove<T>(preferencesKey(key))
//        }
//    }
//
//    suspend fun clear() {
//        dataStore.edit { mutablePreferences ->
//            mutablePreferences.clear()
//        }
//    }
//
//    suspend fun getAll(): Map<String, Any> {
//        var result: Map<String, Any> = emptyMap()
//        dataStore.edit { mutablePreferences ->
//            result = mutablePreferences.asMap().mapKeys { it.key.name }
//        }
//        return result
//    }
//}