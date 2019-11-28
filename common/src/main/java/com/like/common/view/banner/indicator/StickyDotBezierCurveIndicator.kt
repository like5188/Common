package com.like.common.view.banner.indicator

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.like.common.util.DimensionUtils

/**
 * 粘性圆点贝塞尔曲线指示器
 *
 * @param mContext
 * @param mDataCount        指示器的数量
 * @param mContainer        指示器的容器
 * @param indicatorPadding  指示器之间的间隔，单位 dp
 * @param mNormalColor       正常状态的指示器颜色
 * @param mSelectedColors   选中状态的指示器颜色，至少一个，少于[mDataCount]时，循环使用。
 */
@SuppressLint("ViewConstructor")
class StickyDotBezierCurveIndicator(
        private val mContext: Context,
        private val mDataCount: Int,
        private val mContainer: ViewGroup,
        indicatorPadding: Float,
        private val mNormalColor: Int,
        private val mSelectedColors: List<Int>
) : View(mContext), IBannerIndicator {
    private val mIndicatorPaddingPx: Int = DimensionUtils.dp2px(mContext, indicatorPadding)// 指示器之间的间隔

    private val mPositions = mutableListOf<Circle>()// 占位圆点

    private var mMaxCircleRadius: Float = 0f// 最大圆点半径
    private var mMinCircleRadius: Float = 0f// 最小圆点半径

    private var mTransitionalColor = 0// 画过渡阶段（包括过渡圆点和贝塞尔曲线）的颜色

    private val mCurTransitionalCircle1 = Circle()// 两个过渡圆点中的第一个。此圆点会紧跟着mNextTransitionalCircle1圆点到达下一个位置。
    private val mNextTransitionalCircle1 = Circle()// 两个过渡圆点中的第二个
    private val mCurTransitionalCircle2 = Circle()// 用于辅助处理首尾交替的情况
    private val mNextTransitionalCircle2 = Circle()

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val mPath = Path()

    private val mStartInterpolator = AccelerateInterpolator()
    private val mEndInterpolator = DecelerateInterpolator()
    private val mArgbEvaluator = ArgbEvaluator()

    init {
        if (mDataCount > 0) {
            require(mIndicatorPaddingPx > 0) { "indicatorPadding 必须大于0" }
            require(mSelectedColors.isNotEmpty()) { "mSelectedColors 不能为空" }

            // 设置本控制器的宽高
            val containerHeight = mContainer.height - mContainer.paddingTop - mContainer.paddingBottom
            val w = (mMaxCircleRadius * 2 * mDataCount + mIndicatorPaddingPx * mDataCount).toInt()// 左右各留 mIndicatorPaddingPx/2 的位置，用于显示过渡动画
            this.layoutParams = ViewGroup.LayoutParams(w, containerHeight)

            // 计算最大最小圆点半径
            mMaxCircleRadius = containerHeight / 2f
            mMinCircleRadius = 1f

            // 计算所有占位圆点的位置
            var startCenterX = left + mIndicatorPaddingPx / 2 + mMaxCircleRadius
            for (i in 0 until mDataCount) {
                val circle = Circle()
                circle.centerX = startCenterX
                circle.centerY = mMaxCircleRadius
                circle.radius = mMaxCircleRadius
                mPositions.add(circle)
                startCenterX += mIndicatorPaddingPx + mMaxCircleRadius * 2f
            }

            mContainer.addView(this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画所有占位圆点
        mPaint.color = mNormalColor
        mPositions.forEach {
            it.draw(canvas, mPaint)
        }
        // 画过渡圆点
        mPaint.color = mTransitionalColor
        mCurTransitionalCircle1.draw(canvas, mPaint)
        mNextTransitionalCircle1.draw(canvas, mPaint)
        // 画贝过渡圆点之间的塞尔曲线
        mPath.reset()
        mPath.moveTo(mNextTransitionalCircle1.centerX, mNextTransitionalCircle1.centerY)
        mPath.lineTo(mNextTransitionalCircle1.centerX, mMaxCircleRadius - mNextTransitionalCircle1.radius)
        mPath.quadTo(mCurTransitionalCircle1.centerX + (mNextTransitionalCircle1.centerX - mCurTransitionalCircle1.centerX) / 2f, mMaxCircleRadius, mCurTransitionalCircle1.centerX, mMaxCircleRadius - mCurTransitionalCircle1.radius)
        mPath.lineTo(mCurTransitionalCircle1.centerX, mMaxCircleRadius + mCurTransitionalCircle1.radius)
        mPath.quadTo(mCurTransitionalCircle1.centerX + (mNextTransitionalCircle1.centerX - mCurTransitionalCircle1.centerX) / 2f, mMaxCircleRadius, mNextTransitionalCircle1.centerX, mMaxCircleRadius + mNextTransitionalCircle1.radius)
        mPath.close()  // 闭合
        canvas.drawPath(mPath, mPaint)

        // 当处于首尾交替的情况，两头各画一个过渡效果。
        // 因为控制器左右各留 mIndicatorPaddingPx/2 的位置，用于显示过渡动画，所以看起来就是两头各一半的过渡效果。
        if (mCurTransitionalCircle2.radius > 0f && mNextTransitionalCircle2.radius > 0f) {
            mCurTransitionalCircle2.draw(canvas, mPaint)
            mNextTransitionalCircle2.draw(canvas, mPaint)

            mPath.reset()
            mPath.moveTo(mNextTransitionalCircle2.centerX, mNextTransitionalCircle2.centerY)
            mPath.lineTo(mNextTransitionalCircle2.centerX, mMaxCircleRadius - mNextTransitionalCircle2.radius)
            mPath.quadTo(mCurTransitionalCircle2.centerX + (mNextTransitionalCircle2.centerX - mCurTransitionalCircle2.centerX) / 2f, mMaxCircleRadius, mCurTransitionalCircle2.centerX, mMaxCircleRadius - mCurTransitionalCircle2.radius)
            mPath.lineTo(mCurTransitionalCircle2.centerX, mMaxCircleRadius + mCurTransitionalCircle2.radius)
            mPath.quadTo(mCurTransitionalCircle2.centerX + (mNextTransitionalCircle2.centerX - mCurTransitionalCircle2.centerX) / 2f, mMaxCircleRadius, mNextTransitionalCircle2.centerX, mMaxCircleRadius + mNextTransitionalCircle2.radius)
            mPath.close()  // 闭合
            canvas.drawPath(mPath, mPaint)

            mCurTransitionalCircle2.reset()
            mNextTransitionalCircle2.reset()
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

        // 计算过渡圆点，它们的centerX和radius都是不断变化的。
        val distance = mMaxCircleRadius * 2f + mIndicatorPaddingPx// 两圆点圆心之间的距离
        val currentCircleX = mPositions[position].centerX

        mCurTransitionalCircle1.centerX = currentCircleX + distance * mStartInterpolator.getInterpolation(positionOffset)
        mCurTransitionalCircle1.centerY = mMaxCircleRadius
        mCurTransitionalCircle1.radius = mMaxCircleRadius + (mMinCircleRadius - mMaxCircleRadius) * mEndInterpolator.getInterpolation(positionOffset)

        mNextTransitionalCircle1.centerX = currentCircleX + distance * mEndInterpolator.getInterpolation(positionOffset)
        mNextTransitionalCircle1.centerY = mMaxCircleRadius
        mNextTransitionalCircle1.radius = mMinCircleRadius + (mMaxCircleRadius - mMinCircleRadius) * mStartInterpolator.getInterpolation(positionOffset)

        // 当处于首尾交替的情况，在第一个占位左边一个位置再假设一个占位，用于辅助最左边的过渡动画。
        if (position == mDataCount - 1) {
            val beforeFirstCircleX = mPositions[0].centerX - distance// 第一个占位左边一个位置（假设的）
            mCurTransitionalCircle2.centerX = beforeFirstCircleX + distance * mStartInterpolator.getInterpolation(positionOffset)
            mCurTransitionalCircle2.centerY = mMaxCircleRadius
            mCurTransitionalCircle2.radius = mMaxCircleRadius + (mMinCircleRadius - mMaxCircleRadius) * mEndInterpolator.getInterpolation(positionOffset)

            mNextTransitionalCircle2.centerX = beforeFirstCircleX + distance * mEndInterpolator.getInterpolation(positionOffset)
            mNextTransitionalCircle2.centerY = mMaxCircleRadius
            mNextTransitionalCircle2.radius = mMinCircleRadius + (mMaxCircleRadius - mMinCircleRadius) * mStartInterpolator.getInterpolation(positionOffset)
        }

        invalidate()
    }

    class Circle {
        var centerX = 0f
        var centerY = 0f
        var radius = 0f

        fun reset() {
            centerX = 0f
            centerY = 0f
            radius = 0f
        }

        fun draw(canvas: Canvas, paint: Paint) {
            canvas.drawCircle(centerX, centerY, radius, paint)
        }
    }
}