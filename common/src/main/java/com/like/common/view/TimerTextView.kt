package com.like.common.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.like.common.R
import com.like.common.util.SharedPreferencesDelegate
import com.like.common.util.validator.ValidatorFactory
import java.util.*

/**
 * 显示倒计时的AppCompatTextView
 *
 * 倒计时不会因为关闭app而停止。重新打开后会继续倒计时。
 *
 * 在xml布局文件中直接使用。
 *
 */
class TimerTextView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
    // 倒计时总时长(毫秒)
    private var totalTime: Long by SharedPreferencesDelegate(
        context,
        "${context.packageName}${this::class.java.simpleName}",
        "totalTime",
        0L
    )

    // 开始倒计时的时间（毫秒），用于退出界面后，重新计时时候的计算。
    private var startTime: Long by SharedPreferencesDelegate(
        context,
        "${context.packageName}${this::class.java.simpleName}",
        "startTime",
        0L
    )

    // 倒计时的步长（毫秒）
    private var step: Long by SharedPreferencesDelegate(
        context,
        "${context.packageName}${this::class.java.simpleName}",
        "step",
        1000L
    )
    private var remainingTime: Long = 0L// 剩余时长(毫秒)
    private var timer: Timer? = null
    private val onStartText: String?
    private val onTickPrefixText: String?
    private val onTickSuffixText: String?
    private val onEndText: String?
    private val phoneValidator by lazy {
        ValidatorFactory.createPhoneValidator()
    }
    private var tvPhone: TextView? = null

    init {
        (context as? LifecycleOwner)?.lifecycle?.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                destroy()
            }
        })

        val a = context.obtainStyledAttributes(attrs, R.styleable.TimerTextView)
        onStartText = a.getString(R.styleable.TimerTextView_onStartText)
        onTickPrefixText = a.getString(R.styleable.TimerTextView_onTickPrefixText)
        onTickSuffixText = a.getString(R.styleable.TimerTextView_onTickSuffixText)
        onEndText = a.getString(R.styleable.TimerTextView_onEndText)
        a.recycle()

        if (hasRemainingTime()) {
            performStart()
        }

        this.setOnClickListener {
            if (!hasRemainingTime()) {// 上次时间已经走完了
                remainingTime = totalTime
                startTime = System.currentTimeMillis()
            }
            performStart()
        }
    }

    /**
     * @param tvPhone   电话号码文本框，如果设置了，那么当其中输入了正确的电话号码时，enable 才有可能为 true
     * @param length    倒计时总时长，毫秒
     * @param step      倒计时的步长，毫秒
     */
    fun init(tvPhone: TextView? = null, length: Long = 60000L, step: Long = 1000L) {
        if (length <= 0 || step <= 0 || length < step) throw IllegalArgumentException("length or step is invalid")
        this.tvPhone = tvPhone
        this.totalTime = length
        this.step = step

        tvPhone?.doAfterTextChanged {
            // 更新 enable 状态。
            isEnabled = true
        }
        // 此处是为了避免etPhone中已经设置了电话号码，但是不能更新enable
        isEnabled = true
    }

    private fun performStart() {
        timer = Timer().apply {
            schedule(createTimerTask(), 0, step)
        }
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
                        text = onStartText
                        isEnabled = false
                    }
                    remainingTime < step -> {
                        text = onEndText
                        isEnabled = true
                        destroy()
                    }
                    else -> {
                        text = "$onTickPrefixText${remainingTime / 1000}$onTickSuffixText"
                        isEnabled = false
                    }
                }
                remainingTime -= step
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(
            enabled &&
                    (text == onStartText || text == onEndText) &&
                    if (tvPhone != null) {
                        phoneValidator.validate(tvPhone!!.text.toString().trim())
                    } else {
                        true
                    }
        )
    }

}
