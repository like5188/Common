package com.like.common.view.toolbar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ActionMenuView
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.like.common.R
import com.like.common.databinding.ToolbarCustomViewBinding

class ToolbarCustomViewHelper(context: Context, binding: ToolbarCustomViewBinding? = null) {
    private val mBinding: ToolbarCustomViewBinding by lazy {
        binding ?: DataBindingUtil.inflate<ToolbarCustomViewBinding>(
                LayoutInflater.from(context),
                R.layout.toolbar_custom_view,
                null, false
        )
    }

    fun getRootView() = mBinding.root

    fun getContentView() = mBinding.cl

    fun setOnClickListener(clickListener: View.OnClickListener? = null) {
        if (clickListener == null) {
            mBinding.root.setOnClickListener(null)
        } else {
            mBinding.root.setOnClickListener { view -> clickListener.onClick(view) }
        }
    }

    fun setMargin(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        // 如果没有折叠就是：Toolbar.LayoutParams；
        // 如果是折叠的就是：ActionMenuView.LayoutParams，但是折叠的我们不用管它。
        when (mBinding.root.layoutParams) {
            is Toolbar.LayoutParams -> {
                mBinding.root.layoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT)
                        .apply {
                            leftMargin = left
                            topMargin = top
                            rightMargin = right
                            bottomMargin = bottom
                        }
            }
        }
    }

    /**
     * 假如root为第一层，那么真正的内容在第二层，
     * 设置第二层的margin，这样可以配合[com.like.common.view.badgeview.BadgeView]来显示消息并调整其位置
     */
    fun setContentPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        mBinding.cl.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT)
                .apply {
                    leftMargin = left
                    topMargin = top
                    rightMargin = right
                    bottomMargin = bottom
                }
    }

    /**
     *
     * @param size 单位sp
     */
    fun setTitle(title: String? = null, @ColorInt color: Int? = null, size: Float? = null) {
        if (title.isNullOrEmpty()) {
            mBinding.tvTitle.visibility = View.GONE
            mBinding.tvTitle.text = ""
        } else {
            mBinding.tvTitle.visibility = View.VISIBLE
            mBinding.tvTitle.text = title
            if (color != null) {
                mBinding.tvTitle.setTextColor(color)
            }
            if (size != null) {
                mBinding.tvTitle.textSize = size
            }
        }
    }

    fun getTitle(): String {
        return mBinding.tvTitle.text.toString()
    }

    fun setIcon(@DrawableRes iconResId: Int = 0) {
        if (iconResId == 0) {
            mBinding.iv.visibility = View.GONE
            mBinding.iv.setImageDrawable(null)
        } else {
            mBinding.iv.visibility = View.VISIBLE
            mBinding.iv.setImageResource(iconResId)
        }
    }
} 