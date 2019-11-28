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
 * 粘性贝塞尔曲线指示器
 *
 * @param mContext
 * @param mDataCount        指示器的数量
 * @param mContainer        指示器的容器
 * @param indicatorPadding  指示器之间的间隔，单位 dp
 * @param mNormalColor       正常状态的指示器颜色
 * @param mSelectedColors   选中状态的指示器颜色，至少一个，少于[mDataCount]时，循环使用。
 */
@SuppressLint("ViewConstructor")
class StickyBezierCurveIndicator(
        private val mContext: Context,
        private val mDataCount: Int,
        private val mContainer: ViewGroup,
        indicatorPadding: Float,
        private val mNormalColor: Int,
        private val mSelectedColors: List<Int>
) : View(mContext), IBannerIndicator {
    private val mIndicatorPaddingPx: Int = DimensionUtils.dp2px(mContext, indicatorPadding)// 指示器之间的间隔

    private val mCircles = mutableListOf<Circle>()// 占位圆点

    private var mMaxCircleRadius: Float = 0f// 最大圆点半径
    private var mMinCircleRadius: Float = 0f// 最小圆点半径

    private var mTransitionalColor = 0// 画过渡阶段（包括过渡圆点和贝塞尔曲线）的颜色
    private val mCurTransitionalCircle1 = Circle()// 两个过渡圆点中的第一个。此圆点会紧跟着mNextTransitionalCircle1圆点到达下一个位置。
    private val mNextTransitionalCircle1 = Circle()// 两个过渡圆点中的第二个

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val mPath = Path()

    private val mStartInterpolator = AccelerateInterpolator()
    private val mEndInterpolator = DecelerateInterpolator()
    private val mArgbEvaluator = ArgbEvaluator()

    init {
        if (mDataCount > 0) {
            require(mIndicatorPaddingPx > 0) { "indicatorPadding 必须大于0" }
            require(mSelectedColors.isNotEmpty()) { "mSelectedColors 不能为空" }

            val containerHeight = mContainer.height - mContainer.paddingTop - mContainer.paddingBottom
            mMaxCircleRadius = containerHeight / 2f
            mMinCircleRadius = 1f

            // 设置本控制器的宽高
            val w = (mMaxCircleRadius * 2 * mDataCount + mIndicatorPaddingPx * mDataCount).toInt()// 左右各留 mIndicatorPaddingPx/2 的位置，用于显示过渡动画
            this.layoutParams = ViewGroup.LayoutParams(w, containerHeight)

            // 计算所有占位圆点的位置
            var startCenterX = left + mIndicatorPaddingPx / 2 + mMaxCircleRadius
            for (i in 0 until mDataCount) {
                val circle = Circle()
                circle.centerX = startCenterX
                circle.centerY = mMaxCircleRadius
                circle.radius = mMaxCircleRadius
                mCircles.add(circle)
                startCenterX += mIndicatorPaddingPx + mMaxCircleRadius * 2f
            }

            mContainer.addView(this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画所有占位圆点
        mPaint.color = mNormalColor
        mCircles.forEach {
            canvas.drawCircle(it.centerX, it.centerY, it.radius, mPaint)
        }
        // 画过度圆点
        mPaint.color = mTransitionalColor
        canvas.drawCircle(mCurTransitionalCircle1.centerX, mCurTransitionalCircle1.centerY, mCurTransitionalCircle1.radius, mPaint)
        canvas.drawCircle(mNextTransitionalCircle1.centerX, mNextTransitionalCircle1.centerY, mNextTransitionalCircle1.radius, mPaint)
        // 画贝塞尔曲线
        mPath.reset()
        mPath.moveTo(mNextTransitionalCircle1.centerX, mNextTransitionalCircle1.centerY)
        mPath.lineTo(mNextTransitionalCircle1.centerX, mMaxCircleRadius - mNextTransitionalCircle1.radius)
        mPath.quadTo(mCurTransitionalCircle1.centerX + (mNextTransitionalCircle1.centerX - mCurTransitionalCircle1.centerX) / 2f, mMaxCircleRadius, mCurTransitionalCircle1.centerX, mMaxCircleRadius - mCurTransitionalCircle1.radius)
        mPath.lineTo(mCurTransitionalCircle1.centerX, mMaxCircleRadius + mCurTransitionalCircle1.radius)
        mPath.quadTo(mCurTransitionalCircle1.centerX + (mNextTransitionalCircle1.centerX - mCurTransitionalCircle1.centerX) / 2f, mMaxCircleRadius, mNextTransitionalCircle1.centerX, mMaxCircleRadius + mNextTransitionalCircle1.radius)
        mPath.close()  // 闭合
        canvas.drawPath(mPath, mPaint)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mCircles.isEmpty()) {
            return
        }

        // 计算颜色
        val currentColor = mSelectedColors[position % mSelectedColors.size]
        val nextColor = mSelectedColors[(position + 1) % mSelectedColors.size]
        mTransitionalColor = mArgbEvaluator.evaluate(positionOffset, currentColor, nextColor).toString().toInt()

        // 计算锚点位置
        val current = mCircles[position]
        val next = if (position == mDataCount - 1) {
//            mCircles[0]// 这种算法和下面的算法效果不一样
            // 在最右边的占位圆点的右边创建一个假的占位圆点
            Circle().apply {
                centerX = current.centerX + mIndicatorPaddingPx + mMaxCircleRadius * 2f
                centerY = current.centerY
            }
        } else {
            mCircles[position + 1]
        }

        mCurTransitionalCircle1.centerX = current.centerX + (next.centerX - current.centerX) * mStartInterpolator.getInterpolation(positionOffset)
        mCurTransitionalCircle1.centerY = mMaxCircleRadius
        mCurTransitionalCircle1.radius = mMaxCircleRadius + (mMinCircleRadius - mMaxCircleRadius) * mEndInterpolator.getInterpolation(positionOffset)

        mNextTransitionalCircle1.centerX = current.centerX + (next.centerX - current.centerX) * mEndInterpolator.getInterpolation(positionOffset)
        mNextTransitionalCircle1.centerY = mMaxCircleRadius
        mNextTransitionalCircle1.radius = mMinCircleRadius + (mMaxCircleRadius - mMinCircleRadius) * mStartInterpolator.getInterpolation(positionOffset)

        invalidate()
    }

    class Circle {
        var centerX = 0f
        var centerY = 0f
        var radius = 0f
    }
}