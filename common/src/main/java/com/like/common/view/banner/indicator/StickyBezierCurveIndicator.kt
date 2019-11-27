package com.like.common.view.banner.indicator

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
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
    private val mPaint1: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = normalColor
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
            mMinCircleRadius = 0f

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
        // 画占位圆点
        mPositionList.forEach {
            canvas.drawCircle(it.left + mMaxCircleRadius, mMaxCircleRadius, mMaxCircleRadius, mPaint1)
        }
        // 画过度圆点
        canvas.drawCircle(mLeftCircleCenterX, mMaxCircleRadius, mLeftCircleRadius, mPaint)
        canvas.drawCircle(mRightCircleCenterX, mMaxCircleRadius, mRightCircleRadius, mPaint)
        // 画贝塞尔曲线
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
        Log.d("Tag", "position=$position")
        if (mPositionList.isEmpty()) {
            return
        }

        // 计算颜色
        val currentColor = mSelectedColors[position % mSelectedColors.size]
        val nextColor = mSelectedColors[(position + 1) % mSelectedColors.size]
        mPaint.color = argbEvaluator.evaluate(positionOffset, currentColor, nextColor).toString().toInt()

        // 计算锚点位置
        val current = mPositionList[position]
        val next = if (position == mDataCount - 1) {
//            mPositionList[0]// 这种算法和下面的算法效果不一样
            Rect().apply {
                // 在最后一个圆点后面创建一个假的圆点位置
                left = current.right + mIndicatorPaddingPx
                top = current.top
                right = left + current.width()
                bottom = current.bottom
            }
        } else {
            mPositionList[position + 1]
        }

        val leftCircleCenterX = current.left + current.width() / 2f
        val rightCircleCenterX = next.left + next.width() / 2f

        mLeftCircleCenterX = leftCircleCenterX + (rightCircleCenterX - leftCircleCenterX) * mStartInterpolator.getInterpolation(positionOffset)
        mRightCircleCenterX = leftCircleCenterX + (rightCircleCenterX - leftCircleCenterX) * mEndInterpolator.getInterpolation(positionOffset)
        mLeftCircleRadius = mMaxCircleRadius + (mMinCircleRadius - mMaxCircleRadius) * mEndInterpolator.getInterpolation(positionOffset)
        mRightCircleRadius = mMinCircleRadius + (mMaxCircleRadius - mMinCircleRadius) * mStartInterpolator.getInterpolation(positionOffset)

        invalidate()
    }

}