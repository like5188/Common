package com.like.common.util

import android.content.Context
import dalvik.system.DexFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 在指定的包名[packageName]下查找指定类的所有子类（不包括自己）
 */
suspend fun <T> Class<T>.getSubclasses(context: Context, packageName: String?): List<Class<T>> = withContext(Dispatchers.IO) {
    if (packageName.isNullOrEmpty()) {
        return@withContext emptyList()
    }
    val df = DexFile(context.packageCodePath) // 通过DexFile查找当前的APK中可执行文件
    val enumeration = df.entries() // 获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
    val classes = mutableListOf<Class<T>>()
    while (enumeration.hasMoreElements()) { //遍历
        val className = enumeration.nextElement()
        if (!className.isNullOrEmpty() && className.startsWith(packageName) && className != this@getSubclasses.name) {
            val clazz = try {
                Class.forName(className)
            } catch (e: Exception) {
                null
            }
            if (clazz != null && this@getSubclasses.isAssignableFrom(clazz)) {
                classes.add(clazz as Class<T>)
            }
        }
    }
    classes
}
