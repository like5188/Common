package com.like.common.view.banner.indicator

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout

/**
 * 图片指示器
 * 每个位置一张图片。包含正常状态和选中状态两种图片。
 *
 * @param mContext
 * @param mDataCount                指示器的数量
 * @param mContainer                指示器的容器
 * @param mIndicatorPadding         指示器之间的间隔
 * @param mNormalIndicatorResIds    正常状态的指示器图片资源id，至少一个，图片少于[mDataCount]时，循环使用。
 * @param mSelectedIndicatorResIds  选中状态的指示器图片资源id，至少一个，图片少于[mDataCount]时，循环使用。
 */
class ImageIndicator(
        private val mContext: Context,
        private val mDataCount: Int,
        private val mContainer: LinearLayout,
        private val mIndicatorPadding: Int,
        private val mNormalIndicatorResIds: List<Int>,
        private val mSelectedIndicatorResIds: List<Int>
) : IBannerIndicator {
    private var mPreSelectedPosition = 0

    init {
        if (mDataCount > 0) {
            require(mIndicatorPadding > 0) { "mIndicatorPadding 必须大于0" }
            require(mNormalIndicatorResIds.isNotEmpty()) { "mNormalIndicatorResIds 不能为空" }
            mNormalIndicatorResIds.forEach {
                require(it > 0) { "mNormalIndicatorResIds 中的图片资源 id 无效" }
            }
            require(mSelectedIndicatorResIds.isNotEmpty()) { "mSelectedIndicatorResIds 不能为空" }
            mSelectedIndicatorResIds.forEach {
                require(it > 0) { "mSelectedIndicatorResIds 中的图片资源 id 无效" }
            }

            mContainer.removeAllViews()
            for (i in 0 until mDataCount) {
                // 加载指示器图片
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)// 设置指示器宽高
                val iv = ImageView(mContext)
                if (i == 0) {
                    iv.setBackgroundResource(getSelectedIndicatorResId(i))
                    params.setMargins(0, 0, 0, 0)// 设置指示器边距
                } else {
                    iv.setBackgroundResource(getNormalIndicatorResId(i))
                    params.setMargins(mIndicatorPadding, 0, 0, 0)// 设置指示器边距
                }
                iv.layoutParams = params
                mContainer.addView(iv)
            }
        }
    }

    override fun onPageSelected(position: Int) {
        if (mDataCount <= 0) return
        mContainer.getChildAt(mPreSelectedPosition).setBackgroundResource(getNormalIndicatorResId(mPreSelectedPosition))
        mContainer.getChildAt(position).setBackgroundResource(getSelectedIndicatorResId(position))
        mPreSelectedPosition = position
    }

    private fun getNormalIndicatorResId(position: Int): Int = mNormalIndicatorResIds[position % mNormalIndicatorResIds.size]

    private fun getSelectedIndicatorResId(position: Int): Int = mSelectedIndicatorResIds[position % mSelectedIndicatorResIds.size]

}