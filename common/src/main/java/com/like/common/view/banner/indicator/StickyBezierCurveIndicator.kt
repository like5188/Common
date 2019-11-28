package com.like.common.view.banner.indicator

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
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
 * @param normalColor       正常状态的指示器颜色
 * @param mSelectedColors   选中状态的指示器颜色，至少一个，少于[mDataCount]时，循环使用。
 */
@SuppressLint("ViewConstructor")
class StickyBezierCurveIndicator(
        private val mContext: Context,
        private val mDataCount: Int,
        private val mContainer: ViewGroup,
        indicatorPadding: Float,
        normalColor: Int,
        private val mSelectedColors: List<Int>
) : View(mContext), IBannerIndicator {
    private val mIndicatorPaddingPx: Int = DimensionUtils.dp2px(mContext, indicatorPadding)
    private val mCircles = mutableListOf<Circle>()
    private var mMaxCircleRadius: Float = 0f
    private var mMinCircleRadius: Float = 0f

    private val mCurCircle = Circle()
    private val mNextCircle = Circle()

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val mPath = Path()

    private val mStartInterpolator = AccelerateInterpolator()
    private val mEndInterpolator = DecelerateInterpolator()

    private val argbEvaluator = ArgbEvaluator()

    init {
        if (mDataCount > 0) {
            require(mIndicatorPaddingPx > 0) { "indicatorPadding 必须大于0" }
            require(mSelectedColors.isNotEmpty()) { "mSelectedColors 不能为空" }

            val containerHeight = mContainer.height - mContainer.paddingTop - mContainer.paddingBottom
            mMaxCircleRadius = containerHeight / 2f
            mMinCircleRadius = 1f

            // 设置本控制器的宽高
            val w = (mMaxCircleRadius * 2 * mDataCount + mIndicatorPaddingPx * mDataCount).toInt()
            this.layoutParams = ViewGroup.LayoutParams(w, containerHeight)
            setBackgroundColor(Color.WHITE)

            // 计算所有圆点的位置
            var startCenterX = left + mIndicatorPaddingPx / 2 + mMaxCircleRadius
            for (i in 0 until mDataCount) {
                val circle = Circle()
                circle.centerX = startCenterX
                circle.centerY = mMaxCircleRadius
                circle.radius = mMaxCircleRadius
                circle.color = normalColor
                mCircles.add(circle)
                startCenterX += mIndicatorPaddingPx + mMaxCircleRadius * 2f
            }

            mContainer.addView(this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画占位圆点
        mCircles.forEach {
            mPaint.color = it.color
            canvas.drawCircle(it.centerX, it.centerY, it.radius, mPaint)
        }
        // 画过度圆点
        mPaint.color = mCurCircle.color
        canvas.drawCircle(mCurCircle.centerX, mCurCircle.centerY, mCurCircle.radius, mPaint)
        canvas.drawCircle(mNextCircle.centerX, mNextCircle.centerY, mNextCircle.radius, mPaint)
        // 画贝塞尔曲线
        mPath.reset()
        mPath.moveTo(mNextCircle.centerX, mNextCircle.centerY)
        mPath.lineTo(mNextCircle.centerX, mMaxCircleRadius - mNextCircle.radius)
        mPath.quadTo(mNextCircle.centerX + (mCurCircle.centerX - mNextCircle.centerX) / 2.0f, mMaxCircleRadius, mCurCircle.centerX, mMaxCircleRadius - mCurCircle.radius)
        mPath.lineTo(mCurCircle.centerX, mMaxCircleRadius + mCurCircle.radius)
        mPath.quadTo(mNextCircle.centerX + (mCurCircle.centerX - mNextCircle.centerX) / 2.0f, mMaxCircleRadius, mNextCircle.centerX, mMaxCircleRadius + mNextCircle.radius)
        mPath.close()  // 闭合
        canvas.drawPath(mPath, mPaint)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        Log.d("tag", "position=$position positionOffset=$positionOffset positionOffsetPixels=$positionOffsetPixels")
        if (mCircles.isEmpty()) {
            return
        }

        // 计算颜色
        val currentColor = mSelectedColors[position % mSelectedColors.size]
        val nextColor = mSelectedColors[(position + 1) % mSelectedColors.size]
        val color = argbEvaluator.evaluate(positionOffset, currentColor, nextColor).toString().toInt()
        mCurCircle.color = color
        mNextCircle.color = color

        // 计算锚点位置
        val current = mCircles[position]
        val next = if (position == mDataCount - 1) {
//            mCircles[0]// 这种算法和下面的算法效果不一样
            Circle().apply {
                centerX = current.centerX + mIndicatorPaddingPx + mMaxCircleRadius * 2f
                centerY = current.centerY
            }
        } else {
            mCircles[position + 1]
        }

        mCurCircle.centerX = current.centerX + (next.centerX - current.centerX) * mStartInterpolator.getInterpolation(positionOffset)
        mCurCircle.centerY = mMaxCircleRadius
        mCurCircle.radius = mMaxCircleRadius + (mMinCircleRadius - mMaxCircleRadius) * mEndInterpolator.getInterpolation(positionOffset)

        mNextCircle.centerX = current.centerX + (next.centerX - current.centerX) * mEndInterpolator.getInterpolation(positionOffset)
        mNextCircle.centerY = mMaxCircleRadius
        mNextCircle.radius = mMinCircleRadius + (mMaxCircleRadius - mMinCircleRadius) * mStartInterpolator.getInterpolation(positionOffset)

        invalidate()
    }

    class Circle {
        var centerX = 0f
        var centerY = 0f
        var radius = 0f
        var color = 0
    }
}