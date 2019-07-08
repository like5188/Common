package com.like.common.util

import java.io.Closeable
import java.io.IOException

fun Closeable?.close() {
    this?.apply {
        try {
            this.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}