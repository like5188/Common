package com.like.common.view.banner.indicator

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.like.common.util.DimensionUtils

/**
 * 粘性圆角矩形指示器
 *
 * @param mContext
 * @param mDataCount        指示器的数量
 * @param mContainer        指示器的容器
 * @param indicatorWidth    指示器宽度，单位 dp
 * @param indicatorPadding  指示器之间的间隔，单位 dp
 * @param mNormalColor      正常状态的指示器颜色
 * @param mSelectedColors   选中状态的指示器颜色，至少一个，少于[mDataCount]时，循环使用。
 */
@SuppressLint("ViewConstructor")
class StickyRoundRectIndicator(
        private val mContext: Context,
        private val mDataCount: Int,
        private val mContainer: ViewGroup,
        indicatorWidth: Float,
        indicatorPadding: Float,
        private val mNormalColor: Int,
        private val mSelectedColors: List<Int>
) : View(mContext), IBannerIndicator {
    private val mIndicatorWidthPx: Int = DimensionUtils.dp2px(mContext, indicatorWidth)// 指示器的宽度
    private val mIndicatorPaddingPx: Int = DimensionUtils.dp2px(mContext, indicatorPadding)// 指示器之间的间隔

    private val mPositions = mutableListOf<RectF>()// 占位矩形

    private var mTransitionalColor = 0// 画过渡阶段矩形的颜色

    private val mTransitionalRect1 = RectF()// 过渡矩形中
    private val mTransitionalRect2 = RectF()// 用于辅助处理首尾交替的情况

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private val mStartInterpolator = AccelerateInterpolator()
    private val mEndInterpolator = DecelerateInterpolator()
    private val mArgbEvaluator = ArgbEvaluator()

    init {
        if (mDataCount > 0) {
            require(mIndicatorPaddingPx > 0) { "indicatorPadding 必须大于0" }
            require(mSelectedColors.isNotEmpty()) { "mSelectedColors 不能为空" }

            // 设置本控制器的宽高
            val containerHeight = mContainer.height - mContainer.paddingTop - mContainer.paddingBottom
            val w = mIndicatorWidthPx * mDataCount + mIndicatorPaddingPx * mDataCount// 左右各留 mIndicatorPaddingPx/2 的位置，用于显示过渡动画
            this.layoutParams = ViewGroup.LayoutParams(w, containerHeight)

            mTransitionalRect1.top = 0f
            mTransitionalRect1.bottom = containerHeight.toFloat()
            mTransitionalRect2.top = 0f
            mTransitionalRect2.bottom = containerHeight.toFloat()

            // 计算所有占位矩形
            var startLeft = left + mIndicatorPaddingPx / 2f
            for (i in 0 until mDataCount) {
                val rect = RectF()
                rect.left = startLeft
                rect.top = 0f
                rect.right = startLeft + mIndicatorWidthPx
                rect.bottom = containerHeight.toFloat()
                mPositions.add(rect)
                startLeft = rect.right + mIndicatorPaddingPx
            }

            mContainer.addView(this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画所有占位矩形
        mPaint.color = mNormalColor
        mPositions.forEach {
            canvas.drawRoundRect(it, it.height() / 2, it.height() / 2, mPaint)
        }
        // 画过渡矩形
        mPaint.color = mTransitionalColor
        canvas.drawRoundRect(mTransitionalRect1, mTransitionalRect1.height() / 2, mTransitionalRect1.height() / 2, mPaint)

        // 当处于首尾交替的情况，两头各画一个过渡效果。
        // 因为控制器左右各留 mIndicatorPaddingPx/2 的位置，用于显示过渡动画，所以看起来就是两头各一半的过渡效果。
        if (mTransitionalRect2.width() > 0f) {
            canvas.drawRoundRect(mTransitionalRect2, mTransitionalRect2.height() / 3, mTransitionalRect2.height() / 2, mPaint)
            mTransitionalRect2.left = 0f
            mTransitionalRect2.right = 0f
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositions.isEmpty()) {
            return
        }

        // 计算颜色
        val currentColor = mSelectedColors[position % mSelectedColors.size]
        val nextColor = mSelectedColors[(position + 1) % mSelectedColors.size]
        mTransitionalColor = mArgbEvaluator.evaluate(positionOffset, currentColor, nextColor).toString().toInt()

        // 计算过渡矩形，它们的left和right都是不断变化的。
        val distance = mIndicatorWidthPx + mIndicatorPaddingPx// 两矩形中心之间的距离
        val currentRect = mPositions[position]
        mTransitionalRect1.left = currentRect.left + distance * mStartInterpolator.getInterpolation(positionOffset)
        mTransitionalRect1.right = currentRect.right + distance * mEndInterpolator.getInterpolation(positionOffset)

        // 当处于首尾交替的情况，在第一个占位左边一个位置再假设一个占位，用于辅助最左边的过渡动画。
        if (position == mDataCount - 1) {
            val beforeFirstRect = RectF()// 第一个占位左边一个位置（假设的）
            beforeFirstRect.left = mPositions[0].left - distance
            beforeFirstRect.right = mPositions[0].right - distance
            mTransitionalRect2.left = beforeFirstRect.left + distance * mStartInterpolator.getInterpolation(positionOffset)
            mTransitionalRect2.right = beforeFirstRect.right + distance * mEndInterpolator.getInterpolation(positionOffset)
        }

        invalidate()
    }

}