package com.like.common.view.toolbar

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import com.like.common.R
import com.like.common.databinding.ToolbarBinding
import com.like.common.view.badgeview.BadgeViewHelper

/**
 * Toolbar相关工具类
 */
class ToolbarUtils(private val mContext: Context, toolbarContainer: ViewGroup) {
    private val mBinding: ToolbarBinding by lazy { DataBindingUtil.inflate<ToolbarBinding>(LayoutInflater.from(mContext), R.layout.toolbar, toolbarContainer, true) }
    private var navigationBadgeViewHelper: BadgeViewHelper? = null

    init {
        mBinding.toolbar.title = ""// 屏蔽掉原来的标题
    }

    fun getToolbar(): Toolbar = mBinding.toolbar

    fun getToolbarHeight() = mBinding.root.height

    /**
     * 显示标题栏底部的分割线，默认是显示的
     */
    fun showDivider(): ToolbarUtils {
        mBinding.divider.visibility = View.VISIBLE
        return this
    }

    /**
     * 隐藏标题栏底部的分割线
     */
    fun hideDivider(): ToolbarUtils {
        mBinding.divider.visibility = View.GONE
        return this
    }

    fun setDividerHeight(height: Int): ToolbarUtils {
        if (height > 0)
            mBinding.divider.layoutParams.height = height
        return this
    }

    fun getDividerHeight() = mBinding.divider.height

    /**
     * 设置标题栏底部的分割线的颜色
     */
    fun setDividerColor(@ColorInt color: Int): ToolbarUtils {
        mBinding.divider.setBackgroundColor(color)
        return this
    }

    /**
     * 设置标题栏背景颜色
     *
     * @return
     */
    fun setBackgroundByColor(@ColorInt color: Int): ToolbarUtils {
        mBinding.toolbar.setBackgroundColor(color)
        return this
    }

    /**
     * 设置标题栏背景颜色
     *
     * @return
     */
    fun setBackgroundByColorResId(@ColorRes colorResId: Int): ToolbarUtils {
        mBinding.toolbar.setBackgroundColor(ActivityCompat.getColor(mContext, colorResId))
        return this
    }

    /**
     * 设置导航按钮
     *
     * @param listener      导航按钮单击事件
     * @param iconResId     导航按钮图片资源id
     * @return
     */
    @JvmOverloads
    fun showNavigationButton(@DrawableRes iconResId: Int = -1, listener: View.OnClickListener? = null): ToolbarUtils {
        if (iconResId != -1) {
            mBinding.toolbar.setNavigationIcon(iconResId)
        }
        if (listener != null) {
            mBinding.toolbar.setNavigationOnClickListener(listener)
        }
        return this
    }

    /**
     * 屏蔽掉navigation按钮
     *
     * @return
     */
    fun hideNavigationBotton(): ToolbarUtils {
        mBinding.toolbar.navigationIcon = null
        return this
    }

    /**
     * 设置自定义视图的导航按钮
     */
    @JvmOverloads
    fun showCustomNavigationView(@DrawableRes iconResId: Int = -1, name: String = "", listener: View.OnClickListener? = null): ToolbarUtils {
        hideNavigationBotton()
        navigationBadgeViewHelper = BadgeViewHelper(mContext, mBinding.toolbarNavigationCustomView.messageContainer)
        if (!TextUtils.isEmpty(name)) {
            mBinding.toolbarNavigationCustomView.tvTitle.visibility = View.VISIBLE
            mBinding.toolbarNavigationCustomView.tvTitle.text = name
        }
        if (iconResId != -1) {
            mBinding.toolbarNavigationCustomView.iv.visibility = View.VISIBLE
            mBinding.toolbarNavigationCustomView.iv.setImageResource(iconResId)
        }
        if (listener != null) {
            mBinding.toolbarNavigationCustomView.root.setOnClickListener(listener)
        }
        return this
    }

    /**
     * 设置自定义视图的导航按钮文本颜色
     */
    fun setCustomNavigationViewTextColor(@ColorInt color: Int): ToolbarUtils {
        mBinding.toolbarNavigationCustomView.tvTitle.setTextColor(color)
        return this
    }

    /**
     * 设置自定义视图的导航按钮文本大小
     *
     * @param size sp
     */
    fun setCustomNavigationViewTextSize(size: Float): ToolbarUtils {
        if (size > 0f)
            mBinding.toolbarNavigationCustomView.tvTitle.textSize = size
        return this
    }

    /**
     * 设置自定义视图的左边距
     */
    fun setNavigationViewLeftMargin(leftMargin: Int): ToolbarUtils {
        mBinding.toolbar.setContentInsetsAbsolute(leftMargin, 0)
        return this
    }

    /**
     * 设置自定义视图的导航按钮右上角显示的消息数
     */
    fun setCustomNavigationViewMessageCount(messageCount: String): ToolbarUtils {
        navigationBadgeViewHelper?.setMessageCount(messageCount)
        return this
    }

    /**
     * 设置自定义视图的导航按钮右上角显示的消息数的文本颜色
     */
    fun setCustomNavigationViewMessageTextColor(@ColorInt color: Int): ToolbarUtils {
        navigationBadgeViewHelper?.setTextColor(color)
        return this
    }

    /**
     * @param size sp
     */
    fun setCustomNavigationViewMessageTextSize(size: Int): ToolbarUtils {
        navigationBadgeViewHelper?.setTextSize(size)
        return this

    }

    fun setCustomNavigationViewMessageBackgroundColor(@ColorInt color: Int): ToolbarUtils {
        navigationBadgeViewHelper?.setBackgroundColor(color)
        return this
    }

    /**
     * 设置标题
     *
     * @param title
     * @param colorResId 文本颜色
     * @return
     */
    fun showTitle(title: String, @ColorRes colorResId: Int): ToolbarUtils {
        mBinding.tvTitle.setTextColor(ActivityCompat.getColor(mContext, colorResId))
        mBinding.tvTitle.text = title
        return this
    }

    /**
     * 设置Toolbar右侧(标题右侧)的几个菜单按钮
     *
     * @param menuResId
     * @param listener
     * @return
     */
    @JvmOverloads
    fun setRightMenu(@MenuRes menuResId: Int, listener: Toolbar.OnMenuItemClickListener? = null): ToolbarUtils {
        mBinding.toolbar.inflateMenu(menuResId)
        if (listener != null)
            mBinding.toolbar.setOnMenuItemClickListener(listener)
        return this
    }

    /**
     * 替换menu为自定义的视图。并设置是否隐藏
     *
     * @param menuItemId    menu中的某个item的id
     * @param listener
     * @param iconResId     <=0即不显示图片，默认-1
     * @param name          为empty时即不显示名称，默认""
     * @param isShow        是否立即显示，默认true
     * @return
     */
    @SuppressLint("RestrictedApi")
    @JvmOverloads
    fun replaceMenuWithCustomView(menuItemId: Int, @DrawableRes iconResId: Int = -1, name: String = "", isShow: Boolean = true, listener: View.OnClickListener? = null): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.let {
            it.reset()
            if (listener != null)
                it.setOnClickListener(listener)
            if (!TextUtils.isEmpty(name))
                it.name = name
            if (iconResId != -1)
                it.setIcon(iconResId)
            if (isShow) {
                it.show()
            } else {
                it.hide()
            }
        }
        return this
    }

    /**
     * 设置右边指定菜单按钮的消息数量
     *
     * @param menuItemId
     * @param messageCount
     * @return
     */
    fun setRightMenuMessageCount(menuItemId: Int, messageCount: String): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.setMessageCount(messageCount)
        return this
    }

    /**
     * 设置右边指定菜单按钮的文本颜色
     *
     * @param menuItemId
     * @param color
     * @return
     */
    fun setRightMenuTextColor(menuItemId: Int, @ColorInt color: Int): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.setTextColor(color)
        return this
    }

    /**
     * 设置右边指定菜单按钮的文本大小
     *
     * @param menuItemId
     * @param size   单位sp
     * @return
     */
    fun setRightMenuTextSize(menuItemId: Int, size: Float): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.setTextSize(size)
        return this
    }

    /**
     * 设置右边指定菜单按钮的文本
     *
     * @param menuItemId
     * @param name
     * @return
     */
    fun setRightMenuName(menuItemId: Int, name: String): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.name = name
        return this
    }

    /**
     * 设置右边指定菜单按钮的左右margin
     */
    fun setRightMenuMargin(menuItemId: Int, leftAndRightMargin: Int, topAndBottomMargin: Int): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.setMargin(leftAndRightMargin, topAndBottomMargin)
        return this
    }

    /**
     * 设置右边指定菜单按钮的消息视图距离图标的距离
     */
    fun setRightMenuMessageMargin(menuItemId: Int, left: Int, top: Int, right: Int, bottom: Int): ToolbarUtils {
        getCustomActionProvider(menuItemId)?.setMessageMargin(left, top, right, bottom)
        return this
    }

    /**
     * 获取右边指定菜单按钮的文本
     *
     * @param menuItemId
     * @return
     */
    fun getRightMenuName(menuItemId: Int) = getCustomActionProvider(menuItemId)?.name ?: ""

    /**
     * 隐藏右边指定菜单
     *
     * @param menuItemId
     */
    fun hideRightMenu(menuItemId: Int) {
        getCustomActionProvider(menuItemId)?.hide()
    }

    /**
     * 显示右边指定菜单
     *
     * @param menuItemId
     */
    fun showRightMenu(menuItemId: Int) {
        getCustomActionProvider(menuItemId)?.show()
    }

    /**
     * 获取控制message视图相关功能的Provider
     *
     * @param menuItemId
     * @return
     */
    private fun getCustomActionProvider(menuItemId: Int): CustomActionProvider? {
        val item = mBinding.toolbar.menu.findItem(menuItemId)
        if (item != null) {
            val actionProvider = MenuItemCompat.getActionProvider(item)
            if (actionProvider is CustomActionProvider) {
                return actionProvider
            } else {
                Log.w("ToolbarUtils", "getActionProvider: item does not implement SupportMenuItem; returning null")
                return null
            }
        } else {
            return null
        }
    }

}
