package com.like.common.view.titlebar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.like.common.R
import com.like.common.databinding.TitlebarBinding

/**
 * 标题栏封装，保证了中间部分不遮挡左边和右边部分。
 */
class TitleBar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val mBinding: TitlebarBinding by lazy {
        DataBindingUtil.inflate<TitlebarBinding>(LayoutInflater.from(context), R.layout.titlebar, this, true)
    }
    private var mCenterLeft = 0
    private var mCenterRight = 0

    init {
        orientation = VERTICAL
        mBinding
    }

    /**
     * 因为是在RelativeLayout中，为了避免中间部分内容太多导致遮挡左右部分，所以要重新计算中间部分的宽度
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 垂直中心线的位置
        val verticalCenterLine = measuredWidth / 2

        // 左边部分的marginStart、marginEnd
        val leftLayoutParams = mBinding.titleBarLeft.layoutParams as RelativeLayout.LayoutParams
        val leftMarginStart = leftLayoutParams.marginStart
        val leftMarginEnd = leftLayoutParams.marginEnd
        // 左边部分的right
        val leftRight = leftMarginStart - mBinding.titleBarLeft.measuredWidth
        // 左边部分的最右边
        val leftRightMost = leftRight + leftMarginEnd
        // 垂直中心线左边剩余的宽度
        val leftRemaining = verticalCenterLine - leftRightMost

        // 右边部分的marginStart、marginEnd
        val rightLayoutParams = mBinding.titleBarRight.layoutParams as RelativeLayout.LayoutParams
        val rightMarginStart = rightLayoutParams.marginStart
        val rightMarginEnd = rightLayoutParams.marginEnd
        // 右边部分的left
        val rightLeft = measuredWidth - rightMarginEnd - mBinding.titleBarRight.measuredWidth
        // 右边部分的最左边
        val rightLeftMost = rightLeft - rightMarginStart
        // 垂直中心线右边剩余的宽度
        val rightRemaining = rightLeftMost - verticalCenterLine

        // 取左右部分剩余的最小宽度
        val minRemaining = Math.min(leftRemaining, rightRemaining)
        // 重新计算中间部分的left、right、width
        mCenterLeft = verticalCenterLine - minRemaining
        mCenterRight = verticalCenterLine + minRemaining
        val newCenterWidth = mCenterRight - mCenterLeft
        val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, newCenterWidth)
        val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, mBinding.titleBarCenter.measuredHeight)
        // 重新测量中间部分的宽度
        mBinding.titleBarCenter.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        Log.w("tag", "onMeasure menuLeft=$rightLeft titleLeft=$mCenterLeft titleRight=$mCenterRight")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        Log.e("tag", "onLayout")
        mBinding.titleBarCenter.layout(mCenterLeft, mBinding.titleBarCenter.top, mCenterRight, mBinding.titleBarCenter.bottom)
    }
}