package com.like.common.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.like.common.util.SharedPreferencesDelegate
import java.util.*

/**
 * 显示倒计时的AppCompatTextView
 *
 * 倒计时不会因为关闭app而停止。重新打开后会继续倒计时。
 *
 * 在xml布局文件中直接使用。然后调用start()方法启动倒计时
 */
class TimerTextView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
    /**
     * 倒计时总时长(毫秒)
     */
    private var totalTime: Long by SharedPreferencesDelegate(
        context,
        "${context.packageName}${this::class.java.simpleName}",
        "totalTime",
        0L
    )

    /**
     * 开始倒计时的时间（毫秒），用于退出界面后，重新计时时候的计算。
     */
    private var startTime: Long by SharedPreferencesDelegate(
        context,
        "${context.packageName}${this::class.java.simpleName}",
        "startTime",
        0L
    )

    /**
     * 倒计时的步长（毫秒）
     */
    private var step: Long by SharedPreferencesDelegate(
        context,
        "${context.packageName}${this::class.java.simpleName}",
        "step",
        1000L
    )
    private var remainingTime: Long = 0L// 剩余时长(毫秒)
    private var timer: Timer? = null
    private var tickListener: OnTickListener? = null
    private val timerTask = object : TimerTask() {
        override fun run() {
            post {
                when {
                    remainingTime == totalTime -> {
                        this@TimerTextView.isEnabled = false
                        tickListener?.onStart(remainingTime)
                    }
                    remainingTime < step -> {
                        this@TimerTextView.isEnabled = true
                        tickListener?.onEnd()
                        destroy()
                    }
                    else -> {
                        this@TimerTextView.isEnabled = false
                        tickListener?.onTick(remainingTime)
                    }
                }
                remainingTime -= step
            }
        }
    }

    init {
        if (context is LifecycleOwner) {
            (context as LifecycleOwner).lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    destroy()
                }
            })
        }
        if (hasRemainingTime()) {
            timer = Timer()
            timer?.schedule(timerTask, 0, step)
        }
    }

    /**
     * 开始倒计时
     *
     * @param length 倒计时总时长，毫秒
     * @param stepTime 倒计时的步长，毫秒
     */
    fun start(length: Long, stepTime: Long = 1000L) {
        if (length <= 0 || stepTime <= 0 || length < stepTime) return

        timer = Timer()

        if (!hasRemainingTime()) {// 上次时间已经走完了
            remainingTime = length
            totalTime = length
            step = stepTime
            startTime = System.currentTimeMillis()
        }
        timer?.schedule(timerTask, 0, step)
    }

    private fun hasRemainingTime(): Boolean {
        val passTime = System.currentTimeMillis() - startTime// 上次开始倒计时到现在的时间间隔
        remainingTime = totalTime - passTime// 上次剩余的时长
        return remainingTime >= step
    }

    private fun destroy() {
        timerTask.cancel()
        timer?.cancel()
        timer = null
    }

    /**
     * @param tickListener 倒计时回调
     */
    fun setOnTickListener(tickListener: OnTickListener) {
        this.tickListener = tickListener
    }

    interface OnTickListener {
        fun onStart(time: Long)
        fun onTick(time: Long)
        fun onEnd()
    }

}