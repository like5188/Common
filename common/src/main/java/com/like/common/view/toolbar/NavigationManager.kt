package com.like.common.view.toolbar

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import com.like.common.databinding.ToolbarBinding
import com.like.common.view.badgeview.BadgeViewManager
import com.like.common.view.toolbar.custom.CustomViewManager

/**
 * [Toolbar]中的导航按钮管理类
 */
class NavigationManager(context: Context, binding: ToolbarBinding) {
    // 自定义视图中的消息视图管理类，用于控制右上角的消息
    private val mBadgeViewHelper: BadgeViewManager by lazy {
        BadgeViewManager(context, binding.navigationView.cl)
    }
    // 自定义视图的管理类，用于控制自定义视图
    private val mCustomViewHelper: CustomViewManager by lazy {
        CustomViewManager(context, binding.navigationView)
    }

    /**
     * 显示自定义视图的导航按钮
     *
     * @param resid             图标资源id
     * @param title             文本
     * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
     * @param textSize          文本字体大小。默认为null，表示不设置，保持原样。
     * @param listener          点击监听
     */
    fun showView(
            @DrawableRes resid: Int = 0,
            title: String = "",
            @ColorInt textColor: Int? = null,
            textSize: Float? = null,
            listener: View.OnClickListener? = null
    ) {
        mCustomViewHelper.setIcon(resid)
        mCustomViewHelper.setTitle(title, textColor, textSize)
        mCustomViewHelper.setOnClickListener(listener)
    }

    /**
     * 设置自定义视图的导航按钮的margin
     */
    fun setMargin(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        mCustomViewHelper.setMargin(left, top, right, bottom)
    }

    /**
     * 设置自定义视图的导航按钮的内容的padding，用于调整消息位置
     */
    fun setContentPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        mCustomViewHelper.setContentPadding(left, top, right, bottom)
    }

    /**
     * 显示自定义视图的导航按钮的消息
     *
     * @param messageCount      消息数
     * @param textColor         文本颜色。默认为null，表示不设置，保持原样。
     * @param textSize          文本字体大小，sp。默认为null，表示不设置，保持原样。
     * @param backgroundColor   背景颜色。默认为null，表示不设置，保持原样。
     */
    fun showMessageCount(
            messageCount: String,
            @ColorInt textColor: Int? = null,
            textSize: Int? = null,
            @ColorInt backgroundColor: Int? = null
    ) {
        mBadgeViewHelper.setMessageCount(messageCount, textColor, textSize, backgroundColor)
    }
}