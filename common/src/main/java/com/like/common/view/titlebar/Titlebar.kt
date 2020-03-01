package com.like.common.view.titlebar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.like.common.R
import com.like.common.databinding.TitlebarBinding

/**
 * 标题栏封装
 * 定义了左边部分、右边部分、中间部分。
 * 重新计算了中间部分的宽度，保证中间部分不会因为内容太多而遮挡左边部分或者右边部分。
 */
class Titlebar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val mBinding: TitlebarBinding by lazy {
        DataBindingUtil.inflate<TitlebarBinding>(LayoutInflater.from(context), R.layout.titlebar, this, true)
    }
    private var mCenterLeft = 0
    private var mCenterRight = 0

    init {
        orientation = VERTICAL
        mBinding
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 垂直中心线的位置
        val verticalCenterLine = measuredWidth / 2

        // 左边部分的right
        val leftRight = mBinding.titlebarLeft.measuredWidth
        // 垂直中心线左边剩余的宽度
        val leftRemaining = verticalCenterLine - leftRight

        // 右边部分的left
        val rightLeft = measuredWidth - mBinding.titlebarRight.measuredWidth
        // 垂直中心线右边剩余的宽度
        val rightRemaining = rightLeft - verticalCenterLine

        // 取左右部分剩余的最小宽度
        val minRemaining = Math.min(leftRemaining, rightRemaining)
        // 重新计算中间部分的left、right
        when {
            minRemaining > 0 -> {
                mCenterLeft = verticalCenterLine - minRemaining
                mCenterRight = verticalCenterLine + minRemaining
            }
            minRemaining <= 0 -> {
                mCenterLeft = leftRight
                mCenterRight = rightLeft
            }
        }
        // 计算中间部分的width
        val newCenterWidth = mCenterRight - mCenterLeft
        // 中间部分的高度不变
        val newCenterHeight = mBinding.titlebarCenter.measuredHeight
        val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, newCenterWidth)
        val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, newCenterHeight)
        mBinding.titlebarCenter.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        Log.w("tag", "onMeasure menuLeft=$rightLeft titleLeft=$mCenterLeft titleRight=$mCenterRight")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        Log.e("tag", "onLayout")
        mBinding.titlebarCenter.layout(mCenterLeft, mBinding.titlebarCenter.top, mCenterRight, mBinding.titlebarCenter.bottom)
    }
}