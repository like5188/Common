package com.like.common.view.banner.indicator

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
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
 * @param mColors           指示器的颜色，至少一个，少于[mDataCount]时，循环使用。
 */
@SuppressLint("ViewConstructor")
class StickyBezierCurveIndicator(
        private val mContext: Context,
        private val mDataCount: Int,
        private val mContainer: ViewGroup,
        indicatorPadding: Float,
        private val mColors: List<Int>
) : View(mContext), IBannerIndicator {
    private val mIndicatorPaddingPx: Int = DimensionUtils.dp2px(mContext, indicatorPadding)
    private val mPositionList = mutableListOf<Rect>()

    private var mMaxCircleRadius: Float = 0f
    private var mMinCircleRadius: Float = 0f

    private var mLeftCircleRadius: Float = 0f
    private var mLeftCircleCenterX: Float = 0f
    private var mRightCircleRadius: Float = 0f
    private var mRightCircleCenterX: Float = 0f

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
            require(mColors.isNotEmpty()) { "mColors 不能为空" }

            val containerHeight = mContainer.height - mContainer.paddingTop - mContainer.paddingBottom
            mMaxCircleRadius = containerHeight / 2f
            mMinCircleRadius = mMaxCircleRadius / 1.75f

            // 设置本控制器的宽高
            val w = (mMaxCircleRadius * 2 * mDataCount + mIndicatorPaddingPx * (mDataCount - 1)).toInt()
            this.layoutParams = ViewGroup.LayoutParams(w, containerHeight)

            var startLeft = left
            for (i in 0 until mDataCount) {
                val position = Rect()
                position.left = startLeft
                position.top = top
                position.right = startLeft + mMaxCircleRadius.toInt() * 2
                position.bottom = bottom
                mPositionList.add(position)
                startLeft = position.right + mIndicatorPaddingPx
            }

            mContainer.addView(this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(mLeftCircleCenterX, mMaxCircleRadius, mLeftCircleRadius, mPaint)
        canvas.drawCircle(mRightCircleCenterX, mMaxCircleRadius, mRightCircleRadius, mPaint)
        drawBezierCurve(canvas)
    }

    /**
     * 绘制贝塞尔曲线
     */
    private fun drawBezierCurve(canvas: Canvas) {
        mPath.reset()
        mPath.moveTo(mRightCircleCenterX, mMaxCircleRadius)
        mPath.lineTo(mRightCircleCenterX, mMaxCircleRadius - mRightCircleRadius)
        mPath.quadTo(mRightCircleCenterX + (mLeftCircleCenterX - mRightCircleCenterX) / 2.0f, mMaxCircleRadius, mLeftCircleCenterX, mMaxCircleRadius - mLeftCircleRadius)
        mPath.lineTo(mLeftCircleCenterX, mMaxCircleRadius + mLeftCircleRadius)
        mPath.quadTo(mRightCircleCenterX + (mLeftCircleCenterX - mRightCircleCenterX) / 2.0f, mMaxCircleRadius, mRightCircleCenterX, mMaxCircleRadius + mRightCircleRadius)
        mPath.close()  // 闭合
        canvas.drawPath(mPath, mPaint)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositionList.isEmpty()) {
            return
        }

        // 计算颜色
        val currentColor = mColors[position % mColors.size]
        val nextColor = mColors[(position + 1) % mColors.size]
        mPaint.color = argbEvaluator.evaluate(positionOffset, currentColor, nextColor).toString().toInt()

        // 计算锚点位置
        val current = getImitativePosition(position)
        val next = getImitativePosition(position + 1)

        val leftCircleCenterX = (current.left + (current.right - current.left) / 2).toFloat()
        val rightCircleCenterX = (next.left + (next.right - next.left) / 2).toFloat()

        mLeftCircleCenterX = leftCircleCenterX + (rightCircleCenterX - leftCircleCenterX) * mStartInterpolator.getInterpolation(positionOffset)
        mRightCircleCenterX = leftCircleCenterX + (rightCircleCenterX - leftCircleCenterX) * mEndInterpolator.getInterpolation(positionOffset)
        mLeftCircleRadius = mMaxCircleRadius + (mMinCircleRadius - mMaxCircleRadius) * mEndInterpolator.getInterpolation(positionOffset)
        mRightCircleRadius = mMinCircleRadius + (mMaxCircleRadius - mMinCircleRadius) * mStartInterpolator.getInterpolation(positionOffset)

        invalidate()
    }

    /**
     * 获取指定位置锚点的坐标信息
     */
    private fun getImitativePosition(index: Int): Rect {
        return if (index >= 0 && index <= mPositionList.size - 1) { // 越界后，返回假的PositionData
            mPositionList[index]
        } else {
            val referenceData: Rect
            val offset: Int
            if (index < 0) {
                offset = index
                referenceData = mPositionList[0]
            } else {
                offset = index - mPositionList.size + 1
                referenceData = mPositionList[mPositionList.size - 1]
            }
            val result = Rect()
            result.left = referenceData.left + offset * referenceData.width()
            result.top = referenceData.top
            result.right = referenceData.right + offset * referenceData.width()
            result.bottom = referenceData.bottom
            result
        }
    }

}