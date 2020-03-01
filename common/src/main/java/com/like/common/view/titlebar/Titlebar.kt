package com.like.common.view.titlebar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.like.common.R
import com.like.common.databinding.*

/**
 * 标题栏封装
 * 定义了左边部分、右边部分、中间部分。
 * 重新计算了中间部分的宽度，保证中间部分不会因为内容太多而遮挡左边部分或者右边部分。
 */
class Titlebar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val mLayoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }
    private val mBinding: TitlebarBinding by lazy {
        DataBindingUtil.inflate<TitlebarBinding>(mLayoutInflater, R.layout.titlebar, this, true)
    }
    private var mLeftBinding: ViewDataBinding? =
            DataBindingUtil.inflate<TitlebarDefaultLeftBinding>(mLayoutInflater, R.layout.titlebar_default_left, mBinding.leftContainer, true)

    private var mCenterBinding: ViewDataBinding? =
            DataBindingUtil.inflate<TitlebarDefaultCenterBinding>(mLayoutInflater, R.layout.titlebar_default_center, mBinding.centerContainer, true)

    private var mRightBinding: ViewDataBinding? =
            DataBindingUtil.inflate<TitlebarDefaultRightBinding>(mLayoutInflater, R.layout.titlebar_default_right, mBinding.rightContainer, true)

    private var mDividerBinding: ViewDataBinding? =
            DataBindingUtil.inflate<TitlebarDefaultDividerBinding>(mLayoutInflater, R.layout.titlebar_default_divider, this, true)

    private var mCenterLeft = 0
    private var mCenterRight = 0

    init {
        orientation = VERTICAL
        mBinding
        mLeftBinding
        mCenterBinding
        mRightBinding
        mDividerBinding
    }

    /**
     * 如果没有通过[setLeftView]方法重新设置，那么默认为[TitlebarDefaultLeftBinding]
     */
    fun getLeftViewDataBinding() = mLeftBinding

    /**
     * 如果没有通过[setCenterView]方法重新设置，那么默认为[TitlebarDefaultCenterBinding]
     */
    fun getCenterViewDataBinding() = mCenterBinding

    /**
     * 如果没有通过[setRightView]方法重新设置，那么默认为[TitlebarDefaultRightBinding]
     */
    fun getRightViewDataBinding() = mRightBinding

    /**
     * 如果没有通过[setDivider]方法重新设置，那么默认为[TitlebarDefaultDividerBinding]
     */
    fun getDividerViewDataBinding() = mDividerBinding

    fun setLeftView(binding: ViewDataBinding? = null) {
        mLeftBinding = binding
        mBinding.leftContainer.removeAllViews()
        binding?.let {
            mBinding.leftContainer.addView(it.root)
        }
    }

    fun setCenterView(binding: ViewDataBinding? = null) {
        mCenterBinding = binding
        mBinding.centerContainer.removeAllViews()
        binding?.let {
            mBinding.centerContainer.addView(it.root)
        }
    }

    fun setRightView(binding: ViewDataBinding? = null) {
        mRightBinding = binding
        mBinding.rightContainer.removeAllViews()
        binding?.let {
            mBinding.rightContainer.addView(it.root)
        }
    }

    fun setDivider(binding: ViewDataBinding? = null) {
        mDividerBinding = binding
        removeView(mDividerBinding?.root)
        binding?.let {
            addView(it.root)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 垂直中心线的位置
        val verticalCenterLine = measuredWidth / 2

        // 左边部分的right
        val leftRight = mBinding.leftContainer.measuredWidth
        // 垂直中心线左边剩余的宽度
        val leftRemaining = verticalCenterLine - leftRight

        // 右边部分的left
        val rightLeft = measuredWidth - mBinding.rightContainer.measuredWidth
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
        val newCenterHeight = mBinding.centerContainer.measuredHeight
        val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, newCenterWidth)
        val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, newCenterHeight)
        mBinding.centerContainer.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        Log.w("tag", "onMeasure menuLeft=$rightLeft titleLeft=$mCenterLeft titleRight=$mCenterRight")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        Log.e("tag", "onLayout")
        mBinding.centerContainer.layout(mCenterLeft, mBinding.centerContainer.top, mCenterRight, mBinding.centerContainer.bottom)
    }
}