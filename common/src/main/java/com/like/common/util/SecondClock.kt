package com.like.common.util

import android.os.Handler
import android.os.Looper
import android.os.SystemClock

/**
 * 整秒计时器
 */
open class SecondClock {
    // 已经经理的毫秒数
    private var elapsedMillis: Long = -1L
    private var startTime: Long = -1L
    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private fun postRunnable() {
        val now = SystemClock.uptimeMillis()
        val next = now + (1000 - now % 1000)// 整秒
        handler.postAtTime({
            elapsedMillis = System.currentTimeMillis() - startTime
            onTick(elapsedMillis / 1000)
            postRunnable()
        }, next)
    }

    /**
     * 当前状态：
     * 0：空闲；1：运行；2：停止
     */
    private var status: Int = 0

    fun getSeconds() = elapsedMillis / 1000

    fun startOrResume() {
        if (status == 0) {
            start()
        } else if (status == 2) {
            resume()
        }
    }

    fun start() {
        if (status != 0) {
            return
        }
        status = 1
        startTime = System.currentTimeMillis()
        postRunnable()
    }

    fun resume() {
        if (status != 2) {
            return
        }
        status = 1
        startTime = System.currentTimeMillis() - elapsedMillis
        postRunnable()
    }

    fun stop() {
        if (status != 1) {
            return
        }
        status = 2
        handler.removeCallbacksAndMessages(null)
    }

    open fun onTick(seconds: Long) {

    }
}