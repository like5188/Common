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
 * @param indicatorPadding  指示器之间的间隔
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
    private val mPositionDataList = mutableListOf<PositionData>()

    private var mLeftCircleRadius: Float = 0f
    private var mLeftCircleX: Float = 0f
    private var mRightCircleRadius: Float = 0f
    private var mRightCircleX: Float = 0f

    private var mYOffset: Float = 0f
    private var mMaxCircleRadius: Float = 0f
    private var mMinCircleRadius: Float = 0f

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPath = Path()

    private val mStartInterpolator = AccelerateInterpolator()
    private val mEndInterpolator = DecelerateInterpolator()

    private val argbEvaluator = ArgbEvaluator()

    init {
        if (mDataCount > 0) {
            require(mIndicatorPaddingPx > 0) { "indicatorPadding 必须大于0" }
            mPaint.style = Paint.Style.FILL
            mMaxCircleRadius = DimensionUtils.dp2px(context, 5f).toFloat()
            mMinCircleRadius = DimensionUtils.dp2px(context, 2.5f).toFloat()
            mYOffset = DimensionUtils.dp2px(context, 1.5f).toFloat()

            mContainer.removeAllViews()
            var startLeft = mContainer.left
            for (i in 0 until mDataCount) {
                val positionData = PositionData()
                positionData.mLeft = startLeft
                positionData.mTop = mContainer.top
                positionData.mRight = startLeft + mMaxCircleRadius.toInt() * 2
                positionData.mBottom = mContainer.bottom
                mPositionDataList.add(positionData)
                startLeft = positionData.mRight + mIndicatorPaddingPx
            }
            mContainer.addView(this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(mLeftCircleX, height - mYOffset - mMaxCircleRadius, mLeftCircleRadius, mPaint)
        canvas.drawCircle(mRightCircleX, height - mYOffset - mMaxCircleRadius, mRightCircleRadius, mPaint)
        drawBezierCurve(canvas)
    }

    /**
     * 绘制贝塞尔曲线
     *
     * @param canvas
     */
    private fun drawBezierCurve(canvas: Canvas) {
        mPath.reset()
        val y = height.toFloat() - mYOffset - mMaxCircleRadius
        mPath.moveTo(mRightCircleX, y)
        mPath.lineTo(mRightCircleX, y - mRightCircleRadius)
        mPath.quadTo(mRightCircleX + (mLeftCircleX - mRightCircleX) / 2.0f, y, mLeftCircleX, y - mLeftCircleRadius)
        mPath.lineTo(mLeftCircleX, y + mLeftCircleRadius)
        mPath.quadTo(mRightCircleX + (mLeftCircleX - mRightCircleX) / 2.0f, y, mRightCircleX, y + mRightCircleRadius)
        mPath.close()  // 闭合
        canvas.drawPath(mPath, mPaint)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositionDataList.isEmpty()) {
            return
        }

        // 计算颜色
        if (mColors.isNotEmpty()) {
            val currentColor = mColors[position % mColors.size]
            val nextColor = mColors[(position + 1) % mColors.size]
            mPaint.color = argbEvaluator.evaluate(positionOffset, currentColor, nextColor).toString().toInt()
        }

        // 计算锚点位置
        val current = getImitativePositionData(mPositionDataList, position)
        val next = getImitativePositionData(mPositionDataList, position + 1)

        val leftX = (current.mLeft + (current.mRight - current.mLeft) / 2).toFloat()
        val rightX = (next.mLeft + (next.mRight - next.mLeft) / 2).toFloat()

        mLeftCircleX = leftX + (rightX - leftX) * mStartInterpolator.getInterpolation(positionOffset)
        mRightCircleX = leftX + (rightX - leftX) * mEndInterpolator.getInterpolation(positionOffset)
        mLeftCircleRadius = mMaxCircleRadius + (mMinCircleRadius - mMaxCircleRadius) * mEndInterpolator.getInterpolation(positionOffset)
        mRightCircleRadius = mMinCircleRadius + (mMaxCircleRadius - mMinCircleRadius) * mStartInterpolator.getInterpolation(positionOffset)

        invalidate()
    }

    /**
     * IPagerIndicator支持弹性效果的辅助方法
     *
     * @param positionDataList
     * @param index
     * @return
     */
    private fun getImitativePositionData(positionDataList: List<PositionData>, index: Int): PositionData {
        if (index >= 0 && index <= positionDataList.size - 1) { // 越界后，返回假的PositionData
            return positionDataList[index]
        } else {
            val result = PositionData()
            val referenceData: PositionData
            val offset: Int
            if (index < 0) {
                offset = index
                referenceData = positionDataList[0]
            } else {
                offset = index - positionDataList.size + 1
                referenceData = positionDataList[positionDataList.size - 1]
            }
            result.mLeft = referenceData.mLeft + offset * referenceData.width()
            result.mTop = referenceData.mTop
            result.mRight = referenceData.mRight + offset * referenceData.width()
            result.mBottom = referenceData.mBottom
            return result
        }
    }

    class PositionData {
        var mLeft: Int = 0
        var mTop: Int = 0
        var mRight: Int = 0
        var mBottom: Int = 0

        fun width(): Int {
            return mRight - mLeft
        }

        fun height(): Int {
            return mBottom - mTop
        }

    }

}