package com.like.common.util

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import java.util.concurrent.atomic.AtomicInteger

/**
 * 整秒计时器
 */
class SecondsTimer {
    // 已经经历的毫秒数
    private var elapsedMillis: Long = -1L
    private var startTime: Long = -1L
    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    /**
     * 当前状态：0：空闲；1：运行；2：停止
     */
    private val status = AtomicInteger(0)

    /**
     * 每经过1秒回调1次
     */
    var onTick: ((seconds: Long) -> Unit)? = null

    private fun postRunnable() {
        val now = SystemClock.uptimeMillis()
        val next = now + (1000 - now % 1000)// 整秒
        handler.postAtTime({
            elapsedMillis = System.currentTimeMillis() - startTime
            onTick?.invoke(elapsedMillis / 1000)
            // 这里必须加判断，否则runnable永远不会停止，即使使用removeCallbacksAndMessages(null)，因为removeCallbacksAndMessages(null)方法不能移除正在运行的runnable。
            if (status.get() == 1) {
                postRunnable()
            }
        }, next)
    }

    /**
     * 获取当前状态
     * @return 0：空闲；1：运行；2：停止
     */
    fun getStatus(): Int = status.get()

    /**
     * 获取当前已经经历的秒数
     */
    fun getSeconds() = elapsedMillis / 1000

    /**
     * 开始或者恢复计时
     */
    fun startOrResume() {
        if (status.get() == 0) {
            start()
        } else if (status.get() == 2) {
            resume()
        }
    }

    /**
     * 开始计时
     */
    fun start() {
        if (status.compareAndSet(0, 1)) {
            startTime = System.currentTimeMillis()
            postRunnable()
        }
    }

    /**
     * 恢复计时。当使用[pause]停止计时后，可用此方法恢复计时。
     */
    fun resume() {
        if (status.compareAndSet(2, 1)) {
            startTime = System.currentTimeMillis() - elapsedMillis
            postRunnable()
        }
    }

    /**
     * 暂停计时。可用[resume]方法恢复计时。
     */
    fun pause() {
        if (status.compareAndSet(1, 2)) {
            handler.removeCallbacksAndMessages(null)
        }
    }

    /**
     * 停止计时，并重置为初始状态。不可用[resume]方法恢复计时，只能使用[start]方法重新开始计时。
     */
    fun stop() {
        status.set(0)
        elapsedMillis = -1L
        startTime = -1L
        handler.removeCallbacksAndMessages(null)
    }

}