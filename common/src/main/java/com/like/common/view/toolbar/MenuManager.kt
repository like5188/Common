package com.like.common.view.toolbar

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import com.like.common.databinding.ToolbarBinding

/**
 * [Toolbar]中的菜单管理类
 */
class MenuManager(private val mContext: Context, private val mBinding: ToolbarBinding) {
    /**
     * 显示Toolbar原生菜单
     *
     * @param menuResId             菜单资源id
     * @param overflowIconResId     溢出按钮图标。默认为null，表示不设置，保持原样。
     * @param listener              菜单按钮点击监听。
     * 如果菜单没有折叠，那么在这里可以不设置，通过[replaceMenuWithCustomView]方法来设置。
     * 如果菜单折叠了，就只能在这里设置了，通过[replaceMenuWithCustomView]方法来设置是无效的。
     */
    fun showMenu(@MenuRes menuResId: Int, @DrawableRes overflowIconResId: Int? = null, listener: Toolbar.OnMenuItemClickListener? = null) {
        mBinding.toolbar.inflateMenu(menuResId)
        if (overflowIconResId != null && overflowIconResId != 0) {
            mBinding.toolbar.overflowIcon = BitmapDrawable(mContext.resources, BitmapFactory.decodeResource(mContext.resources, overflowIconResId))
        }
        if (listener != null) {
            mBinding.toolbar.setOnMenuItemClickListener(listener)
        }
    }

    /**
     * 替换原生菜单按钮为自定义的视图。注意：如果原生菜单是折叠的，那么将不会生效。
     *
     * @param menuItemId    菜单中的某个item的id
     * @param listener
     */
    fun replaceMenuWithCustomView(menuItemId: Int, listener: View.OnClickListener? = null) {
        getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.apply {
            if (listener != null) {
                setOnClickListener(listener)
            }
        }
    }

    /**
     * 设置指定自定义视图菜单按钮的icon
     *
     * @param menuItemId    菜单中的某个item的id
     * @param iconResId     图标资源id
     */
    fun setCustomViewMenuIcon(menuItemId: Int, @DrawableRes iconResId: Int) {
        getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.apply {
            setIcon(iconResId)
        }
    }

    /**
     * 设置指定自定义视图菜菜单按钮的文本
     *
     * @param menuItemId        菜单中的某个item的id
     * @param title             文本
     * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
     * @param textSize          文本字体大小。默认为null，表示不设置，保持原样。
     */
    fun setCustomViewMenuTitle(menuItemId: Int, title: String, @ColorInt textColor: Int? = null, textSize: Float? = null) {
        getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.apply {
            setTitle(title, textColor, textSize)
        }
    }

    /**
     * 获取指定自定义视图菜单按钮的文本
     */
    fun getCustomViewMenuTitle(menuItemId: Int) = getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.getTitle() ?: ""

    /**
     * 设置指定自定义视图菜单按钮的margin
     * 只能指定top、bottom；由于[ActionMenuView]的原因，left、right指定了也无效。
     */
    fun setCustomViewMenuMargin(menuItemId: Int, top: Int = 0, bottom: Int = 0) {
        getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.setMargin(0, top, 0, bottom)
    }

    /**
     * 设置指定自定义视图菜单按钮的内容的padding，用于调整消息位置
     */
    fun setCustomViewMenuContentPadding(menuItemId: Int, left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        getCustomActionProvider(menuItemId)?.getToolbarCustomViewHelper()?.setContentPadding(left, top, right, bottom)
    }

    /**
     * 设置指定自定义视图菜单按钮的消息
     *
     * @param menuItemId
     * @param messageCount      消息数
     * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
     * @param textSize          文本字体大小，sp。默认为null，表示不设置，保持原样。
     * @param backgroundColor   背景颜色。默认为null，表示不设置，保持原样。
     */
    fun setCustomViewMenuMessageCount(
            menuItemId: Int,
            messageCount: String,
            @ColorInt textColor: Int? = null,
            textSize: Int? = null,
            @ColorInt backgroundColor: Int? = null
    ) {
        getCustomActionProvider(menuItemId)?.getBadgeViewHelper()?.apply {
            setMessageCount(messageCount, textColor, textSize, backgroundColor)
        }
    }

    /**
     * 获取指定自定义视图菜单按钮的消息
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