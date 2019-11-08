package com.like.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import com.like.common.util.DimensionUtils
import com.like.common.util.onPreDrawListener

/**
 * 侧边栏
 *
 * 在xml布局文件中直接使用。然后调用init()初始化，最后调用setDataAndShow()设置数据并显示
 */
class SidebarView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var selectedTextChangedListener: ((String) -> Unit)? = null
    private val list = mutableListOf<String>()
    private lateinit var paint: Paint
    private var curSelectedPosition = -1
    private var normalBackgroundColor = -1
    private var touchBackgroundColor = -1
    private var normalTextColor = -1
    private var touchTextColor = -1
    private var selectedTextColor = -1
    private var textGap = 0// 文本之间的间隙大小
    private var textSize = 0// 字体大小
    private var totalTextHeight = 0// 文本的总高度
    private var onTouching = false// 是否正则触摸

    /**
     * 初始化参数
     *
     * @param normalTextColor               正常状态下的文本颜色
     * @param touchTextColor                触摸状态下的文本颜色
     * @param selectedTextColor             选中的文本颜色
     * @param normalBackgroundColor         正常状态下的背景色，默认透明
     * @param touchBackgroundColor          触摸状态下的背景色，默认透明
     * @param textGap                       文本之间的间隙大小，默认0dp，<=0表示充满整个SidebarView。
     * @param textSize                      文本大小，默认14sp
     * @param selectedTextChangedListener   选中文本改变的监听，默认null
     */
    @JvmOverloads
    fun init(
            @ColorInt normalTextColor: Int,
            @ColorInt touchTextColor: Int,
            @ColorInt selectedTextColor: Int,
            @ColorInt normalBackgroundColor: Int = Color.TRANSPARENT,
            @ColorInt touchBackgroundColor: Int = Color.TRANSPARENT,
            textGap: Int = 0,
            textSize: Int = 14,
            selectedTextChangedListener: ((String) -> Unit)? = null
    ): SidebarView {
        this.normalBackgroundColor = normalBackgroundColor
        this.touchBackgroundColor = touchBackgroundColor
        this.normalTextColor = normalTextColor
        this.touchTextColor = touchTextColor
        this.selectedTextColor = selectedTextColor
        this.textGap = DimensionUtils.px2dp(context, textGap.toFloat())
        this.textSize = DimensionUtils.px2sp(context, textSize.toFloat())
        this.selectedTextChangedListener = selectedTextChangedListener
        paint = Paint().apply {
            this.textSize = textSize.toFloat()
            this.isAntiAlias = true
        }
        return this
    }

    /**
     * 设置数据并显示
     */
    fun setDataAndShow(list: List<String>) {
        if (!::paint.isInitialized) {
            throw RuntimeException("you must call addNotificationChannel() before setDataAndShow()")
        }
        this.list.addAll(list)
        if (textGap <= 0) {// 充满整个SidebarView
            onPreDrawListener {
                textGap = (measuredHeight - list.size * textSize) / (list.size + 1)// 间隙要多1个。
                totalTextHeight = (textGap + textSize) * list.size// 不算最底部的那个间隙。为了准确计算位置
                invalidate()
            }
        } else {
            totalTextHeight = (textGap + textSize) * list.size// 不算最底部的那个间隙。为了准确计算位置
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!::paint.isInitialized) return
        if (list.isEmpty()) return

        // 画背景色
        canvas.drawColor(if (onTouching) {
            touchBackgroundColor
        } else {
            normalBackgroundColor
        })

        for (i in list.indices) {
            paint.isFakeBoldText = false
            if (onTouching) {
                if (i == curSelectedPosition) {
                    paint.color = selectedTextColor
                    paint.isFakeBoldText = true
                } else {
                    paint.color = touchTextColor
                }
            } else {
                paint.color = normalTextColor
            }
            canvas.drawText(
                    list[i],
                    (width - paint.measureText(list[i])) / 2,
                    (textGap + textSize) * (i + 1).toFloat(),
                    paint
            )
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val tempY = event.y - textGap / 2// 为了控制触摸范围为文本+上下各textGap/2
        val newSelectedPosition = when {
            tempY < 0 -> 0// 最顶部的所有范围触摸都认为位置为0
            tempY > totalTextHeight -> list.size - 1// 最底部的所有范围触摸都认为位置为list.size-1
            else -> (tempY / totalTextHeight * list.size).toInt()
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouching = true
                if (newSelectedPosition != curSelectedPosition) {
                    selectedTextChangedListener?.invoke(list[newSelectedPosition])
                    curSelectedPosition = newSelectedPosition
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (newSelectedPosition != curSelectedPosition) {
                    selectedTextChangedListener?.invoke(list[newSelectedPosition])
                    curSelectedPosition = newSelectedPosition
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                onTouching = false
                curSelectedPosition = -1
                invalidate()
            }
        }
        return true
    }
}