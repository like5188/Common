package com.like.common.util.ble.utils

import java.nio.ByteBuffer
import java.util.*

object BleUtils {

    /**
     * 把 ByteArray 按照指定的 chunkSize 进行分批处理
     */
    fun batch(data: ByteArray, chunkSize: Int): List<ByteArray> {
        val result = ArrayList<ByteArray>()
        val packetSize = Math.ceil(data.size / chunkSize.toDouble()).toInt()
        for (i in 0 until packetSize) {
            if (i == packetSize - 1) {
                var lastLen = data.size % chunkSize
                if (lastLen == 0) {
                    lastLen = chunkSize
                }
                val temp = ByteArray(lastLen)
                System.arraycopy(data, i * chunkSize, temp, 0, lastLen)
                result.add(temp)
            } else {
                val temp = ByteArray(chunkSize)
                System.arraycopy(data, i * chunkSize, temp, 0, chunkSize)
                result.add(temp)
            }
        }
        return result
    }

    /**
     * ByteBuffer 转换成 ByteArray
     */
    fun convert(byteBuffer: ByteBuffer): ByteArray? {
        byteBuffer.flip()
        val len = byteBuffer.limit() - byteBuffer.position()
        val bytes = ByteArray(len)

        if (byteBuffer.isReadOnly) {
            return null
        } else {
            byteBuffer.get(bytes)
        }
        return bytes
    }
}