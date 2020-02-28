package com.like.common.view.toolbar

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.like.common.R
import com.like.common.databinding.ToolbarBinding
import com.like.common.util.DimensionUtils

/**
 * Toolbar相关工具类
 */
class ToolbarUtils(private val mContext: Context, toolbarContainer: ViewGroup) {
    private val mBinding: ToolbarBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.toolbar, toolbarContainer, true)
    private val mNavigationManager: NavigationManager by lazy {
        NavigationManager(mContext, mBinding)
    }
    private val mMenuManager: MenuManager by lazy {
        MenuManager(mContext, mBinding)
    }

    /**
     * 获取 [Toolbar]
     */
    fun getToolbar(): Toolbar = mBinding.toolbar

    /**
     * 获取高度，包括标题栏和分割线
     */
    fun getHeight() = mBinding.root.height

    /**
     * 设置背景色。如果不设置，那么默认为[colorPrimary]
     */
    fun setBackgroundColor(@ColorInt color: Int) {
        mBinding.toolbar.setBackgroundColor(color)
    }

    /**
     * 显示原生标题
     *
     * @param title             文本
     * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
     */
    fun showTitle(title: String, @ColorInt textColor: Int? = null) {
        mBinding.toolbar.title = title
        if (textColor != null) {
            mBinding.toolbar.setTitleTextColor(textColor)
        }
    }

    /**
     * 显示自定义的居中的标题
     *
     * @param title             文本
     * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
     * @param textSize          文本字体大小。默认为null，表示不设置，保持原样。
     */
    fun showCustomTitle(title: String, @ColorInt textColor: Int? = null, textSize: Float? = null) {
        if (title.isEmpty()) {
            mBinding.tvTitle.visibility = View.GONE
            mBinding.tvTitle.text = ""
        } else {
            mBinding.tvTitle.visibility = View.VISIBLE
            mBinding.tvTitle.text = title
            if (textColor != null) {
                mBinding.tvTitle.setTextColor(textColor)
            }
            if (textSize != null) {
                mBinding.tvTitle.textSize = textSize
            }
        }
    }

    /**
     * 显示标题栏底部的分割线，默认未显示
     *
     * @param height    分割线高度，dp
     * @param color     分割线颜色
     */
    fun showDivider(height: Float = 1f, @ColorInt color: Int = Color.LTGRAY) {
        mBinding.divider.visibility = View.VISIBLE
        if (height > 0) {
            mBinding.divider.layoutParams.height = DimensionUtils.dp2px(mContext, height)
        }
        mBinding.divider.setBackgroundColor(color)
    }

    /**
     * 获取导航按钮管理类
     */
    fun getNavigationManager(): NavigationManager {
        return mNavigationManager
    }

    /**
     * 获取菜单管理类
     */
    fun getMenuManager(): MenuManager {
        return mMenuManager
    }

}
