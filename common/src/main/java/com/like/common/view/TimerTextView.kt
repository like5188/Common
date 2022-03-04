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

    /**
     * 计时开始回调
     */
    var onStart: ((Long) -> Unit)? = null

    /**
     * 计时中回调
     */
    var onTick: ((Long) -> Unit)? = null

    /**
     * 计时结束回调
     */
    var onEnd: (() -> Unit)? = null

    fun updateEnable() {
        this@TimerTextView.isEnabled = true
    }

    /**
     * 是否能 enable 的条件
     */
    var canEnable: () -> Boolean = { true }

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
            timer?.schedule(createTimerTask(), 0, step)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled && canEnable())
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
        timer?.schedule(createTimerTask(), 0, step)
    }

    private fun hasRemainingTime(): Boolean {
        val passTime = System.currentTimeMillis() - startTime// 上次开始倒计时到现在的时间间隔
        remainingTime = totalTime - passTime// 上次剩余的时长
        return remainingTime >= step
    }

    private fun destroy() {
        timer?.cancel()
        timer = null
    }

    private fun createTimerTask() = object : TimerTask() {
        override fun run() {
            post {// 主线程进行
                when {
                    remainingTime == totalTime -> {
                        onStart?.invoke(remainingTime)
                        this@TimerTextView.isEnabled = false
                    }
                    remainingTime < step -> {
                        onEnd?.invoke()
                        this@TimerTextView.isEnabled = true
                        destroy()
                    }
                    else -> {
                        onTick?.invoke(remainingTime)
                        this@TimerTextView.isEnabled = false
                    }
                }
                remainingTime -= step
            }
        }
    }

}