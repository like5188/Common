package com.like.common.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

inline fun <T, reified V> List<T>?.map(): List<V> {
    val result = mutableListOf<V>()
    this?.forEach {
        if (it is V) {
            result.add(it)
        }
    }
    return result
}

/**
 * 对集合进行深拷贝
 * 注意：泛型类必须要实现 Serializable 接口
 */
@Suppress("UNCHECKED_CAST")
fun <T> List<T>.deepCopy(): List<T> {
    val baos = ByteArrayOutputStream()
    ObjectOutputStream(baos).use { oos ->
        oos.writeObject(this)
    }
    return ObjectInputStream(ByteArrayInputStream(baos.toByteArray())).use { ois ->
        ois.readObject()
    } as List<T>
}
