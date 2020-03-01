package com.like.common.view.titlebar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.like.common.R
import com.like.common.databinding.TitlebarBinding
import com.like.common.util.DimensionUtils

/**
 * 标题栏封装
 *
 * 定义了左边部分、右边部分、中间部分的容器。
 * 重新measure、layout了中间部分，保证中间部分不会因为内容太多而遮挡左边部分或者右边部分。
 * 重新layout右边部分，保证不会出现各部分交叉的情况。
 */
class Titlebar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val mLayoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }
    private val mBinding: TitlebarBinding by lazy {
        DataBindingUtil.inflate<TitlebarBinding>(mLayoutInflater, R.layout.titlebar, this, true)
    }

    private var mCenterLeft = 0
    private var mCenterRight = 0
    private var mRightLeft = 0
    private var mRightRight = 0

    init {
        orientation = VERTICAL
        mBinding
    }

    /**
     * 设置自定义的左边布局。默认为null，会移除左边布局
     */
    fun setLeftView(@LayoutRes layoutId: Int? = null): ViewDataBinding? {
        mBinding.leftContainer.removeAllViews()
        if (layoutId == null) {
            return null
        }
        return DataBindingUtil.inflate(mLayoutInflater, layoutId, mBinding.leftContainer, true)
    }

    /**
     * 设置自定义的中间布局。默认为null，会移除中间布局
     */
    fun setCenterView(@LayoutRes layoutId: Int? = null): ViewDataBinding? {
        mBinding.centerContainer.removeAllViews()
        if (layoutId == null) {
            return null
        }
        return DataBindingUtil.inflate(mLayoutInflater, layoutId, mBinding.centerContainer, true)
    }

    /**
     * 设置自定义的右边布局。默认为null，会移除右边布局
     */
    fun setRightView(@LayoutRes layoutId: Int? = null): ViewDataBinding? {
        mBinding.rightContainer.removeAllViews()
        if (layoutId == null) {
            return null
        }
        return DataBindingUtil.inflate(mLayoutInflater, layoutId, mBinding.rightContainer, true)
    }

    /**
     * 设置自定义的分割线。默认为null，会移除分割线
     */
    fun setDivider(@LayoutRes layoutId: Int? = null): ViewDataBinding? {
        removeView(mBinding.divider)
        if (layoutId == null) {
            return null
        }
        return DataBindingUtil.inflate(mLayoutInflater, layoutId, this, true)
    }

    /**
     * 使用默认的标题栏布局。
     *
     * 包括以下三个部分：
     * 左边部分：一个[ImageView]
     * 中间部分：一个[TextView]
     * 右边部分：一个水平的[LinearLayout]，可以随意添加menu
     */
    inner class Default {

        /**
         * 显示分割线
         *
         * @param height        分割线高度，dp。默认为0.5dp。如果设置为小于等于0，表示隐藏分割线。
         * @param color         背景颜色。默认为null，表示不设置，保持原样。
         */
        fun showDivider(height: Float = 0.5f, @ColorInt color: Int? = null) {
            if (height > 0) {
                mBinding.divider.visibility = View.VISIBLE
                mBinding.divider.layoutParams.height = DimensionUtils.dp2px(context, height)
                if (color != null) {
                    mBinding.divider.setBackgroundColor(color)
                }
            } else {
                mBinding.divider.visibility = View.GONE
            }
        }

        /**
         * 添加右边部分的菜单按钮。可以添加多个，父布局为水平的LinearLayout
         */
        fun addMenu(view: View) {
            mBinding.rightContainer.addView(view)
        }

        /**
         * 显示导航按钮
         *
         * @param iconResId         图标资源id。如果设置为0，表示去掉图标及其点击监听。
         * @param listener          点击监听。默认为null，表示不设置，保持原样。
         */
        fun showNavigation(@DrawableRes iconResId: Int, listener: View.OnClickListener? = null) {
            if (iconResId == 0) {
                mBinding.ivNavigation.visibility = View.GONE
                mBinding.ivNavigation.setImageDrawable(null)
                mBinding.ivNavigation.setOnClickListener(null)
            } else {
                mBinding.ivNavigation.visibility = View.VISIBLE
                mBinding.ivNavigation.setImageResource(iconResId)
                if (listener != null) {
                    mBinding.ivNavigation.setOnClickListener(listener)
                }
            }
        }

        /**
         * 显示标题
         *
         * @param title             文本，如果文本为空，会去掉点击监听
         * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
         * @param textSize          文本字体大小。默认为null，表示不设置，保持原样。
         * @param listener          点击监听。默认为null，表示不设置，保持原样。
         */
        fun showTitle(title: String, @ColorInt textColor: Int? = null, textSize: Float? = null, listener: View.OnClickListener? = null) {
            if (title.isEmpty()) {
                mBinding.tvTitle.visibility = View.GONE
                mBinding.tvTitle.text = ""
                mBinding.tvTitle.setOnClickListener(null)
            } else {
                mBinding.tvTitle.visibility = View.VISIBLE
                mBinding.tvTitle.text = title
                if (textColor != null) {
                    mBinding.tvTitle.setTextColor(textColor)
                }
                if (textSize != null) {
                    mBinding.tvTitle.textSize = textSize
                }
                if (listener != null) {
                    mBinding.tvTitle.setOnClickListener(listener)
                }
            }
        }

        fun getTitle(): String {
            return mBinding.tvTitle.text.toString()
        }

        fun getLeftView(): ImageView = mBinding.ivNavigation

        fun getCenterView(): TextView = mBinding.tvTitle

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
            minRemaining > 0 -> {// 垂直中心线左右两边都有剩余空间。调整中间部分的宽度，使得标题居中
                mCenterLeft = verticalCenterLine - minRemaining
                mCenterRight = verticalCenterLine + minRemaining
                mRightLeft = rightLeft
                mRightRight = measuredWidth
            }
            leftRight > rightLeft -> {// 垂直中心线左右两边交叉。直接把三部分按照水平平铺，效果如水平LinearLayout
                mCenterLeft = leftRight
                mCenterRight = mCenterLeft + mBinding.centerContainer.measuredWidth
                mRightLeft = mCenterRight
                mRightRight = mCenterRight + mBinding.rightContainer.measuredWidth
            }
            minRemaining <= 0 -> {// 垂直中心线左右两边至少一边没有剩余空间。调整中间部分的宽度，并使三部分按照水平平铺，效果如水平LinearLayout
                mCenterLeft = leftRight
                mCenterRight = rightLeft
                mRightLeft = rightLeft
                mRightRight = measuredWidth
            }
        }
        // 重新计算中间部分的width
        val newCenterWidth = mCenterRight - mCenterLeft
        // 中间部分的高度不变
        val newCenterHeight = mBinding.centerContainer.measuredHeight
        val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, newCenterWidth)
        val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, newCenterHeight)
        mBinding.centerContainer.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        mBinding.centerContainer.layout(mCenterLeft, mBinding.centerContainer.top, mCenterRight, mBinding.centerContainer.bottom)
        mBinding.rightContainer.layout(mRightLeft, mBinding.rightContainer.top, mRightRight, mBinding.rightContainer.bottom)
    }
}