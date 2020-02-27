package com.like.common.view.toolbar

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import com.like.common.R
import com.like.common.databinding.ToolbarBinding
import com.like.common.util.DimensionUtils
import com.like.common.view.badgeview.BadgeViewHelper

/**
 * Toolbar相关工具类
 */
class ToolbarUtils(private val mContext: Context, toolbarContainer: ViewGroup) {
    private val mBinding: ToolbarBinding by lazy {
        DataBindingUtil.inflate<ToolbarBinding>(LayoutInflater.from(mContext), R.layout.toolbar, toolbarContainer, true)
    }
    private val mNavigationBadgeViewHelper: BadgeViewHelper by lazy {
        BadgeViewHelper(mContext, mBinding.navigationView.cl)
    }
    private val mNavigationToolbarCustomViewHelper: ToolbarCustomViewHelper by lazy {
        ToolbarCustomViewHelper(mContext, mBinding.navigationView)
    }

    init {
        mBinding
    }

    fun getToolbar(): Toolbar = mBinding.toolbar

    /**
     * 获取高度，包括标题栏和分割线
     */
    fun getHeight() = mBinding.root.height

    /**
     * 设置背景色
     */
    fun setBackgroundColor(@ColorInt color: Int): ToolbarUtils {
        mBinding.toolbar.setBackgroundColor(color)
        return this
    }

    /**
     * 显示标题
     *
     * @param title
     * @param color     文本颜色
     * @return
     */
    fun showTitle(title: String, @ColorInt color: Int = Color.WHITE): ToolbarUtils {
        mBinding.tvTitle.setTextColor(color)
        mBinding.tvTitle.text = title
        return this
    }

    /**
     * 显示标题栏底部的分割线，默认未显示
     *
     * @param height    分割线高度，dp
     * @param color     分割线颜色
     */
    fun showDivider(
            height: Float = 1f,
            @ColorInt color: Int = Color.LTGRAY
    ): ToolbarUtils {
        mBinding.divider.visibility = View.VISIBLE
        if (height > 0) {
            mBinding.divider.layoutParams.height = DimensionUtils.dp2px(mContext, height)
        }
        mBinding.divider.setBackgroundColor(color)
        return this
    }

    /**
     * 显示自定义视图的导航按钮
     *
     * @param resid             图标资源id
     * @param title             文本
     * @param titleColor        文本颜色
     * @param titleTextSize     文本字体大小
     * @param listener          点击监听
     */
    fun showNavigationView(
            @DrawableRes resid: Int = 0,
            title: String? = null,
            @ColorInt titleColor: Int = Color.WHITE,
            titleTextSize: Float = 12f,
            listener: View.OnClickListener? = null
    ): ToolbarUtils {
        mNavigationToolbarCustomViewHelper.setIcon(resid)
        mNavigationToolbarCustomViewHelper.setTitle(title, titleColor, titleTextSize)
        mNavigationToolbarCustomViewHelper.setOnClickListener(listener)
        return this
    }

    /**
     * 设置自定义视图的导航按钮的margin
     */
    fun setNavigationViewMargin(left: Int = 42, top: Int = 10, right: Int = 20, bottom: Int = 10): ToolbarUtils {
        mNavigationToolbarCustomViewHelper.setMargin(left, top, right, bottom)
        return this
    }

    /**
     * 设置自定义视图的导航按钮的内容的padding，用于调整消息位置
     */
    fun setNavigationViewContentPadding(left: Int = 0, top: Int = 0, right: Int = 30, bottom: Int = 0): ToolbarUtils {
        mNavigationToolbarCustomViewHelper.setContentPadding(left, top, right, bottom)
        return this
    }

    /**
     * 显示自定义视图的导航按钮的消息
     */
    fun showNavigationViewMessageCount(
            messageCount: String,
            @ColorInt textColor: Int = Color.WHITE,
            @ColorInt backgroundColor: Int = Color.RED,
            textSize: Int = 10
    ): ToolbarUtils {
        mNavigationBadgeViewHelper.setMessageCount(messageCount, textColor, textSize, backgroundColor)
        return this
    }

    /**
     * 显示Toolbar原生菜单
     *
     * @param menuResId
     * @param listener
     */
    fun showMenu(@MenuRes menuResId: Int, listener: Toolbar.OnMenuItemClickListener? = null): ToolbarUtils {
        mBinding.toolbar.inflateMenu(menuResId)
        if (listener != null) {
            mBinding.toolbar.setOnMenuItemClickListener(listener)
        }
        return this
    }

    /**
     * 替换原生菜单按钮为自定义的视图。注意：如果原生菜单是折叠的，那么将不会生效。
     *
     * @param menuItemId    menu中的某个item的id
     * @param listener
     */
    fun replaceMenuWithCustomView(menuItemId: Int, listener: View.OnClickListener? = null): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.apply {
            if (listener != null) {
                setOnClickListener(listener)
            }
        }
        return this
    }

    /**
     * 设置指定菜单按钮的icon
     *
     * @param menuItemId
     * @param iconResId
     */
    fun setCustomViewMenuIcon(menuItemId: Int, @DrawableRes iconResId: Int): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.apply {
            setIcon(iconResId)
        }
        return this
    }

    /**
     * 设置指定菜单按钮的文本
     *
     * @param menuItemId
     * @param title
     * @param color
     * @param size   单位sp
     */
    fun setCustomViewMenuTitle(menuItemId: Int, title: String, @ColorInt color: Int? = null, size: Float? = null): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.apply {
            setTitle(title, color, size)
        }
        return this
    }

    /**
     * 获取指定菜单按钮的文本
     */
    fun getCustomViewMenuTitle(menuItemId: Int) = getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.getTitle() ?: ""


    /**
     * 设置指定菜单按钮的margin，只能指定top、bottom；left、right指定了也无效。
     */
    fun setCustomViewMenuMargin(menuItemId: Int, top: Int = 0, bottom: Int = 0): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.setMargin(0, top, 0, bottom)
        return this
    }

    /**
     * 设置指定菜单按钮的内容的padding，用于调整消息位置
     */
    fun setCustomViewMenuContentPadding(menuItemId: Int, left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.setContentPadding(left, top, right, bottom)
        return this
    }

    /**
     * 设置指定菜单按钮的消息
     *
     * @param menuItemId
     * @param messageCount
     */
    fun setCustomViewMenuMessageCount(
            menuItemId: Int,
            messageCount: String,
            @ColorInt textColor: Int? = null,
            textSize: Int? = null,
            @ColorInt backgroundColor: Int? = null
    ): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.getBadgeViewHelper()?.apply {
            setMessageCount(messageCount, textColor, textSize, backgroundColor)
        }
        return this
    }

    /**
     * 获取指定菜单按钮的消息
     *
     * @param menuItemId
     */
    fun getCustomViewMenuMessageCount(menuItemId: Int): String {
        return getCustomActionProvider(menuItemId)?.getBadgeViewHelper()?.getMessageCount() ?: ""
    }

    /**
     * 获取自定义视图的控制器ActionProvider
     *
     * @param menuItemId
     */
    private fun getCustomActionProvider(menuItemId: Int): CustomActionProvider? {
        return MenuItemCompat.getActionProvider(mBinding.toolbar.menu.findItem(menuItemId)) as? CustomActionProvider
    }

}
